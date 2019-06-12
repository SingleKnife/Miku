package com.fyd.miku.model.pmd;

import android.text.TextUtils;
import android.util.Log;

import com.fyd.miku.io.ObjectBufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PMDFile {
    private ObjectBufferedInputStream pmdStream;
    private PMDHeader header;

    public AllVertex allVertex;
    public List<Material> materials;
    public List<Bone> bones;
    public List<IKInfo> ikInfos;
    public List<FaceMorph> faceMorphs;
    public DisplayNameInfo displayNameInfo;
    public List<RigidBody> rigidBodies;
    public List<Joint> joints;

    String filePath;

    private boolean baseInfoReadFinish;

    public PMDFile() {
    }

    public boolean parse(File pmdFile){
        try {
            filePath = pmdFile.getParent() + File.separator;
            InputStream inputStream = new FileInputStream(pmdFile);
            parse(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseInfoReadFinish;
    }

    private boolean parse(InputStream inputStream){
        try{
            pmdStream = new ObjectBufferedInputStream(inputStream, ObjectBufferedInputStream.ByteOrder.LITTLE_ENDIAN);
            parseHeader();
            parseVertices();
            parseVertexIndices();
            parseMaterials();
            parseBones();
            parseIk();
            parseFaceMorph();
            parseDisplayInfo();
            baseInfoReadFinish = true;
            //下面是扩展信息
            if(pmdStream.available() > 0) {
                parseEnglishNameInfo();
                parseRigidBody();
                parseJoint();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baseInfoReadFinish;
    }

    private void parseHeader() throws IOException {
        header = new PMDHeader();
        header.magic = pmdStream.readSJISString(3);
        header.version = pmdStream.readFloat();
        header.modelName = pmdStream.readSJISString(20);
        header.modelComment = pmdStream.readSJISString(256);
    }

    private void parseVertices() throws IOException {
        int vertexNum = pmdStream.readInt();
        if(vertexNum > 0) {
            int byteNum = vertexNum * AllVertex.BYTE_SIZE_PER_VERTEX;
            byte[] buffer = new byte[byteNum];
            if(pmdStream.read(buffer) == -1) {
                throw new IOException("parseVertices not read enough bytes");
            }
            allVertex = new AllVertex();
            allVertex.setVertices(buffer);
            allVertex.getAllVertices().position(0);
        }
    }

    private void parseVertexIndices() throws IOException {
        int indicesNum = pmdStream.readInt();
        if(indicesNum > 0) {
            int byteNum = indicesNum * AllVertex.BYTE_SIZE_PER_INDEX;
            byte[] buffer = new byte[byteNum];
            if(pmdStream.read(buffer) == -1) {
                throw new IOException("parseVertexIndices not read enough bytes");
            }
            allVertex.setIndices(buffer);
        }
    }

    private void parseMaterials() throws IOException {
        int materialsNum = pmdStream.readInt();
        materials = new ArrayList<>();
        if(materialsNum <= 0) {
            return;
        }

        int vertexIndexOffset = 0;
        for(int i = 0; i < materialsNum; ++i) {
            Material material = new Material();
            pmdStream.readFloats(material.diffuseColor, 0, 4);
            material.specularPower = pmdStream.readFloat();
            pmdStream.readFloats(material.specularColor, 0, 3);
            pmdStream.readFloats(material.ambientColor, 0, 3);
            material.toonIndex = pmdStream.read();
            material.edgeFlag = pmdStream.read();
            material.vertexIndicesNum = pmdStream.readInt();
            String textureNames = pmdStream.readSJISString(20);
            if(!TextUtils.isEmpty(textureNames)) {
                String[] tex = textureNames.split("\\*");
                material.textureName =  filePath + tex[0];
                Log.i("fyd", "textureName name: " + material.textureName);
                if(tex.length > 1) {
                   material.sphereMapName = tex[1];
                }
            }

            material.vertexIndexOffset =vertexIndexOffset;
            vertexIndexOffset += material.vertexIndicesNum;
            materials.add(material);

            Log.i("mmd", "material: " + material);
        }
    }

    private void parseBones() throws IOException{
        int boneNum = pmdStream.readUnsignedShort();
        bones = new ArrayList<>();
        if(boneNum <= 0) {
            return;
        }

        for (int i = 0; i < boneNum; ++i) {
            Bone bone = new Bone();
            bone.boneName = pmdStream.readSJISString(20);
            bone.parentBoneIndex = pmdStream.readUnsignedShort();
            bone.childBoneIndex = pmdStream.readUnsignedShort();
            bone.boneType = pmdStream.read();
            bone.ikParent = pmdStream.readUnsignedShort();
            bone.isKnee = bone.boneName.contains("ひざ");
            pmdStream.readFloats(bone.position,0, 3);
            bones.add(bone);
        }
    }

    private void parseIk() throws IOException {
        int ikNum = pmdStream.readUnsignedShort();
        ikInfos = new ArrayList<>();
        if(ikNum <= 0) {
            return;
        }

        for(int i = 0; i < ikNum; ++i) {
            IKInfo ikInfo = new IKInfo();
            ikInfo.ikBoneIndex = pmdStream.readUnsignedShort();
            ikInfo.effectorBoneIndex = pmdStream.readUnsignedShort();
            ikInfo.boneNum = pmdStream.read();
            ikInfo.iterationNum = pmdStream.readUnsignedShort();
            ikInfo.rotateLimit = pmdStream.readFloat();

            for(int j = 0; j < ikInfo.boneNum; ++j) {
                ikInfo.boneList.add(pmdStream.readUnsignedShort());
            }
            ikInfos.add(ikInfo);
        }
    }

    private void parseFaceMorph() throws IOException {
        int morphNum = pmdStream.readUnsignedShort();
        faceMorphs = new ArrayList<>();

        if(morphNum <= 0) {
            return;
        }

        for(int i = 0; i < morphNum; ++i) {
            String morphName = pmdStream.readSJISString(20);
            int verticesCount = pmdStream.readInt();
            int morphType = pmdStream.read();
            FaceMorph faceMorph = new FaceMorph();
            faceMorph.morphName = morphName;
            faceMorph.morphType = morphType;
            for(int j = 0; j < verticesCount; ++j) {
                FaceMorph.Vertex vertex = new FaceMorph.Vertex();
                vertex.vertexIndex = pmdStream.readInt();
                pmdStream.readFloats(vertex.posOffset, 0, 3);
                faceMorph.vertices.add(vertex);
            }
            faceMorphs.add(faceMorph);
        }
    }

    private void parseDisplayInfo() throws IOException{
        displayNameInfo = new DisplayNameInfo();
        int faceMorphNum = pmdStream.read();
        for(int i = 0; i < faceMorphNum; ++i) {
            displayNameInfo.faceMorphIndices.add(pmdStream.readUnsignedShort());
        }
        int boneGroupNum = pmdStream.read();
        for(int i = 0; i < boneGroupNum; ++i) {
            displayNameInfo.boneGroupNames.add(pmdStream.readSJISString(50));
        }

        int boneNum = pmdStream.readInt();
        for(int i = 0; i < boneNum; ++i) {
            DisplayNameInfo.BoneGroup boneGroup = new DisplayNameInfo.BoneGroup();
            boneGroup.boneIndex = pmdStream.readUnsignedShort();
            boneGroup.boneGroupIndex = pmdStream.read();
            displayNameInfo.boneGroups.add(boneGroup);
        }
    }

    private void parseEnglishNameInfo() throws IOException{
        int hasEnglishInfo = pmdStream.read();
        if(hasEnglishInfo == 1) {
            header.modelNameEnglish = pmdStream.readSJISString(20);
            header.modelCommentEnglish = pmdStream.readSJISString(256);
            for(Bone bone : bones) {
                bone.boneNameEnglish = pmdStream.readSJISString(20);
            }
            for(FaceMorph faceMorph : faceMorphs) {
                //不包含base动画
                if(faceMorph.morphType == 0) {
                    faceMorph.morphNameEnglish = "base";
                } else {
                    faceMorph.morphNameEnglish = pmdStream.readSJISString(20);
                }
            }
            for(int i = 0; i < displayNameInfo.boneGroupNames.size(); ++i) {
                displayNameInfo.boenGroupNamesEnglish.add(pmdStream.readSJISString(50));
            }

            for(int i = 0; i < 10; ++i) {
                String toonName = pmdStream.readSJISString(100);
                if(!TextUtils.isEmpty(toonName)) {
                    Material.toonNames[i] = toonName;
                }
            }
        }
    }

    private void parseRigidBody() throws IOException{
        int rigidBodyNum = pmdStream.readInt();
        rigidBodies = new ArrayList<>();
        if(rigidBodyNum <= 0) {
            return;
        }
        for(int i = 0; i < rigidBodyNum; ++i) {
            RigidBody rigidBody = new RigidBody();
            rigidBody.name = pmdStream.readSJISString(20);
            rigidBody.boneIndex = pmdStream.readUnsignedShort();
            rigidBody.group = pmdStream.read();
            rigidBody.mask = pmdStream.readUnsignedShort();
            rigidBody.shape = pmdStream.read();
            rigidBody.shapeWidth = pmdStream.readFloat();
            rigidBody.shapeHeight = pmdStream.readFloat();
            rigidBody.shapeDepth = pmdStream.readFloat();
            pmdStream.readFloats(rigidBody.shapePos, 0, 3);
            pmdStream.readFloats(rigidBody.shapeRotation, 0, 3);
            rigidBody.mass = pmdStream.readFloat();
            rigidBody.linearDimmer = pmdStream.readFloat();
            rigidBody.angularDamping = pmdStream.readFloat();
            rigidBody.rigidBodyRecoil = pmdStream.readFloat();
            rigidBody.rigidBodyFriction = pmdStream.readFloat();
            rigidBody.rigidBodyType = pmdStream.read();
            rigidBodies.add(rigidBody);
        }
    }

    private void parseJoint() throws IOException{
        int jointNum = pmdStream.readInt();
        joints = new ArrayList<>();
        if(jointNum <= 0) {
            return;
        }
        for(int i = 0; i < jointNum; ++i) {
            Joint joint = new Joint();
            joint.name = pmdStream.readSJISString(20);
            joint.firstRigidBody = pmdStream.readInt();
            joint.secondRigidBody = pmdStream.readInt();
            pmdStream.readFloats(joint.jointPos, 0, 3);
            pmdStream.readFloats(joint.jointRotation, 0, 3);
            pmdStream.readFloats(joint.posLowerLimit, 0, 3);
            pmdStream.readFloats(joint.posUpperLimit, 0, 3);
            pmdStream.readFloats(joint.rotationLowerLimit, 0, 3);
            pmdStream.readFloats(joint.rotationUpperLimit, 0, 3);
            pmdStream.readFloats(joint.posSpringStiffness, 0, 3);
            pmdStream.readFloats(joint.rotationSpringStiffness, 0, 3);
            joints.add(joint);
        }
    }
}

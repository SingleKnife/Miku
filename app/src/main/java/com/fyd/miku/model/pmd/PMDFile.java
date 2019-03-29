package com.fyd.miku.model.pmd;

import android.text.TextUtils;
import android.util.Log;

import com.fyd.miku.io.ObjectBufferedInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PMDFile {
    ObjectBufferedInputStream pmdStream;
    PMDHeader header;
    AllVertex allVertex;
    List<Material> materials;
    List<Bone> bones;
    List<IKInfo> ikInfos;
    List<FaceMorph> faceMorphs;
    DisplayNameInfo displayNameInfo;
    List<RigidBody> rigidBodies;
    List<Joint> joints;

    private boolean baseInfoReadFinish;

    public PMDFile() {
    }

    public boolean parse(InputStream inputStream) throws IOException{
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
        return true;
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
        Log.i("mmd", "vertexIndicesNum: " + indicesNum);
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
        Log.i("mmd", "materialsNum: " + materialsNum);
        if(materialsNum <= 0) {
            return;
        }

        int vertexIndexOffset = 0;
        for(int i = 0; i < materialsNum; ++i) {
            Material material = new Material();
            pmdStream.readFloats(material.diffuseColor);
            material.specularPower = pmdStream.readFloat();
            pmdStream.readFloats(material.specularColor);
            pmdStream.readFloats(material.ambientColor);
            material.toonIndex = pmdStream.read();
            material.edgeFlag = pmdStream.read();
            material.vertexIndicesNum = pmdStream.readInt();
            String textureNames = pmdStream.readSJISString(20);
            if(!TextUtils.isEmpty(textureNames)) {
                String[] tex = textureNames.split("\\*");
                material.textureName = tex[0];
                if(tex.length > 1) {
                   material.sphereMapName = tex[1];
                }
            }

            material.vertexIndexOffset =vertexIndexOffset;
            vertexIndexOffset += material.vertexIndicesNum;
            materials.add(material);
            Log.i("mmd", "texture name: " + material.textureName);
        }
    }

    private void parseBones() throws IOException{
        int boneNum = pmdStream.readUnsignedShort();
        bones = new ArrayList<>();
        Log.i("mmd", "bone num: " + boneNum);
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
            pmdStream.readFloats(bone.position);
            bones.add(bone);
        }
    }

    private void parseIk() throws IOException {
        int ikNum = pmdStream.readUnsignedShort();
        ikInfos = new ArrayList<>();
        Log.i("mmd", "ikNum: " + ikNum);
        if(ikNum <= 0) {
            return;
        }

        for(int i = 0; i < ikNum; ++i) {
            IKInfo ikInfo = new IKInfo();
            ikInfo.ikBoneIndex = pmdStream.readUnsignedShort();
            ikInfo.targetBoneIndex = pmdStream.readUnsignedShort();
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
        Log.i("mmd", "morphNum: " + morphNum);

        if(morphNum <= 0) {
            return;
        }

        for(int i = 0; i < morphNum; ++i) {
            FaceMorph faceMorph = new FaceMorph();
            faceMorph.morphName = pmdStream.readSJISString(20);
            faceMorph.verticesCount = pmdStream.readInt();
            faceMorph.morphType = pmdStream.read();
            for(int j = 0; j < faceMorph.verticesCount; ++j) {
                FaceMorph.Vertex vertex = new FaceMorph.Vertex();
                vertex.vertexIndex = pmdStream.readInt();
                pmdStream.readFloats(vertex.position);
                faceMorph.vertices.add(vertex);
            }
            Log.i("mmd", "faceMorph: " + faceMorph);
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
            Log.i("fyd", "english: " + header.modelCommentEnglish);
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
                Log.i("mmd", "toonName: " + toonName);
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
            Log.i("mmd", "rigid name: " + rigidBody.name);
            rigidBody.boneIndex = pmdStream.readUnsignedShort();
            rigidBody.groupIndex = pmdStream.read();
            rigidBody.groupTarget = pmdStream.readUnsignedShort();
            rigidBody.shape = pmdStream.read();
            rigidBody.shapeWidth = pmdStream.readFloat();
            rigidBody.shapeHeight = pmdStream.readFloat();
            rigidBody.shapeDepth = pmdStream.readFloat();
            pmdStream.readFloats(rigidBody.shapePos);
            pmdStream.readFloats(rigidBody.shapeRotation);
            rigidBody.rigidBodyWeight = pmdStream.readFloat();
            rigidBody.rigidBodyPosDimmer = pmdStream.readFloat();
            rigidBody.rigidBodyRotationDimmer = pmdStream.readFloat();
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
            Log.i("mmd", "joint name: " + joint.name);
            joint.firstEffectRigidBody = pmdStream.readInt();
            joint.secondEffectRigidBody = pmdStream.readInt();
            pmdStream.readFloats(joint.jointPos);
            pmdStream.readFloats(joint.jointRotation);
            pmdStream.readFloats(joint.posLowerLimit);
            pmdStream.readFloats(joint.posUpperLimit);
            pmdStream.readFloats(joint.rotationLowerLimit);
            pmdStream.readFloats(joint.rotationUpperLimit);
            pmdStream.readFloats(joint.posSpringStiffness);
            pmdStream.readFloats(joint.rotationSpringStiffness);
            joints.add(joint);
        }
    }
}

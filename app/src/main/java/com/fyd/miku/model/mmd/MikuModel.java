package com.fyd.miku.model.mmd;

import android.util.Log;

import com.fyd.miku.model.pmd.AllVertex;
import com.fyd.miku.model.pmd.IKInfo;
import com.fyd.miku.model.pmd.Material;
import com.fyd.miku.model.pmd.PMDFile;
import com.fyd.miku.model.vmd.VMDFile;
import com.fyd.miku.model.vmd.VMDMorph;
import com.fyd.miku.model.vmd.VMDMotion;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;

public class MikuModel {
    private AllVertex allVertex;
    private List<IKInfo> ikInfos;
    private List<Material> materials;
    private MikuBoneManager boneManager;
    private MikuFaceMorphManager faceMorphManager;
    private MikuAnimation mikuAnimation;
    private MikuPhysicsManager physicisManager;

    public MikuModel(PMDFile pmdFile) {
        this.allVertex = pmdFile.allVertex;
        this.ikInfos = pmdFile.ikInfos;
        this.materials = pmdFile.materials;
        boneManager = new MikuBoneManager(pmdFile.bones, pmdFile.ikInfos);
        faceMorphManager = new MikuFaceMorphManager(pmdFile.faceMorphs, pmdFile.allVertex);
        physicisManager = new MikuPhysicsManager(boneManager, pmdFile.rigidBodies, pmdFile.joints);
        reconstructMaterials(pmdFile.materials);
    }

    public void attachMotion(VMDFile vmdFile) {
        mikuAnimation = new MikuAnimation(boneManager, faceMorphManager, physicisManager);
        initBoneFrames(vmdFile.getMotions());
        initFaceMorphFrames(vmdFile.getMorphs());

        mikuAnimation.sortFrame();
        mikuAnimation.setMotion(0);
    }

    //call on GL thread
    public void updateMotion() {
        if(mikuAnimation != null) {
            mikuAnimation.update();
        }
    }

    public void startAnimation() {
        if(mikuAnimation != null) {
            mikuAnimation.startAnimation();
        }
    }

    public void setFrame(float frame) {
        if(mikuAnimation != null) {
            mikuAnimation.setMotion(frame);
        }
    }

    public AllVertex getAllVertex() {
        return allVertex;
    }


    public List<Material> getMaterials() {
        return materials;
    }

    public MikuBoneManager getBoneManager() {
        return boneManager;
    }

    private void initBoneFrames(List<VMDMotion> vmdMotions) {
        for(VMDMotion vmdMotion : vmdMotions) {
            String boneName = vmdMotion.getBoneName();
            int boneIndex = boneManager.findBone(boneName);
            if(boneIndex == -1) {
                continue;
            }
            BoneFrameManager.BoneFrame boneFrame = new BoneFrameManager.BoneFrame();
            boneFrame.frame = vmdMotion.getFrame();
            boneFrame.boneRotation = vmdMotion.getBoneQuaternion();
            boneFrame.boneTranslate = vmdMotion.getBoneTranslate();
            boneFrame.interpolation = BoneFrameManager.BezierParameters.parse(vmdMotion.getInterpolation());
            mikuAnimation.addBoneFrame(boneIndex, boneFrame);
        }
    }

    private void initFaceMorphFrames(List<VMDMorph> vmdMorphs) {
        for(VMDMorph vmdMorph : vmdMorphs) {
            String morphName = vmdMorph.getMorphName();
            int morphIndex = faceMorphManager.findMorph(morphName);
            if(morphIndex == -1) {
                continue;
            }
            MorphFrameManager.MorphFrame morphFrame = new MorphFrameManager.MorphFrame();
            morphFrame.frame = vmdMorph.getFrame();
            morphFrame.weight = vmdMorph.getWeight();
            mikuAnimation.addMorphFrame(morphIndex, morphFrame);
        }
    }

    private void reconstructMaterials(List<Material> materials) {
        ByteBuffer indicesBuffer = allVertex.getIndices();
        ByteBuffer verticesBuffer = allVertex.getAllVertices();

        for(int m = 0; m < materials.size(); ++m) {
            Material material = materials.get(m);
            material.initBoneIndexBuffer(verticesBuffer.limit() / AllVertex.BYTE_SIZE_PER_VERTEX);
            ByteBuffer boneIndexBuffer = material.getBoneIndexBuffer();

            Log.i("mmd", "material: " +  m);

            indicesBuffer.position(material.getVertexIndexOffset() * AllVertex.BYTE_SIZE_PER_INDEX);

            HashMap<Short, Short> boneMap = new HashMap<>(); //<boneIndexOfAll, boneIndexOfMesh>
            short iterator = 0;

            for(int i = 0; i < material.getVertexIndicesNum(); i += 3) {
                //映射mesh中的骨骼和所有骨骼中的关系，并更新顶点中骨骼数据，这样做是为了以mesh为绘制单元，
                //减少每次传到shader里的骨骼数量， 三个顶点一组组成三角形

                for(int j = 0; j < 3; j++) {
                    int vertexIndex = indicesBuffer.getShort();
                    int boneInfoPos = vertexIndex  * AllVertex.BYTE_SIZE_PER_VERTEX
                            + AllVertex.BONE_INDEX_OFFSET ;
                    verticesBuffer.position(boneInfoPos);
                    short firstBoneIndexOfAll = verticesBuffer.getShort();
                    Short boneIndexOfMesh = boneMap.get(firstBoneIndexOfAll);
                    if(boneIndexOfMesh == null) {
                        boneMap.put(firstBoneIndexOfAll, iterator);
                        material.addBone(firstBoneIndexOfAll);
                        boneIndexOfMesh = iterator;
                        iterator++;
                    }
                    boneIndexBuffer.position(vertexIndex * 4);
                    boneIndexBuffer.putShort(boneIndexOfMesh);

                    short secondBoneIndexOfAll = verticesBuffer.getShort();
                    boneIndexOfMesh = boneMap.get(secondBoneIndexOfAll);
                    if(boneIndexOfMesh == null) {
                        boneMap.put(secondBoneIndexOfAll, iterator);
                        material.addBone(secondBoneIndexOfAll);
                        boneIndexOfMesh = iterator;
                        iterator++;
                    }

                    boneIndexBuffer.position(vertexIndex * 4 + 2);
                    boneIndexBuffer.putShort(boneIndexOfMesh);
                }
                if(material.getRelativeBoneSize() > Material.DESIRED_BONE_SIZE) {

                    //超过最大骨骼限制，创建一个新的material
                    Material newMaterial = material.clone();
                    newMaterial.setVertexIndexOffset(material.getVertexIndexOffset() + i + 3);
                    newMaterial.setVertexIndicesNum(material.getVertexIndicesNum() - i - 3);

                    materials.add(newMaterial);

                    material.setVertexIndicesNum(i + 3);
                    break;
                }

            }
            Log.i("mmd", "bone size: " + material.getBoneIndexMapping().size());
        }
    }
}

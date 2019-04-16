package com.fyd.miku.model.mmd;

import com.fyd.miku.model.pmd.AllVertex;
import com.fyd.miku.model.pmd.FaceMorph;
import com.fyd.miku.model.pmd.IKInfo;
import com.fyd.miku.model.pmd.Joint;
import com.fyd.miku.model.pmd.Material;
import com.fyd.miku.model.pmd.PMDFile;
import com.fyd.miku.model.pmd.RigidBody;
import com.fyd.miku.model.vmd.VMDFile;
import com.fyd.miku.model.vmd.VMDMotion;

import java.util.ArrayList;
import java.util.List;

public class MikuModel {
    private List<FaceMorph> faceMorph;
    private AllVertex allVertex;
    private List<IKInfo> ikInfos;
    private List<Joint> joints;
    private List<RigidBody> rigidBodies;
    private List<Mesh> meshes;
    private MikuBoneManager boneManager;
    private MikuAnimation mikuAnimation;

    public MikuModel(PMDFile pmdFile) {
        this.faceMorph = pmdFile.faceMorphs;
        this.allVertex = pmdFile.allVertex;
        this.ikInfos = pmdFile.ikInfos;
        this.joints = pmdFile.joints;
        this.rigidBodies = pmdFile.rigidBodies;
        boneManager = new MikuBoneManager(pmdFile.bones);
        initMeshes(pmdFile.materials);
    }

    public void attachMotion(VMDFile vmdFile) {
        mikuAnimation = new MikuAnimation(boneManager);
        for(VMDMotion vmdMotion : vmdFile.getMotions()) {
            String boneName = vmdMotion.getBoneName();
            BoneFrames.BoneFrame boneFrame = new BoneFrames.BoneFrame();
            int boneIndex = boneManager.findBone(boneName);
            if(boneIndex == -1) {
                continue;
            }
            boneFrame.frame = vmdMotion.getFrame();
            boneFrame.boneRotation = vmdMotion.getBoneQuaternion();
            boneFrame.boneTranslate = vmdMotion.getBoneTranslate();
            boneFrame.interpolation = vmdMotion.getInterpolation();
            mikuAnimation.addBoneFrame(boneIndex, boneFrame);
        }
        mikuAnimation.sortFrame();
        mikuAnimation.update();
    }

    public void updateMotion() {
        mikuAnimation.update();
    }

    public AllVertex getAllVertex() {
        return allVertex;
    }


    public List<Mesh> getMeshes() {
        return meshes;
    }

    public MikuBoneManager getBoneManager() {
        return boneManager;
    }



    public void updateAnimation() {

    }

    private void initMeshes(List<Material> materials) {
        meshes = new ArrayList<>();
        for(Material material : materials) {
            Mesh mesh = new Mesh();
            mesh.material = material;
            /*ByteBuffer indicesBuffer = allVertex.getIndices();
            ByteBuffer verticesBuffer = allVertex.getAllVertices();
            indicesBuffer.position(material.getVertexIndexOffset() * AllVertex.BYTE_SIZE_PER_INDEX);

            HashMap<Short, Short> addedBone = new HashMap<>(); //<boneIndexOfAll, boneIndexOfMesh>
            short iterator = 0;

            for(int i = material.getVertexIndexOffset(); i < material.getVertexIndicesNum(); i++) {
                int vertexIndex = indicesBuffer.getShort();
                int boneInfoPos = vertexIndex  * AllVertex.BYTE_SIZE_PER_VERTEX
                        + AllVertex.BONE_INDEX_OFFSET;
                verticesBuffer.position(boneInfoPos);

                //映射mesh中的骨骼和所有骨骼中的关系，并更新顶点中骨骼数据，这样做是为了以mesh为绘制单元，
                //减少每次传到shader里的骨骼数量
                short firstBoneIndexOfAll = verticesBuffer.getShort();
                Short boneIndexOfMesh = addedBone.get(firstBoneIndexOfAll);
                if(boneIndexOfMesh == null) {
                    addedBone.put(firstBoneIndexOfAll, iterator);
                    mesh.addBone(firstBoneIndexOfAll);
                    boneIndexOfMesh = iterator;
                    iterator++;
                }
                verticesBuffer.putShort(boneIndexOfMesh);

                short secondBoneIndexOfAll = verticesBuffer.getShort();
                boneIndexOfMesh = addedBone.get(secondBoneIndexOfAll);
                if(boneIndexOfMesh == null) {
                    addedBone.put(secondBoneIndexOfAll, iterator);
                    mesh.addBone(secondBoneIndexOfAll);
                    boneIndexOfMesh = iterator;
                    iterator++;
                }
                verticesBuffer.putShort(boneIndexOfMesh);
            }*/
            meshes.add(mesh);
        }
    }
}

package com.fyd.miku.model.pmd;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MikuModel {
    private List<Bone> bones;
    private List<FaceMorph> faceMorph;
    private AllVertex allVertex;
    private List<IKInfo> ikInfos;
    private List<Joint> joints;
    private List<RigidBody> rigidBodies;
    private List<Mesh> meshes;

    public MikuModel(PMDFile pmdFile) {
        this.bones = pmdFile.bones;
        this.faceMorph = pmdFile.faceMorphs;
        this.allVertex = pmdFile.allVertex;
        this.ikInfos = pmdFile.ikInfos;
        this.joints = pmdFile.joints;
        this.rigidBodies = pmdFile.rigidBodies;
        initMeshes(pmdFile.materials);
    }

    private void initMeshes(List<Material> materials) {
        meshes = new ArrayList<>();
        for(Material material : materials) {
            Mesh mesh = new Mesh();
            mesh.material = material;
            ByteBuffer indicesBuffer = allVertex.getIndices();
            ByteBuffer verticesBuffer = allVertex.getVertices();
            indicesBuffer.position(material.vertexIndexOffset * AllVertex.BYTE_SIZE_PER_INDEX);

            HashMap<Short, Short> addedBone = new HashMap<>(); //<boneIndexOfAll, boneIndexOfMesh>
            short iterator = 0;

            Log.i("mmd", "material: " + material.vertexIndexOffset);
            for(int i = material.vertexIndexOffset; i < material.vertexIndicesNum; i++) {
                int vertexIndex = indicesBuffer.getShort();
                Log.i("mmd", "verexIndices index: " + vertexIndex);
                int boneInfoPos = vertexIndex  * AllVertex.BYTE_SIZE_PER_VERTEX
                        + AllVertex.FIRST_BONE_INDEX_OFFSET;
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
            }
            meshes.add(mesh);
        }
    }
}

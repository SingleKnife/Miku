package com.fyd.miku.model.mmd;

import android.opengl.Matrix;

import com.fyd.miku.helper.MatrixHelper;
import com.fyd.miku.model.pmd.Bone;

import java.util.ArrayList;
import java.util.List;

public class MikuBoneManager {
    private static int INVALID_BONE_INDEX = 0xFFFF;

    private List<MikuBone> mikuBones;
    private float[] allMatrices;       //所有骨骼矩阵

    MikuBoneManager(List<Bone> bones) {
        this.mikuBones = new ArrayList<>(bones.size());
        allMatrices = new float[bones.size() * 16];
        for(int i = 0; i < bones.size(); ++i) {
            Bone bone = bones.get(i);
            MikuBone mikuBone = new MikuBone();
            mikuBone.name = bone.getBoneName();
            mikuBone.position = bone.getPosition();
            mikuBone.matrixIndex = i;
            mikuBone.parentIndex = bone.getParentBoneIndex();
            Matrix.setIdentityM(allMatrices, i * 16);
            mikuBones.add(mikuBone);
        }
    }

    /**
     * 更新骨骼动作矩阵
     * @param boneIndex     骨骼序号
     * @param rotation    旋转四元组
     * @param translate     位移矢量
     */
    public void setBoneMotion(int boneIndex, float[] rotation, float[] translate) {
        MikuBone bone = mikuBones.get(boneIndex);
        bone.rotation = rotation;
        bone.translate = translate;

        float[] localTranslate = new float[3];
        localTranslate[0] = bone.position[0] + translate[0];
        localTranslate[1] = bone.position[1] + translate[1];
        localTranslate[2] = bone.position[2] + translate[2];
        if(bone.parentIndex != INVALID_BONE_INDEX) {
            MikuBone parent = mikuBones.get(bone.parentIndex);
            localTranslate[0] -= parent.position[0];
            localTranslate[1] -= parent.position[1];
            localTranslate[2] -= parent.position[2];
        }
        MatrixHelper.fromRotationTranslationScale(bone.localTransform, 0, rotation, localTranslate, bone.scale);
    }


    public void updateAllBonesMotion() {
        for(MikuBone mikuBone : mikuBones) {
            updateBoneMotion(mikuBone);
        }

        for (int i = 0; i < mikuBones.size(); i++) {
            MikuBone bone = mikuBones.get(i);
            bone.isUpdated = false;
            Matrix.translateM(allMatrices, i * 16,
                    -bone.position[0], -bone.position[1], -bone.position[2]);
        }
    }

    private void updateBoneMotion(MikuBone mikuBone) {
        if(mikuBone.isUpdated) {
            return;
        }
        if(mikuBone.parentIndex != INVALID_BONE_INDEX) {
            MikuBone parentBone = mikuBones.get(mikuBone.parentIndex);
            updateBoneMotion(parentBone);
            Matrix.multiplyMM(allMatrices, mikuBone.matrixIndex * 16,
                    allMatrices, parentBone.matrixIndex * 16,
                    mikuBone.localTransform, 0
                    );
        } else {
            System.arraycopy(mikuBone.localTransform, 0, allMatrices, mikuBone.matrixIndex * 16, 16);
        }
        mikuBone.isUpdated = true;
    }

    /**
     * 根据名称找到骨骼的序号
     * @param boneName 骨骼名称
     * @return  骨骼序号
     */
    public int findBone(String boneName) {
        int result = -1;
        for(int i = 0; i < mikuBones.size(); ++i) {
            if(boneName.equals(mikuBones.get(i).name)){
                result = i;
                break;
            }
        }
        return result;
    }

    public float[] getAllMatrices() {
        return allMatrices;
    }
}

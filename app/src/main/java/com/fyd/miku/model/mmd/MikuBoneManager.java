package com.fyd.miku.model.mmd;

import android.opengl.Matrix;

import com.fyd.miku.helper.MatrixHelper;
import com.fyd.miku.model.pmd.Bone;

import java.util.ArrayList;
import java.util.List;

public class MikuBoneManager {
    private List<MikuBone> mikuBones;
    private float[] allMatrices;       //所有骨骼矩阵

    MikuBoneManager(List<Bone> bones) {
        this.mikuBones = new ArrayList<>(bones.size());
        allMatrices = new float[bones.size() * 16];
        for(int i = 0; i < bones.size(); ++i) {
            Bone bone = bones.get(i);
            MikuBone mikuBone = new MikuBone();
            mikuBone.name = bone.getBoneName();
            mikuBone.matrixIndex = i;
            Matrix.setIdentityM(allMatrices, i * 16);
            mikuBones.add(mikuBone);
        }
    }

    /**
     * 更新骨骼矩阵
     * @param boneIndex     骨骼序号
     * @param quaternion    旋转四元组
     * @param translate     位移矢量
     */
    public void updateBoneMatrix(int boneIndex, float quaternion[], float[] translate) {
        MatrixHelper.setQuaternionM(allMatrices, boneIndex * 16, quaternion);
        Matrix.translateM(allMatrices, boneIndex * 16, translate[0], translate[1], translate[2]);
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

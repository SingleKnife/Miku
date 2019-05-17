package com.fyd.miku.model.mmd;

import android.opengl.Matrix;
import android.util.Log;

import com.fyd.miku.helper.MatrixHelper;
import com.fyd.miku.model.pmd.Bone;
import com.fyd.miku.model.pmd.IKInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MikuBoneManager {
    private static final String TAG = "MikuBoneManager";

    private static int INVALID_BONE_INDEX = 0xFFFF;

    private List<MikuBone> mikuBones;
    private List<IKInfo> ikInfos;
    private float[] allBoneMatrices;

    MikuBoneManager(List<Bone> bones, List<IKInfo> ikInfos) {
        this.mikuBones = new ArrayList<>(bones.size());
        this.ikInfos = ikInfos;
        allBoneMatrices = new float[bones.size() * 16];
        for(int i = 0; i < bones.size(); ++i) {
            Bone bone = bones.get(i);
            MikuBone mikuBone = new MikuBone();
            mikuBone.name = bone.getBoneName();
            mikuBone.position = bone.getPosition();
            mikuBone.matrixIndex = i;
            mikuBone.parentIndex = bone.getParentBoneIndex();
            mikuBone.isKnee = bone.isKnee();
            mikuBones.add(mikuBone);
            Matrix.setIdentityM(allBoneMatrices, i * 16);
        }

        for(MikuBone mikuBone : mikuBones) {
            initBoneLocalTransform(mikuBone);
        }

    }

    int getBoneNum() {
        return mikuBones.size();
    }

    public float[] getAllMatrices() {
        return allBoneMatrices;
    }


    /**
     * 更新骨骼动作矩阵
     * @param boneIndex     骨骼序号
     * @param rotation    旋转四元组
     * @param translate     位移矢量
     */
    void setBoneMotion(int boneIndex, float[] rotation, float[] translate) {
        MikuBone bone = mikuBones.get(boneIndex);
        float[] localTranslate = new float[3];
        localTranslate[0] = bone.position[0] + translate[0];
        localTranslate[1] = bone.position[1] + translate[1];
        localTranslate[2] = bone.position[2] + translate[2];
        if(bone.parentIndex != INVALID_BONE_INDEX) {
            //转换到父骨骼坐标系中
            MikuBone parent = mikuBones.get(bone.parentIndex);
            localTranslate[0] -= parent.position[0];
            localTranslate[1] -= parent.position[1];
            localTranslate[2] -= parent.position[2];
        }
        MatrixHelper.fromRotationTranslationScale(bone.localTransform, 0, rotation, localTranslate, bone.scale);
    }


    void updateAllBonesMotion() {
        for(MikuBone mikuBone : mikuBones) {
            calculateBoneGlobalMatrix(mikuBone);
        }

        updateIk();

        for (int i = 0; i < mikuBones.size(); i++) {
            MikuBone bone = mikuBones.get(i);
            bone.isUpdated = false;
            //计算出来的是在骨骼坐标系中的顶点变换，而顶点坐标是在世界坐标系中的，需要先转换到骨骼坐标系
            Matrix.translateM(bone.globalTransform, 0,
                    -bone.position[0], -bone.position[1], -bone.position[2]);
            System.arraycopy(bone.globalTransform, 0, allBoneMatrices, i * 16, 16);
        }
    }

    /**
     * 计算骨骼在世界坐标系中的变换
     */
    private void calculateBoneGlobalMatrix(MikuBone mikuBone) {
        if(mikuBone.isUpdated) {
            return;
        }
        if(mikuBone.parentIndex != INVALID_BONE_INDEX) {
            MikuBone parentBone = mikuBones.get(mikuBone.parentIndex);
            calculateBoneGlobalMatrix(parentBone);
            Matrix.multiplyMM(mikuBone.globalTransform, 0,
                    parentBone.globalTransform, 0,
                    mikuBone.localTransform, 0
                    );
        } else {
            System.arraycopy(mikuBone.localTransform, 0, mikuBone.globalTransform, 0, 16);
        }
        mikuBone.isUpdated = true;
    }

    private void updateIk() {
        float[] effectorPos = new float[4];
        float[] targetPos = new float[4];

        float[] localEffectorPos = new float[4];
        float[] localTargetPos = new float[4];

        float[] localEffectorDir = new float[3];
        float[] localTargetDir = new float[3];

        float[] axis = new float[3];

        float[] invCoord = new float[16];
        float[] legPos = new float[3];

        for(IKInfo ikInfo : ikInfos) {
            MikuBone ikBone = mikuBones.get(ikInfo.ikBoneIndex);
            MikuBone effectorBone = mikuBones.get(ikInfo.effectorBoneIndex);
            calculateBoneGlobalMatrix(ikBone);
            targetPos[0] = ikBone.globalTransform[12];
            targetPos[1] = ikBone.globalTransform[13];
            targetPos[2] = ikBone.globalTransform[14];
            targetPos[3] = 1;
            for(int i = 0; i < ikInfo.iterationNum; ++i) {
                for(int boneIndex : ikInfo.boneList) {
                    MikuBone linkBone = mikuBones.get(boneIndex);
                    calculateBoneGlobalMatrix(effectorBone);
                    effectorPos[0] = effectorBone.globalTransform[12];
                    effectorPos[1] = effectorBone.globalTransform[13];
                    effectorPos[2] = effectorBone.globalTransform[14];
                    effectorPos[3] = 1;

                    calculateBoneGlobalMatrix(linkBone);
                    if(linkBone.isKnee) {
                        //如果是膝盖的话，大腿位置，膝盖位置，和目标点位置三个点确定，
                        //直接根据勾股定理计算出大腿和小腿之间的夹角
                        if(i == 0) {
                            MikuBone legBone = mikuBones.get(linkBone.parentIndex);
                            calculateBoneGlobalMatrix(legBone);
                            legPos[0] = legBone.globalTransform[12];
                            legPos[1] = legBone.globalTransform[13];
                            legPos[2] = legBone.globalTransform[14];

                            float shankLength = MatrixHelper.length(linkBone.position, effectorBone.position);  //小腿长度
                            float thighLength = MatrixHelper.length(legBone.position, linkBone.position);       //大腿长度
                            float targetLength = MatrixHelper.length(legPos, targetPos);                        //大腿骨骼到目标的距离

                            //小腿和大腿之间夹角
                            double inclination = Math.acos((shankLength * shankLength + thighLength * thighLength - targetLength * targetLength) / (2 * shankLength * thighLength));
                            if(!Double.isNaN(inclination)) {
                                Matrix.rotateM(linkBone.localTransform, 0, (float)Math.toDegrees(Math.PI - inclination), -1, 0, 0);
                            }
                        }
                        continue;
                    }
                    Matrix.invertM(invCoord, 0, linkBone.globalTransform, 0);

                    Matrix.multiplyMV(localEffectorPos, 0, invCoord, 0, effectorPos, 0);
                    Matrix.multiplyMV(localTargetPos, 0, invCoord, 0, targetPos, 0);

                    MatrixHelper.normaizeVec3(localEffectorDir, localEffectorPos);
                    MatrixHelper.normaizeVec3(localTargetDir, localTargetPos);

                    float p = MatrixHelper.dotVec3(localEffectorDir, localTargetDir);
                    if(p > 1 - 1.0e-6f) continue;

                    float angle = (float) Math.acos(p);
                    angle *= ikInfo.rotateLimit;
                    angle = (float) Math.toDegrees(angle);

                    MatrixHelper.crossVec3(axis, localEffectorDir, localTargetDir);
                    Matrix.rotateM(linkBone.localTransform, 0, angle, axis[0], axis[1], axis[2]);

                    resetBoneUpdateStatus(effectorBone);
                    resetBoneUpdateStatus(linkBone);
                }
                if(MatrixHelper.length(effectorPos, targetPos) < 0.001f) {
                    break;
                }
            }
            resetBoneUpdateStatus(ikBone);
        }
    }

    private void resetBoneUpdateStatus(MikuBone mikuBone) {
        mikuBone.isUpdated = false;
        while (mikuBone.parentIndex != INVALID_BONE_INDEX) {
            MikuBone parentBone = mikuBones.get(mikuBone.parentIndex);
            parentBone.isUpdated = false;
            mikuBone = parentBone;
        }
    }

    private void initBoneLocalTransform(MikuBone mikuBone) {
        if(mikuBone.parentIndex != INVALID_BONE_INDEX) {
            MikuBone parentBone = mikuBones.get(mikuBone.parentIndex);
            Matrix.translateM(mikuBone.localTransform, 0,
                    mikuBone.position[0] - parentBone.position[0],
                    mikuBone.position[1] - parentBone.position[1],
                    mikuBone.position[2] - parentBone.position[2]);
        } else {
            Matrix.translateM(mikuBone.localTransform, 0,
                    mikuBone.position[0], mikuBone.position[1], mikuBone.position[2]);
        }
    }

    /**
     * 根据名称找到骨骼的序号
     * @param boneName 骨骼名称
     * @return  骨骼序号
     */
    public int findBone(String boneName) {
        for(int i = 0; i < mikuBones.size(); ++i) {
            if(boneName.equals(mikuBones.get(i).name)){
                return i;
            }
        }
        return -1;
    }
}

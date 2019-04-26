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
    private float[] allMatrices;       //所有骨骼矩阵

    MikuBoneManager(List<Bone> bones, List<IKInfo> ikInfos) {
        this.mikuBones = new ArrayList<>(bones.size());
        allMatrices = new float[bones.size() * 16];
        this.ikInfos = ikInfos;
        for(int i = 0; i < bones.size(); ++i) {
            Bone bone = bones.get(i);
            MikuBone mikuBone = new MikuBone();
            mikuBone.name = bone.getBoneName();
            mikuBone.position = bone.getPosition();
            mikuBone.matrixIndex = i;
            mikuBone.parentIndex = bone.getParentBoneIndex();
            mikuBone.isKnee = bone.isKnee();
            Matrix.setIdentityM(allMatrices, i * 16);
            mikuBones.add(mikuBone);
        }

        for(MikuBone mikuBone : mikuBones) {
            initBoneLocalTransform(mikuBone);
        }
    }

    int getBoneNum() {
        return mikuBones.size();
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
            MikuBone parent = mikuBones.get(bone.parentIndex);
            localTranslate[0] -= parent.position[0];
            localTranslate[1] -= parent.position[1];
            localTranslate[2] -= parent.position[2];
        }
        MatrixHelper.fromRotationTranslationScale(bone.localTransform, 0, rotation, localTranslate, bone.scale);
    }


    void updateAllBonesMotion() {
        Log.i(TAG, "updateAllBonesMotion");

        for(MikuBone mikuBone : mikuBones) {
            calculateBoneGlobalMatrix(mikuBone);
        }

        updateIk();

        for (int i = 0; i < mikuBones.size(); i++) {
            MikuBone bone = mikuBones.get(i);
            bone.isUpdated = false;
            Matrix.translateM(allMatrices, i * 16,
                    -bone.position[0], -bone.position[1], -bone.position[2]);
        }
    }

    private void calculateBoneGlobalMatrix(MikuBone mikuBone) {
        if(mikuBone.isUpdated) {
            return;
        }
        if(mikuBone.parentIndex != INVALID_BONE_INDEX) {
            MikuBone parentBone = mikuBones.get(mikuBone.parentIndex);
            calculateBoneGlobalMatrix(parentBone);
            Matrix.multiplyMM(allMatrices, mikuBone.matrixIndex * 16,
                    allMatrices, parentBone.matrixIndex * 16,
                    mikuBone.localTransform, 0
                    );
        } else {
            System.arraycopy(mikuBone.localTransform, 0, allMatrices, mikuBone.matrixIndex * 16, 16);
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
        float[] tempMatrix = new float[16];

        for(IKInfo ikInfo : ikInfos) {
            if(ikInfo.ikBoneIndex != 89) {
                continue;
            }
            MikuBone ikBone = mikuBones.get(ikInfo.ikBoneIndex);
            MikuBone targetBone = mikuBones.get(ikInfo.targetBoneIndex);
            calculateBoneGlobalMatrix(ikBone);
            targetPos[0] = allMatrices[ikBone.matrixIndex * 16 + 12];
            targetPos[1] = allMatrices[ikBone.matrixIndex * 16 + 13];
            targetPos[2] = allMatrices[ikBone.matrixIndex * 16 + 14];
            targetPos[3] = 1;
            Log.i(TAG, "targetPos: " + ikInfo.targetBoneIndex + ": " + targetPos[0] + ", " + targetPos[1] + ", " + targetPos[2]);
            for(int i = 0; i < ikInfo.iterationNum; ++i) {
                for(int boneIndex : ikInfo.boneList) {
                    MikuBone linkBone = mikuBones.get(boneIndex);
                    calculateBoneGlobalMatrix(targetBone);
                    effectorPos[0] = allMatrices[targetBone.matrixIndex * 16 + 12];
                    effectorPos[1] = allMatrices[targetBone.matrixIndex * 16 + 13];
                    effectorPos[2] = allMatrices[targetBone.matrixIndex * 16 + 14];
                    effectorPos[3] = 1;

                    calculateBoneGlobalMatrix(linkBone);
                    Matrix.invertM(invCoord, 0, allMatrices, linkBone.matrixIndex * 16);

                    Matrix.multiplyMV(localEffectorPos, 0, invCoord, 0, effectorPos, 0);
                    Matrix.multiplyMV(localTargetPos, 0, invCoord, 0, targetPos, 0);
                    Log.i(TAG, "localEffectorPos: " + linkBone.matrixIndex + ": " + localEffectorPos[0] + ", " + localEffectorPos[1] + ", " + localEffectorPos[2]);
                    Log.i(TAG, "localTargetPos: " + linkBone.matrixIndex + ": " + localTargetPos[0] + ", " + localTargetPos[1] + ", " + localTargetPos[2]);

                    MatrixHelper.normaizeVec3(localEffectorDir, localEffectorPos);
                    MatrixHelper.normaizeVec3(localTargetDir, localTargetPos);

                    float p = MatrixHelper.dotVec3(localEffectorDir, localTargetDir);
                    Log.i(TAG, "p: " + p);
                    if(p > 1 - 1.0e-6f) continue;

                    float angle = (float) Math.acos(p);
                    angle *= ikInfo.rotateLimit;
                    angle = (float) Math.toDegrees(angle);
                    Log.i(TAG, "updateIk: angle: " + angle);

                    MatrixHelper.crossVec3(axis, localEffectorDir, localTargetDir);

                    if(linkBone.isKnee) {
                        Matrix.rotateM(tempMatrix, 0, linkBone.localTransform, 0, angle, axis[0], axis[1], axis[2]);
                        float[] desiredRotation = new float[4];
                        MatrixHelper.matrixToQuaternion(desiredRotation, tempMatrix);
                        float[] desiredEuler = new float[3];
                        MatrixHelper.quaternionToEulerRadius(desiredEuler, desiredRotation);
                        Log.i(TAG, "euler: " + Arrays.toString(desiredEuler));
                        float minDegree = 0f;
                        float maxDegree = (float) Math.PI;

                        desiredEuler[0] = (float) (Math.max(minDegree, Math.min(maxDegree, desiredEuler[0])) / Math.PI * 180);
                        desiredEuler[1] = 0;
                        desiredEuler[2] = 0;
                        float translateX = linkBone.localTransform[12];
                        float translateY = linkBone.localTransform[13];
                        float translateZ = linkBone.localTransform[14];
                        Matrix.setRotateEulerM(linkBone.localTransform, 0, desiredEuler[0], desiredEuler[1], desiredEuler[2]);
                        linkBone.localTransform[12] = translateX;
                        linkBone.localTransform[13] = translateY;
                        linkBone.localTransform[14] = translateZ;

                    } else {
                        Matrix.rotateM(linkBone.localTransform, 0, angle, axis[0], axis[1], axis[2]);
                    }

                    resetBoneUpdateStatus(targetBone);
                    resetBoneUpdateStatus(linkBone);
                }
                if(MatrixHelper.length(effectorPos, targetPos) < 0.001f) {
                    Log.i(TAG, "updateIk: reach target pos");
                    break;
                }
            }
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

    public void printBoneMatrix(int boneIndex) {
        float[] matrix = new float[16];
        System.arraycopy(allMatrices, boneIndex * 16, matrix, 0, 16);
        Log.i(TAG, "bone " + boneIndex + ": " + Arrays.toString(matrix));
    }
}

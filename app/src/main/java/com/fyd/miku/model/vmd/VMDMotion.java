package com.fyd.miku.model.vmd;

import java.util.Arrays;

public class VMDMotion {
    String boneName;
    int frame;

    float[] boneTranslate;      //骨骼位移
    float[] boneQuaternion;     //骨骼旋转四元组
    byte[] interpolation;       //动画插值数据

    public VMDMotion() {
        boneTranslate = new float[3];
        boneQuaternion = new float[4];
        interpolation = new byte[64];
    }

    public String getBoneName() {
        return boneName;
    }

    public int getFrame() {
        return frame;
    }

    public float[] getBoneTranslate() {
        return boneTranslate;
    }

    public float[] getBoneQuaternion() {
        return boneQuaternion;
    }

    public byte[] getInterpolation() {
        return interpolation;
    }

    @Override
    public String toString() {
        return "VMDMotion{" +
                "boneName='" + boneName + '\'' +
                ", frame=" + frame +
                ", boneTranslate=" + Arrays.toString(boneTranslate) +
                ", boneQuaternion=" + Arrays.toString(boneQuaternion) +
                '}';
    }
}

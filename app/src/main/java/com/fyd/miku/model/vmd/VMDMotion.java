package com.fyd.miku.model.vmd;

public class VMDMotion {
    String boneName;
    int frameIndex;

    float[] boneTranslate;      //骨骼位移
    float[] boneQuaternion;     //骨骼旋转四元组
    byte[] interpolation;       //动画插值数据

    public VMDMotion() {
        boneTranslate = new float[3];
        boneQuaternion = new float[4];
        interpolation = new byte[64];
    }
}

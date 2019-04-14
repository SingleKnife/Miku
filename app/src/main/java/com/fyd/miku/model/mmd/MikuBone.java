package com.fyd.miku.model.mmd;

import android.opengl.Matrix;

public class MikuBone {
    String name;
    int matrixIndex;        //

    float[] boneTranslate;      //骨骼位移
    float[] boneQuaternion;     //骨骼旋转四元组
    byte[] interpolation;       //动画插值数据

    MikuBone() {
    }
}

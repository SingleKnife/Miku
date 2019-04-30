package com.fyd.miku.model.mmd;

import android.opengl.Matrix;

public class MikuBone {
    String name;
    int matrixIndex;        //
    int parentIndex;
    boolean isKnee;         //是否是膝盖

    float[] position;
    float[] scale;          //骨骼缩放

    float[] localTransform; //骨骼相对于父骨骼变换
    float[] globalTransform;//骨骼相对于相对于世界坐标系的变换

    boolean isUpdated = false;

    MikuBone() {
        localTransform = new float[16];
        globalTransform = new float[16];
        scale = new float[] {1.0f, 1.0f, 1.0f};
        Matrix.setIdentityM(localTransform, 0);
        Matrix.setIdentityM(globalTransform, 0);
    }
}

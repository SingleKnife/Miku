package com.fyd.miku.model.mmd;

import android.opengl.Matrix;

public class MikuBone {
    String name;
    int matrixIndex;        //
    int parentIndex;

    float[] position;
    float[] translate;      //骨骼位移
    float[] rotation;       //骨骼旋转四元组
    float[] scale;          //骨骼缩放

    float[] localTransform; //骨骼以本身为参考系的变换

    boolean isUpdated = false;

    MikuBone() {
        localTransform = new float[16];
        scale = new float[] {1.0f, 1.0f, 1.0f};
        Matrix.setIdentityM(localTransform, 0);
    }
}

package com.fyd.miku.model.pmd;

import android.util.SparseArray;
import android.util.SparseIntArray;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * opengl中绘制模型的基本单元，基于Material
 */
public class Mesh {
    Material material;
    List<Short> boneIndexMapping;
    Set<Integer> relevantFaceMorgh;     //当前绘制相关的面部动画

    public Mesh() {
        boneIndexMapping = new ArrayList<>();
    }

    public void addBone(short boneIndex) {
        boneIndexMapping.add(boneIndex);
    }
}

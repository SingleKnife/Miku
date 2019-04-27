package com.fyd.miku.model.mmd;

import com.fyd.miku.model.pmd.Material;

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

    public int getIndexCount() {
        return material.getVertexIndicesNum();
    }

    public int getIndexOffset() {
        return material.getVertexIndexOffset();
    }

    public Material getMaterial() {
        return material;
    }

    public List<Short> getBoneIndexMapping() {
        return boneIndexMapping;
    }
}

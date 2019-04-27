package com.fyd.miku.model.pmd;

import java.util.ArrayList;
import java.util.List;

public class IKInfo {
    public int ikBoneIndex;      //
    public int effectorBoneIndex;
    public int boneNum;           //当前ik链中的骨骼数量
    public int iterationNum;     //到达target bone的迭代次数
    public float rotateLimit;      //旋转最大度数
    public List<Integer> boneList;   //当前ik链中的骨骼

    IKInfo() {
        boneList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "IKInfo{" +
                "ikBoneIndex=" + ikBoneIndex +
                ", effectorBoneIndex=" + effectorBoneIndex +
                ", boneNum=" + boneNum +
                ", iterationNum=" + iterationNum +
                ", rotateLimit=" + rotateLimit +
                ", boneList=" + boneList +
                '}';
    }
}

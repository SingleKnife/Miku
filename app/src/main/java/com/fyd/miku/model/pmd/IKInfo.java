package com.fyd.miku.model.pmd;

import java.util.ArrayList;
import java.util.List;

public class IKInfo {
    short ikBoneIndex;      //
    short targetBoneIndex;
    int boneNum;           //当前ik链中的骨骼数量
    short iterationNum;     //到达target bone的迭代次数
    float rotateLimit;      //旋转最大度数
    List<Short> boneList;   //当前ik链中的骨骼

    IKInfo() {
        boneList = new ArrayList<>();
    }
}

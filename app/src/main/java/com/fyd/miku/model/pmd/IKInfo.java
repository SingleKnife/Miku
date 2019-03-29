package com.fyd.miku.model.pmd;

import java.util.ArrayList;
import java.util.List;

public class IKInfo {
    int ikBoneIndex;      //
    int targetBoneIndex;
    int boneNum;           //当前ik链中的骨骼数量
    int iterationNum;     //到达target bone的迭代次数
    float rotateLimit;      //旋转最大度数
    List<Integer> boneList;   //当前ik链中的骨骼

    IKInfo() {
        boneList = new ArrayList<>();
    }
}

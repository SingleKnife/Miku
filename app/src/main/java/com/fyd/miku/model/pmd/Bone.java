package com.fyd.miku.model.pmd;

public class Bone {
    String boneName;
    String boneNameEnglish;
    short parentBoneIndex;
    short childBoneIndex;
    byte boneType;       //4表示ik bone，一般是腿和脚，9表示旋转骨骼，
    short ikParent;       //如果是ik bone的话，是驱动这个bone的bone序号
    float[] position;     //当前bone位置

    public Bone() {
        position = new float[3];
    }
}

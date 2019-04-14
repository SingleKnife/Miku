package com.fyd.miku.model.pmd;

import java.util.Arrays;

public class Bone {
    String boneName;
    String boneNameEnglish;
    int parentBoneIndex;
    int childBoneIndex;
    int boneType;       //4表示ik bone，一般是腿和脚，9表示旋转骨骼，
    int ikParent;       //如果是ik bone的话，是驱动这个bone的bone序号
    float[] position;     //当前bone位置

    public Bone() {
        position = new float[3];
    }

    public String getBoneName() {
        return boneName;
    }

    @Override
    public String toString() {
        return "Bone{" +
                "boneName='" + boneName + '\'' +
                ", boneNameEnglish='" + boneNameEnglish + '\'' +
                ", parentBoneIndex=" + parentBoneIndex +
                ", childBoneIndex=" + childBoneIndex +
                ", boneType=" + boneType +
                ", ikParent=" + ikParent +
                ", position=" + Arrays.toString(position) +
                '}';
    }
}

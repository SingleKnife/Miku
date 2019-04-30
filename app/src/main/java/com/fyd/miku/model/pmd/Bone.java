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
    boolean isKnee;     //是否是膝盖

    public Bone() {
        position = new float[3];
    }

    public String getBoneName() {
        return boneName;
    }

    public float[] getPosition() {
        return position;
    }

    public int getParentBoneIndex() {
        return parentBoneIndex;
    }

    public boolean isKnee() {
        return isKnee;
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
                ", isKnee=" + isKnee +
                ", maxOffset=" + Arrays.toString(position) +
                '}';
    }
}

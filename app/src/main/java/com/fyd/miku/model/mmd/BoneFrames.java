package com.fyd.miku.model.mmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BoneFrames {
    List<BoneFrame> boneFrames;
    int currentFrameIndex;

    public BoneFrames() {
        boneFrames = new ArrayList<>();
    }

    void addBoneFrame(BoneFrame boneFrame) {
        boneFrames.add(boneFrame);
    }

    void sort() {
        Collections.sort(boneFrames, new Comparator<BoneFrame>() {
            @Override
            public int compare(BoneFrame frame1, BoneFrame frame2) {
                return frame1.frame - frame2.frame;
            }
        });
    }

    /**
     * 获取该帧对应的位移
     * @param frame
     * @return
     */
    BoneFrame getBoneFrame(int frame) {
        if(currentFrameIndex < (boneFrames.size() - 1)) {
            int currentFrame = boneFrames.get(currentFrameIndex).frame;
            if(frame > currentFrame) {
                currentFrameIndex++;
            }
        }
        return boneFrames.get(currentFrameIndex);
    }


    public static class BoneFrame {
        int frame;
        float[] boneTranslate;      //骨骼位移
        float[] boneQuaternion;     //骨骼旋转四元组
        byte[] interpolation;       //动画插值数据

        public BoneFrame() {
            boneTranslate = new float[3];
            boneQuaternion = new float[4];
            interpolation = new byte[64];
        }
    }
}

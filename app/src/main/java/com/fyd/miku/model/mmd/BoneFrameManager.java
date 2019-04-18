package com.fyd.miku.model.mmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BoneFrameManager {
    //动画对应的关键帧
    private List<BoneFrame> keyFrames;
    private int currentKeyFrameIndex = 0;
    private int nextKeyFrameIndex = 1;

    BoneFrameManager() {
        keyFrames = new ArrayList<>();
    }

    void addBoneFrame(BoneFrame boneFrame) {
        keyFrames.add(boneFrame);
    }

    void sort() {
        Collections.sort(keyFrames, new Comparator<BoneFrame>() {
            @Override
            public int compare(BoneFrame frame1, BoneFrame frame2) {
                return frame1.frame - frame2.frame;
            }
        });
    }

    /**
     * 获取该帧对应的动画
     * @param frame 帧数
     */
    BoneFrame getBoneFrame(int frame) {
        if(nextKeyFrameIndex < keyFrames.size()) {
            int nextKeyFrame = keyFrames.get(nextKeyFrameIndex).frame;
            if(frame > nextKeyFrame) {
                currentKeyFrameIndex = Math.min(currentKeyFrameIndex + 1, keyFrames.size() - 1);
                nextKeyFrameIndex = Math.min(nextKeyFrameIndex + 1, keyFrames.size() - 1);
            }
        }

        return keyFrames.get(currentKeyFrameIndex);
    }


    static class BoneFrame {
        int frame;
        float[] boneTranslate;      //骨骼位移  x, y, z
        float[] boneRotation;     //骨骼旋转四元组 i, j, k, w
        BezierParameters interpolation;//动画插值数据

        BoneFrame() {
        }
    }

    static class BezierParameters {
        //x轴插值
        int[] x1;  //x1.x, x1.y
        int[] x2;

        //y轴插值
        int[] y1;
        int[] y2;

        //z轴插值
        int[] z1;
        int[] z2;

        //旋转插值
        int[] r1;
        int[] r2;

        public BezierParameters() {
            x1 = new int[2];
            x2 = new int[2];

            y1 = new int[2];
            y2 = new int[2];

            z1 = new int[2];
            z2 = new int[2];

            r1 = new int[2];
            r2 = new int[2];
        }

        public void parse(byte[] interpolation) {
            x1[0] = interpolation[0];
            y1[0] = interpolation[1];
            z1[0] = interpolation[2];
            r1[0] = interpolation[3];

            x1[1] = interpolation[4];
            y1[1] = interpolation[5];
            z1[1] = interpolation[6];
            r1[1] = interpolation[7];

            x2[0] = interpolation[8];
            y2[0] = interpolation[9];
            z2[0] = interpolation[10];
            r2[0] = interpolation[11];

            x2[1] = interpolation[12];
            y2[1] = interpolation[13];
            z2[1] = interpolation[14];
            r2[1] = interpolation[15];
        }
    }
}

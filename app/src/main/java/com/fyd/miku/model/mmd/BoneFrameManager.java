package com.fyd.miku.model.mmd;

import com.fyd.miku.helper.InterpolatorHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BoneFrameManager {
    private static final String TAG = "BoneFrameManager";

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
    BoneFrame getBoneFrame(float frame) {
        currentKeyFrameIndex = Math.min(currentKeyFrameIndex, keyFrames.size() - 1);
        nextKeyFrameIndex = Math.min(nextKeyFrameIndex, keyFrames.size() - 1);

        if(nextKeyFrameIndex < keyFrames.size()) {
            int nextKeyFrame = keyFrames.get(nextKeyFrameIndex).frame;
            if(frame > nextKeyFrame) {
                currentKeyFrameIndex = Math.min(currentKeyFrameIndex + 1, keyFrames.size() - 1);
                nextKeyFrameIndex = Math.min(nextKeyFrameIndex + 1, keyFrames.size() - 1);
            }
        }
        if(currentKeyFrameIndex == nextKeyFrameIndex) {
            return keyFrames.get(currentKeyFrameIndex);
        } else {
            return interpolateFrame(frame);
        }

    }

    private BoneFrame interpolateFrame(float frame) {
        BoneFrame currentKeyFrame = keyFrames.get(currentKeyFrameIndex);
        BoneFrame nextKeyFrame = keyFrames.get(nextKeyFrameIndex);
        BezierParameters bezier = currentKeyFrame.interpolation;

        BoneFrame boneFrame = new BoneFrame();
        float t = (frame - currentKeyFrame.frame) / (nextKeyFrame.frame - currentKeyFrame.frame);
        float s;
        //calculate x interpolated value
        s = InterpolatorHelper.bezier(t, bezier.x1[0], bezier.x1[1], bezier.x2[0], bezier.x2[1]);
        boneFrame.boneTranslate[0] = currentKeyFrame.boneTranslate[0]
                + (nextKeyFrame.boneTranslate[0] - currentKeyFrame.boneTranslate[0]) * s;

        //calculate y interpolated value
        s = InterpolatorHelper.bezier(t, bezier.y1[0], bezier.y1[1], bezier.y2[0], bezier.y2[1]);
        boneFrame.boneTranslate[1] = currentKeyFrame.boneTranslate[1]
                + (nextKeyFrame.boneTranslate[1] - currentKeyFrame.boneTranslate[1]) * s;

        //calculate z interpolated value
        s = InterpolatorHelper.bezier(t, bezier.z1[0], bezier.z1[1], bezier.z2[0], bezier.z2[1]);
        boneFrame.boneTranslate[2] = currentKeyFrame.boneTranslate[2]
                + (nextKeyFrame.boneTranslate[2] - currentKeyFrame.boneTranslate[2]) * s;

        //calculate rotation interpolated value
        s = InterpolatorHelper.bezier(t, bezier.r1[0], bezier.r1[1], bezier.r2[0], bezier.r2[1]);
        InterpolatorHelper.slerp(boneFrame.boneRotation, currentKeyFrame.boneRotation, nextKeyFrame.boneRotation, s);

        return boneFrame;
    }


    static class BoneFrame {
        int frame;
        float[] boneTranslate;      //骨骼位移  x, y, z
        float[] boneRotation;     //骨骼旋转四元组 i, j, k, w
        BezierParameters interpolation;//动画插值数据

        BoneFrame() {
            boneTranslate = new float[3];
            boneRotation = new float[4];
        }
    }

    static class BezierParameters {
        //x轴插值
        float[] x1;  //x1.x, x1.y
        float[] x2;

        //y轴插值
        float[] y1;
        float[] y2;

        //z轴插值
        float[] z1;
        float[] z2;

        //旋转插值
        float[] r1;
        float[] r2;

         BezierParameters() {
            x1 = new float[2];
            x2 = new float[2];

            y1 = new float[2];
            y2 = new float[2];

            z1 = new float[2];
            z2 = new float[2];

            r1 = new float[2];
            r2 = new float[2];
        }

        static BezierParameters parse(byte[] interpolation) {
            BezierParameters parameters = new BezierParameters();
            parameters.x1[0] = interpolation[0] / 127.0f;
            parameters.y1[0] = interpolation[1] / 127.0f;
            parameters.z1[0] = interpolation[2] / 127.0f;
            parameters.r1[0] = interpolation[3] / 127.0f;

            parameters.x1[1] = interpolation[4] / 127.0f;
            parameters.y1[1] = interpolation[5] / 127.0f;
            parameters.z1[1] = interpolation[6] / 127.0f;
            parameters.r1[1] = interpolation[7] / 127.0f;

            parameters.x2[0] = interpolation[8] / 127.0f;
            parameters.y2[0] = interpolation[9] / 127.0f;
            parameters.z2[0] = interpolation[10] / 127.0f;
            parameters.r2[0] = interpolation[11] / 127.0f;

            parameters.x2[1] = interpolation[12] / 127.0f;
            parameters.y2[1] = interpolation[13] / 127.0f;
            parameters.z2[1] = interpolation[14] / 127.0f;
            parameters.r2[1] = interpolation[15] / 127.0f;

            return parameters;
        }
    }
}

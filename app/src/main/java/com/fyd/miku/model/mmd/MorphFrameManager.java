package com.fyd.miku.model.mmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MorphFrameManager {
    private List<MorphFrame> keyFrames;
    private int currentKeyFrameIndex = 0;
    private int nextKeyFrameIndex = 1;

    MorphFrameManager() {
        keyFrames = new ArrayList<>();
    }

    void addFrame(MorphFrame frame) {
        keyFrames.add(frame);
    }

    void sort() {
        Collections.sort(keyFrames, new Comparator<MorphFrame>() {
            @Override
            public int compare(MorphFrame frame1, MorphFrame frame2) {
                return frame1.frame - frame2.frame;
            }
        });
    }

    MorphFrame getFrame(float frame) {
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

    private MorphFrame interpolateFrame(float frame) {
        MorphFrame currentKeyFrame = keyFrames.get(currentKeyFrameIndex);
        MorphFrame nextKeyFrame = keyFrames.get(nextKeyFrameIndex);

        MorphFrame morphFrame = new MorphFrame();
        //线性插值
        float t = (frame - currentKeyFrame.frame) / (nextKeyFrame.frame - currentKeyFrame.frame);
        morphFrame.weight = currentKeyFrame.weight + (nextKeyFrame.weight - currentKeyFrame.weight) * t;

        return morphFrame;
    }


    public static class MorphFrame {
        int frame;
        float weight;
    }


}

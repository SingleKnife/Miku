package com.fyd.miku.model.mmd;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MikuAnimation {
    public static final int STATUS_PAUSE = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int MMD_FPS = 30;       //mmd动画插值fps

    Map<Integer, BoneFrameManager> allBonesFrames;  //<boneIndex, FramesOfBone>
    MikuBoneManager boneManager;

    int fps = 30;
    long startTime;
    long currentTime;
    long prevTime;
    int maxFrame;
    int currentFrame;

    int status = STATUS_PAUSE;

    public MikuAnimation(MikuBoneManager boneManager) {
        allBonesFrames = new HashMap<>();
        this.boneManager = boneManager;
    }

    public void addBoneFrame(int boneIndex, BoneFrameManager.BoneFrame boneFrame) {
        BoneFrameManager boneFrameManager = allBonesFrames.get(boneIndex);
        if(boneFrameManager == null) {
            boneFrameManager = new BoneFrameManager();
            allBonesFrames.put(boneIndex, boneFrameManager);
        }
        boneFrameManager.addBoneFrame(boneFrame);
    }

    public void sortFrame() {
        for (BoneFrameManager boneFrames : allBonesFrames.values()) {
            boneFrames.sort();
        }
    }

    public void update() {
        if(startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        prevTime = currentTime;
        currentTime = System.currentTimeMillis() - startTime;
        if(status == STATUS_PLAYING) {
            float currentFrame = getCurrentFrame();
            Log.i("anim", "currentFrame: " + currentFrame);
            setBoneMotion(getCurrentFrame());
        }
    }

    void setBoneMotion(float frame) {
        Log.i("anim", "setBoneMotion");
        for(int boneIndex = 0; boneIndex < boneManager.getBoneNum(); ++boneIndex) {
            float[] boneRotation = {0f, 0f, 0f, 1f};
            float[] boneTranslate = {0f, 0f, 0f};
            //获取骨骼对应的动画帧
            BoneFrameManager boneFrames = allBonesFrames.get(boneIndex);
            if(boneFrames != null) {
                BoneFrameManager.BoneFrame boneFrame = boneFrames.getBoneFrame(frame);
                boneRotation = boneFrame.boneRotation;
                boneTranslate = boneFrame.boneTranslate;
            }
            Log.i("anim", "setBoneMotion, " + boneIndex + ", rotation: " + Arrays.toString(boneRotation) + ", boneTranslate: " + Arrays.toString(boneTranslate));
            boneManager.setBoneMotion(boneIndex, boneRotation, boneTranslate);
        }
        boneManager.updateAllBonesMotion();
    }

    private float getCurrentFrame() {
        return currentTime * 30.0f / 1000.0f;
    }

    void startAnimation() {
        status = STATUS_PLAYING;
    }

    void pauseAnimation() {
        status = STATUS_PAUSE;
    }
}

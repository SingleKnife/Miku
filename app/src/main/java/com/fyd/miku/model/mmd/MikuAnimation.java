package com.fyd.miku.model.mmd;

import android.os.SystemClock;

import java.util.HashMap;
import java.util.Map;

public class MikuAnimation {
    public static final int STATUS_PAUSE = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int MMD_FPS = 30;       //mmd动画插值fps

    private Map<Integer, BoneFrameManager> allBonesFrames;  //<boneIndex, FramesOfBone>
    private Map<Integer, MorphFrameManager> allMorphFrames; //<morphIndex, FramesOfMorph>
    private MikuBoneManager boneManager;
    private MikuFaceMorphManager morphManager;
    private MikuPhysicsManager physicsManager;

    private long startTime;
    private long currentTime;
    private long prevTime;

    int status = STATUS_PAUSE;

    MikuAnimation(MikuBoneManager boneManager, MikuFaceMorphManager morphManager, MikuPhysicsManager physicsManager) {
        allBonesFrames = new HashMap<>();
        allMorphFrames = new HashMap<>();
        this.boneManager = boneManager;
        this.morphManager = morphManager;
        this.physicsManager = physicsManager;
    }

    void addBoneFrame(int boneIndex, BoneFrameManager.BoneFrame boneFrame) {
        BoneFrameManager boneFrameManager = allBonesFrames.get(boneIndex);
        if(boneFrameManager == null) {
            boneFrameManager = new BoneFrameManager();
            allBonesFrames.put(boneIndex, boneFrameManager);
        }
        boneFrameManager.addBoneFrame(boneFrame);
    }

    void addMorphFrame(int morphIndex, MorphFrameManager.MorphFrame morphFrame) {
        MorphFrameManager morphFrameManager = allMorphFrames.get(morphIndex);
        if(morphFrameManager == null) {
            morphFrameManager = new MorphFrameManager();
            allMorphFrames.put(morphIndex, morphFrameManager);
        }
        morphFrameManager.addFrame(morphFrame);
    }

    void sortFrame() {
        for (BoneFrameManager boneFrames : allBonesFrames.values()) {
            boneFrames.sort();
        }
        for(MorphFrameManager morphFrames : allMorphFrames.values()) {
            morphFrames.sort();
        }
    }

    void update() {
        if(status == STATUS_PLAYING) {
            float timeStep = 0;
            if(currentTime != 0 && prevTime != 0) {
                timeStep = currentTime - prevTime;
            }
            prevTime = currentTime;
            currentTime = SystemClock.uptimeMillis() - startTime;
            setMotion(getCurrentFrame());
            physicsManager.stepSimulation(timeStep);
        }
    }

    void setMotion(float frame) {
        setMorphMotion(frame);
        setBoneMotion(frame);
    }

    private void setBoneMotion(float frame) {
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
            boneManager.setBoneMotion(boneIndex, boneRotation, boneTranslate);
        }
        boneManager.updateAllBonesMotion();
    }

    private void setMorphMotion(float frame) {
        morphManager.resetMorphPos();
        for(int morphIndex = 0; morphIndex < morphManager.getMorphNum(); ++morphIndex) {
            float weight = 0f;
            MorphFrameManager morphFrames = allMorphFrames.get(morphIndex);
            if(morphFrames != null) {
                MorphFrameManager.MorphFrame morphFrame = morphFrames.getFrame(frame);
                weight = morphFrame.weight;
            }
            if (weight == 0.0) {
                continue;
            }
            morphManager.setMorphMotion(morphIndex, weight);
        }
        morphManager.updatePos();
    }

    private float getCurrentFrame() {
        return currentTime * MMD_FPS / 1000.0f;
    }

    void startAnimation() {
        status = STATUS_PLAYING;
        if(startTime == 0) {
            startTime = SystemClock.uptimeMillis();
            prevTime = SystemClock.uptimeMillis();
        }
    }

    void pauseAnimation() {
        status = STATUS_PAUSE;
    }
}

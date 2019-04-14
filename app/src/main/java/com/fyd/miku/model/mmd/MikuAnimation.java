package com.fyd.miku.model.mmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MikuAnimation {
    Map<Integer, BoneFrames> allBonesFrames;
    MikuBoneManager boneManager;

    int fps = 30;
    long startTime;
    long currentTime;
    int maxFrame;
    int currentFrame;

    public MikuAnimation(MikuBoneManager boneManager) {
        allBonesFrames = new HashMap<>();
        this.boneManager = boneManager;
    }

    public void addBoneFrame(int boneIndex, BoneFrames.BoneFrame boneFrame) {
        BoneFrames boneFrames = allBonesFrames.get(boneIndex);
        if(boneFrames == null) {
            boneFrames = new BoneFrames();
            allBonesFrames.put(boneIndex, boneFrames);
        }
        boneFrames.addBoneFrame(boneFrame);
    }

    public void sortFrame() {
        for (BoneFrames boneFrames : allBonesFrames.values()) {
            boneFrames.sort();
        }
    }

    public void update() {
        int currentFrame = getCurrentFrame();
        for(Map.Entry<Integer, BoneFrames> entry : allBonesFrames.entrySet()) {
            int boneIndex = entry.getKey();
            BoneFrames boneFrames = entry.getValue();
            BoneFrames.BoneFrame boneFrame = boneFrames.getBoneFrame(currentFrame);
            boneFrame.boneQuaternion[2] *= -1;
            boneFrame.boneTranslate[2] *= -1;
            boneManager.updateBoneMatrix(boneIndex, boneFrame.boneQuaternion, boneFrame.boneTranslate);
        }
    }

    public int getCurrentFrame() {
        return currentFrame++;
    }
}

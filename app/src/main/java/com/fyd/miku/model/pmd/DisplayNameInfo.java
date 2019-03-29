package com.fyd.miku.model.pmd;

import java.util.ArrayList;
import java.util.List;

/**
 * 在mmd软件中显示的相关信息
 */
public class DisplayNameInfo {
    //在面部操作中显示的动画
    List<Integer> faceMorphIndices;

    List<String> boneGroupNames;
    List<String> boenGroupNamesEnglish;

    List<BoneGroup> boneGroups;

    public DisplayNameInfo() {
        faceMorphIndices = new ArrayList<>();
        boneGroupNames = new ArrayList<>();
        boenGroupNamesEnglish = new ArrayList<>();
        boneGroups = new ArrayList<>();
    }

    static class BoneGroup {
        int boneIndex;
        int boneGroupIndex;
    }
}

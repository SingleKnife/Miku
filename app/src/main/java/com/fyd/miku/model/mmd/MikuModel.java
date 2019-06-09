package com.fyd.miku.model.mmd;

import com.fyd.miku.model.pmd.AllVertex;
import com.fyd.miku.model.pmd.IKInfo;
import com.fyd.miku.model.pmd.Material;
import com.fyd.miku.model.pmd.PMDFile;
import com.fyd.miku.model.vmd.VMDFile;
import com.fyd.miku.model.vmd.VMDMorph;
import com.fyd.miku.model.vmd.VMDMotion;

import java.util.ArrayList;
import java.util.List;

public class MikuModel {
    private AllVertex allVertex;
    private List<IKInfo> ikInfos;
    private List<Mesh> meshes;
    private MikuBoneManager boneManager;
    private MikuFaceMorphManager faceMorphManager;
    private MikuAnimation mikuAnimation;
    private MikuPhysicsManager physicisManager;

    public MikuModel(PMDFile pmdFile) {
        this.allVertex = pmdFile.allVertex;
        this.ikInfos = pmdFile.ikInfos;
        boneManager = new MikuBoneManager(pmdFile.bones, pmdFile.ikInfos);
        faceMorphManager = new MikuFaceMorphManager(pmdFile.faceMorphs, pmdFile.allVertex);
        physicisManager = new MikuPhysicsManager(boneManager, pmdFile.rigidBodies, pmdFile.joints);
        initMeshes(pmdFile.materials);
    }

    public void attachMotion(VMDFile vmdFile) {
        mikuAnimation = new MikuAnimation(boneManager, faceMorphManager, physicisManager);
        initBoneFrames(vmdFile.getMotions());
        initFaceMorphFrames(vmdFile.getMorphs());

        mikuAnimation.sortFrame();
        mikuAnimation.setMotion(0);
    }

    //call on GL thread
    public void updateMotion() {
        if(mikuAnimation != null) {
            mikuAnimation.update();
        }
    }

    public void startAnimation() {
        if(mikuAnimation != null) {
            mikuAnimation.startAnimation();
        }
    }

    public void setFrame(float frame) {
        if(mikuAnimation != null) {
            mikuAnimation.setMotion(frame);
        }
    }

    public AllVertex getAllVertex() {
        return allVertex;
    }


    public List<Mesh> getMeshes() {
        return meshes;
    }

    public MikuBoneManager getBoneManager() {
        return boneManager;
    }

    private void initMeshes(List<Material> materials) {
        meshes = new ArrayList<>();
        for(Material material : materials) {
            Mesh mesh = new Mesh();
            mesh.material = material;
            meshes.add(mesh);
        }
    }

    private void initBoneFrames(List<VMDMotion> vmdMotions) {
        for(VMDMotion vmdMotion : vmdMotions) {
            String boneName = vmdMotion.getBoneName();
            int boneIndex = boneManager.findBone(boneName);
            if(boneIndex == -1) {
                continue;
            }
            BoneFrameManager.BoneFrame boneFrame = new BoneFrameManager.BoneFrame();
            boneFrame.frame = vmdMotion.getFrame();
            boneFrame.boneRotation = vmdMotion.getBoneQuaternion();
            boneFrame.boneTranslate = vmdMotion.getBoneTranslate();
            boneFrame.interpolation = BoneFrameManager.BezierParameters.parse(vmdMotion.getInterpolation());
            mikuAnimation.addBoneFrame(boneIndex, boneFrame);
        }
    }

    private void initFaceMorphFrames(List<VMDMorph> vmdMorphs) {
        for(VMDMorph vmdMorph : vmdMorphs) {
            String morphName = vmdMorph.getMorphName();
            int morphIndex = faceMorphManager.findMorph(morphName);
            if(morphIndex == -1) {
                continue;
            }
            MorphFrameManager.MorphFrame morphFrame = new MorphFrameManager.MorphFrame();
            morphFrame.frame = vmdMorph.getFrame();
            morphFrame.weight = vmdMorph.getWeight();
            mikuAnimation.addMorphFrame(morphIndex, morphFrame);
        }
    }
}

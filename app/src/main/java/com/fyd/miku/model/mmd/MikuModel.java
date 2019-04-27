package com.fyd.miku.model.mmd;

import com.fyd.miku.model.pmd.AllVertex;
import com.fyd.miku.model.pmd.FaceMorph;
import com.fyd.miku.model.pmd.IKInfo;
import com.fyd.miku.model.pmd.Joint;
import com.fyd.miku.model.pmd.Material;
import com.fyd.miku.model.pmd.PMDFile;
import com.fyd.miku.model.pmd.RigidBody;
import com.fyd.miku.model.vmd.VMDFile;
import com.fyd.miku.model.vmd.VMDMotion;

import java.util.ArrayList;
import java.util.List;

public class MikuModel {
    private List<FaceMorph> faceMorph;
    private AllVertex allVertex;
    private List<IKInfo> ikInfos;
    private List<Joint> joints;
    private List<RigidBody> rigidBodies;
    private List<Mesh> meshes;
    private MikuBoneManager boneManager;
    private MikuAnimation mikuAnimation;

    public MikuModel(PMDFile pmdFile) {
        this.faceMorph = pmdFile.faceMorphs;
        this.allVertex = pmdFile.allVertex;
        this.ikInfos = pmdFile.ikInfos;
        this.joints = pmdFile.joints;
        this.rigidBodies = pmdFile.rigidBodies;
        boneManager = new MikuBoneManager(pmdFile.bones, pmdFile.ikInfos);
        initMeshes(pmdFile.materials);
    }

    public void attachMotion(VMDFile vmdFile) {
        mikuAnimation = new MikuAnimation(boneManager);
        for(VMDMotion vmdMotion : vmdFile.getMotions()) {
            String boneName = vmdMotion.getBoneName();
            BoneFrameManager.BoneFrame boneFrame = new BoneFrameManager.BoneFrame();
            int boneIndex = boneManager.findBone(boneName);
            if(boneIndex == -1) {
                continue;
            }
            boneFrame.frame = vmdMotion.getFrame();
            boneFrame.boneRotation = vmdMotion.getBoneQuaternion();
            boneFrame.boneTranslate = vmdMotion.getBoneTranslate();
            boneFrame.interpolation = BoneFrameManager.BezierParameters.parse(vmdMotion.getInterpolation());
            mikuAnimation.addBoneFrame(boneIndex, boneFrame);
        }
        mikuAnimation.sortFrame();
        mikuAnimation.setBoneMotion(0);
    }

    public void updateMotion() {
        if(mikuAnimation != null) {
            mikuAnimation.update();
        }
    }

    public void setFrame(float frame) {
        if(mikuAnimation != null) {
            mikuAnimation.setBoneMotion(frame);
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
}

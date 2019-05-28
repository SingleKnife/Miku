package com.fyd.miku.model.mmd;

import android.opengl.Matrix;
import android.util.Log;

import com.fyd.bullet.Physics;
import com.fyd.miku.model.pmd.Joint;
import com.fyd.miku.model.pmd.RigidBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MikuPhysicsManager {
    private List<RigidBody> rigidBodies;
    private List<Joint> joints;
    private MikuBoneManager boneManager;

    public MikuPhysicsManager(MikuBoneManager boneManager, List<RigidBody> rigidBodies, List<Joint> joints) {
        this.boneManager = boneManager;
        this.rigidBodies = new ArrayList<>();
        this.joints = new ArrayList<>();
        this.rigidBodies.addAll(rigidBodies);
        this.joints.addAll(joints);
        init();
    }

    private void init() {
        Physics.init();
        for(int i = 0; i < rigidBodies.size(); ++i) {
            RigidBody rigidBody = rigidBodies.get(i);
            MikuBone bone = boneManager.getBone(rigidBody.boneIndex);
            if(bone != null) {
                rigidBody.shapePos[0] += bone.position[0];
                rigidBody.shapePos[1] += bone.position[1];
                rigidBody.shapePos[2] += bone.position[2];
            }
            rigidBody.calcOriginTransform();

            Physics.addRigidBody(rigidBody.shape, rigidBody.mass, rigidBody.rigidBodyType,
                    new float[]{rigidBody.shapeWidth, rigidBody.shapeHeight, rigidBody.shapeDepth},
                    rigidBody.originTransform, rigidBody.linearDimmer, rigidBody.angularDamping,
                    rigidBody.rigidBodyRecoil, rigidBody.rigidBodyFriction, rigidBody.group, rigidBody.mask);
        }

        for(int i = 0; i < joints.size(); ++i) {
            Joint joint = joints.get(i);
            Physics.addJoint(joint.firstRigidBody, joint.secondRigidBody,
                    joint.jointRotation, joint.jointPos,
                    joint.posLowerLimit, joint.posUpperLimit,
                    joint.rotationLowerLimit, joint.rotationUpperLimit,
                    joint.posSpringStiffness, joint.rotationSpringStiffness
                    );
        }

    }

    public void updateRigidBodyTransform() {
        float[] temp = new float[16];
        for(int i = 0; i < rigidBodies.size(); ++i) {
            RigidBody rigidBody = rigidBodies.get(i);
            MikuBone bone = boneManager.getBone(rigidBody.boneIndex);
            if(bone != null) {
                Matrix.multiplyMM(temp, 0, bone.globalTransform, 0, rigidBody.originTransform, 0);
                Physics.setRigidBodyTransform(i, temp);
            }
        }
    }

    public void stepSimulation(float timeStep) {
        updateRigidBodyTransform();

        Physics.stepSimulation(timeStep);

        for(int i = 0; i < rigidBodies.size(); ++i) {
            RigidBody rigidBody = rigidBodies.get(i);
            if(rigidBody.rigidBodyType == 0) {
                continue;
            }
            int boneIndex = rigidBody.boneIndex;
            float[] temp = new float[16];
            Physics.getRigidBodyTransform(i, temp);
            Log.i("MikuPhysicsManager", "rigidBody: " + i + ", " + Arrays.toString(temp));
        }
    }

}

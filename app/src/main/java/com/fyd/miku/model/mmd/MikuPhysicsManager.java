package com.fyd.miku.model.mmd;

import com.fyd.bullet.Physics;
import com.fyd.miku.math.Quaternion;
import com.fyd.miku.model.pmd.Joint;
import com.fyd.miku.model.pmd.RigidBody;

import java.util.ArrayList;
import java.util.List;

public class MikuPhysicsManager {
    private List<RigidBody> rigidBodies;
    private List<Joint> joints;

    public MikuPhysicsManager(List<RigidBody> rigidBodies, List<Joint> joints) {
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
            Physics.addRigidBody(rigidBody.shape, rigidBody.mass, rigidBody.rigidBodyType,
                    new float[]{rigidBody.shapeWidth, rigidBody.shapeHeight, rigidBody.shapeDepth},
                    rigidBody.shapePos, rigidBody.shapeRotation, rigidBody.linearDimmer, rigidBody.angularDamping,
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

    public void stepSimulation(float timeStep) {
        Physics.stepSimulation(timeStep);
    }
}

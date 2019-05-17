//
// Created by admin on 2019/5/6.
//

#ifndef BULLETDEMO_MMDPHYSICS_H
#define BULLETDEMO_MMDPHYSICS_H

#include <BulletCollision/CollisionDispatch/btDefaultCollisionConfiguration.h>
#include <BulletDynamics/ConstraintSolver/btSequentialImpulseConstraintSolver.h>
#include <BulletCollision/BroadphaseCollision/btDbvtBroadphase.h>
#include <BulletDynamics/ConstraintSolver/btGeneric6DofConstraint.h>
#include "bullet/src/BulletDynamics/Dynamics/btDiscreteDynamicsWorld.h"
#include "BulletDebugDrawer.h"

class MMDPhysics {
public:
    MMDPhysics();
    ~MMDPhysics();

    void addRigidBody(int shape, float mass, int type, float halfExtents[], float position[], float rotation[],
                      float linearDamping, float angularDamping, float restitution, float friction,
                      int group, int mask);

    void addJoint(int rigidBodyAIndex, int rigidBodyBIndex, float rotation[], float position[],
            float linearLowerLimit[], float linearUpperLimit[],
            float angularLowerLimit[], float angularUpperLimit[],
            float posStiffness[], float rotationStiffness[]);

    void stepSimulation(float timeStep);

    void updateViewProjectMatrix(float vpMatrix[]);

    void getRigidBodyTransform(int index, float *transform);

    void destroy();

private:
    /**
     * 创建刚体通用方法
     * @param shape         刚体形状
     * @param mass          刚体质量
     * @param location      刚体坐标
     * @return
     */
    btRigidBody* createRigidBody(btCollisionShape *shape, int type, btScalar mass,
                                 btVector3& location, btQuaternion& rotation, float linearDamping,
                                 float angularDamping, float restitution, float friction);
    btCollisionShape* createShape(int shape, float halfExtents[]);

private:
    btDefaultCollisionConfiguration* collisionConfiguration;
    btCollisionDispatcher* dispatcher;
    btBroadphaseInterface* overlappingPairCache;
    btSequentialImpulseConstraintSolver* solver;
    btDiscreteDynamicsWorld* dynamicsWorld;

    btAlignedObjectArray<btCollisionShape*> collisionShapes;
    btAlignedObjectArray<btRigidBody*> dynamicRigidBodys;

    bool debugDraw = true;
    BulletDebugDrawer *debugDrawer = nullptr;
};


#endif //BULLETDEMO_MMDPHYSICS_H

//
// Created by admin on 2019/5/6.
//

#include <BulletCollision/CollisionShapes/btBoxShape.h>
#include <LinearMath/btDefaultMotionState.h>
#include <BulletCollision/CollisionShapes/btSphereShape.h>
#include <BulletCollision/CollisionShapes/btCapsuleShape.h>
#include <BulletDynamics/ConstraintSolver/btGeneric6DofSpringConstraint.h>
#include "MMDPhysics.h"



MMDPhysics::MMDPhysics() {
    collisionConfiguration = new btDefaultCollisionConfiguration();
    dispatcher = new btCollisionDispatcher(collisionConfiguration);
    overlappingPairCache = new btDbvtBroadphase();
    solver = new btSequentialImpulseConstraintSolver;
    dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
    dynamicsWorld->setGravity(btVector3(0, -20, 0));
}

MMDPhysics::~MMDPhysics() {
    destroy();
}

void MMDPhysics::addRigidBody(int shape, float mass, int type, float halfExtents[], float transform[],
                              float linearDamping, float angularDamping, float restitution, float friction, int group, int mask) {
    btCollisionShape *collisionShape = createShape(shape, halfExtents);
    collisionShapes.push_back(collisionShape);

    btTransform tran;
    tran.setFromOpenGLMatrix(transform);
    btRigidBody *rigidBody = createRigidBody(collisionShape, type, mass,tran,
            linearDamping, angularDamping, restitution, friction);
    dynamicRigidBodies.push_back(rigidBody);
    dynamicsWorld->addRigidBody(rigidBody, group, mask);
}

void MMDPhysics::addJoint(int rigidBodyAIndex, int rigidBodyBIndex, float *rotation, float *position,
                          float *linearLowerLimit, float *linearUpperLimit,
                          float *angularLowerLimit, float *angularUpperLimit,
                          float *posStiffness, float *rotationStiffness) {
    btRigidBody *rigidBodyA = dynamicRigidBodies[rigidBodyAIndex];
    btRigidBody *rigidBodyB = dynamicRigidBodies[rigidBodyBIndex];
    btTransform frameInA;
    frameInA.setIdentity();
    frameInA.setOrigin(btVector3(.0, .0, .0));

    btTransform frameInB;
    frameInB.setIdentity();
    frameInB.setOrigin(btVector3(.0, .0, .0));

    btGeneric6DofSpringConstraint *dof = new btGeneric6DofSpringConstraint(*rigidBodyA, *rigidBodyB,
                                                                        frameInA, frameInB, false);
    dof->enableSpring(0, true);
    dof->setEquilibriumPoint(0, 0);
    dof->setLinearLowerLimit(btVector3(linearLowerLimit[0], linearLowerLimit[1], linearLowerLimit[2]));
    dof->setLinearUpperLimit(btVector3(linearUpperLimit[0], linearUpperLimit[1], linearUpperLimit[2]));
    dof->setAngularLowerLimit(btVector3(angularLowerLimit[0], angularLowerLimit[1], angularLowerLimit[2]));
    dof->setAngularUpperLimit(btVector3(angularUpperLimit[0], angularUpperLimit[1], angularUpperLimit[2]));
    for(int i = 0; i < 3; ++i) {
        dof->setStiffness(i, posStiffness[i]);
    }
    for(int i = 0; i < 3; ++i) {
        dof->setStiffness(i + 3, rotationStiffness[i]);
    }

    dynamicsWorld->addConstraint(dof);

}


void MMDPhysics::updateViewProjectMatrix(float *vpMatrix) {
    if(debugDrawer != nullptr) {
        debugDrawer->updateProjectionMatrix(vpMatrix);
    }
}

void MMDPhysics::stepSimulation(float timeStep) {
    dynamicsWorld->stepSimulation(timeStep);
    if(debugDraw) {
        if(debugDrawer == nullptr) {
            debugDrawer = new BulletDebugDrawer;
            dynamicsWorld->setDebugDrawer(debugDrawer);
        }
        dynamicsWorld->debugDrawWorld();

        debugDrawer->render();
    }
}

void MMDPhysics::setRigidBodyTransform(int rigidBodyIndex, float *matrix) {
    if(rigidBodyIndex >= dynamicRigidBodies.size()) {
        return;
    }

    btRigidBody *rigidBody = dynamicRigidBodies[rigidBodyIndex];
    btTransform transform;
    transform.setFromOpenGLMatrix(matrix);
    rigidBody->getMotionState()->setWorldTransform(transform);
}

void MMDPhysics::getRigidBodyTransform(int index, float *result) {
    if(index >= dynamicRigidBodies.size()) {
        return;
    }
    btRigidBody *rigidBody = dynamicRigidBodies[index];
    btTransform trans;
    rigidBody->getMotionState()->getWorldTransform(trans);
    trans.getOpenGLMatrix(result);
}

btRigidBody* MMDPhysics::createRigidBody(btCollisionShape *shape, int type, btScalar mass,
                            btTransform& transform, float linearDamping,
                            float angularDamping, float restitution, float friction) {
    if(type == 0) {
        mass = 0;
    }
    bool isDynamic = (mass != .0);
    btVector3 localInertia(.0, .0, .0);
    if(isDynamic) {
        shape->calculateLocalInertia(mass, localInertia);
    }
    auto *motionState = new btDefaultMotionState(transform);
    btRigidBody::btRigidBodyConstructionInfo rbInfo(mass, motionState, shape, localInertia);
    rbInfo.m_linearDamping = linearDamping;
    rbInfo.m_angularDamping = angularDamping;
    rbInfo.m_restitution = restitution;
    rbInfo.m_friction = friction;
    auto *body = new btRigidBody(rbInfo);
    body->setActivationState(DISABLE_DEACTIVATION);
    if(type == 0) {
        //type == 0表示不受重力影响的刚体，但是刚体可以移动
        body->setCollisionFlags(body->getCollisionFlags() | btCollisionObject::CF_KINEMATIC_OBJECT);
    }

    return body;
}

btCollisionShape *MMDPhysics::createShape(int shape, float *halfExtents) {
    btCollisionShape *collisionShape = nullptr;
    btVector3 shapeHalfExtents(halfExtents[0], halfExtents[1], halfExtents[2]);

    switch (shape) {
        case 1: //box
            collisionShape = new btBoxShape(shapeHalfExtents);
            break;
        case 0: //Sphere:
            collisionShape = new btSphereShape(halfExtents[0]);
            break;
        case 2: //Capsule:
            collisionShape = new btCapsuleShape(halfExtents[0], halfExtents[1]);
            break;
    }
    return collisionShape;
}

void MMDPhysics::destroy() {
    for(int i = dynamicsWorld->getNumConstraints() - 1; i >= 0; i--) {
        btTypedConstraint *constraint =dynamicsWorld->getConstraint(i);
        if(constraint) {
            dynamicsWorld->removeConstraint(constraint);
            delete constraint;
        }
    }

    for (int i = dynamicsWorld->getNumCollisionObjects() - 1; i >= 0; i--){
        btCollisionObject* obj = dynamicsWorld->getCollisionObjectArray()[i];
        btRigidBody* body = btRigidBody::upcast(obj);
        if (body && body->getMotionState()){
            delete body->getMotionState();
        }
        dynamicsWorld->removeCollisionObject(obj);
        delete obj;
    }

    //delete collision shapes
    for (int j = 0; j < collisionShapes.size(); j++){
        btCollisionShape* shape = collisionShapes[j];
        collisionShapes[j] = 0;
        delete shape;
    }

    if(debugDrawer != nullptr) {
        delete debugDrawer;
    }

    //delete dynamics world
    delete dynamicsWorld;

    //delete solver
    delete solver;

    //delete broadphase
    delete overlappingPairCache;

    //delete dispatcher
    delete dispatcher;

    delete collisionConfiguration;

    //next line is optional: it will be cleared by the destructor when the array goes out of scope
    collisionShapes.clear();
}


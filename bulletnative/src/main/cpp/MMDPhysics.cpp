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

    btCollisionShape *groundShape = new btBoxShape(btVector3(btScalar(10.), btScalar(0.1), btScalar(10)));
    collisionShapes.push_back(groundShape);

    btVector3 location(btScalar(.0), btScalar(-5), btScalar(.0));
    btQuaternion rotation(btScalar(0), btScalar(0), btScalar(0), btScalar(1));
    btRigidBody *rigidBody = createRigidBody(groundShape, btScalar(.0), location, rotation);
    //add the body to the dynamics world
    dynamicsWorld->addRigidBody(rigidBody);
}

MMDPhysics::~MMDPhysics() {
    destroy();
}

void MMDPhysics::addRigidBody(int shape, float mass, float halfExtents[], float position[], float quaternion[]) {
    btCollisionShape *collisionShape = createShape(shape, halfExtents);
    collisionShapes.push_back(collisionShape);

    btVector3 bodyLocation(position[0], position[1], position[2]);
    btQuaternion rotation(quaternion[0], quaternion[1], quaternion[2], quaternion[3]);
    btRigidBody *rigidBody = createRigidBody(collisionShape, mass, bodyLocation, rotation);
    dynamicRigidBodys.push_back(rigidBody);
    dynamicsWorld->addRigidBody(rigidBody);
}

void MMDPhysics::addJoint(int rigidBodyAIndex, int rigidBodyBIndex, float *rotation, float *position,
                          float *linearLowerLimit, float *linearUpperLimit,
                          float *angularLowerLimit, float *angularUpperLimit) {
    btRigidBody *rigidBodyA = dynamicRigidBodys[rigidBodyAIndex];
    btRigidBody *rigidBodyB = dynamicRigidBodys[rigidBodyBIndex];
    btTransform frameInA;
    frameInA.setIdentity();
    frameInA.setOrigin(btVector3(.0, 0, 5));

    btTransform frameInB;
    frameInB.setIdentity();
    frameInB.setOrigin(btVector3(.0, 0, .0));

    btGeneric6DofSpringConstraint *dof = new btGeneric6DofSpringConstraint(*rigidBodyA, *rigidBodyB,
                                                                        frameInA, frameInB, false);
    dof->enableSpring(0, true);
    dof->setEquilibriumPoint(0, 0);
//    dof->setStiffness(0, 200);
//    dof->setStiffness(1, 8);
    dof->setLinearLowerLimit(btVector3(linearLowerLimit[0], linearLowerLimit[1], linearLowerLimit[2]));
    dof->setLinearUpperLimit(btVector3(linearUpperLimit[0], linearUpperLimit[1], linearUpperLimit[2]));
    dof->setAngularLowerLimit(btVector3(angularLowerLimit[0], angularLowerLimit[1], angularLowerLimit[2]));
    dof->setAngularUpperLimit(btVector3(angularUpperLimit[0], angularUpperLimit[1], angularUpperLimit[2]));

    dynamicsWorld->addConstraint(dof);

}

void MMDPhysics::stepSimulation(float timeStep) {
    dynamicsWorld->stepSimulation(timeStep);
}

void MMDPhysics::getRigidBodyTransform(int index, float *result) {
    if(index >= dynamicRigidBodys.size()) {
        return;
    }
    btCollisionObject *collisionObject = dynamicRigidBodys[index];
    btRigidBody *rigidBody = btRigidBody::upcast(collisionObject);
    btTransform trans;
    if(rigidBody && rigidBody->getMotionState()) {
        rigidBody->getMotionState()->getWorldTransform(trans);
    } else {
        trans = collisionObject->getWorldTransform();
    }
    trans.getOpenGLMatrix(result);
}

btRigidBody* MMDPhysics::createRigidBody(btCollisionShape *shape, btScalar mass,
                            btVector3& location, btQuaternion& rotation) {
    btTransform transform;
    transform.setIdentity();
    transform.setOrigin(location);
    transform.setRotation(rotation);
    bool isDynamic = (mass != .0);
    btVector3 localInertia(.0, .0, .0);
    if(isDynamic) {
        shape->calculateLocalInertia(mass, localInertia);
    }
    auto *motionState = new btDefaultMotionState(transform);
    btRigidBody::btRigidBodyConstructionInfo rbInfo(mass, motionState, shape, localInertia);
    auto *body = new btRigidBody(rbInfo);

    return body;
}

btCollisionShape *MMDPhysics::createShape(int shape, float *halfExtents) {
    btCollisionShape *collisionShape = nullptr;
    btVector3 shapeHalfExtents(halfExtents[0], halfExtents[1], halfExtents[2]);

    switch (shape) {
        case 0: //box
            collisionShape = new btBoxShape(shapeHalfExtents);
            break;
        case 1: //Sphere:
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


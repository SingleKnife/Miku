//
// Created by admin on 2019/5/6.
//

#include <BulletCollision/CollisionShapes/btBoxShape.h>
#include <LinearMath/btDefaultMotionState.h>
#include <BulletCollision/CollisionShapes/btSphereShape.h>
#include <BulletCollision/CollisionShapes/btCapsuleShape.h>
#include <BulletDynamics/ConstraintSolver/btGeneric6DofSpringConstraint.h>
#include "MMDPhysics.h"
#include "log.h"



MMDPhysics::MMDPhysics() {
    collisionConfiguration = new btDefaultCollisionConfiguration();
    dispatcher = new btCollisionDispatcher(collisionConfiguration);
    overlappingPairCache = new btDbvtBroadphase();
    solver = new btSequentialImpulseConstraintSolver;

    dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
    dynamicsWorld->getSolverInfo().m_numIterations = 4;
    dynamicsWorld->getSolverInfo().m_solverMode = SOLVER_SIMD + SOLVER_USE_WARMSTARTING;

    dynamicsWorld->setGravity(btVector3(0, -9.8f * 5, 0));
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
    dynamicsWorld->addRigidBody(rigidBody, 1 << group, mask);
}

void printTransform(btTransform& tran) {
    LOGD("rotation: %f, %f, %f, %f, %f, %f, %f, %f, %f", tran.getBasis()[0], tran.getBasis()[1], tran.getBasis()[2],
         tran.getBasis()[3], tran.getBasis()[4], tran.getBasis()[5], tran.getBasis()[6], tran.getBasis()[7],tran.getBasis()[8]);
    LOGD("transform: %f, %f, %f", tran.getOrigin().getX(), tran.getOrigin().getY(), tran.getOrigin().getZ());
}

void MMDPhysics::addJoint(int rigidBodyAIndex, int rigidBodyBIndex,
                          float rigidBodyAOriginTrans[], float rigidBodyBOriginTrans[],
                          float *rotation, float *position,
                          float *linearLowerLimit, float *linearUpperLimit,
                          float *angularLowerLimit, float *angularUpperLimit,
                          float *posStiffness, float *rotationStiffness) {
    btTransform originTranA;
    originTranA.setFromOpenGLMatrix(rigidBodyAOriginTrans);
//    printTransform(originTranA);
    btTransform originTranB;
    originTranB.setFromOpenGLMatrix(rigidBodyBOriginTrans);
//    printTransform(originTranB);

    btMatrix3x3 jointRotate;
    jointRotate.setEulerZYX(rotation[0], rotation[1], rotation[2]);
    btVector3 jointPosition(position[0], position[1], position[2]);
    btTransform jointTran = btTransform(jointRotate, jointPosition);
    printTransform(jointTran);

    btTransform frameInA = originTranA.inverse() * jointTran;
    btTransform frameInB = originTranB.inverse() * jointTran;

    LOGD("rigidA: %d", rigidBodyAIndex);
    printTransform(originTranA);
    printTransform(frameInA);
    LOGD("rigidB: %d", rigidBodyBIndex);
    printTransform(originTranB);
    printTransform(frameInB);


    btRigidBody *rigidBodyA = dynamicRigidBodies[rigidBodyAIndex];
    btRigidBody *rigidBodyB = dynamicRigidBodies[rigidBodyBIndex];

    auto *dof = new btGeneric6DofSpringConstraint(*rigidBodyA, *rigidBodyB,
            frameInA, frameInB, true);
    dof->setLinearLowerLimit(btVector3(linearLowerLimit[0], linearLowerLimit[1], linearLowerLimit[2]));
    dof->setLinearUpperLimit(btVector3(linearUpperLimit[0], linearUpperLimit[1], linearUpperLimit[2]));
    dof->setAngularLowerLimit(btVector3(angularLowerLimit[0], angularLowerLimit[1], angularLowerLimit[2]));
    dof->setAngularUpperLimit(btVector3(angularUpperLimit[0], angularUpperLimit[1], angularUpperLimit[2]));
    for(int i = 0; i < 3; ++i) {
        if(posStiffness[i] != 0) {
            dof->setStiffness(i, posStiffness[i]);
            dof->enableSpring(i , true);
        } else {
            dof->enableSpring(i, false);
        }
    }
    for(int i = 0; i < 3; ++i) {
        if(rotationStiffness[i] != 0) {
            dof->setStiffness(i + 3, rotationStiffness[i]);
            dof->enableSpring(i + 3, true);
        } else {
            dof->enableSpring(i + 3, false);
        }
    }

    dynamicsWorld->addConstraint(dof);

}


void MMDPhysics::updateViewProjectMatrix(float *vpMatrix) {
    if(debugDrawer != nullptr) {
        debugDrawer->updateProjectionMatrix(vpMatrix);
    }
}

void MMDPhysics::stepSimulation(float timeStep) {
    LOGD("timestep %f", timeStep);
    dynamicsWorld->stepSimulation(timeStep, 2);
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


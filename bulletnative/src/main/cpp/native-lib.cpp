#include <jni.h>
#include <string>
#include <android/log.h>
#include "MMDPhysics.h"

MMDPhysics *mmdPhysics;

void create() {
    if(mmdPhysics == nullptr) {
        mmdPhysics = new MMDPhysics;
    }
}

void destroy() {
    if(mmdPhysics != nullptr) {
        delete mmdPhysics;
        mmdPhysics = nullptr;
    }
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_fyd_bullet_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_fyd_bullet_Physics_nativeCreate(JNIEnv *env, jclass type) {
    create();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_fyd_bullet_Physics_nativeDestroy(JNIEnv *env, jclass type) {
    destroy();
}


extern "C"
JNIEXPORT void JNICALL
Java_com_fyd_bullet_Physics_nativeAddJoint(JNIEnv *env, jclass type, jint rigidAIndex,
                                           jint rigidBIndex, jfloatArray rotation_,
                                           jfloatArray position_, jfloatArray linearLowerLimit_,
                                           jfloatArray linearUpperLimit_,
                                           jfloatArray angularLowerLimit_,
                                           jfloatArray angularUpperLimit_) {
    jfloat *rotation = env->GetFloatArrayElements(rotation_, NULL);
    jfloat *position = env->GetFloatArrayElements(position_, NULL);
    jfloat *linearLowerLimit = env->GetFloatArrayElements(linearLowerLimit_, NULL);
    jfloat *linearUpperLimit = env->GetFloatArrayElements(linearUpperLimit_, NULL);
    jfloat *angularLowerLimit = env->GetFloatArrayElements(angularLowerLimit_, NULL);
    jfloat *angularUpperLimit = env->GetFloatArrayElements(angularUpperLimit_, NULL);

    // TODO
    mmdPhysics->addJoint(rigidAIndex, rigidBIndex, rotation, position,
                         linearLowerLimit, linearUpperLimit, angularLowerLimit, angularUpperLimit);

    env->ReleaseFloatArrayElements(rotation_, rotation, 0);
    env->ReleaseFloatArrayElements(position_, position, 0);
    env->ReleaseFloatArrayElements(linearLowerLimit_, linearLowerLimit, 0);
    env->ReleaseFloatArrayElements(linearUpperLimit_, linearUpperLimit, 0);
    env->ReleaseFloatArrayElements(angularLowerLimit_, angularLowerLimit, 0);
    env->ReleaseFloatArrayElements(angularUpperLimit_, angularUpperLimit, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_fyd_bullet_Physics_nativeStepSimulation(JNIEnv *env, jclass type, jfloat timeStep) {

    mmdPhysics->stepSimulation(timeStep);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_fyd_bullet_Physics_nativeGetRigidBodyTransform(JNIEnv *env, jclass type, jint index,
                                                        jfloatArray result_) {
    jfloat *result = env->GetFloatArrayElements(result_, NULL);

    mmdPhysics->getRigidBodyTransform(index, result);

    env->ReleaseFloatArrayElements(result_, result, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_fyd_bullet_Physics_nativeAddRigidBody(JNIEnv *env, jclass type_, jint shape, jfloat mass,
                                               jint type, jfloatArray halfExtents_,
                                               jfloatArray position_, jfloatArray rotation_,
                                               jfloat linearDamping, jfloat angularDamping,
                                               jfloat restitution, jfloat friction, jint group,
                                               jint mask) {
    jfloat *halfExtents = env->GetFloatArrayElements(halfExtents_, NULL);
    jfloat *position = env->GetFloatArrayElements(position_, NULL);
    jfloat *quaternion = env->GetFloatArrayElements(rotation_, NULL);

    mmdPhysics->addRigidBody(shape, mass, type, halfExtents, position, quaternion, linearDamping, angularDamping,
            restitution, friction, group, mask);


    env->ReleaseFloatArrayElements(halfExtents_, halfExtents, 0);
    env->ReleaseFloatArrayElements(position_, position, 0);
    env->ReleaseFloatArrayElements(rotation_, quaternion, 0);
}
package com.fyd.bullet;

public class Physics {
    private static boolean isInit;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private Physics() {}

    public static void init() {
        if(!isInit) {
            nativeCreate();
            isInit = true;
        }
    }

    public static void addRigidBody(int shape, float mass, int type,
                                    float[] halfExtents, float[] position, float[] rotation,
                                    float linearDamping, float angularDamping, float restitution,
                                    float friction, int group, int mask) {
        nativeAddRigidBody(shape, mass, type,
                halfExtents, position, rotation,
                linearDamping, angularDamping, restitution,
                friction, group, mask);
    }

    public static void addJoint(int rigidAIndex, int rigidBIndex, float[] rotation, float[] position,
                                float[] linearLowerLimit, float[] linearUpperLimit,
                                float[] angularLowerLimit, float[] angularUpperLimit,
                                float[] posStiffness, float[] rotationStiffness) {
        nativeAddJoint(rigidAIndex, rigidBIndex, rotation, position,
                linearLowerLimit, linearUpperLimit,
                angularLowerLimit, angularUpperLimit,
                posStiffness, rotationStiffness);
    }

    public static void updateProjectionMatrix(float[] matrix) {
        nativeUpdateProjectionMatrix(matrix);
    }

    public static void stepSimulation(float timeStep) {
        nativeStepSimulation(timeStep);
    }

    public static void getRigidBodyTransform(int index, float[] result) {
        nativeGetRigidBodyTransform(index, result);
    }

    public static void destroy() {
        if (isInit) {
            nativeDestroy();
            isInit = false;
        }
    }

    private static native void nativeCreate();

    private static native void nativeAddRigidBody(int shape, float mass, int type, float[] halfExtents, float[] position, float[] rotation,
                                                  float linearDamping, float angularDamping, float restitution, float friction, int group, int mask);

    private static native void nativeAddJoint(int rigidAIndex, int rigidBIndex, float[] rotation, float[] position,
                                              float[] linearLowerLimit, float[] linearUpperLimit,
                                              float[] angularLowerLimit, float[] angularUpperLimit,
                                              float[] posStiffness, float[] rotationStiffness);

    private static native void nativeUpdateProjectionMatrix(float[] matrix);

    private static native void nativeStepSimulation(float timeStep);

    private static native void nativeGetRigidBodyTransform(int index, float[] result);

    private static native void nativeDestroy();
}

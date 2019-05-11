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

    public static void addRigidBody(int shape, float mass, float[] halfExtends, float[] position, float[] rotation) {
        nativeAddRigidBody(shape, mass, halfExtends, position, rotation);
    }

    public static void addJoint(int rigidAIndex, int rigidBIndex, float[] rotation, float[] position,
                                float[] linearLowerLimit, float[] linearUpperLimit,
                                float[] angularLowerLimit, float[] angularUpperLimit) {
        nativeAddJoint(rigidAIndex, rigidBIndex, rotation, position, linearLowerLimit, linearUpperLimit,
                angularLowerLimit, angularUpperLimit);
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

    private static native void nativeAddRigidBody(int shape, float mass, float[] halfExtends, float[] position, float[] rotation);

    private static native void nativeAddJoint(int rigidAIndex, int rigidBIndex, float[] rotation, float[] position,
                                              float[] linearLowerLimit, float[] linearUpperLimit,
                                              float[] angularLowerLimit, float[] angularUpperLimit);

    private static native void nativeStepSimulation(float timeStep);

    private static native void nativeGetRigidBodyTransform(int index, float[] result);

    private static native void nativeDestroy();
}

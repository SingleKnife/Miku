package com.fyd.miku.model.pmd;

import android.opengl.Matrix;

import java.util.Arrays;

/**
 * 刚体信息，基于bullet物理引擎
 */
public class RigidBody {
    public static final int BODY_TYPE_STATIC = 0;               //不受物理引擎影响
    public static final int BODY_TYPE_DYNAMIC = 0;              //只受物理引擎影响
    public static final int BODY_TYPE_DYNAMIC_ADJUST_BONE = 0;  //刚体同时受bone和物理引擎

    String name;
    public int boneIndex;
    public int group;
    public int mask;
    public int shape;                  //刚体形状 0.sphere 球 1. box箱 2. Capsule
    public float shapeWidth;
    public float shapeHeight;
    public float shapeDepth;           //only for box shape
    public float[] shapePos;           //x, y, z
    public float[] shapeRotation;      //x, y, z旋转角度
    public float mass;      //刚体重量
    public float linearDimmer;   //位置阻尼系数
    public float angularDamping;//旋转阻尼系数
    public float rigidBodyRecoil;          //反冲系数
    public float rigidBodyFriction;        //摩擦系数
    public int rigidBodyType;            //刚体类型 0.静态刚体，不受物理影响，1，动态的，受物理影响

    public float[] originTransform;


    public RigidBody() {
        shapePos = new float[3];
        shapeRotation = new float[3];
        originTransform = new float[16];
    }

    @Override
    public String toString() {
        return "RigidBody{" +
                "name='" + name + '\'' +
                ", boneIndex=" + boneIndex +
                ", group=" + group +
                ", mask=" + mask +
                ", shape=" + shape +
                ", shapeWidth=" + shapeWidth +
                ", shapeHeight=" + shapeHeight +
                ", shapeDepth=" + shapeDepth +
                ", shapePos=" + Arrays.toString(shapePos) +
                ", shapeRotation=" + Arrays.toString(shapeRotation) +
                ", mass=" + mass +
                ", linearDimmer=" + linearDimmer +
                ", angularDamping=" + angularDamping +
                ", rigidBodyRecoil=" + rigidBodyRecoil +
                ", rigidBodyFriction=" + rigidBodyFriction +
                ", rigidBodyType=" + rigidBodyType +
                '}';
    }

    public void calcOriginTransform() {
        Matrix.setRotateEulerM(originTransform, 0,
                (float)Math.toDegrees(shapeRotation[0]),
                (float)Math.toDegrees(shapeRotation[1]),
                (float)Math.toDegrees(-shapeRotation[2]));
        originTransform[12] = shapePos[0];
        originTransform[13] = shapePos[1];
        originTransform[14] = shapePos[2];
    }
}

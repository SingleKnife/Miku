package com.fyd.miku.model.pmd;

/**
 * 刚体信息，基于bullet物理引擎
 */
public class RigidBody {
    public static final int BODY_TYPE_STATIC = 0;               //不受物理引擎影响
    public static final int BODY_TYPE_DYNAMIC = 0;              //只受物理引擎影响
    public static final int BODY_TYPE_DYNAMIC_ADJUST_BONE = 0;  //刚体同时受bone和物理引擎

    String name;
    int boneIndex;
    int groupIndex;
    int groupTarget;
    int shape;                  //刚体形状 0.sphere 球 1. box箱 2. Capsule
    float shapeWidth;
    float shapeHeight;
    float shapeDepth;           //only for box shape
    float[] shapePos;           //x, y, z
    float[] shapeRotation;      //x, y, z旋转角度
    float rigidBodyWeight;      //刚体重量
    float rigidBodyPosDimmer;   //位置阻尼系数
    float rigidBodyRotationDimmer;//旋转阻尼系数
    float rigidBodyRecoil;          //反冲系数
    float rigidBodyFriction;        //摩擦系数
    int rigidBodyType;            //刚体类型


    public RigidBody() {
        shapePos = new float[3];
        shapeRotation = new float[3];
    }
}

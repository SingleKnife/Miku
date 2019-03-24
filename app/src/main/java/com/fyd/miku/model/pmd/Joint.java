package com.fyd.miku.model.pmd;

/**
 * 刚体之间的关节
 */
public class Joint {
    String name;
    int firstEffectRigidBody;       //第一个受影响的刚体序号
    int secondEffectRigidBody;      //第二个受影响的刚体序号
    float[] jointPos;               //关节位置，相对第一个刚体位置
    float[] jointRotation;          //旋转角度
    float[] posLowerLimit;          //x,y,z位置下限
    float[] posUpperLimit;          //x,y,z位置上限
    float[] rotationLowerLimit;     //x,y,z旋转下限
    float[] rotationUpperLimit;     //x,y,z旋转上限
    float[] posSpringStiffness;     //x,y,z位置弹性约束的刚度
    float[] rotationSpringStiffness;//旋转弹性约束的刚度

    Joint() {
        jointPos = new float[3];
        jointRotation = new float[3];
        posLowerLimit = new float[3];
        posUpperLimit = new float[3];
        rotationLowerLimit = new float[3];
        rotationUpperLimit = new float[3];
        posSpringStiffness = new float[3];
        rotationSpringStiffness = new float[3];
    }
}

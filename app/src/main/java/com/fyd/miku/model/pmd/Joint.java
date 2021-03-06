package com.fyd.miku.model.pmd;

import java.util.Arrays;

/**
 * 刚体之间的关节
 */
public class Joint {
    String name;
    public int firstRigidBody;       //第一个受影响的刚体序号
    public int secondRigidBody;      //第二个受影响的刚体序号
    public float[] jointPos;               //关节位置，相对第一个刚体位置
    public float[] jointRotation;          //旋转角度
    public float[] posLowerLimit;          //x,y,z位置下限
    public float[] posUpperLimit;          //x,y,z位置上限
    public float[] rotationLowerLimit;     //x,y,z旋转下限
    public float[] rotationUpperLimit;     //x,y,z旋转上限
    public float[] posSpringStiffness;     //x,y,z位置弹性约束的刚度
    public float[] rotationSpringStiffness;//旋转弹性约束的刚度

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

    @Override
    public String toString() {
        return "Joint{" +
                "name='" + name + '\'' +
                ", firstRigidBody=" + firstRigidBody +
                ", secondRigidBody=" + secondRigidBody +
                ", jointPos=" + Arrays.toString(jointPos) +
                ", jointRotation=" + Arrays.toString(jointRotation) +
                ", posLowerLimit=" + Arrays.toString(posLowerLimit) +
                ", posUpperLimit=" + Arrays.toString(posUpperLimit) +
                ", rotationLowerLimit=" + Arrays.toString(rotationLowerLimit) +
                ", rotationUpperLimit=" + Arrays.toString(rotationUpperLimit) +
                ", posSpringStiffness=" + Arrays.toString(posSpringStiffness) +
                ", rotationSpringStiffness=" + Arrays.toString(rotationSpringStiffness) +
                '}';
    }
}

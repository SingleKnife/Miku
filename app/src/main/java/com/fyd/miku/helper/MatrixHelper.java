package com.fyd.miku.helper;

public class MatrixHelper {

    /**
     * 将旋转四元组转为矩阵
     * @param rm  目标矩阵数组
     * @param rmOffset 目标矩阵数组偏移量
     * @param quaternion 旋转四元组
     */
    static public void setQuaternionM(float rm[], int rmOffset, float quaternion[]) {
        float x2 = quaternion[0] * quaternion[0] * 2.0f;
        float y2 = quaternion[1] * quaternion[1] * 2.0f;
        float z2 = quaternion[2] * quaternion[2] * 2.0f;
        float xy = quaternion[0] * quaternion[1] * 2.0f;
        float yz = quaternion[1] * quaternion[2] * 2.0f;
        float zx = quaternion[2] * quaternion[0] * 2.0f;
        float xw = quaternion[0] * quaternion[3] * 2.0f;
        float yw = quaternion[1] * quaternion[3] * 2.0f;
        float zw = quaternion[2] * quaternion[3] * 2.0f;

        rm[rmOffset + 0] = 1.0f - y2 - z2;
        rm[rmOffset + 1] = xy + zw;
        rm[rmOffset + 2] = zx - yw;
        rm[rmOffset + 4] = xy - zw;
        rm[rmOffset + 5] = 1.0f - z2 - x2;
        rm[rmOffset + 6] = yz + xw;
        rm[rmOffset + 8] = zx + yw;
        rm[rmOffset + 9] = yz - xw;
        rm[rmOffset + 10] = 1.0f - x2 - y2;

        rm[rmOffset + 3] = rm[rmOffset + 7] = rm[rmOffset + 11]
                = rm[rmOffset + 12] = rm[rmOffset + 13] = rm[rmOffset + 14] = 0.0f;
        rm[rmOffset + 15] = 1.0f;
    }
}

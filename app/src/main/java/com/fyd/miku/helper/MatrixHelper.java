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

    /**
     * 将旋转四元组，位移，缩放矢量转为矩阵
     * @param rm  目标矩阵数组
     * @param rmOffset 目标矩阵数组偏移量
     * @param rotation 旋转四元组
     */
    static public void fromRotationTranslationScale(float rm[], int rmOffset,
                                                    float rotation[],
                                                    float translation[],
                                                    float scale[] ) {
        float x2 = rotation[0] * rotation[0] * 2.0f;
        float y2 = rotation[1] * rotation[1] * 2.0f;
        float z2 = rotation[2] * rotation[2] * 2.0f;
        float xy = rotation[0] * rotation[1] * 2.0f;
        float yz = rotation[1] * rotation[2] * 2.0f;
        float zx = rotation[2] * rotation[0] * 2.0f;
        float xw = rotation[0] * rotation[3] * 2.0f;
        float yw = rotation[1] * rotation[3] * 2.0f;
        float zw = rotation[2] * rotation[3] * 2.0f;

        float sx = scale[0];
        float sy = scale[1];
        float sz = scale[2];

        rm[rmOffset + 0] = (1.0f - y2 - z2) * sx;
        rm[rmOffset + 1] = (xy + zw) * sx;
        rm[rmOffset + 2] = (zx - yw) * sx;
        rm[rmOffset + 4] = (xy - zw) * sy;
        rm[rmOffset + 5] = (1.0f - z2 - x2) * sy;
        rm[rmOffset + 6] = (yz + xw) * sy;
        rm[rmOffset + 8] = (zx + yw) * sz;
        rm[rmOffset + 9] = (yz - xw) * sz;
        rm[rmOffset + 10] = (1.0f - x2 - y2) * sz;

        rm[rmOffset + 3] = rm[rmOffset + 7] = rm[rmOffset + 11] = 0.0f;
        rm[rmOffset + 12] = translation[0];
        rm[rmOffset + 13] = translation[1];
        rm[rmOffset + 14] = translation[2];
        rm[rmOffset + 15] = 1.0f;
    }

}

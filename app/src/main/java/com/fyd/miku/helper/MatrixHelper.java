package com.fyd.miku.helper;

import android.opengl.Matrix;

import com.flowpowered.math.TrigMath;
import com.flowpowered.math.imaginary.Quaternionf;
import com.flowpowered.math.vector.Vector3f;

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

    static public void normaizeVec3(float[] result, float[] vector) {
        float length = Matrix.length(vector[0], vector[1], vector[2]);
        result[0] = vector[0] / length;
        result[1] = vector[1] / length;
        result[2] = vector[2] / length;
    }

    public static float dotVec3(float[] v1, float[] v2) {
        return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
    }

    public static void crossVec3(float[] d, float[] v1, float[] v2) {
        d[0] = v1[1] * v2[2] - v1[2] * v2[1];
        d[1] = v1[2] * v2[0] - v1[0] * v2[2];
        d[2] = v1[0] * v2[1] - v1[1] * v2[0];
    }


    static public void createQuaternionFromAngleAxis(float[] r, double angle, float[] axis) {
        float halfAngle = (float) (0.5f * angle);
        float sin = (float) Math.sin(halfAngle);
        r[3] = (float) Math.cos(halfAngle);
        r[0] = sin * axis[0];
        r[1] = sin * axis[1];
        r[2] = sin * axis[2];
    }

    public static float length(float[] v1, float[] v2) {
        float x = v1[0] - v2[0];
        float y = v1[1] - v2[1];
        float z = v1[2] - v2[2];
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * matrix 4 x 4 to Quaternion
     * @param result
     * @param matrix
     * @return
     */
    public static void matrixToQuaternion(float[] result, float[] matrix) {
        final float trace = matrix[0] + matrix[5] + matrix[10];
        if (trace < 0) {
            if (matrix[5] > matrix[0]) {
                if (matrix[10] > matrix[5]) {
                    final float r = (float) Math.sqrt(matrix[10] - matrix[0] - matrix[5] + 1);
                    final float s = 0.5f / r;
                    result[0] = (matrix[8] + matrix[2]) * s;
                    result[1] = (matrix[6] + matrix[9]) * s;
                    result[2] = 0.5f * r;
                    result[3] = (matrix[4] - matrix[1]) * s;
                } else {
                    final float r = (float) Math.sqrt(matrix[5] - matrix[10] - matrix[0] + 1);
                    final float s = 0.5f / r;
                    result[0] = (matrix[1] + matrix[4]) * s;
                    result[1] = 0.5f * r;
                    result[2] = (matrix[6] + matrix[9]) * s;
                    result[3] = (matrix[2] - matrix[8]) * s;
                }
            } else if (matrix[10] > matrix[0]) {
                final float r = (float) Math.sqrt(matrix[10] - matrix[0] - matrix[5] + 1);
                final float s = 0.5f / r;
                result[0] = (matrix[8] + matrix[2]) * s;
                result[1] = (matrix[6] + matrix[9]) * s;
                result[2] = 0.5f * r;
                result[3] = (matrix[4] - matrix[1]) * s;
            } else {
                final float r = (float) Math.sqrt(matrix[0] - matrix[5] - matrix[10] + 1);
                final float s = 0.5f / r;
                result[0] = 0.5f * r;
                result[1] = (matrix[1] + matrix[4]) * s;
                result[2] = (matrix[8] - matrix[2]) * s;
                result[3] = (matrix[9] - matrix[6]) * s;
            }
        } else {
            final float r = (float) Math.sqrt(trace + 1);
            final float s = 0.5f / r;
            result[0] = (matrix[9] - matrix[6]) * s;
            result[1] = (matrix[2] - matrix[8]) * s;
            result[2] = (matrix[4] - matrix[1]) * s;
            result[3] = 0.5f * r;
        }
    }

    /**
     * 旋转四元组转换成欧拉角
     * @param result 分别为绕x， y, z的旋转角度
     * @param quaternion 四元组 i, j, k, w
     */
    public static void quaternionToEulerRadius(float result[], float[] quaternion) {
        final double roll;
        final double pitch;
        double yaw;
        float x = quaternion[0];
        float y = quaternion[1];
        float z = quaternion[2];
        float w = quaternion[3];

        final double test = w * x - y * z;
        if (Math.abs(test) < 0.4999) {
            roll = Math.atan2(2 * (w * z + x * y), 1 - 2 * (x * x + z * z));
            pitch = Math.asin(2 * test);
            yaw = Math.atan2(2 * (w * y + z * x), 1 - 2 * (x * x + y * y));
        } else {
            final int sign = (test < 0) ? -1 : 1;
            roll = 0;
            pitch = sign * Math.PI / 2;
            yaw = -sign * 2 * Math.atan2(z, w);
        }
        result[0] = (float) pitch;
        result[1] = (float) yaw;
        result[2] = (float) roll;
    }


    /**
     * Converts Euler angles to a rotation matrix.
     *
     * @param rm returns the result
     * @param rmOffset index into rm where the result matrix starts
     * @param x angle of rotation, in degrees
     * @param y angle of rotation, in degrees
     * @param z angle of rotation, in degrees
     */
    public static void setRotateEulerZYXM(float[] rm, int rmOffset,
                                       float x, float y, float z) {
        x *= (float) (Math.PI / 180.0f);
        y *= (float) (Math.PI / 180.0f);
        z *= (float) (Math.PI / 180.0f);
        float cx = (float) Math.cos(x);
        float sx = (float) Math.sin(x);
        float cy = (float) Math.cos(y);
        float sy = (float) Math.sin(y);
        float cz = (float) Math.cos(z);
        float sz = (float) Math.sin(z);
        float cxsy = cx * sy;
        float sxsy = sx * sy;

        rm[rmOffset + 0]  =   cy * cz;
        rm[rmOffset + 1]  =  cy * sz;
        rm[rmOffset + 2]  =   -sy;
        rm[rmOffset + 3]  =  0.0f;

        rm[rmOffset + 4]  =  sxsy * cz - cx * sz;
        rm[rmOffset + 5]  =  sxsy * sz + cx * cz;
        rm[rmOffset + 6]  =  sx * cy;
        rm[rmOffset + 7]  =  0.0f;

        rm[rmOffset + 8]  =  cxsy * cz + sx * sz;
        rm[rmOffset + 9]  =  cxsy * sz - sx * cz;
        rm[rmOffset + 10] =  cx * cy;
        rm[rmOffset + 11] =  0.0f;

        rm[rmOffset + 12] =  0.0f;
        rm[rmOffset + 13] =  0.0f;
        rm[rmOffset + 14] =  0.0f;
        rm[rmOffset + 15] =  1.0f;
    }
}

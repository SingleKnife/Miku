package com.fyd.miku.helper;

public class InterpolatorHelper {


    public static float bezier(float t, float x1, float y1, float x2, float y2) {
        float min = 0;
        float max = 1;

        float ct = t;
        while (true) {
            float x11 = x1 * ct;
            float x12 = x1 + (x2 - x1) * ct;
            float x13 = x2 + (1 - x2) * ct;

            float x21 = x11 + (x12 - x11) * ct;
            float x22 = x12 + (x13 - x12) * ct;

            float x3 = x21 + (x22 - x21) * ct;

            if (Math.abs(x3 - t) < 0.0001) {
                float y11 = y1 * ct;
                float y12 = y1 + (y2 - y1) * ct;
                float y13 = y2 + (1 - y2) * ct;

                float y21 = y11 + (y12 - y11) * ct;
                float y22 = y12 + (y13 - y12) * ct;

                float y3 = y21 + (y22 - y21) * ct;

                return y3;
            } else if (x3 < t) {
                min = ct;
            } else {
                max = ct;
            }
            ct = min * 0.5f + max * 0.5f;
        }
    }

    /**
     * 球面插值
     * @param result    结果数据
     * @param quatFrom  开始位置四元组 (i, j, k, w)
     * @param quatTo    结束位置四元组 (i, j, k, w)
     * @param t         插值比例 0 - 1
     */
    public static void slerp(float result[], float[] quatFrom, float[] quatTo, float t) {
        double qr = quatFrom[0] * quatTo[0] + quatFrom[1] * quatTo[1] + quatFrom[2] * quatTo[2] + quatFrom[3] * quatTo[3];
        double ss = 1.0 - qr * qr;

        if (qr < 0) {
            qr = -qr;

            double sp = Math.sqrt(ss);
            double ph = Math.acos(qr);
            double pt = ph * t;
            double t1 = Math.sin(pt) / sp;
            double t0 = Math.sin(ph - pt) / sp;

            if (Double.isNaN(t0) || Double.isNaN(t1)) {
                result[0] = quatFrom[0];
                result[1] = quatFrom[1];
                result[2] = quatFrom[2];
                result[3] = quatFrom[3];
            } else {
                result[0] = (float) (quatFrom[0] * t0 - quatTo[0] * t1);
                result[1] = (float) (quatFrom[1] * t0 - quatTo[1] * t1);
                result[2] = (float) (quatFrom[2] * t0 - quatTo[2] * t1);
                result[3] = (float) (quatFrom[3] * t0 - quatTo[3] * t1);
            }

        } else {
            double sp = Math.sqrt(ss);
            double ph = Math.acos(qr);
            double pt = ph * t;
            double t1 = Math.sin(pt) / sp;
            double t0 = Math.sin(ph - pt) / sp;

            if (Double.isNaN(t0) || Double.isNaN(t1)) {
                result[0] = quatFrom[0];
                result[1] = quatFrom[1];
                result[2] = quatFrom[2];
                result[3] = quatFrom[3];
            } else {
                result[0] = (float) (quatFrom[0] * t0 + quatTo[0] * t1);
                result[1] = (float) (quatFrom[1] * t0 + quatTo[1] * t1);
                result[2] = (float) (quatFrom[2] * t0 + quatTo[2] * t1);
                result[3] = (float) (quatFrom[3] * t0 + quatTo[3] * t1);
            }
        }
    }
}

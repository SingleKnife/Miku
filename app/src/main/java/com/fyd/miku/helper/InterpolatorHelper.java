package com.fyd.miku.helper;

public class InterpolatorHelper {
    public static float bezier(float t, float x1, float y1, float x2, float y2) {
        float lowTGuess = 0.0f;
        float dividingT = 0.5f;
        float highTGuess = 1.0f;

        float lowXCalc;
        float divideXCalc;

        for (int i = 0; i < 100; ++i) {
            lowXCalc = t - parBezFunc(lowTGuess, x1, x2);
            divideXCalc = t - parBezFunc(dividingT, x1, x2);

            if (Math.abs(divideXCalc) < 0.0001) {
                break;
            }

            if (lowXCalc * divideXCalc <0 ) {
                highTGuess = dividingT;
                dividingT = (dividingT + lowTGuess) / 2.0f;
            } else {
                lowTGuess = dividingT;
                dividingT = (highTGuess + dividingT) / 2.0f;
            }
        }
        return parBezFunc(dividingT,y1,y2);
    }

    //parameterized Bezier Curve Function
    private static float parBezFunc(float t, float p1, float p2) {
        return (3*( (1-t)*(1-t) )*t*p1) + (3*(1-t)*(t*t)*p2) + (t*t*t);
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

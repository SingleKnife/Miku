package com.fyd.miku.model.pmd;

import java.util.ArrayList;
import java.util.List;

/**
 * 面部动画
 */
public class FaceMorph {
    String morphName;       //动画名称
    String morphNameEnglish;
    int verticesCount;      //动画影响的顶点数量
    byte morphType;          //动画类 0.base 1. 眉毛动画，2. 眼睛动画， 3嘴唇动画 4.其他
    List<Vertex> vertices;

    FaceMorph() {
        vertices = new ArrayList<>();
    }


    static class Vertex {
        int vertexIndex;    //顶点索引
        float[] position;   //最大移动位置

        Vertex() {
            position = new float[3];
        }
    }

    @Override
    public String toString() {
        return "FaceMorph{" +
                "morphName='" + morphName + '\'' +
                ", verticesCount=" + verticesCount +
                ", morphType=" + morphType +
                '}';
    }
}

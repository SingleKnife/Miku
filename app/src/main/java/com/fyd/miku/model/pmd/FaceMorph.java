package com.fyd.miku.model.pmd;

import java.util.ArrayList;
import java.util.List;

/**
 * 面部动画
 */
public class FaceMorph {
    String morphName;       //动画名称
    String morphNameEnglish;
    int morphType;          //动画类 0.base 1. 眉毛动画，2. 眼睛动画， 3嘴唇动画 4.其他
    List<Vertex> vertices;  //动画影响的顶点

    FaceMorph() {
        vertices = new ArrayList<>();
    }

    public String getMorphName() {
        return morphName;
    }

    public int getVerticesCount() {
        return vertices.size();
    }

    public List<Vertex> getRelatedVertices() {
        return vertices;
    }

    public static class Vertex {
        public int vertexIndex;     //顶点索引
        public float[] maxOffset;   //最大移动位置
        public float[] basePos;     //未做动画时顶点位置

        Vertex() {
            maxOffset = new float[3];
        }
    }

    @Override
    public String toString() {
        return "FaceMorph{" +
                "morphName='" + morphName + '\'' +
                ", morphType=" + morphType +
                '}';
    }
}

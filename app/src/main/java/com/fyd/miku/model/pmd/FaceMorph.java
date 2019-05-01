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

    public int getMorphType() {
        return morphType;
    }

    public static class Vertex {
        /**
         *  当morphType是base类型当时候，是模型顶点数据中当索引；
         *  当morphType是另外几种类型当时候，是在base顶点中当索引，
         *  即在FaceMorph.vertices中当索引
         */
        public int vertexIndex;
        /**
         *  当morphType是base类型当时候，是模型顶点位置；
         *  当morphType是另外几种类型当时候，相对顶点位置当最大偏移量，
         *  计算动画当时候乘以动画计算出来当weight来计算偏移量
         */

        public float[] posOffset;   //最大移动位置

        public Vertex() {
            posOffset = new float[3];
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

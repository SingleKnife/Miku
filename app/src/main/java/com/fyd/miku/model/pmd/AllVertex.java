package com.fyd.miku.model.pmd;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AllVertex {
    public static final int BYTE_SIZE_PER_VERTEX = 38;
    public static final int BYTE_SIZE_PER_INDEX = 2;
    public static final int COORDINATE_OFFSET = 0;
    public static final int NORMAL_OFFSET = 12;
    public static final int UV_OFFSET = 24;
    public static final int BONE_INDEX_OFFSET = 32;
    public static final int BONE_WEIGHT_OFFSET = 36;
    public static final int EDGE_FLAG_OFFSET = 37;

    public static final int VERTEX_COMPONENT_SIZE = 3;      //顶点坐标分量数x, y, z
    public static final int NORMAL_COMPONENT_SIZE = 3;      //发现坐标分量数
    public static final int UV_COMPONENT_SIZE = 2;          //贴图坐标分量数
    public static final int BONE_INDEX_COMPONENT_SIZE = 2;  //影响顶点的骨骼序号，有2根骨骼
    public static final int BONE_WEIGHT_AND_EDGE_FLAG_COMPONENT_SIZE = 2;

    private ByteBuffer vertices;
    private ByteBuffer indices;

    public AllVertex() {

    }

    public void setVertices(byte[] bytes) {
        vertices = ByteBuffer.allocateDirect(bytes.length)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(bytes);
    }

    public void setIndices(byte[] bytes) {
        indices = ByteBuffer.allocateDirect(bytes.length)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(bytes);
    }

    public ByteBuffer getAllVertices() {
        return vertices;
    }

    public ByteBuffer getPositionBuffer() {
        vertices.position(0);
        return vertices;
    }

    public ByteBuffer getNormalBuffer() {
        vertices.position(NORMAL_OFFSET);
        return vertices;
    }

    public ByteBuffer getUVBuffer() {
        vertices.position(UV_OFFSET);
        return vertices;
    }

    public ByteBuffer getBoneIndexBuffer() {
        vertices.position(BONE_INDEX_OFFSET);
        return vertices;
    }

    public ByteBuffer getBoneWeightAndEdgeFlagBuffer() {
        vertices.position(BONE_WEIGHT_OFFSET);
        return vertices;
    }

    public ByteBuffer getIndices(int indexOffset) {
        indices.position(indexOffset * BYTE_SIZE_PER_INDEX);
        return indices;
    }

    public ByteBuffer getIndices() {
        indices.position(0);
        return indices;
    }

}

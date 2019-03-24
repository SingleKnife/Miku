package com.fyd.miku.model.pmd;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AllVertex {
    public static final int BYTE_SIZE_PER_VERTEX = 38;
    public static final int BYTE_SIZE_PER_INDEX = 2;
    public static final int COORDINATE_OFFSET = 0;
    public static final int NORMAL_OFFSET = 12;
    public static final int UV_OFFSET = 24;
    public static final int FIRST_BONE_INDEX_OFFSET = 32;
    public static final int SECOND_BONE_INDEX_OFFSET = 34;
    public static final int FIRST_BONE_WEIGHT_OFFSET = 36;
    public static final int EDGE_FLAG_OFFSET = 37;

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

    public ByteBuffer getVertices() {
        return vertices;
    }

    public ByteBuffer getIndices() {
        return indices;
    }
}

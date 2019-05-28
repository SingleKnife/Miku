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

    public static final int FLOAT_BYTE_SIZE = 4;            //一个float所占字节数

    private ByteBuffer vertices;
    private ByteBuffer indices;
    private ByteBuffer verticesUpdater; //使用单独的buffer来更新数据,避免position冲突

    public AllVertex() {

    }

    public void setVertices(byte[] bytes) {
        vertices = ByteBuffer.allocateDirect(bytes.length)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(bytes);
        vertices.position(0);
        verticesUpdater = vertices.duplicate()
                .order(ByteOrder.LITTLE_ENDIAN);
    }

    public void setIndices(byte[] bytes) {
        indices = ByteBuffer.allocateDirect(bytes.length)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(bytes);
    }

    public ByteBuffer getAllVertices() {
        vertices.position(0);
        return vertices;
    }

    public int getPositionByteOffset() {
        return COORDINATE_OFFSET;
    }

    public int getNormalByteOffset() {
        return NORMAL_OFFSET;
    }

    public int getUVByteOffset() {
        return UV_OFFSET;
    }

    public int getBoneIndexByteOffset() {
        return BONE_INDEX_OFFSET;
    }

    public int getBoneWeightAndEdgeFlagByteOffset() {
        return BONE_WEIGHT_OFFSET;
    }

    public int getIndicesByteOffset(int indexOffset) {
        return indexOffset * BYTE_SIZE_PER_INDEX;
    }

    public ByteBuffer getIndices() {
        indices.position(0);
        return indices;
    }

    /**
     *  根据顶点索引获取顶点坐标
     * @param vertexOffset 顶点偏移量
     */
    public float[] getPosValue(int vertexOffset) {
        float[] result = new float[3];
        vertices.position(BYTE_SIZE_PER_VERTEX * vertexOffset);
        result[0] = vertices.getFloat();
        result[1] = vertices.getFloat();
        result[2] = vertices.getFloat();
        vertices.position(0);

        return result;
    }

    /**
     * 更新定点坐标
     * @param vertexOffset   顶点偏移量
     * @param pos           新的顶点值
     */
    public void updatePosValue(int vertexOffset, float[] pos) {
        verticesUpdater.position(BYTE_SIZE_PER_VERTEX * vertexOffset);
        verticesUpdater.putFloat(pos[0]);   //x
        verticesUpdater.putFloat(pos[1]);   //y
        verticesUpdater.putFloat(pos[2]);   //z
        verticesUpdater.position(0);
    }

}

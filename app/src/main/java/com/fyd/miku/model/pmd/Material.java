package com.fyd.miku.model.pmd;

import android.text.TextUtils;

import com.fyd.miku.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Material implements Cloneable{
    public static final int MAX_BONE_SIZE = 50;
    public static final int DESIRED_BONE_SIZE = 40;

    static String[] toonNames = new String[] {
            "toon01.bmp", "toon02.bmp","toon03.bmp", "toon04.bmp","toon05.bmp",
            "toon06.bmp", "toon07.bmp","toon08.bmp", "toon09.bmp","toon10.bmp",
    };

    public static int[] toonRes = new int[] {
            R.drawable.toon01, R.drawable.toon02, R.drawable.toon03, R.drawable.toon04,R.drawable.toon05,
            R.drawable.toon06, R.drawable.toon07, R.drawable.toon08, R.drawable.toon09,R.drawable.toon10,
    };

    float[] diffuseColor;   //漫反射颜色 rgba
    float specularPower;    //材质光泽度
    float[] specularColor;  //镜面反射颜色 rgb
    float[] ambientColor;   //环境光颜色 rgb
    int toonIndex;          //toon纹理序号0-9，255表示没有纹理
    int edgeFlag;           //是否绘制材质边缘
    int vertexIndicesNum;   //顶点索引数量
    String textureName;     //纹理名称，如果有球面贴图(sphere map)的话， 纹理文件和球面贴图文件用*号分开，eg "tex0.bmp*sphere01.spa"
    String sphereMapName;   //球面贴图文件

    int vertexIndexOffset;  //顶点索引在所有顶点中的偏移量
    List<Short> boneIndexMapping;

    ByteBuffer boneIndexBuffer;

    Material() {
        diffuseColor = new float[4];
        specularColor = new float[3];
        ambientColor = new float[3];

        boneIndexMapping = new ArrayList<>();
    }

    public String getTextureName() {
        return textureName;
    }

    public String getSphereMapName() {
        return sphereMapName;
    }


    public int getToonTextureRes() {
        return toonIndex == 255 ? -1 : toonRes[toonIndex];
    }

    public float[] getDiffuseColor() {
        return diffuseColor;
    }

    public float getSpecularPower() {
        return specularPower;
    }

    public float[] getSpecularColor() {
        return specularColor;
    }

    public float[] getAmbientColor() {
        return ambientColor;
    }

    public int getToonIndex() {
        return toonIndex;
    }

    public int getVertexIndicesNum() {
        return vertexIndicesNum;
    }

    public int getVertexIndexOffset() {
        return vertexIndexOffset;
    }

    public void setVertexIndicesNum(int vertexIndicesNum) {
        this.vertexIndicesNum = vertexIndicesNum;
    }

    public void setVertexIndexOffset(int vertexIndexOffset) {
        this.vertexIndexOffset = vertexIndexOffset;
    }

    public boolean hasToonTexture() {
        return toonIndex != 255;
    }

    public boolean hasTexture() {
        return !TextUtils.isEmpty(textureName);
    }

    public List<Short> getBoneIndexMapping() {
        return boneIndexMapping;
    }

    public void addBone(Short boneIndex) {
        boneIndexMapping.add(boneIndex);
    }

    public int getRelativeBoneSize() {
        return boneIndexMapping.size();
    }

    public ByteBuffer getBoneIndexBuffer() {
        boneIndexBuffer.position(0);
        return boneIndexBuffer;
    }

    public void initBoneIndexBuffer(int vertexNum) {
        boneIndexBuffer = ByteBuffer.allocateDirect(vertexNum * 2)
                .order(ByteOrder.nativeOrder());
    }

    @Override
    public String toString() {
        return "Material{" +
                "diffuseColor=" + Arrays.toString(diffuseColor) +
                ", specularPower=" + specularPower +
                ", specularColor=" + Arrays.toString(specularColor) +
                ", ambientColor=" + Arrays.toString(ambientColor) +
                ", toonIndex=" + toonIndex +
                ", edgeFlag=" + edgeFlag +
                ", vertexIndicesNum=" + vertexIndicesNum +
                ", textureName='" + textureName + '\'' +
                ", sphereMapName='" + sphereMapName + '\'' +
                ", vertexIndexOffset=" + vertexIndexOffset +
                '}';
    }

    @Override
    public Material clone() {
        Material result = null;
        try {
            result = (Material) super.clone();
            result.boneIndexMapping = new ArrayList<>();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return result;
    }
}

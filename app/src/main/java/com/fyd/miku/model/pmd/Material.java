package com.fyd.miku.model.pmd;

import com.fyd.miku.R;

public class Material {
    static String[] toonNames = new String[] {
            "toon01.bmp", "toon02.bmp","toon03.bmp", "toon04.bmp","toon05.bmp",
            "toon06.bmp", "toon07.bmp","toon08.bmp", "toon09.bmp","toon10.bmp",
    };

    public static int[] toonRes = new int[] {
            R.drawable.toon01, R.drawable.toon02, R.drawable.toon03, R.drawable.toon04,R.drawable.toon05,
            R.drawable.toon06, R.drawable.toon07, R.drawable.toon08, R.drawable.toon09,R.drawable.toon10,
    };

    float[] diffuseColor;   //漫反射颜色 rgba
    float specularPower;        //材质光泽度
    float[] specularColor;  //镜面反射颜色 rgb
    float[] ambientColor;   //环境光颜色 rgb
    int toonIndex;            //toon纹理序号0-9，255表示没有纹理
    int edgeFlag;           //是否绘制材质边缘
    int vertexIndicesNum;         //顶点索引数量
    String textureName;     //纹理名称，如果有球面贴图(sphere map)的话， 纹理文件和球面贴图文件用*号分开，eg "tex0.bmp*sphere01.spa"
    String sphereMapName;   //球面贴图文件

    int vertexIndexOffset;        //顶点索引在所有顶点中的偏移量

    Material() {
        diffuseColor = new float[4];
        specularColor = new float[3];
        ambientColor = new float[3];
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

    public boolean hasToonTexture() {
        return toonIndex != 255;
    }


}

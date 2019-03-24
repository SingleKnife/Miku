package com.fyd.miku.model.pmd;

public class Material {
    static String[] toonNames = new String[] {
            "toon01.bmp", "toon02.bmp","toon03.bmp", "toon04.bmp","toon05.bmp",
            "toon06.bmp", "toon07.bmp","toon08.bmp", "toon09.bmp","toon10.bmp",
    };

    float[] diffuseColor;   //漫反射颜色 rgba
    float shininess;        //材质光泽度
    float[] specularColor;  //镜面反射颜色 rgb
    float[] ambientColor;   //环境光颜色 rgb
    byte toonNum;            //toon纹理序号0-9，255表示没有纹理
    byte edgeFlag;           //是否绘制材质边缘
    int vertexIndicesNum;         //顶点索引数量
    String textureName;     //纹理名称，如果有球面贴图(sphere map)的话， 纹理文件和球面贴图文件用*号分开，eg "tex0.bmp*sphere01.spa"

    int vertexIndexOffset;        //顶点索引在所有顶点中的偏移量

    Material() {
        diffuseColor = new float[4];
        specularColor = new float[3];
        ambientColor = new float[3];
    }
}

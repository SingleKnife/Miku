package com.fyd.miku.model.pmd;

public class PMDHeader {
    String magic;               //"Pmd"
    float version;              //pmd文件格式版本
    String modelName;           //模型
    String modelComment;        //模型说明

    String modelNameEnglish;    //模型英文名字
    String modelCommentEnglish; //英文说明
}

package com.fyd.miku.model.vmd;

public class VMDMorph {
    String morphName;
    int frame;
    float weight;

    public String getMorphName() {
        return morphName;
    }

    public int getFrame() {
        return frame;
    }

    public float getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "VMDMorph{" +
                "morphName='" + morphName + '\'' +
                ", frame=" + frame +
                ", weight=" + weight +
                '}';
    }
}

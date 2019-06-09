package com.fyd.miku.model.vmd;


import com.fyd.miku.io.ObjectBufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VMDFile {
    ObjectBufferedInputStream vmdStream;
    boolean parseSuccess = true;

    VMDHeader header;
    List<VMDMorph> morphs;
    List<VMDMotion> motions;

    public VMDFile() {
    }

    public void parse(File pmdFile){
        try {
            InputStream inputStream = new FileInputStream(pmdFile);
            parse(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parse(InputStream inputStream) {
        vmdStream = new ObjectBufferedInputStream(inputStream, ObjectBufferedInputStream.ByteOrder.LITTLE_ENDIAN);
        try {
            parseHeader();
            parseMotion();
            parseMorph();
            parseCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<VMDMorph> getMorphs() {
        return morphs;
    }

    public List<VMDMotion> getMotions() {
        return motions;
    }

    private void parseHeader() throws IOException {
        header = new VMDHeader();
        header.magic = vmdStream.readSJISString(30);
        if(!"Vocaloid Motion Data 0002".equals(header.magic)) {
            parseSuccess = false;
            throw new IOException("wrong file format");
        }
        header.modelName = vmdStream.readSJISString(20);
    }

    private void parseMotion() throws IOException {
        motions = new ArrayList<>();
        int num = vmdStream.readInt();
        if(num <= 0) {
            return;
        }
        for (int i = 0; i < num; i++) {
            VMDMotion motion = new VMDMotion();
            motion.boneName = vmdStream.readSJISString(15);
            motion.frame = vmdStream.readInt();
            vmdStream.readFloats(motion.boneTranslate, 0, 3);
            vmdStream.readFloats(motion.boneQuaternion, 0, 4);
            vmdStream.read(motion.interpolation, 0, motion.interpolation.length);
            motions.add(motion);
        }
    }

    private void parseMorph() throws IOException {
        morphs = new ArrayList<>();
        int num = vmdStream.readInt();
        if(num <= 0) {
            return;
        }
        for (int i = 0; i < num; i++) {
            VMDMorph morph = new VMDMorph();
            morph.morphName = vmdStream.readSJISString(15);
            morph.frame = vmdStream.readInt();
            morph.weight = vmdStream.readFloat();
            morphs.add(morph);
        }
    }

    private void parseCamera() throws IOException {
        int num = vmdStream.readInt();
    }
}

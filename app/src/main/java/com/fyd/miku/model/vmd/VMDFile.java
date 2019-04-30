package com.fyd.miku.model.vmd;

import android.util.Log;

import com.fyd.miku.io.ObjectBufferedInputStream;

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

    public void parse(InputStream inputStream) {
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
        Log.i("vmd", "modelName: " + header.modelName);
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
            vmdStream.readFloats(motion.boneTranslate);
            vmdStream.readFloats(motion.boneQuaternion);
            vmdStream.read(motion.interpolation, 0, motion.interpolation.length);
            Log.i("vmd", "vmdMotion: " + motion);
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
            Log.i("vmd", "morph: " + morph);
            morphs.add(morph);
        }
        Log.i("fyd", "avaliable: " + vmdStream.available());
    }

    private void parseCamera() throws IOException {
        int num = vmdStream.readInt();
        Log.i("vmd", "camera num: " + num);
    }
}

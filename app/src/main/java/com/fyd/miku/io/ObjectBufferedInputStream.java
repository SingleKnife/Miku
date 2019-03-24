package com.fyd.miku.io;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class ObjectBufferedInputStream extends BufferedInputStream {
    private ByteOrder byteOrder;

    public enum ByteOrder {
        BIG_ENDIAN,     //default
        LITTLE_ENDIAN
    }

    public ObjectBufferedInputStream(InputStream in, ByteOrder byteOrder) {
        super(in);
        this.byteOrder = byteOrder;
    }

    public short readShort() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        if(byteOrder == ByteOrder.LITTLE_ENDIAN) {
            return (short) ((ch2 << 8 ) + (ch1 << 0));
        } else {
            return (short) ((ch1 << 8) + (ch2 << 0));
        }
    }

    public int readInt() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        int ch3 = this.read();
        int ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        if(byteOrder == ByteOrder.LITTLE_ENDIAN) {
            return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
        } else {
            return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
        }
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public void readFloats(float[] des) throws IOException {
        for (int i = 0; i < des.length; ++i) {
            des[i] = readFloat();
        }
    }

    public synchronized byte readByte() throws IOException {
        return (byte) super.read();
    }

    /**
     *  读取Shift-JIS编码的字符串
     * @param byteNum 读取的字节长度
     * @return 结果字符串
     * @throws IOException
     */
    public String readSJISString(int byteNum) throws IOException{
        byte[] buffer = new byte[byteNum];
        if(byteNum > read(buffer, 0, byteNum)) {
            throw new EOFException("readByte SJIS string error.");
        }
        int strByteCount = byteNum;
        for(int i = 0; i < byteNum; ++i) {
            if(buffer[i] == '\0') {
                strByteCount = i;
                break;
            }
        }
        return new String(buffer, 0, strByteCount, "SJIS");
    }
}

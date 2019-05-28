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

    /**
     * See the general contract of the <code>readUnsignedByte</code>
     * method of <code>DataInput</code>.
     * <p>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @return     the next byte of this input stream, interpreted as an
     *             unsigned 8-bit number.
     * @exception  EOFException  if this input stream has reached the end.
     * @exception  IOException   the stream has been closed and the contained
     *             input stream does not support reading after close, or
     *             another I/O error occurs.
     * @see         java.io.FilterInputStream#in
     */
    public final int readUnsignedByte() throws IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return ch;
    }

    public int readUnsignedShort() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        if(byteOrder == ByteOrder.LITTLE_ENDIAN) {
            return (ch2 << 8 ) + (ch1 << 0);
        } else {
            return (ch1 << 8) + (ch2 << 0);
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

    /**
     *
     * @param des   目标数组
     * @param offset   目标数组偏移量
     * @param num   读取float数量
     * @throws IOException
     */
    public void readFloats(float[] des, int offset, int num) throws IOException {
        for (int i = 0; i < num; ++i) {
            des[i + offset] = readFloat();
        }
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
            throw new EOFException("read SJIS string error.");
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

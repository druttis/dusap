package org.dru.dusap.io;

import org.dru.dusap.data.DataUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.util.Objects;

public final class OutputStreamUtils {
    public static void writeBoolean(final OutputStream out, final boolean val) throws IOException {
        out.write(val ? 1 : 0);
    }

    public static void writeByte(final OutputStream out, final int val) throws IOException {
        out.write(val);
    }

    public static void writeShort(final OutputStream out, final int val) throws IOException {
        final byte[] temp = new byte[2];
        temp[0] = (byte) (val >>> 8);
        temp[1] = (byte) val;
        out.write(temp);
    }

    public static void writeChar(final OutputStream out, final int val) throws IOException {
        final byte[] temp = new byte[2];
        temp[0] = (byte) (val >>> 8);
        temp[1] = (byte) val;
        out.write(temp);
    }

    public static void writeInt(final OutputStream out, final int val) throws IOException {
        final byte[] temp = new byte[4];
        temp[0] = (byte) (val >>> 24);
        temp[1] = (byte) (val >>> 16);
        temp[2] = (byte) (val >>> 8);
        temp[3] = (byte) val;
        out.write(temp);
    }

    public static void writeLong(final OutputStream out, final long val) throws IOException {
        final byte[] temp = new byte[8];
        temp[0] = (byte) (val >>> 56);
        temp[1] = (byte) (val >>> 48);
        temp[2] = (byte) (val >>> 40);
        temp[3] = (byte) (val >>> 32);
        temp[4] = (byte) (val >>> 24);
        temp[5] = (byte) (val >>> 16);
        temp[6] = (byte) (val >>> 8);
        temp[7] = (byte) val;
        out.write(temp);
    }

    public static void writeFloat(final OutputStream out, final float val) throws IOException {
        writeInt(out, Float.floatToIntBits(val));
    }

    public static void writeDouble(final OutputStream out, final double val) throws IOException {
        writeLong(out, Double.doubleToLongBits(val));
    }

    public static void writeVarN(final OutputStream out, final int bits, final long val) throws IOException {
        long n = val;
        final int varLength = DataUtils.varLength(val, bits);
        final byte[] temp = new byte[varLength];
        for (int i = varLength; --i >= 0; ) {
            int b = (int) (n & 0x7f);
            if (i != (varLength - 1)) {
                b |= 0x80;
            }
            temp[i] = (byte) b;
            n >>>= 7;
        }
        out.write(temp);
    }

    public static void writeVarShort(final OutputStream out, final int val) throws IOException {
        writeVarN(out, 16, val);
    }

    public static void writeVarInt(final OutputStream out, final int val) throws IOException {
        writeVarN(out, 32, val);
    }

    public static void writeVarLong(final OutputStream out, final long val) throws IOException {
        writeVarN(out, 64, val);
    }

    public static void writeString(final OutputStream out, final String str) throws IOException {
        final int strLength = str.length();
        int utfLength = DataUtils.utfLength(str);
        if (utfLength < strLength || utfLength >= 65536) {
            throw new UTFDataFormatException();
        }
        writeVarShort(out, utfLength);
        final byte[] bytes = new byte[utfLength];
        int byteIndex = 0;
        int strIndex;
        char ch;
        for (strIndex = 0; strIndex < strLength; strIndex++) {
            ch = str.charAt(strIndex);
            if (ch >= 128 || ch == 0) {
                break;
            }
            bytes[byteIndex++] = (byte) ch;
        }
        for (; strIndex < strLength; strIndex++) {
            ch = str.charAt(strIndex);
            if (ch < 128 && ch != 0) {
                bytes[byteIndex++] = (byte) ch;
            } else if (ch >= 2048) {
                bytes[byteIndex++] = (byte) (224 | ch >> 12 & 15);
                bytes[byteIndex++] = (byte) (128 | ch >> 6 & 63);
                bytes[byteIndex++] = (byte) (128 | ch & 63);
            } else {
                bytes[byteIndex++] = (byte) (192 | ch >> 6 & 31);
                bytes[byteIndex++] = (byte) (128 | ch & 63);
            }
        }
        writeBytes(out, bytes);
    }

    public static void writeBooleans(final OutputStream out, final boolean[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            writeBoolean(out, buf[ofs + i]);
        }
    }

    public static void writeBooleans(final OutputStream out, final boolean[] buf) throws IOException {
        writeBooleans(out, buf, 0, buf.length);
    }

    public static void writeBytes(final OutputStream out, final byte[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        out.write(buf, ofs, len);
    }

    public static void writeBytes(final OutputStream out, final byte[] buf) throws IOException {
        writeBytes(out, buf, 0, buf.length);
    }

    public static void writeShorts(final OutputStream out, final short[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            writeShort(out, buf[ofs + i]);
        }
    }

    public static void writeShorts(final OutputStream out, final short[] buf) throws IOException {
        writeShorts(out, buf, 0, buf.length);
    }

    public static void writeChars(final OutputStream out, final char[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            writeChar(out, buf[ofs + i]);
        }
    }

    public static void writeChars(final OutputStream out, final char[] buf) throws IOException {
        writeChars(out, buf, 0, buf.length);
    }

    public static void writeInts(final OutputStream out, final int[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            writeInt(out, buf[ofs + i]);
        }
    }

    public static void writeInts(final OutputStream out, final int[] buf) throws IOException {
        writeInts(out, buf, 0, buf.length);
    }

    public static void writeLongs(final OutputStream out, final long[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            writeLong(out, buf[ofs + i]);
        }
    }

    public static void writeLongs(final OutputStream out, final long[] buf) throws IOException {
        writeLongs(out, buf, 0, buf.length);
    }

    public static void writeFloats(final OutputStream out, final float[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            writeFloat(out, buf[ofs + i]);
        }
    }

    public static void writeFloats(final OutputStream out, final float[] buf) throws IOException {
        writeFloats(out, buf, 0, buf.length);
    }

    public static void writeDoubles(final OutputStream out, final double[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            writeDouble(out, buf[ofs + i]);
        }
    }

    public static void writeDoubles(final OutputStream out, final double[] buf) throws IOException {
        writeDoubles(out, buf, 0, buf.length);
    }

    public static void writeBits(final OutputStream out, final boolean[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        final byte[] temp = new byte[DataUtils.bitsInBytes(len)];
        for (int i = 0; i < len; i++) {
            if (buf[i]) {
                temp[i >> 3] |= (byte) (1 << (7 - (i & 7)));
            }
        }
        out.write(temp);
    }

    public static void writeBits(final OutputStream out, final boolean[] buf) throws IOException {
        writeBits(out, buf, 0, buf.length);
    }

    public static void writeVarNs(final OutputStream out, final int bits, final long[] buf, final int ofs,
                                  final int len) throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            writeVarN(out, bits, buf[ofs + i]);
        }
    }

    public static void writeVarNs(final OutputStream out, final int bits, final long[] buf) throws IOException {
        writeVarNs(out, bits, buf, 0, buf.length);
    }

    public static void writeVarShorts(final OutputStream out, final short[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            writeVarShort(out, buf[ofs + i]);
        }
    }

    public static void writeVarShorts(final OutputStream out, final short[] buf) throws IOException {
        writeVarShorts(out, buf, 0, buf.length);
    }

    public static void writeVarInts(final OutputStream out, final int[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            writeVarInt(out, buf[ofs + i]);
        }
    }

    public static void writeVarInts(final OutputStream out, final int[] buf) throws IOException {
        writeVarInts(out, buf, 0, buf.length);
    }

    public static void writeVarLongs(final OutputStream out, final long[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            writeVarLong(out, buf[ofs + i]);
        }
    }

    public static void writeVarLongs(final OutputStream out, final long[] buf) throws IOException {
        writeVarLongs(out, buf, 0, buf.length);
    }

    public static void writeStrings(final OutputStream out, final String[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            writeString(out, buf[ofs + i]);
        }
    }

    public static void writeStrings(final OutputStream out, final String[] buf) throws IOException {
        writeStrings(out, buf, 0, buf.length);
    }

    private OutputStreamUtils() {
    }
}

package org.dru.dusap.io;

import org.dru.dusap.data.DataUtils;

import java.io.*;
import java.util.Objects;

public final class InputStreamUtils {
    public static boolean readBoolean(final InputStream in) throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (ch != 0);
    }

    public static byte readByte(final InputStream in) throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte) ch;
    }

    public static int readUnsignedByte(final InputStream in) throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch;
    }

    public static short readShort(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        } else {
            return (short) ((ch1 << 8) | ch2);
        }
    }

    public static int readUnsignedShort(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        } else {
            return ((ch1 << 8) | ch2);
        }
    }

    public static char readChar(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        } else {
            return (char) ((ch1 << 8) | ch2);
        }
    }

    public static int readInt(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        final int ch3 = in.read();
        final int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        } else {
            return (ch1 << 24) | (ch2 << 16) | (ch3 << 8) | ch4;
        }
    }

    public static long readUnsignedInt(final InputStream in) throws IOException {
        final long ch1 = in.read();
        final long ch2 = in.read();
        final long ch3 = in.read();
        final long ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        } else {
            return (ch1 << 24) | (ch2 << 16) | (ch3 << 8) | ch4;
        }
    }

    public static long readLong(final InputStream in) throws IOException {
        final byte[] temp = readBytes(in, 8);
        return ((long) temp[0] << 56)
                | ((long) (temp[1] & 255) << 48)
                | ((long) (temp[2] & 255) << 40)
                | ((long) (temp[3] & 255) << 32)
                | ((long) (temp[4] & 255) << 24)
                | (long) ((temp[5] & 255) << 16)
                | (long) ((temp[6] & 255) << 8)
                | (long) (temp[7] & 255);
    }

    public static float readFloat(final InputStream in) throws IOException {
        return Float.intBitsToFloat(readInt(in));
    }

    public static double readDouble(final InputStream in) throws IOException {
        return Double.longBitsToDouble(readLong(in));
    }

    public static long readVarN(final InputStream in, final int bits) throws IOException {
        if (bits < 0 || bits > 64) {
            throw new IllegalArgumentException("bits out of range: " + bits);
        }
        final int maxVarLength = DataUtils.varLength(bits);
        long value = 0;
        for (int count = 0; count < maxVarLength; count++) {
            final long ch = in.read();
            if (ch == -1) {
                throw new EOFException();
            }
            value = (value << 7) | (ch & 0x7f);
            if ((ch & 0x80) == 0) {
                return value;
            }
        }
        throw new NumberFormatException();
    }

    public static short readVarShort(final InputStream in) throws IOException {
        return (short) readVarN(in, 16);
    }

    public static int readUnsignedVarShort(final InputStream in) throws IOException {
        return (int) readVarN(in, 16) & 0xffff;
    }

    public static int readVarInt(final InputStream in) throws IOException {
        return (int) readVarN(in, 32);
    }

    public static long readUnsignedVarInt(final InputStream in) throws IOException {
        return readVarN(in, 32) & 0xffffffffL;
    }

    public static long readVarLong(final InputStream in) throws IOException {
        return readVarN(in, 64);
    }

    public static String readString(final InputStream in) throws IOException {
        final int utfLength = readUnsignedVarShort(in);
        final byte[] bytes = new byte[utfLength];
        final char[] chars = new char[utfLength];
        int byteIndex = 0;
        int charIndex = 0;
        readBytes(in, bytes, 0, utfLength);

        int c;
        while (byteIndex < utfLength) {
            c = bytes[byteIndex] & 255;
            if (c > 127) {
                break;
            }
            ++byteIndex;
            chars[charIndex++] = (char) c;
        }

        while (byteIndex < utfLength) {
            c = bytes[byteIndex] & 255;
            byte char2;
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    ++byteIndex;
                    chars[charIndex++] = (char) c;
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                default:
                    throw new UTFDataFormatException("malformed input around byte " + byteIndex);
                case 12:
                case 13:
                    byteIndex += 2;
                    if (byteIndex > utfLength) {
                        throw new UTFDataFormatException("malformed input: partial character at end");
                    }
                    char2 = bytes[byteIndex - 1];
                    if ((char2 & 192) != 128) {
                        throw new UTFDataFormatException("malformed input around byte " + byteIndex);
                    }
                    chars[charIndex++] = (char) ((c & 31) << 6 | char2 & 63);
                    break;
                case 14:
                    byteIndex += 3;
                    if (byteIndex > utfLength) {
                        throw new UTFDataFormatException("malformed input: partial character at end");
                    }

                    char2 = bytes[byteIndex - 2];
                    final int char3 = bytes[byteIndex - 1];
                    if ((char2 & 192) != 128 || (char3 & 192) != 128) {
                        throw new UTFDataFormatException("malformed input around byte " + (byteIndex - 1));
                    }
                    chars[charIndex++] = (char) ((c & 15) << 12 | (char2 & 63) << 6 | (char3 & 63));
            }
        }
        return new String(chars, 0, charIndex);
    }

    public static boolean[] readBooleans(final InputStream in, final boolean[] buf, final int ofs, final int len)
            throws IOException {
        final byte[] temp = readBytes(in, len);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = (temp[i] != 0);
        }
        return buf;
    }

    public static boolean[] readBooleans(final InputStream in, final boolean[] buf) throws IOException {
        return readBooleans(in, buf, 0, buf.length);
    }

    public static boolean[] readBooleans(final InputStream in, final int len) throws IOException {
        return readBooleans(in, new boolean[len]);
    }

    public static byte[] readBytes(final InputStream in, final byte[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        int numBytes;
        for (int i = 0; i < len; i += numBytes) {
            numBytes = in.read(buf, ofs + i, len - i);
            if (numBytes < 0) {
                throw new EOFException();
            }
        }
        return buf;
    }

    public static byte[] readBytes(final InputStream in, final byte[] buf) throws IOException {
        return readBytes(in, buf, 0, buf.length);
    }

    public static byte[] readBytes(final InputStream in, final int len) throws IOException {
        return readBytes(in, new byte[len]);
    }

    public static int[] readUnsignedBytes(final InputStream in, final int[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        final byte[] bytes = readBytes(in, len);
        for (int i = 0; i < len; i += 1) {
            buf[i] = bytes[i] & 0xff;
        }
        return buf;
    }

    public static int[] readUnsignedBytes(final InputStream in, final int[] buf) throws IOException {
        return readUnsignedBytes(in, buf, 0, buf.length);
    }

    public static int[] readUnsignedBytes(final InputStream in, final int len) throws IOException {
        return readUnsignedBytes(in, new int[len]);
    }

    public static short[] readShorts(final InputStream in, final short[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readShort(in);
        }
        return buf;
    }

    public static short[] readShorts(final InputStream in, final short[] buf) throws IOException {
        return readShorts(in, buf, 0, buf.length);
    }

    public static short[] readShorts(final InputStream in, final int len) throws IOException {
        return readShorts(in, new short[len]);
    }

    public static int[] readUnsignedShorts(final InputStream in, final int[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readUnsignedShort(in);
        }
        return buf;
    }

    public static int[] readUnsignedShorts(final InputStream in, final int[] buf) throws IOException {
        return readUnsignedShorts(in, buf, 0, buf.length);
    }

    public static int[] readUnsignedShorts(final InputStream in, final int len) throws IOException {
        return readUnsignedShorts(in, new int[len]);
    }

    public static char[] readChars(final InputStream in, final char[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readChar(in);
        }
        return buf;
    }

    public static char[] readChars(final InputStream in, final char[] buf) throws IOException {
        return readChars(in, buf, 0, buf.length);
    }

    public static char[] readChars(final InputStream in, final int len) throws IOException {
        return readChars(in, new char[len]);
    }

    public static int[] readInts(final InputStream in, final int[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readInt(in);
        }
        return buf;
    }

    public static int[] readInts(final InputStream in, final int[] buf) throws IOException {
        return readInts(in, buf, 0, buf.length);
    }

    public static int[] readInts(final InputStream in, final int len) throws IOException {
        return readInts(in, new int[len]);
    }

    public static long[] readUnsignedInts(final InputStream in, final long[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readUnsignedInt(in);
        }
        return buf;
    }

    public static long[] readUnsignedInts(final InputStream in, final long[] buf) throws IOException {
        return readUnsignedInts(in, buf, 0, buf.length);
    }

    public static long[] readUnsignedInts(final InputStream in, final int len) throws IOException {
        return readUnsignedInts(in, new long[len]);
    }

    public static long[] readLongs(final InputStream in, final long[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readLong(in);
        }
        return buf;
    }

    public static long[] readLongs(final InputStream in, final long[] buf) throws IOException {
        return readLongs(in, buf, 0, buf.length);
    }

    public static long[] readLongs(final InputStream in, final int len) throws IOException {
        return readLongs(in, new long[len]);
    }

    public static float[] readFloats(final InputStream in, final float[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readFloat(in);
        }
        return buf;
    }

    public static float[] readFloats(final InputStream in, final float[] buf) throws IOException {
        return readFloats(in, buf, 0, buf.length);
    }

    public static float[] readFloats(final InputStream in, final int len) throws IOException {
        return readFloats(in, new float[len]);
    }

    public static double[] readDoubles(final InputStream in, final double[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readDouble(in);
        }
        return buf;
    }

    public static double[] readDoubles(final InputStream in, final double[] buf) throws IOException {
        return readDoubles(in, buf, 0, buf.length);
    }

    public static double[] readDoubles(final InputStream in, final int len) throws IOException {
        return readDoubles(in, new double[len]);
    }

    public static boolean[] readBits(final InputStream in, final boolean[] buf, final int ofs, final int len)
            throws IOException {
        final int byteLen = (len + 7) >> 3;
        final byte[] temp = readBytes(in, byteLen);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = (temp[i >> 3] & 1 << (7 - (i & 7))) != 0;
        }
        return buf;
    }

    public static boolean[] readBits(final InputStream in, final boolean[] buf) throws IOException {
        return readBits(in, buf, 0, buf.length);
    }

    public static boolean[] readBits(final InputStream in, final int len) throws IOException {
        return readBits(in, new boolean[len]);
    }

    public static long[] readVarNs(final InputStream in, final int bits, final long[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readVarN(in, bits);
        }
        return buf;
    }

    public static long[] readVarNs(final InputStream in, final int bits, final long[] buf) throws IOException {
        return readVarNs(in, bits, buf, 0, buf.length);
    }

    public static long[] readVarNs(final InputStream in, final int bits, final int len) throws IOException {
        return readVarNs(in, bits, new long[len]);
    }

    public static short[] readVarShorts(final InputStream in, final short[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readVarShort(in);
        }
        return buf;
    }

    public static short[] readVarShorts(final InputStream in, final short[] buf) throws IOException {
        return readVarShorts(in, buf, 0, buf.length);
    }

    public static short[] readVarShorts(final InputStream in, final int len) throws IOException {
        return readVarShorts(in, new short[len]);
    }

    public static int[] readUnsignedVarShorts(final InputStream in, final int[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readUnsignedVarShort(in);
        }
        return buf;
    }

    public static int[] readUnsignedVarShorts(final InputStream in, final int[] buf) throws IOException {
        return readUnsignedVarShorts(in, buf, 0, buf.length);
    }

    public static int[] readUnsignedVarShorts(final InputStream in, final int len) throws IOException {
        return readUnsignedVarShorts(in, new int[len]);
    }

    public static int[] readVarInts(final InputStream in, final int[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readVarInt(in);
        }
        return buf;
    }

    public static int[] readVarInts(final InputStream in, final int[] buf) throws IOException {
        return readVarInts(in, buf, 0, buf.length);
    }

    public static int[] readVarInts(final InputStream in, final int len) throws IOException {
        return readVarInts(in, new int[len]);
    }

    public static long[] readUnsignedVarInts(final InputStream in, final long[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readUnsignedVarInt(in);
        }
        return buf;
    }

    public static long[] readUnsignedVarInts(final InputStream in, final long[] buf) throws IOException {
        return readUnsignedVarInts(in, buf, 0, buf.length);
    }

    public static long[] readUnsignedVarInts(final InputStream in, final int len) throws IOException {
        return readUnsignedVarInts(in, new long[len]);
    }

    public static long[] readVarLongs(final InputStream in, final long[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readVarLong(in);
        }
        return buf;
    }

    public static long[] readVarLongs(final InputStream in, final long[] buf) throws IOException {
        return readVarLongs(in, buf, 0, buf.length);
    }

    public static long[] readVarLongs(final InputStream in, final int len) throws IOException {
        return readVarLongs(in, new long[len]);
    }

    public static String[] readStrings(final InputStream in, final String[] buf, final int ofs, final int len)
            throws IOException {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        for (int i = 0; i < len; i++) {
            buf[ofs + i] = readString(in);
        }
        return buf;
    }

    public static String[] readStrings(final InputStream in, final String[] buf) throws IOException {
        return readStrings(in, buf, 0, buf.length);
    }

    public static String[] readStrings(final InputStream in, final int len) throws IOException {
        return readStrings(in, new String[len]);
    }

    private InputStreamUtils() {
    }
}

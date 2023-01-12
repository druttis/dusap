package org.dru.dusap.util;

import java.nio.InvalidMarkException;
import java.util.Objects;

public final class ByteArray {
    private static int idx(final int[] pos, final int size, final int end) {
        final int index = pos[0];
        Objects.checkFromIndexSize(index, size, end);
        pos[0] += size;
        return index;
    }

    public static int getNumBitsInBytes(final int bits) {
        return (bits + 7) >> 3;
    }

    public static int getBytesInNumBits(final int bytes) {
        return bytes << 3;
    }

    public static int getVarLengthInBytes(final int bits) {
        return Math.max(1, (bits + 6) / 7);
    }

    public static int getVarLengthInBytes(final int bits, final long val) {
        final long mask = (bits < 64 ? (1L << bits) - 1L : -1L);
        return getVarLengthInBytes(64 - Long.numberOfLeadingZeros(val & mask));
    }

    public static boolean getBoolean(final byte[] buf, final int idx) {
        return (buf[idx] != 0);
    }

    public static boolean getBoolean(final byte[] buf, final int[] pos, final int end) {
        return getBoolean(buf, idx(pos, 1, end));
    }

    public static byte getByte(final byte[] buf, final int idx) {
        return buf[idx];
    }

    public static byte getByte(final byte[] buf, final int[] pos, final int end) {
        return getByte(buf, idx(pos, 1, end));
    }

    public static int getUnsignedByte(final byte[] buf, final int idx) {
        return buf[idx] & 255;
    }

    public static int getUnsignedByte(final byte[] buf, final int[] pos, final int end) {
        return getUnsignedByte(buf, idx(pos, 1, end));
    }

    public static char getChar(final byte[] buf, final int idx) {
        return (char) ((buf[idx + 1] & 255)
                | (buf[idx] << 8));
    }

    public static char getChar(final byte[] buf, final int[] pos, final int end) {
        return getChar(buf, idx(pos, 2, end));
    }

    public static short getShort(final byte[] buf, final int idx) {
        return (short) ((buf[idx + 1] & 255)
                | (buf[idx] << 8));
    }

    public static short getShort(final byte[] buf, final int[] pos, final int end) {
        return getShort(buf, idx(pos, 2, end));
    }

    public static int getUnsignedShort(final byte[] buf, final int idx) {
        return getShort(buf, idx) & 65535;
    }

    public static int getUnsignedShort(final byte[] buf, final int[] pos, final int end) {
        return getUnsignedShort(buf, idx(pos, 2, end));
    }

    public static int getInt(final byte[] buf, final int idx) {
        return (buf[idx + 3] & 255)
                | ((buf[idx + 2] & 255) << 8)
                | ((buf[idx + 1] & 255) << 16)
                | (buf[idx] << 24);
    }

    public static int getInt(final byte[] buf, final int[] pos, final int end) {
        return getInt(buf, idx(pos, 4, end));
    }

    public static long getUnsignedInt(final byte[] buf, final int idx) {
        return getInt(buf, idx) & 4294967295L;
    }

    public static long getUnsignedInt(final byte[] buf, final int[] pos, final int end) {
        return getUnsignedInt(buf, idx(pos, 4, end));
    }

    public static long getLong(final byte[] buf, final int idx) {
        return ((long) buf[idx + 7] & 255L)
                | (((long) buf[idx + 6] & 255L) << 8)
                | (((long) buf[idx + 5] & 255L) << 16)
                | (((long) buf[idx + 4] & 255L) << 24)
                | (((long) buf[idx + 3] & 255L) << 32)
                | (((long) buf[idx + 2] & 255L) << 40)
                | (((long) buf[idx + 1] & 255L) << 48)
                | ((long) buf[idx] << 56);
    }

    public static long getLong(final byte[] buf, final int[] pos, final int end) {
        return getLong(buf, idx(pos, 8, end));
    }

    public static float getFloat(final byte[] buf, final int idx) {
        return Float.intBitsToFloat(getInt(buf, idx));
    }

    public static float getFloat(final byte[] buf, final int[] pos, final int end) {
        return getFloat(buf, idx(pos, 4, end));
    }

    public static double getDouble(final byte[] buf, final int idx) {
        return Double.longBitsToDouble(getLong(buf, idx));
    }

    public static double getDouble(final byte[] buf, final int[] pos, final int end) {
        return getDouble(buf, idx(pos, 8, end));
    }

    public static long getVarNum(final byte[] buf, final int[] pos, final int end, final int bits) {
        final int beg = pos[0];
        final int max = getVarLengthInBytes(bits);
        long val = 0;
        for (int cnt = 0; cnt < max; cnt++) {
            final int idx = beg + cnt;
            if (idx >= end) {
                throw new ArrayIndexOutOfBoundsException();
            }
            final long ch = buf[idx] & 255;
            val = (val << 7) | (ch & 127);
            if ((ch & 128) == 0) {
                pos[0] = idx + cnt + 1;
                return val;
            }
        }
        throw new NumberFormatException();
    }

    public static short getVarShort(final byte[] buf, final int[] pos, final int end) {
        return (short) getVarNum(buf, pos, end, 16);
    }

    public static int getUnsignedVarShort(final byte[] buf, final int[] pos, final int end) {
        return (int) getVarNum(buf, pos, end, 16) & 65535;
    }

    public static int getVarInt(final byte[] buf, final int[] pos, final int end) {
        return (int) getVarNum(buf, pos, end, 32);
    }

    public static long getUnsignedVarInt(final byte[] buf, final int[] pos, final int end) {
        return getVarNum(buf, pos, end, 32) & 4294967295L;
    }

    public static long getVarLong(final byte[] buf, final int[] pos, final int end) {
        return getVarNum(buf, pos, end, 64);
    }

    public static boolean[] getBits(final byte[] buf, final int[] pos, final int end,
                                    final boolean[] dst, final int ofs, final int len) {
        Objects.checkFromIndexSize(ofs, len, dst.length);
        final int idx = idx(pos, getNumBitsInBytes(len), end);
        for (int cnt = 0; cnt < len; cnt++) {
            dst[ofs + cnt] = getBoolean(buf, idx + cnt);
        }
        return dst;
    }

    public static boolean[] getBits(final byte[] buf, final int[] pos, final int end,
                                    final boolean[] dst) {
        return getBits(buf, pos, end, dst, 0, dst.length);
    }

    public static boolean[] getBits(final byte[] buf, final int[] pos, final int end,
                                    final int len) {
        return getBits(buf, pos, end, new boolean[len], 0, len);
    }

    private static int getUnsignedByte(final byte[] buf, final int pos, final int end) {
        Objects.checkFromIndexSize(pos, 1, end);
        return buf[pos] & 255;
    }

    public static void putBoolean(final byte[] buf, final int[] pos, final int end, final boolean val) {
        buf[idx(pos, 1, end)] = (val ? (byte) 1 : 0);
    }

    public static void putByte(final byte[] buf, final int[] pos, final int end, final int val) {
        buf[idx(pos, 1, end)] = (byte) val;
    }

    public static void putChar(final byte[] buf, final int[] pos, final int end, final int val) {
        final int idx = idx(pos, 2, end);
        buf[idx + 1] = (byte) val;
        buf[idx] = (byte) (val >>> 8);
    }

    public static void putShort(final byte[] buf, final int[] pos, final int end, final int val) {
        final int idx = idx(pos, 2, end);
        buf[idx + 1] = (byte) val;
        buf[idx] = (byte) (val >>> 8);
    }

    public static void putInt(final byte[] buf, final int[] pos, final int end, final int val) {
        final int idx = idx(pos, 4, end);
        buf[idx + 3] = (byte) val;
        buf[idx + 2] = (byte) (val >>> 8);
        buf[idx + 1] = (byte) (val >>> 16);
        buf[idx] = (byte) (val >>> 24);
    }

    public static void putLong(final byte[] buf, final int[] pos, final int end, final long val) {
        final int idx = idx(pos, 8, end);
        buf[idx + 7] = (byte) val;
        buf[idx + 6] = (byte) (val >>> 8);
        buf[idx + 5] = (byte) (val >>> 16);
        buf[idx + 4] = (byte) (val >>> 24);
        buf[idx + 3] = (byte) (val >>> 32);
        buf[idx + 2] = (byte) (val >>> 40);
        buf[idx + 1] = (byte) (val >>> 48);
        buf[idx] = (byte) (val >>> 56);
    }

    public static void putFloat(final byte[] buf, final int[] pos, final int end, final float val) {
        putInt(buf, pos, end, Float.floatToIntBits(val));
    }

    public static void putDouble(final byte[] buf, final int[] pos, final int end, final double val) {
        putLong(buf, pos, end, Double.doubleToLongBits(val));
    }

    public static void putVarNum(final byte[] buf, final int[] pos, final int end, final int bits, long val) {
        final int len = getVarLengthInBytes(bits, val);
        final int idx = idx(pos, len, end);
        for (int cnt = len; --cnt >= 0; ) {
            int tmp = (int) (val & 127);
            if (cnt != (len - 1)) {
                tmp |= 128;
            }
            buf[idx + cnt] = (byte) tmp;
            val >>>= 7;
        }
    }

    public static void putVarShort(final byte[] buf, final int[] pos, final int end, final int val) {
        putVarNum(buf, pos, end, 16, val);
    }

    public static void putVarInt(final byte[] buf, final int[] pos, final int end, final int val) {
        putVarNum(buf, pos, end, 32, val);
    }

    public static void putVarLong(final byte[] buf, final int[] pos, final int end, final long val) {
        putVarNum(buf, pos, end, 64, val);
    }

    public static ByteArray wrap(final byte[] buf, final int ofs, final int len) {
        return new ByteArray(buf, ofs, len);
    }

    public static ByteArray wrap(final byte[] buf) {
        return new ByteArray(buf, 0, buf.length);
    }

    public static ByteArray create(final int len) {
        return new ByteArray(new byte[len], 0, len);
    }

    private final byte[] buf;
    private final int ofs;
    private final int len;
    private int lim;
    private int pos;
    private int mark;

    private ByteArray(final byte[] buf, final int ofs, final int len) {
        Objects.checkFromIndexSize(ofs, len, buf.length);
        this.buf = buf;
        this.ofs = ofs;
        this.len = len;
        lim = len;
        mark = -1;
    }

    public ByteArray mark() {
        mark = pos;
        return this;
    }

    public ByteArray reset() {
        if (mark == -1) {
            throw new InvalidMarkException();
        }
        pos = mark;
        mark = -1;
        return this;
    }

    public ByteArray clear() {
        pos = 0;
        lim = len;
        mark = -1;
        return this;
    }

    public ByteArray flip() {
        lim = pos;
        pos = 0;
        mark = -1;
        return this;
    }

    public ByteArray rewind() {
        pos = 0;
        mark = -1;
        return this;
    }

    public int remaining() {
        return (lim - pos);
    }

    public boolean hasRemaining() {
        return (pos < lim);
    }

    public boolean getBoolean(final int pos) {
        final int[] tmp = {ofs + pos};
        return getBoolean(buf, tmp, lim);
    }

    public boolean getBoolean() {
        final int[] tmp = {ofs + pos};
        final boolean res = getBoolean(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public byte getByte(final int pos) {
        final int[] tmp = {ofs + pos};
        return getByte(buf, tmp, lim);
    }

    public byte getByte() {
        final int[] tmp = {ofs + pos};
        final byte res = getByte(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public int getUnsignedByte(final int pos) {
        final int[] tmp = {ofs + pos};
        return getUnsignedByte(buf, tmp, lim);
    }

    public int getUnsignedByte() {
        final int[] tmp = {ofs + pos};
        final int res = getUnsignedByte(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public char getChar(final int pos) {
        final int[] tmp = {ofs + pos};
        return getChar(buf, tmp, lim);
    }

    public char getChar() {
        final int[] tmp = {ofs + pos};
        final char res = getChar(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public short getShort(final int pos) {
        final int[] tmp = {ofs + pos};
        return getShort(buf, tmp, lim);
    }

    public short getShort() {
        final int[] tmp = {ofs + pos};
        final short res = getShort(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public int getUnsignedShort(final int pos) {
        final int[] tmp = {ofs + pos};
        return getUnsignedShort(buf, tmp, lim);
    }

    public int getUnsignedShort() {
        final int[] tmp = {ofs + pos};
        final int res = getUnsignedShort(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public int getInt(final int pos) {
        final int[] tmp = {ofs + pos};
        return getInt(buf, tmp, lim);
    }

    public int getInt() {
        final int[] tmp = {ofs + pos};
        final int res = getInt(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public long getUnsignedInt(final int pos) {
        final int[] tmp = {ofs + pos};
        return getUnsignedInt(buf, tmp, lim);
    }

    public long getUnsignedInt() {
        final int[] tmp = {ofs + pos};
        final long res = getUnsignedInt(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public long getLong(final int pos) {
        final int[] tmp = {ofs + pos};
        return getLong(buf, tmp, lim);
    }

    public long getLong() {
        final int[] tmp = {ofs + pos};
        final long res = getLong(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public float getFloat(final int pos) {
        final int[] tmp = {ofs + pos};
        return getFloat(buf, tmp, lim);
    }

    public float getFloat() {
        final int[] tmp = {ofs + pos};
        final float res = getFloat(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public double getDouble(final int pos) {
        final int[] tmp = {ofs + pos};
        return getDouble(buf, tmp, lim);
    }

    public double getDouble() {
        final int[] tmp = {ofs + pos};
        final double res = getDouble(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public long getVarNum(final int pos, final int bits) {
        final int[] tmp = {ofs + pos};
        return getVarNum(buf, tmp, lim, bits);
    }

    public long getVarNum(final int bits) {
        final int[] tmp = {ofs + pos};
        final long res = getVarNum(buf, tmp, lim, bits);
        pos = tmp[0];
        return res;
    }

    public short getVarShort(final int pos) {
        final int[] tmp = {ofs + pos};
        return getVarShort(buf, tmp, lim);
    }

    public short getVarShort() {
        final int[] tmp = {ofs + pos};
        final short res = getVarShort(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public int getUnsignedVarShort(final int pos) {
        final int[] tmp = {ofs + pos};
        return getUnsignedVarShort(buf, tmp, lim);
    }

    public int getUnsignedVarShort() {
        final int[] tmp = {ofs + pos};
        final int res = getUnsignedVarShort(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public int getVarInt(final int pos) {
        final int[] tmp = {ofs + pos};
        return getVarInt(buf, tmp, lim);
    }

    public int getVarInt() {
        final int[] tmp = {ofs + pos};
        final int res = getVarInt(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public long getUnsignedVarInt(final int pos) {
        final int[] tmp = {ofs + pos};
        return getUnsignedVarInt(buf, tmp, lim);
    }

    public long getUnsignedVarInt() {
        final int[] tmp = {ofs + pos};
        final long res = getUnsignedVarInt(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public long getVarLong(final int pos) {
        final int[] tmp = {ofs + pos};
        return getVarLong(buf, tmp, lim);
    }

    public long getVarLong() {
        final int[] tmp = {ofs + pos};
        final long res = getVarLong(buf, tmp, lim);
        pos = tmp[0];
        return res;
    }

    public void putBoolean(final int pos, final boolean val) {
        final int[] tmp = {ofs + pos};
        putBoolean(buf, tmp, lim, val);
    }

    public void putBoolean(final boolean val) {
        final int[] tmp = {ofs + pos};
        putBoolean(buf, tmp, lim, val);
        pos = tmp[0];
    }

    public void putByte(final int pos, final int val) {
        final int[] tmp = {ofs + pos};
        putByte(buf, tmp, lim, val);
    }

    public void putByte(final int val) {
        final int[] tmp = {ofs + pos};
        putByte(buf, tmp, lim, val);
        pos = tmp[0];
    }

    public void putChar(final int pos, final int val) {
        final int[] tmp = {ofs + pos};
        putChar(buf, tmp, lim, val);
    }

    public void putChar(final int val) {
        final int[] tmp = {ofs + pos};
        putChar(buf, tmp, lim, val);
        pos = tmp[0];
    }

    public void putShort(final int pos, final int val) {
        final int[] tmp = {ofs + pos};
        putShort(buf, tmp, lim, val);
    }

    public void putShort(final int val) {
        final int[] tmp = {ofs + pos};
        putShort(buf, tmp, lim, val);
        pos = tmp[0];
    }

    public void putInt(final int pos, final int val) {
        final int[] tmp = {ofs + pos};
        putInt(buf, tmp, lim, val);
    }

    public void putInt(final int val) {
        final int[] tmp = {ofs + pos};
        putInt(buf, tmp, lim, val);
        pos = tmp[0];
    }

    public void putLong(final int pos, final long val) {
        final int[] tmp = {ofs + pos};
        putLong(buf, tmp, lim, val);
    }

    public void putLong(final long val) {
        final int[] tmp = {ofs + pos};
        putLong(buf, tmp, lim, val);
        pos = tmp[0];
    }

    public void putFloat(final int pos, final float val) {
        final int[] tmp = {ofs + pos};
        putFloat(buf, tmp, lim, val);
    }

    public void putFloat(final float val) {
        final int[] tmp = {ofs + pos};
        putFloat(buf, tmp, lim, val);
        pos = tmp[0];
    }

    public void putDouble(final int pos, final double val) {
        final int[] tmp = {ofs + pos};
        putDouble(buf, tmp, lim, val);
    }

    public void putDouble(final double val) {
        final int[] tmp = {ofs + pos};
        putDouble(buf, tmp, lim, val);
        pos = tmp[0];
    }

    public void putVarNum(final int pos, final int bits, final long val) {
        final int[] tmp = {ofs + pos};
        putVarNum(buf, tmp, lim, bits, val);
    }

    public void putVarNum(final int bits, final long val) {
        final int[] tmp = {ofs + pos};
        putVarNum(buf, tmp, lim, bits, val);
        pos = tmp[0];
    }

    public void putVarShort(final int pos, final int val) {
        final int[] tmp = {ofs + pos};
        putVarShort(buf, tmp, lim, val);
    }

    public void putVarShort(final int val) {
        final int[] tmp = {ofs + pos};
        putVarShort(buf, tmp, lim, val);
        pos = tmp[0];
    }

    public void putVarInt(final int pos, final int val) {
        final int[] tmp = {ofs + pos};
        putVarInt(buf, tmp, lim, val);
    }

    public void putVarInt(final int val) {
        final int[] tmp = {ofs + pos};
        putVarInt(buf, tmp, lim, val);
        pos = tmp[0];
    }

    public void putVarLong(final int pos, final long val) {
        final int[] tmp = {ofs + pos};
        putVarLong(buf, tmp, lim, val);
    }

    public void putVarShort(final long val) {
        final int[] tmp = {ofs + pos};
        putVarLong(buf, tmp, lim, val);
        pos = tmp[0];
    }
}

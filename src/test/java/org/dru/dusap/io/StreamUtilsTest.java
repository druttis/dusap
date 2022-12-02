package org.dru.dusap.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.dru.dusap.io.InputStreamUtils.*;
import static org.dru.dusap.io.OutputStreamUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class StreamUtilsTest {
    private ByteArrayOutputStream out;

    private ByteArrayInputStream createIn() {
        return new ByteArrayInputStream(out.toByteArray());
    }

    @BeforeEach
    public void setup() {
        out = new ByteArrayOutputStream();
    }

    @Test
    public void testBoolean() throws IOException {
        writeBoolean(out, true);
        final ByteArrayInputStream in = createIn();
        assertTrue(readBoolean(in));
    }

    @Test
    public void testByte() throws IOException {
        writeByte(out, -1);
        final ByteArrayInputStream in = createIn();
        assertEquals(-1, readByte(in));
    }

    @Test
    public void testUnsignedByte() throws IOException {
        writeByte(out, (1 << 8) - 1);
        final ByteArrayInputStream in = createIn();
        assertEquals((1 << 8) - 1, readUnsignedByte(in));
    }

    @Test
    public void testShort() throws IOException {
        writeShort(out, -1);
        final ByteArrayInputStream in = createIn();
        assertEquals(-1, readShort(in));
    }

    @Test
    public void testUnsignedShort() throws IOException {
        writeShort(out, (1 << 16) - 1);
        final ByteArrayInputStream in = createIn();
        assertEquals((1 << 16) - 1, readUnsignedShort(in));
    }

    @Test
    public void testChar() throws IOException {
        writeChar(out, 'A');
        final ByteArrayInputStream in = createIn();
        assertEquals('A', readChar(in));
    }

    @Test
    public void testInt() throws IOException {
        writeInt(out, -1);
        final ByteArrayInputStream in = createIn();
        assertEquals(-1, readInt(in));
    }

    @Test
    public void testUnsignedInt() throws IOException {
        writeInt(out, (int) ((1L << 32) - 1L));
        final ByteArrayInputStream in = createIn();
        assertEquals((1L << 32) - 1L, readUnsignedInt(in));
    }

    @Test
    public void testLong() throws IOException {
        writeLong(out, -1L);
        final ByteArrayInputStream in = createIn();
        assertEquals(-1L, readLong(in));
    }

    @Test
    public void testFloat() throws IOException {
        writeFloat(out, 1.5f);
        final ByteArrayInputStream in = createIn();
        assertEquals(1.5f, readFloat(in));
    }

    @Test
    public void testDouble() throws IOException {
        writeDouble(out, 1.5);
        final ByteArrayInputStream in = createIn();
        assertEquals(1.5, readDouble(in));
    }

    @Test
    public void testVarShort() throws IOException {
        writeVarShort(out, (1 << 16) - 1);
        final ByteArrayInputStream in = createIn();
        assertEquals((short) ((1 << 16) - 1), readVarShort(in));
    }

    @Test
    public void testUnsignedVarShort() throws IOException {
        writeVarShort(out, (1 << 16) - 1);
        final ByteArrayInputStream in = createIn();
        assertEquals((1 << 16) - 1, readUnsignedVarShort(in));
    }

    @Test
    public void testVarInt() throws IOException {
        writeVarInt(out, (int) ((1L << 32) - 1));
        final ByteArrayInputStream in = createIn();
        assertEquals((int) ((1L << 32) - 1), readVarInt(in));
    }

    @Test
    public void testUnsignedVarInt() throws IOException {
        writeVarInt(out, (int) ((1L << 32) - 1));
        final ByteArrayInputStream in = createIn();
        assertEquals((1L << 32) - 1, readUnsignedVarInt(in));
    }

    @Test
    public void testVarLong() throws IOException {
        writeVarLong(out, -1);
        final ByteArrayInputStream in = createIn();
        assertEquals(-1L, readVarLong(in));
    }

    @Test
    public void testString() throws IOException {
        writeString(out, "arne");
        final ByteArrayInputStream in = createIn();
        assertEquals("arne", readString(in));
    }

    @Test
    public void testBooleans() throws IOException {
        final boolean[] values = {true, false, true, true};
        writeBooleans(out, values);
        final ByteArrayInputStream in = createIn();
        assertArrayEquals(values, readBooleans(in, values.length));
    }

    @Test
    public void testBytes() throws IOException {
        final byte[] values = {1, -2, 3, -4};
        writeBytes(out, values);
        final ByteArrayInputStream in = createIn();
        assertArrayEquals(values, readBytes(in, values.length));
    }

    @Test
    public void testUnsignedBytes() throws IOException {
        final byte[] values = {1, -2, 3, -4};
        writeBytes(out, values);
        final ByteArrayInputStream in = createIn();
        final int[] unsigned = {1, 254, 3, 252};
        assertArrayEquals(unsigned, readUnsignedBytes(in, values.length));
    }

    @Test
    public void testShorts() throws IOException {
        final short[] values = {1, -2, 3, -4};
        writeShorts(out, values);
        final ByteArrayInputStream in = createIn();
        assertArrayEquals(values, readShorts(in, values.length));
    }

    @Test
    public void testUnsignedShorts() throws IOException {
        final short[] values = {1, -2, 3, -4};
        writeShorts(out, values);
        final ByteArrayInputStream in = createIn();
        final int[] unsigned = {1, 65534, 3, 65532};
        assertArrayEquals(unsigned, readUnsignedShorts(in, values.length));
    }

    @Test
    public void testChars() throws IOException {
        final char[] values = {'A', 'B', 'C', 'D'};
        writeChars(out, values);
        final ByteArrayInputStream in = createIn();
        assertArrayEquals(values, readChars(in, values.length));
    }

    @Test
    public void testInts() throws IOException {
        final int[] values = {1, -2, 3, -4};
        writeInts(out, values);
        final ByteArrayInputStream in = createIn();
        assertArrayEquals(values, readInts(in, values.length));
    }

    @Test
    public void testUnsignedInits() throws IOException {
        final int[] values = {1, -2, 3, -4};
        writeInts(out, values);
        final ByteArrayInputStream in = createIn();
        final long[] unsigned = {1, 4294967294L, 3, 4294967292L};
        assertArrayEquals(unsigned, readUnsignedInts(in, values.length));
    }

    @Test
    public void testLongs() throws IOException {
        final long[] values = {1, -2, 3, -4};
        writeLongs(out, values);
        final ByteArrayInputStream in = createIn();
        assertArrayEquals(values, readLongs(in, values.length));
    }

    @Test
    public void testFloats() throws IOException {
        final float[] values = {1, -2.5f, 3, 4.5f};
        writeFloats(out, values);
        final ByteArrayInputStream in = createIn();
        assertArrayEquals(values, readFloats(in, values.length));
    }

    @Test
    public void testDoubles() throws IOException {
        final double[] values = {1, -2.5f, 3, 4.5f};
        writeDoubles(out, values);
        final ByteArrayInputStream in = createIn();
        assertArrayEquals(values, readDoubles(in, values.length));
    }

    @Test
    public void testBits() throws IOException {
        final boolean[] values = {true, false, true, true};
        writeBits(out, values);
        assertEquals(1, out.size());
        final ByteArrayInputStream in = createIn();
        assertArrayEquals(values, readBits(in, values.length));
    }

    @Test
    public void testVarShorts() throws IOException {
        final short[] values = {1, -2, 3, -4};
        writeVarShorts(out, values);
        final ByteArrayInputStream in = createIn();
        assertArrayEquals(values, readVarShorts(in, values.length));
    }

    @Test
    public void testUnsignedVarShorts() throws IOException {
        final short[] values = {1, -2, 3, -4};
        writeVarShorts(out, values);
        final ByteArrayInputStream in = createIn();
        final int[] unsigned = {1, 65534, 3, 65532};
        assertArrayEquals(unsigned, readUnsignedVarShorts(in, values.length));
    }

    @Test
    public void testVarInts() throws IOException {
        final int[] values = {1, -2, 3, -4};
        writeVarInts(out, values);
        final ByteArrayInputStream in = createIn();
        assertArrayEquals(values, readVarInts(in, values.length));
    }

    @Test
    public void testUnsignedVarInts() throws IOException {
        final int[] values = {1, -2, 3, -4};
        writeVarInts(out, values);
        final ByteArrayInputStream in = createIn();
        final long[] unsigned = {1, 4294967294L, 3, 4294967292L};
        assertArrayEquals(unsigned, readUnsignedVarInts(in, values.length));
    }

    @Test
    public void testVarLongs() throws IOException {
        final long[] values = {1, -2, 3, -4};
        writeVarLongs(out, values);
        final ByteArrayInputStream in = createIn();
        assertArrayEquals(values, readVarLongs(in, values.length));
    }

    @Test
    public void testStrings() throws IOException {
        final String[] values = {"A", "AB", "BAC", "CBAD"};
        writeStrings(out, values);
        final ByteArrayInputStream in = createIn();
        assertArrayEquals(values, readStrings(in, values.length));
    }
}
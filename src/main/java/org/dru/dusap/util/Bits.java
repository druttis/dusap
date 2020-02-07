package org.dru.dusap.util;

import java.util.Arrays;

public class Bits {
    private long[] bits = {};

    public Bits(final int nbits) {
        ensureCapacity(nbits >>> 6);
    }

    public Bits() {
    }

    public boolean get(final int index) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        final int word = index >>> 6;
        return (word < bits.length && (bits[word] & (1L << (index & 0x3f))) != 0L);
    }

    public void set(final int index) {
        final int word = index >>> 6;
        ensureCapacity(word);
        bits[word] |= 1L << (index & 0x3f);
    }

    public void clear(final int index) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        final int word = index >>> 6;
        if (word < bits.length) {
            bits[word] &= ~(1L << (index & 0x3f));
        }
    }

    public void clear() {
        Arrays.fill(bits, 0L);
    }

    public boolean intersects(final Bits other) {
        long[] bits = this.bits;
        long[] otherBits = other.bits;
        final int length = Math.min(bits.length, otherBits.length);
        for (int index = 0; index < length; index++) {
            if ((bits[index] & otherBits[index]) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(final Bits other) {
        long[] bits = this.bits;
        long[] otherBits = other.bits;
        int otherBitsLength = otherBits.length;
        int bitsLength = bits.length;
        for (int index = bitsLength; index < otherBitsLength; index++) {
            if (otherBits[index] != 0) {
                return false;
            }
        }
        final int length = Math.min(bitsLength, otherBitsLength);
        for (int index = 0; index < length; index++) {
            if ((bits[index] & otherBits[index]) != otherBits[index]) {
                return false;
            }
        }
        return true;
    }

    public int length() {
        long[] bits = this.bits;
        for (int word = bits.length - 1; word >= 0; --word) {
            long bitsAtWord = bits[word];
            if (bitsAtWord != 0) {
                for (int bit = 63; bit >= 0; --bit) {
                    if ((bitsAtWord & (1L << (bit & 0x3F))) != 0L) {
                        return (word << 6) + bit + 1;
                    }
                }
            }
        }
        return 0;
    }

    public boolean isEmpty() {
        long[] bits = this.bits;
        int length = bits.length;
        for (int index = 0; index < length; index++) {
            if (bits[index] != 0L) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int word = length() >>> 6;
        int hash = 0;
        for (int i = 0; word >= i; i++) {
            hash = 127 * hash + (int) (bits[i] ^ (bits[i] >>> 32));
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Bits other = (Bits) obj;
        final long[] otherBits = other.bits;
        int commonWords = Math.min(bits.length, otherBits.length);
        for (int i = 0; commonWords > i; i++) {
            if (bits[i] != otherBits[i])
                return false;
        }
        if (bits.length == otherBits.length) {
            return true;
        }
        return length() == other.length();
    }

    private void ensureCapacity(final int size) {
        if (size > bits.length) {
            bits = Arrays.copyOf(bits, size + 1);
        }
    }
}

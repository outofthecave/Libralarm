package com.outofthecave.libralarm.model;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

public class LongArrayConverterTest {
    @Test
    public void testRoundtrip() {
        runRoundtrip(new long[] {5});
        runRoundtrip(new long[] {0, -4, Math.round(Math.pow(2, 32))});
        runRoundtrip(new long[0]);
    }

    private void runRoundtrip(long[] longArray) {
        String string = LongArrayConverter.longArrayToString(longArray);
        long[] newLongArray = LongArrayConverter.toLongArray(string);
        assertArrayEquals(
                String.format("Expected: %s\nActual:%s",
                        Arrays.toString(longArray), Arrays.toString(newLongArray)),
                longArray, newLongArray);
    }
}
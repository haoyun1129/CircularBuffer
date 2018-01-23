package com.haoyun;

import com.sun.deploy.util.ArrayUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CircularBufferTest {
    @Test
    void test1() {
        CircularBuffer buffer = new CircularBuffer(100);
        assertEquals(buffer.size(), 100);
        assertEquals(buffer.available(), 0);
        byte[] testData = new byte[10];
        buffer.add(testData);
        assertEquals(buffer.size(), 100);
        assertEquals(buffer.available(), 10);

        buffer.add(testData);
        assertEquals(buffer.size(), 100);
        assertEquals(buffer.available(), 20);

        buffer.add(testData);
        assertEquals(buffer.size(), 100);
        assertEquals(buffer.available(), 30);
    }

    @Test
    void testAvailable() {
        CircularBuffer buffer = new CircularBuffer(64);
        byte[] testData = new byte[10];
        buffer.add(testData);
        System.out.println(buffer);
        assertEquals(10, buffer.available());

        buffer.add(testData);
        System.out.println(buffer);
        assertEquals(20, buffer.available());

        buffer.add(testData);
        System.out.println(buffer);
        assertEquals(30, buffer.available());
        buffer.add(testData);
        System.out.println(buffer);
        assertEquals(40, buffer.available());
        buffer.add(testData);
        System.out.println(buffer);
        assertEquals(50, buffer.available());
        buffer.add(testData);
        System.out.println(buffer);
        assertEquals(60, buffer.available());
        buffer.add(testData);
        System.out.println(buffer);
        assertEquals(64, buffer.available());
        buffer.add(testData);
        System.out.println(buffer);
        assertEquals(64, buffer.available());
        buffer.add(testData);
        System.out.println(buffer);
        assertEquals(64, buffer.available());
    }

    @Test
    void testAddGet() {
        byte[] addData, getData;
        CircularBuffer buffer = new CircularBuffer(64);
        addData = generateBytes(0, 64);
        buffer.add(addData);
        getData = new byte[64];
        buffer.get(getData);
        assertArrayEquals(addData, getData);
    }

    @Test void testAddGetUnderrun() {
        byte[] addData, getData;
        CircularBuffer buffer = new CircularBuffer(64);
        addData = generateBytes(0, 32);
        buffer.add(addData);
        getData = new byte[64];

        boolean result = buffer.get(getData);
        assertFalse(result);
        assertArrayEquals(generateZeroBytes(64), getData);
    }

    @Test void testAddGetExceedEndOfBuffer() {
        byte[] addData, getData, exceptedData;
        boolean result;
        CircularBuffer buffer = new CircularBuffer(64);


        addData = generateBytes(0, 10);
        buffer.add(addData);
        getData = new byte[10];
        result = buffer.get(getData);
        System.out.println("after get 10: " + buffer);
        assertTrue(result);
        exceptedData = generateBytes(0, 10);
        assertArrayEquals(exceptedData, getData);

        buffer.add(generateBytes(0, 64));
        System.out.println("after add 64: " + buffer);
        assertEquals(64, buffer.available());
        getData = new byte[64];
        result = buffer.get(getData);
        assertTrue(result);
        exceptedData = generateBytes(0, 64);
        assertArrayEquals(exceptedData, getData);
    }

    @Test
    void testAddGetRandom1() {
        byte[] addData, getData, exceptedData;
        boolean result;
        CircularBuffer buffer = new CircularBuffer(64);

        buffer.add(generateBytes(0, 10));
        buffer.add(generateBytes(10, 10));
        buffer.add(generateBytes(20, 10));
        buffer.add(generateBytes(30, 10));
        buffer.add(generateBytes(40, 10));
        buffer.add(generateBytes(50, 10));

        getData = new byte[60];
        buffer.get(getData);
        exceptedData = generateBytes(0, 60);
        assertArrayEquals(exceptedData, getData);

        buffer.add(generateBytes(0, 64));
        getData = new byte[64];
        buffer.get(getData);
        exceptedData = generateBytes(0, 64);
        assertArrayEquals(exceptedData, getData);

        buffer.add(generateBytes(0, 32));
        buffer.add(generateBytes(32, 32));
        getData = new byte[32];
        buffer.get(getData);
        exceptedData = generateBytes(0, 32);
        assertArrayEquals(exceptedData, getData);
        buffer.get(getData);
        exceptedData = generateBytes(32, 32);
        assertArrayEquals(exceptedData, getData);
    }

    @Test
    void testOverrunAndGetLatest() {
        byte[] addData, getData, exceptedData;
        boolean result;
        CircularBuffer buffer = new CircularBuffer(64);

        buffer.add(generateBytes(0, 50));
        buffer.add(generateBytes(50,50));
        buffer.add(generateBytes(100,50));
        buffer.add(generateBytes(150,50));
        buffer.add(generateBytes(200,56));

        getData = new byte[64];
        buffer.get(getData);
        exceptedData = generateBytes(192, 64);
        assertArrayEquals(exceptedData, getData);
    }

    @Test
    void testUnderrunGetEmpty() {
        byte[] addData, getData, exceptedData;
        boolean result;
        CircularBuffer buffer = new CircularBuffer(64);

        buffer.add(generateBytes(0, 50));
        getData = new byte[64];
        buffer.get(getData);
        exceptedData = generateZeroBytes(64);
        assertArrayEquals(exceptedData, getData);

        getData = new byte[51];
        buffer.get(getData);
        exceptedData = generateZeroBytes(51);
        assertArrayEquals(exceptedData, getData);

        getData = new byte[50];
        buffer.get(getData);
        exceptedData = generateBytes(0, 50);
        assertArrayEquals(exceptedData, getData);
    }

    private byte[] generateBytes(int start, int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i ++) {
            bytes[i] = (byte) (i + start);
        }
        return bytes;
    }

    private byte[] generateZeroBytes(int length) {
        byte[] bytes = new byte[length];
        Arrays.fill(bytes, (byte) 0);
        return bytes;
    }
}
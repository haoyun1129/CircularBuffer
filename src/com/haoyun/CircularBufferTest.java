package com.haoyun;

import com.sun.deploy.util.ArrayUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
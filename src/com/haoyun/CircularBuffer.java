package com.haoyun;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

public class CircularBuffer {

    private byte[] buffer;

    private int tail;

    private int head;

    private int overrun, underrun;

    public CircularBuffer(int n) {
        buffer = new byte[n];
        tail = 0;
        head = 0;
    }

    public void add(byte toAdd) {
        synchronized (buffer) {
            if (head != (tail - 1)) {
                buffer[head++] = toAdd;
            } else {
                throw new BufferOverflowException();
            }
            head = head % buffer.length;
        }
    }

    public void add(byte[] toAdd) {
        synchronized (buffer) {
            if (toAdd.length > buffer.length) {
                overrun++;
                throw new BufferOverflowException();
            } else if (head + toAdd.length < buffer.length) {
                // Not exceed end of buffer
                System.arraycopy(toAdd, 0, buffer, head, toAdd.length);
                if (head < tail && head + toAdd.length >= tail) {
                    // Overrun
                    head += toAdd.length;
                    updateOverrun();
                } else {
                    // Overrun
                    head += toAdd.length;
                }
            } else {
                // Exceeds the end of buffer (2 segment copy)
                assert head + toAdd.length > buffer.length;
                System.arraycopy(toAdd, 0, buffer, head, buffer.length - head);
                System.arraycopy(toAdd, buffer.length - head, buffer, 0, toAdd.length - (buffer.length - head));
                int newHead = (head + toAdd.length) % buffer.length;
                if (head < tail) {
                    head = newHead;
                    updateOverrun();
                } else if (newHead >= tail) {
                    head = newHead;
                    updateOverrun();
                } else {
                    assert head >= tail && newHead < tail;
                    head = newHead;
                }
            }
        }
    }

    private void updateOverrun() {
        overrun++;
        tail = (head + 1) % buffer.length;
    }

    public byte get() {
        synchronized (buffer) {
            byte t;
            int adjTail = tail > head ? tail - buffer.length : tail;
            if (adjTail < head) {
                t = buffer[tail++];
                tail = tail % buffer.length;
            } else {
                throw new BufferUnderflowException();
            }
            return t;
        }
    }

    public void get(byte[] data) {
        synchronized (buffer) {

        }
    }

    public String toString() {
        return "com.haoyun.CircularBuffer(size=" + buffer.length + ", head=" + head + ", tail=" + tail + ", available=" + available() + ", overrun=" + overrun + ", underrun=" + underrun + ")";
    }

    public int size() {
        return buffer.length;
    }

    public int available() {
        if (head >= tail) {
            return head - tail;
        } else {
            return head + (buffer.length - tail) + 1;
        }
    }

}
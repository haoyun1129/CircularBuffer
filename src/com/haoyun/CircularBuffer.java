package com.haoyun;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.Arrays;

public class CircularBuffer {

    private byte[] buffer;

    private int tail;

    private int head;

    private int overrun, underrun;

    public CircularBuffer(int n) {
        buffer = new byte[n + 1];
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
                throw new BufferOverflowException();
            } else if (head + toAdd.length <= buffer.length) {
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

    public boolean get(byte[] data) {
        synchronized (buffer) {
            if (tail + data.length <= buffer.length) {
                if (tail + data.length <= head) {
                    // Normal
                    System.arraycopy(buffer, tail, data, 0, data.length);
                    tail = tail + data.length;
                    return true;
                } else {
                    // Underrun
                    underrun++;
                    return false;
                }
            } else {
                // Exceed end of buffer
                int newTail = (tail + data.length) % buffer.length;
                if (tail <= head) {
                    underrun++;
                    return false;
                } else if (tail > head && newTail > (head + 1)) {
                    underrun++;
                    return false;
                } else {
                    System.arraycopy(buffer, tail, data, 0, buffer.length - tail);
                    System.arraycopy(buffer, 0, data, buffer.length - tail, newTail);
                    tail = newTail;
                    return true;
                }
            }
        }
    }

    public String toString() {
        return "com.haoyun.CircularBuffer(size=" + size() + ", head=" + head + ", tail=" + tail + ", available=" + available() + ", overrun=" + overrun + ", underrun=" + underrun + ")";
    }

    public int size() {
        return buffer.length - 1;
    }

    public int available() {
        if (head >= tail) {
            return head - tail;
        } else {
            return head + (buffer.length - tail);
        }
    }

}
package com.seapip.thomas.huffman.huffman;

import java.util.*;

public class BitQueue implements Queue<Boolean> {
    private int startOffset = 0;
    private int endOffset = 0;
    private BitSet bitSet;

    public BitQueue() {
        bitSet = new BitSet();
    }

    public BitQueue(int size) {
        bitSet = new BitSet(size);
    }

    public BitQueue(Collection<? extends Boolean> c) {
        this();
        addAll(c);
    }

    @Override
    public boolean add(Boolean aBoolean) {
        return offer(aBoolean);
    }

    @Override
    public boolean offer(Boolean aBoolean) {
        bitSet.set(endOffset, aBoolean);
        endOffset++;
        return true;
    }

    @Override
    public Boolean remove() {
        Boolean bit = poll();
        if (bit == null) throw new EmptyStackException();
        return bit;
    }

    @Override
    public Boolean poll() {
        if (isEmpty()) return null; //NOSONAR
        Boolean bit = peek();
        startOffset++;
        return bit;
    }

    @Override
    public Boolean element() {
        Boolean bit = peek();
        if (bit == null) throw new EmptyStackException();
        return bit;
    }

    @Override
    public Boolean peek() {
        if (isEmpty()) return null; //NOSONAR
        return bitSet.get(startOffset);
    }

    @Override
    public int size() {
        return endOffset - startOffset;
    }

    @Override
    public boolean isEmpty() {
        return startOffset == endOffset;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Boolean> iterator() {
        return new Iterator<Boolean>() {
            int offset = startOffset;

            @Override
            public boolean hasNext() {
                return offset < endOffset;
            }

            @Override
            public Boolean next() {
                if (!hasNext()) throw new NoSuchElementException();
                Boolean bit = bitSet.get(offset);
                offset++;
                return bit;
            }
        };
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Boolean> c) {
        for (Boolean aBoolean : c) {
            offer(aBoolean);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        bitSet = new BitSet();
        startOffset = 0;
        endOffset = 0;
    }

    public byte[] toByteArray() {
        bitSet.set(endOffset, true);
        byte[] data = new byte[(int) Math.ceil((size() + 1) / 8.0)];
        System.arraycopy(bitSet.get(startOffset, endOffset + 1).toByteArray(), 0, data, 0, data.length);
        return data;
    }

    public BitQueue copyAndAdd(boolean aBoolean) {
        BitQueue collectionCopy = new BitQueue(this);
        collectionCopy.add(aBoolean);
        return collectionCopy;
    }
}

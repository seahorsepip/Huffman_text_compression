package com.seapip.thomas.huffman.huffman;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Collection;

/**
 * The {@code Util} class consists of helper methods used by other classes.
 *
 * @author Thomas Gladdines
 * @see <a href="https://sonarcloud.io/dashboard?id=com.seapip.thomas.huffman%3AHuffman">Code analysis</a>
 * @since 1.8
 */
public class Util {
    private Util() {
    }

    public static byte[] booleansToBytes(Collection<Boolean> collection) {
        BitSet bitSet = new BitSet(collection.size());
        int index = 0;
        for (Boolean value : collection) {
            bitSet.set(index, value);
            index++;
        }
        bitSet.set(index, true); //Set additional bit after last bit to true to prevent bits from being trimmed
        byte[] data = bitSet.toByteArray();

        //Reduce size by another bytes if possible
        int size = (int) Math.ceil(collection.size() / 8.0);
        if (data.length > size) {
            byte[] smallerData = new byte[size];
            System.arraycopy(data, 0, smallerData, 0, size);
            data = smallerData;
        }

        return data;
    }

    public static Collection<Boolean> addBoolean(Collection<Boolean> collection, Boolean b) {
        Collection<Boolean> collectionCopy = new ArrayDeque<>(collection);
        collectionCopy.add(b);
        return collectionCopy;
    }
}

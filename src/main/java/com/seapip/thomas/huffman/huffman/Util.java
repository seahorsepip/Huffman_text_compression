package com.seapip.thomas.huffman.huffman;

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
        return bitSet.toByteArray();
    }
}
package com.seapip.thomas.huffman.huffman;

import java.util.BitSet;
import java.util.Collection;

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

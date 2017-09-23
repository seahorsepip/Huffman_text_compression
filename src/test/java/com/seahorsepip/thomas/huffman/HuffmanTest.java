package com.seahorsepip.thomas.huffman;

import org.junit.Test;

import static org.junit.Assert.*;

public class HuffmanTest {

    @Test
    public void huffman() throws Huffman.CompressionException {
        String content = "Eerie eyes seen near lake.";
        byte[] data = Huffman.compress(content);
        assertEquals("Compressed data should be 64 bytes", 64, data.length);
        assertEquals("Decompressed data is equal to original data", content, Huffman.decompress(data));
    }
}
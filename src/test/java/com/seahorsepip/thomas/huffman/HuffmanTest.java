package com.seahorsepip.thomas.huffman;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class HuffmanTest {

    @Test
    public void compression() {
        String content = "Eerie eyes seen near lake.";
        byte[] data = new byte[0];
        try {
            data = Huffman.compress(content);
            assertEquals("Compressed data should be 64 bytes", 64, data.length);
        } catch (Huffman.CompressionException e) {
            fail("Failed to compress content");
        }
        try {
            assertEquals("Decompressed data is equal to original data", content, Huffman.decompress(data));
        } catch (Huffman.CompressionException e) {
            fail("Failed to decompress data");
        }
    }

    @Test(expected = Huffman.CompressionException.class)
    public void compressException() throws Huffman.CompressionException {
        Huffman.compress("");
    }

    @Test(expected = Huffman.CompressionException.class)
    public void decompressException() throws Huffman.CompressionException {
        Huffman.decompress(new byte[64]);
    }
}
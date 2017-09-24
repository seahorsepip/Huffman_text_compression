package com.seapip.thomas.huffman;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

    @Test
    public void compressionSingleCharacter() {
        String content = "A";
        byte[] data;
        try {
            data = Huffman.compress(content);
        } catch (Huffman.CompressionException e) {
            fail("Failed to compress content");
        }
    }

    @Test(expected = Huffman.CompressionException.class)
    public void compressException() throws Huffman.CompressionException {
        Huffman.compress("");
    }


    @Test(expected = Huffman.CompressionException.class)
    public void decompressException() throws Huffman.CompressionException {
        Huffman.decompress(new byte[0]);
    }
}
package com.seapip.thomas.huffman;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HuffmanTest {

    //Tests have to rewritten due to refactoring :/

    /*
    @Test
    public void compression() throws Exception {
        String content = "Eerie eyes seen near lake.";
        byte[] data = Huffman.compress(content);
        assertEquals("Compressed data should be 64 bytes", 64, data.length);
        assertEquals("Decompressed data is equal to original data", content, Huffman.decompress(data));
    }

    @Test
    public void compressionSingleCharacter() throws Exception {
        Huffman.compress("A");
    }

    @Test(expected = Huffman.CompressionException.class)
    public void compressException() throws Exception {
        Huffman.compress("");
    }


    @Test(expected = Huffman.CompressionException.class)
    public void decompressException() throws Exception {
        Huffman.decompress(new byte[0]);
    }*/
}
package com.seapip.thomas.huffman;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class HuffmanTest {

    @Test
    public void compression() throws Exception  {
        String content = "Eerie eyes seen near lake.";
        byte[] data = Huffman.compress(content);
        //assertEquals("Compressed data should be 64 bytes", 64, data.length);
        assertEquals("Decompressed data is equal to original data", content, Huffman.decompress(data));
    }

    @Test
    public void compressionSingleCharacter() throws Exception {
        String content = "A";
        byte[] data = Huffman.compress(content);
    }

    @Test(expected = Huffman.CompressionException.class)
    public void compressException() throws Exception {
        Huffman.compress("");
    }


    @Test(expected = Huffman.CompressionException.class)
    public void decompressException() throws Exception {
        Huffman.decompress(new byte[0]);
    }
}
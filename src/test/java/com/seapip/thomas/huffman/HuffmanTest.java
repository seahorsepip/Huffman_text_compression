package com.seapip.thomas.huffman;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

public class HuffmanTest {

    @Test
    public void compression() throws Exception {
        String content = "Eerie eyes seen near lake.";
        byte[] data;

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes());
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            Huffman.compress(byteArrayInputStream, byteArrayOutputStream);
            data = byteArrayOutputStream.toByteArray();
            assertEquals("Compressed data size should be 58 bytes", 58, data.length);
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            Huffman.decompress(byteArrayInputStream, byteArrayOutputStream);
            data = byteArrayOutputStream.toByteArray();
            assertEquals("Decompressed data is equal to original data", content, new String(data));
        }
    }

    @Test
    public void compressionSingleCharacter() throws Exception {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("A".getBytes());
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            Huffman.compress(byteArrayInputStream, byteArrayOutputStream);
        }
    }

    @Test(expected = Huffman.CompressionException.class)
    public void compressException() throws Exception {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("".getBytes());
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            Huffman.compress(byteArrayInputStream, byteArrayOutputStream);
        }
    }


    @Test(expected = Huffman.CompressionException.class)
    public void decompressException() throws Exception {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[0]);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            Huffman.compress(byteArrayInputStream, byteArrayOutputStream);
        }
    }
}
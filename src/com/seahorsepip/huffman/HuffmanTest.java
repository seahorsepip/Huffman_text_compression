package com.seahorsepip.huffman;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HuffmanTest {
    @org.junit.jupiter.api.Test
    void huffman() throws IOException, ClassNotFoundException {
        String content = "Eerie eyes seen near lake.";
        byte[] data = Huffman.compress(content);
        assertEquals(64, data.length, "Size of compressed sample data is 64 bytes");
        assertEquals(content, Huffman.decompress(data), "Decompressed data is equal to original data");
    }
}
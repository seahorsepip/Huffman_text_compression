package com.seapip.thomas.huffman;

import com.seapip.thomas.huffman.huffman.EncodedText;
import com.seapip.thomas.huffman.huffman.TreeNode;

import javax.annotation.Nonnull;
import java.io.*;

/**
 * The {@code Huffman} class consists exclusively of static methods to compress and decompress
 * text data using the Huffman algorithm.
 *
 * @author Thomas Gladdines
 * @see <a href="https://sonarcloud.io/dashboard?id=com.seapip.thomas.huffman%3AHuffman">Code analysis</a>
 * @since 1.8
 */
public final class Huffman {
    private Huffman() {
    }

    /**
     * Returns a byte array of compressed data for the given text input
     *
     * @param content The content that should be compressed
     * @return The compressed data bytes
     * @throws CompressionException Exception thrown when compressions fails
     */
    public static byte[] compress(@Nonnull String content) throws CompressionException {
        if (content.length() == 0) throw new CompressionException("Content length has to be larger then zero");

        TreeNode tree = new TreeNode(content); //Create Huffman tree
        EncodedText encodedText = new EncodedText(content, tree); //Encode content with Huffman tree

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            tree.writeObject(objectOutputStream); //Write Huffman tree to stream *without class header*
            encodedText.writeObject(objectOutputStream); //Write encoded text to stream *without class header*
            objectOutputStream.flush();
        } catch (IOException e) {
            throw new CompressionException(e.getMessage());
        }

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Returns the original decompressed text for a given compressed data byte array
     *
     * @param data The compressed data bytes
     * @return The decompressed text
     * @throws CompressionException Exception thrown when decompression fails
     */
    public static String decompress(@Nonnull byte[] data) throws CompressionException {
        TreeNode tree;
        EncodedText encodedText;

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            tree = new TreeNode(objectInputStream); //Read Huffman tree from stream *without class header*
            encodedText = new EncodedText(objectInputStream); //Read encoded text stream file *without class header*
        } catch (IOException | ClassNotFoundException e) {
            throw new CompressionException(e.getMessage());
        }

        return encodedText.decode(tree); //Decode text using Huffman tree
    }

    public static class CompressionException extends Exception {
        public CompressionException(String message) {
            super(message);
        }
    }
}
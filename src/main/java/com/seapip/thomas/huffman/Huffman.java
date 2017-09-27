package com.seapip.thomas.huffman;

import com.seapip.thomas.huffman.huffman.CharNode;
import com.seapip.thomas.huffman.huffman.Node;
import com.seapip.thomas.huffman.huffman.TreeNode;
import com.seapip.thomas.huffman.huffman.Util;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

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
     * @param inputStream  The data stream to read and compress
     * @param outputStream The data stream to write the compressed data too
     * @throws CompressionException Exception thrown when compressions fails
     */
    public static void compress(InputStream inputStream, OutputStream outputStream) throws CompressionException {
        try {
            //Read text from input stream
            StringBuilder content = new StringBuilder();
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            while (scanner.hasNext()) {
                content.append(scanner.next());
            }

            //Create Huffman tree
            TreeNode tree = new TreeNode(content.toString());

            //Convert Huffman tree to map
            Map<Character, Collection<Boolean>> map = tree.toMap();

            //Encode characters using map
            Queue<Boolean> booleans = new ArrayDeque<>();
            for (char character : content.toString().toCharArray()) booleans.addAll(map.get(character));

            byte[] data = Util.booleansToBytes(booleans); //Convert encoded data to byte array

            //Write Huffman tree
            tree.write(outputStream);

            //Write character count
            outputStream.write(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(content.length()).array());

            //Write compressed data size in bytes
            outputStream.write(ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(data.length).array());

            //Write compressed data
            outputStream.write(data);
        } catch (IOException e) {
            throw new CompressionException(e.getMessage());
        }
    }

    /**
     * Returns the original decompressed text for a given compressed data byte array
     *
     * @param inputStream  The compressed data stream
     * @param outputStream The data stream to write the decompressed data too
     * @throws CompressionException Exception thrown when decompression fails
     */
    public static void decompress(InputStream inputStream, OutputStream outputStream) throws CompressionException {
        try {
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            TreeNode tree = TreeNode.read(inputStream);
            byte[] data;

            //Read character count
            data = new byte[Long.SIZE / Byte.SIZE];
            dataInputStream.readFully(data, 0, data.length);
            long length = ByteBuffer.wrap(data).getLong();

            //Read compressed data size in bytes
            data = new byte[Integer.SIZE / Byte.SIZE];
            dataInputStream.readFully(data, 0, data.length);
            int size = ByteBuffer.wrap(data).getInt();

            Node node = tree;
            int offset = 0;

            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)), true);) {
                for (int i = 0; i < size; i++) {
                    byte b = dataInputStream.readByte();
                    for (int mask = 1; mask != 256; mask <<= 1) {
                        if (offset >= length) break;
                        if (node instanceof CharNode) {
                            writer.print(((CharNode) node).getCharacter());
                            node = tree;
                            offset++;
                        }
                        node = ((b & mask) != 0) ? ((TreeNode) node).getRightNode() : ((TreeNode) node).getLeftNode();
                    }
                }
            }
        } catch (IOException e) {
            throw new CompressionException(e.getMessage());
        }
    }

    public static class CompressionException extends Exception {
        public CompressionException(String message) {
            super(message);
        }
    }
}
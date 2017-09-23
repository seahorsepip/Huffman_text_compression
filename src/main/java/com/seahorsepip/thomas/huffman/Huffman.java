package com.seahorsepip.thomas.huffman;

import javax.annotation.Nonnull;
import java.io.*;
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
        TreeNode tree = new TreeNode();
        EncodedText encodedText = new EncodedText();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            tree.readObject(objectInputStream); //Read Huffman tree from stream *without class header*
            encodedText.readObject(objectInputStream); //Read encoded text stream file *without class header*
        } catch (IOException | ClassNotFoundException e) {
            throw new CompressionException(e.getMessage());
        }

        return encodedText.decode(tree); //Decode text using Huffman tree
    }

    private static byte[] booleansToBytes(Collection<Boolean> collection) {
        BitSet bitSet = new BitSet(collection.size());
        int index = 0;
        for (Boolean value : collection) {
            bitSet.set(index, value);
            index++;
        }
        bitSet.set(index, true); //Set additional bit after last bit to true to prevent bits from being trimmed
        return bitSet.toByteArray();
    }

    private static void getCharacterBits(Node node, Map<Character, Queue<Boolean>> bitMap, Queue<Boolean> bits) {
        Node leftNode = ((TreeNode) node).getLeftNode();
        Node rightNode = ((TreeNode) node).getRightNode();

        Queue<Boolean> leftBits = new ArrayDeque<>(bits);
        leftBits.add(false);

        if (leftNode != null) {
            if (leftNode instanceof CharacterNode) {
                bitMap.put(((CharacterNode) leftNode).getCharacter(), leftBits);
            } else {
                getCharacterBits(leftNode, bitMap, leftBits);
            }
        }

        Queue<Boolean> rightBits = new ArrayDeque<>(bits);
        rightBits.add(true);

        if (rightNode != null) {
            if (rightNode instanceof CharacterNode) {
                bitMap.put(((CharacterNode) rightNode).getCharacter(), rightBits);
            } else {
                getCharacterBits(rightNode, bitMap, rightBits);
            }
        }
    }

    private interface Node {
        int getValue();
    }

    private static class TreeNode implements Node, Serializable {
        private transient Node leftNode;
        private transient Node rightNode;

        TreeNode() {
        }

        TreeNode(String content) {
            //Create character frequency map
            Map<Character, Integer> frequencyMap = new HashMap<>();
            for (char character : content.toCharArray()) {
                frequencyMap.put(character, frequencyMap.getOrDefault(character, 0) + 1);
            }

            //Create Huffman tree from character frequency map
            if (frequencyMap.size() == 1) {
                leftNode = new CharacterNode(content.charAt(0));
                return;
            }

            Queue<Node> queue = new PriorityQueue<>(frequencyMap.size(),
                    (o1, o2) -> ((Integer) o1.getValue()).compareTo(o2.getValue()));
            for (Map.Entry entry : frequencyMap.entrySet()) {
                queue.add(new CharacterNode((char) entry.getKey(), (int) entry.getValue()));
            }
            while (queue.size() > 1) queue.add(new TreeNode(queue.poll(), queue.poll()));

            //Set child node values of this tree to values from created Huffman tree
            leftNode = ((TreeNode) queue.peek()).getLeftNode();
            rightNode = ((TreeNode) queue.peek()).getRightNode();
        }

        TreeNode(Node leftNode, Node rightNode) {
            this.leftNode = leftNode;
            this.rightNode = rightNode;
        }

        @Override
        public int getValue() {
            return leftNode.getValue() + rightNode.getValue();
        }

        Node getLeftNode() {
            return leftNode;
        }

        Node getRightNode() {
            return rightNode;
        }


        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            Queue<Character> characters = new ArrayDeque<>();
            Queue<Boolean> structure = new ArrayDeque<>();

            //Get characters and tree structure in pre order tree traversal
            flattenTreeData(characters, structure, this);

            objectOutputStream.writeInt(characters.size()); //Write character count
            for (Character character : characters) objectOutputStream.writeChar(character); //Write characters
            objectOutputStream.writeInt(structure.size()); //Write structure size
            objectOutputStream.write(booleansToBytes(structure)); //Write tree structure
        }

        private void flattenTreeData(Collection<Character> characters, Collection<Boolean> structure, Node node) throws IOException {
            if (node != null) {
                if (node instanceof CharacterNode) {
                    characters.add(((CharacterNode) node).getCharacter());
                    structure.add(true);
                } else {
                    structure.add(false);
                    flattenTreeData(characters, structure, ((TreeNode) node).getLeftNode());
                    flattenTreeData(characters, structure, ((TreeNode) node).getRightNode());
                }
            }
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            int count = objectInputStream.readInt(); //Read character count
            Queue<Character> characters = new ArrayDeque<>(count);
            for (int i = 0; i < count; i++) characters.add(objectInputStream.readChar()); //Read characters
            int size = objectInputStream.readInt(); //Read structure size
            byte[] structureBytes = new byte[(int) Math.ceil(size / 8.0)];
            objectInputStream.readFully(structureBytes, 0, structureBytes.length); //Read structure bytes

            //Convert structure bytes to booleans
            Queue<Boolean> structure = new ArrayDeque<>();
            for (byte b : structureBytes) for (int mask = 1; mask != 256; mask <<= 1) structure.add((b & mask) != 0);

            //Create Huffman tree from characters and structure data
            TreeNode tree = (TreeNode) unflattenTreeData(characters, structure);


            //Set child node values of this tree to values from created Huffman tree
            if (tree != null) {
                leftNode = tree.getLeftNode();
                rightNode = tree.getRightNode();
            }
        }

        private Node unflattenTreeData(Queue<Character> characters, Queue<Boolean> structure) {
            if (!characters.isEmpty()) {
                if (structure.poll()) {
                    return new CharacterNode(characters.poll());
                } else {
                    return new TreeNode(unflattenTreeData(characters, structure), unflattenTreeData(characters, structure));
                }
            }
            return null;
        }
    }

    private static class CharacterNode implements Node {
        private char character;
        private int value;

        CharacterNode(char character) {
            this.character = character;
        }

        CharacterNode(char character, int value) {
            this.character = character;
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }

        char getCharacter() {
            return character;
        }
    }

    private static class EncodedText implements Serializable {
        private transient byte[] data;
        private transient long length;

        EncodedText() {
        }

        EncodedText(String content, TreeNode tree) {
            //Convert Huffman tree to map
            Map<Character, Queue<Boolean>> map = new HashMap<>();
            getCharacterBits(tree, map, new ArrayDeque<>());

            //Compress character using map with bit value of each character
            Queue<Boolean> booleans = new ArrayDeque<>();
            for (char character : content.toCharArray()) booleans.addAll(map.get(character));

            data = booleansToBytes(booleans); //Store encoded data as byte array
            length = content.length(); //Store content length
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeLong(length); //Write character count
            objectOutputStream.writeInt(data.length); //Write compressed data size in bytes
            objectOutputStream.write(data); //Write compressed data
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            length = objectInputStream.readLong(); //Read character count
            int size = objectInputStream.readInt(); //Read compressed data size in bytes
            data = new byte[size];
            objectInputStream.readFully(data, 0, size); //Read compressed data
        }

        String decode(TreeNode tree) {
            StringBuilder content = new StringBuilder();
            Node node = tree;
            int offset = 0;

            //Decode data with Huffman tree to the original content
            for (byte b : data) {
                for (int mask = 1; mask != 256; mask <<= 1) {
                    if (offset >= length) break;
                    if (node instanceof CharacterNode) {
                        content.append(((CharacterNode) node).getCharacter());
                        node = tree;
                        offset++;
                    }
                    node = ((b & mask) != 0) ? ((TreeNode) node).getRightNode() : ((TreeNode) node).getLeftNode();
                }
            }

            return content.toString();
        }
    }

    public static class CompressionException extends Exception {
        public CompressionException(String message) {
            super(message);
        }
    }
}
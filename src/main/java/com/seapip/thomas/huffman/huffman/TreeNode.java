package com.seapip.thomas.huffman.huffman;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.*;

import static com.seapip.thomas.huffman.huffman.Util.addBoolean;
import static com.seapip.thomas.huffman.huffman.Util.booleansToBytes;

/**
 * The {@code TreeNode} class consists of a left and right node and has constructor methods
 * to be simply created from a given text. It also has it's own serialization implementation
 * so it can be stored using less bytes.
 *
 * @author Thomas Gladdines
 * @see <a href="https://sonarcloud.io/dashboard?id=com.seapip.thomas.huffman%3AHuffman">Code analysis</a>
 * @since 1.8
 */
public class TreeNode implements Node {
    private Node leftNode;
    private Node rightNode;

    public TreeNode(String content) {
        //Create character frequency map
        Map<Character, Integer> map = new HashMap<>();
        for (char character : content.toCharArray()) map.put(character, map.getOrDefault(character, 0) + 1);

        //Create Huffman tree from character frequency map
        if (map.size() == 1) {
            leftNode = new CharNode(content.charAt(0));
            return;
        }
        Queue<Node> queue = new PriorityQueue<>(map.size(), (o1, o2) -> ((Integer) o1.getValue()).compareTo(o2.getValue()));
        for (Map.Entry entry : map.entrySet()) queue.add(new CharNode((char) entry.getKey(), (int) entry.getValue()));
        while (queue.size() > 1) queue.add(new TreeNode(queue.poll(), queue.poll()));

        //Set child node values of this tree to values from created Huffman tree
        leftNode = ((TreeNode) queue.peek()).getLeftNode();
        rightNode = ((TreeNode) queue.peek()).getRightNode();
    }

    public TreeNode(Node leftNode, Node rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    public TreeNode(Queue<Character> characters, Queue<Boolean> structure) {
        TreeNode tree = (TreeNode) unflatten(characters, structure);
        if (tree != null) {
            leftNode = tree.getLeftNode();
            rightNode = tree.getRightNode();
        }
    }

    public static TreeNode read(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        byte[] data = new byte[4];

        //Read character count
        dataInputStream.readFully(data, 0, 4);
        int count = ByteBuffer.wrap(data).getInt();

        //Read characters
        Queue<Character> characters = new ArrayDeque<>(count);
        for (int i = 0; i < count; i++) {
            dataInputStream.readFully(data, 0, 2);
            characters.add(ByteBuffer.wrap(data).getChar());
        }

        //Read structure size
        dataInputStream.readFully(data, 0, 4);
        int size = ByteBuffer.wrap(data).getInt();

        //Read structure bytes
        byte[] structureBytes = new byte[(int) Math.ceil(size / 8.0)];
        dataInputStream.readFully(structureBytes, 0, structureBytes.length);

        //Convert structure bytes to booleans
        Queue<Boolean> structure = new ArrayDeque<>();
        for (byte b : structureBytes) for (int mask = 1; mask != 256; mask <<= 1) structure.add((b & mask) != 0);

        //Create Huffman tree from characters and tree structure
        return new TreeNode(characters, structure);
    }

    @Override
    public void flatten(Collection<Character> characters, Collection<Boolean> structure) {
        structure.add(false);
        if (leftNode != null) leftNode.flatten(characters, structure);
        if (rightNode != null) rightNode.flatten(characters, structure);
    }

    private Node unflatten(Queue<Character> characters, Queue<Boolean> structure) {
        if (characters.isEmpty()) return null;
        return structure.poll() ? new CharNode(characters.poll()) : new TreeNode(unflatten(characters, structure), unflatten(characters, structure));
    }

    @Override
    public int getValue() {
        return leftNode.getValue() + rightNode.getValue();
    }

    public Node getLeftNode() {
        return leftNode;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public void write(OutputStream outputStream) throws IOException {
        Queue<Character> characters = new ArrayDeque<>();
        Queue<Boolean> structure = new ArrayDeque<>();

        //Get characters and tree structure in pre order tree traversal
        flatten(characters, structure);

        //Write character count
        outputStream.write(ByteBuffer.allocate(4).putInt(characters.size()).array());

        //Write character
        for (Character character : characters) outputStream.write(ByteBuffer.allocate(2).putChar(character).array());

        //Write structure size
        outputStream.write(ByteBuffer.allocate(4).putInt(structure.size()).array());

        //Write tree structure
        outputStream.write(booleansToBytes(structure));
    }

    @Override
    public void toMap(Map<Character, Collection<Boolean>> map, Collection<Boolean> booleans) {
        if (leftNode != null) leftNode.toMap(map, addBoolean(booleans, false));
        if (rightNode != null) rightNode.toMap(map, addBoolean(booleans, true));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder().append("|--------------|\r\n| Huffman tree |\r\n|--------------|\r\n|\r\n");
        toString(stringBuilder, new StringBuilder(), true);
        return stringBuilder.toString();
    }

    @Override
    public void toString(StringBuilder stringBuilder, StringBuilder prefix, boolean isTail) {
        if (rightNode != null) rightNode.toString(stringBuilder, newString(prefix, isTail), false);
        stringBuilder.append(prefix).append("|---|\r\n");
        if (leftNode != null) leftNode.toString(stringBuilder, newString(prefix, !isTail), true);
    }

    private StringBuilder newString(StringBuilder prefix, boolean isTail) {
        return new StringBuilder().append(prefix).append(isTail ? "|   " : "    ");
    }
}

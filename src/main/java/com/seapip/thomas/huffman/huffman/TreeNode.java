package com.seapip.thomas.huffman.huffman;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * The {@code TreeNode} class consists of a left and right node and has constructor methods
 * to be simply created from a given text. It also has it's own serialization implementation
 * so it can be stored using less bytes.
 *
 * @author Thomas Gladdines
 * @see <a href="https://sonarcloud.io/dashboard?id=com.seapip.thomas.huffman%3AHuffman">Code analysis</a>
 * @since 1.8
 */
public class TreeNode implements Node, Serializable {
    private transient Node leftNode;
    private transient Node rightNode;

    public TreeNode(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        readObject(objectInputStream);
    }

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

    public void write(ObjectOutputStream objectOutputStream) throws IOException {
        writeObject(objectOutputStream);
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        Queue<Character> characters = new ArrayDeque<>();
        Queue<Boolean> structure = new ArrayDeque<>();

        //Get characters and tree structure in pre order tree traversal
        flatten(characters, structure, this);

        objectOutputStream.writeInt(characters.size()); //Write character count
        for (Character character : characters) objectOutputStream.writeChar(character); //Write characters
        objectOutputStream.writeInt(structure.size()); //Write structure size
        objectOutputStream.write(Util.booleansToBytes(structure)); //Write tree structure
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

        //Create Huffman tree from characters and tree structure
        TreeNode tree = (TreeNode) unflatten(characters, structure);

        //Set child node values of this tree to values from created Huffman tree
        if (tree != null) {
            leftNode = tree.getLeftNode();
            rightNode = tree.getRightNode();
        }
    }

    private void flatten(Collection<Character> characters, Collection<Boolean> structure, Node node) throws IOException {
        if (node == null) return;
        if (node instanceof CharNode) {
            characters.add(((CharNode) node).getCharacter());
            structure.add(true);
        } else {
            structure.add(false);
            flatten(characters, structure, ((TreeNode) node).getLeftNode());
            flatten(characters, structure, ((TreeNode) node).getRightNode());
        }
    }

    private Node unflatten(Queue<Character> characters, Queue<Boolean> structure) {
        if (!characters.isEmpty()) {
            if (structure.poll()) {
                return new CharNode(characters.poll());
            } else {
                return new TreeNode(unflatten(characters, structure), unflatten(characters, structure));
            }
        }
        return null;
    }

    public Map<Character, Collection<Boolean>> toMap() {
        Map<Character, Collection<Boolean>> map = new HashMap<>();
        toMap(leftNode, map, new ArrayDeque<>(), false);
        toMap(rightNode, map, new ArrayDeque<>(), true);
        return map;
    }

    private void toMap(Node node, Map<Character, Collection<Boolean>> map, Deque<Boolean> booleans, boolean b) {
        booleans = new ArrayDeque<>(booleans);
        booleans.add(b);

        if (node != null) {
            if (node instanceof CharNode) {
                map.put(((CharNode) node).getCharacter(), booleans);
            } else {
                toMap(((TreeNode) node).getLeftNode(), map, booleans, false);
                toMap(((TreeNode) node).getRightNode(), map, booleans, true);
            }
        }
    }
}
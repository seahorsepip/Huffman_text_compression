package com.seapip.thomas.huffman.huffman;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;

public class EncodedText implements Serializable {
    private transient byte[] data;
    private transient long length;

    public EncodedText(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        readObject(objectInputStream);
    }

    public EncodedText(String content, TreeNode tree) {
        //Convert Huffman tree to map
        Map<Character, Collection<Boolean>> map = tree.toMap();

        //Compress character using map with bit value of each character
        Queue<Boolean> booleans = new ArrayDeque<>();
        for (char character : content.toCharArray()) booleans.addAll(map.get(character));

        data = Util.booleansToBytes(booleans); //Store encoded data as byte array
        length = content.length(); //Store content length
    }

    public void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
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

    public String decode(TreeNode tree) {
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

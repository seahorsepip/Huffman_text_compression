package com.seapip.thomas.huffman.huffman;

/**
 * The {@code CharNode} class consists of a single character and it's frequency
 * and is used in combination with the {@code TreeNode} class.
 *
 * @author Thomas Gladdines
 * @see <a href="https://sonarcloud.io/dashboard?id=com.seapip.thomas.huffman%3AHuffman">Code analysis</a>
 * @since 1.8
 */
public class CharNode implements Node {
    private char character;
    private int value;

    CharNode(char character) {
        this.character = character;
    }

    CharNode(char character, int value) {
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

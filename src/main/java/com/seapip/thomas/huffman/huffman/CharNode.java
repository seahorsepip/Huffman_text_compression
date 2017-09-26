package com.seapip.thomas.huffman.huffman;

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

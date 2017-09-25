package com.seapip.thomas.huffman.huffman;

public class CharacterNode implements Node {
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

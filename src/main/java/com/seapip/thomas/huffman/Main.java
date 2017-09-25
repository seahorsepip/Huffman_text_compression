package com.seapip.thomas.huffman;

import com.seapip.thomas.huffman.huffman.Huffman;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class Main {

    public static void main(String[] args) {
        if (args.length > 1) {
            try {
                File input = new File(args[1]);
                File output;
                String content;
                byte[] data;
                switch (args[0].toLowerCase()) {
                    case "compress":
                    case "encode":
                    case "enc":
                    case "-c":
                    case "-e":
                    case "c":
                    case "e":
                        output = new File(args.length > 2 ? args[2] : input.toPath() + ".compressed");
                        content = new String(Files.readAllBytes(input.toPath()));
                        data = Huffman.compress(content);
                        Files.write(output.toPath(), data, StandardOpenOption.CREATE);
                        break;
                    case "decompress":
                    case "decode":
                    case "dec":
                    case "-d":
                    case "d":
                        String path = input.toPath().toString();
                        path = (path.endsWith(".compressed") ? path.substring(0, path.length() - 11) : path);
                        output = new File(args.length > 2 ? args[2] : path + ".decompressed");
                        data = Files.readAllBytes(input.toPath());
                        content = Huffman.decompress(data);
                        Files.write(output.toPath(), content.getBytes(), StandardOpenOption.CREATE);
                        break;
                    default:
                        //Incorrect method parameter
                        break;
                }
            } catch (IOException | Huffman.CompressionException ignored) {
                //Files could not be read and/or written
            }
        }
    }
}

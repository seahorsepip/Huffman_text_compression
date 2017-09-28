package com.seapip.thomas.huffman;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        if (args.length > 1) {
            File input = new File(args[1]);
            try (FileInputStream fileInputStream = new FileInputStream(input)) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                switch (args[0].toLowerCase()) {
                    case "compress":
                    case "encode":
                    case "enc":
                    case "-c":
                    case "-e":
                    case "c":
                    case "e":
                        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(args.length > 2 ? args[2] : input.toPath() + ".compressed"));
                             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);) {

                            Huffman.compress(bufferedInputStream, bufferedOutputStream);
                            bufferedOutputStream.flush();
                        }
                        break;
                    case "decompress":
                    case "decode":
                    case "dec":
                    case "-d":
                    case "d":
                        String path = input.toPath().toString();
                        path = (path.endsWith(".compressed") ? path.substring(0, path.length() - 11) : path);
                        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(args.length > 2 ? args[2] : path + ".decompressed"));
                             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);) {

                            Huffman.decompress(bufferedInputStream, bufferedOutputStream);
                            bufferedOutputStream.flush();
                        }
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

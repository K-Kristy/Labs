package ru.ifmo.ctddev.kuplkri.walk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SuppressWarnings("ALL")
public class Walk {

    private static final int BYTES_SIZE = 100;

    public static void main(final String[] args) {
        /*String inputFilePath = "D:\\input\\Lab_1_input.txt";
        String outputFilePath = "D:\\input\\Lab_1_output.txt";*/

        String inputFilePath;
        String outputFilePath;
        try {
            inputFilePath = args[0];
            outputFilePath = args[1];
        } catch (Exception ex) {
            System.err.println("Missing parameter!");
            return;
        }

        List<String> filePaths;
        try {
            filePaths = Files.readAllLines(Paths.get(inputFilePath), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Can not find input file!");
            return;
        }

        getHashesAndWriteOutput(outputFilePath, filePaths);
    }

    private static void getHashesAndWriteOutput(String outputFilePath, List<String> filePaths) {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(outputFilePath), StandardCharsets.UTF_8)) {
            for (String path : filePaths) {
                int hash;
                try {
                    hash = getFileHash(path);
                } catch (Exception e) {
                    hash = 0;
                }

                w.write(String.format("%08x", hash) + " " + path + System.lineSeparator());
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private static int getFileHash(String path) throws IOException {
        int hash = 0x811c9dc5;
        File file = new File(path);
        byte[] content = new byte[BYTES_SIZE];
        try (FileInputStream inputStream = new FileInputStream(file)) {
            int readResult = BYTES_SIZE;
            while (readResult != -1 && readResult == BYTES_SIZE) {
                readResult = inputStream.read(content, 0, BYTES_SIZE);
                hash = getFNVHash(content, hash, readResult);
            }
        }

        return hash;
    }


    private static int getFNVHash(byte[] content, int startValue, int len) {
        int hash = startValue;
        final int fnvPrime = 0x01000193;
        final int twoFiveFive = 0xff;

        for (int i = 0; i < len; i++) {
            hash *= fnvPrime;
            hash ^= content[i] & twoFiveFive;
        }

        return hash;
    }

}

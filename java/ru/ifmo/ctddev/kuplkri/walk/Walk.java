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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
            filePaths = getFilePaths(inputFilePath);
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

                w.write(String.format("%08x", hash) + " " + path + "\n");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private static int getFileHash(String path) throws IOException {
        int hash = 0x811c9dc5;
        File file = new File(path);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            long fileLength = file.length();
            while (fileLength > 0) {
                int len = fileLength - BYTES_SIZE > 0 ? BYTES_SIZE : (int) fileLength;
                byte[] content = new byte[len];
                inputStream.read(content, 0, len);
                hash = getFNVHash(content, hash);
                fileLength -= len;
            }
        }

        return hash;
    }

    private static List<String> getFilePaths(String inputFilePath) throws IOException {
        List<String> files = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(inputFilePath))) {
            stream.forEach(files::add);
        }

        return files;
    }

    private static int getFNVHash(byte[] content, int startValue) {
        int hash = startValue;
        final int fnvPrime = 0x01000193;
        final int twoFiveFive = 0xff;

        for (byte b : content) {
            hash *= fnvPrime;
            hash ^= b & twoFiveFive;
        }

        return hash;
    }

}

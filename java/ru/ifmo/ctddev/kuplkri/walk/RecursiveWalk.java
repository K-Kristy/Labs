package ru.ifmo.ctddev.kuplkri.walk;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecursiveWalk {

    private static int BYTES_SIZE = 100;

    public static void main(final String[] args) {
/*        String inputFilePath = "D:\\input\\Lab_1_input.txt";
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

        if (StringUtils.isEmpty(inputFilePath) || StringUtils.isEmpty(outputFilePath)) {
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

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {
            getHashesAndWriteOutput(filePaths, bw);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private static void getHashesAndWriteOutput(List<String> filePaths, BufferedWriter bw) throws IOException {
        int hash;
        for (String path : filePaths) {
            File file = new File(path);
            if (file.isDirectory()) {
                if (file.listFiles() != null && file.listFiles().length != 0) {
                    List<String> nestedFilePaths = Arrays.stream(file.listFiles())
                            .map(File::getPath)
                            .collect(Collectors.toList());

                    getHashesAndWriteOutput(nestedFilePaths, bw);
                }
            } else {
                try {
                    hash = getFileHash(path);
                } catch (Exception e) {
                    hash = 0;
                }

                bw.write(String.format("%08x", hash) + " " + path);
                bw.newLine();
            }
        }
    }


    private static int getFileHash(String path) throws IOException {
        int hash = 0x811c9dc5;

        File file = new File(path);
        long fileLength = file.length();

        try (FileInputStream inputStream = new FileInputStream(file)) {
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
        int fnvPrime = 0x01000193;

        for (byte b : content) {
            hash *= fnvPrime;
            hash ^= (b & 0xff);
        }

        return hash;
    }
}

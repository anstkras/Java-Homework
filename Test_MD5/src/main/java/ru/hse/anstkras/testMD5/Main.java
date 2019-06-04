package ru.hse.anstkras.testMD5;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Enter the directory path:");
        var Scanner = new Scanner(System.in);
        Path path = Paths.get(Scanner.next());

        var checkerMD5 = new CheckerMD5MultiThreaded();
        var checkerMD5SingleThreaded = new CheckerMD5SingleThreaded();

        long timeNow = System.currentTimeMillis();
        checkerMD5SingleThreaded.checkSum(path);
        System.out.println("single thread time is " + (System.currentTimeMillis() - timeNow));
        timeNow = System.currentTimeMillis();
        String result = checkerMD5.checkSum(path);
        System.out.println("multi threads time is " + (System.currentTimeMillis() - timeNow));
        System.out.println("checkSum is " + result);
    }
}

package ru.hse.anstkras.testMD5;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class CheckerMD5MultiThreaded implements Checker {
    private ForkJoinPool forkJoinPool = new ForkJoinPool(8); //TODO

    @Override
    public String checkSum(Path path) {
        return forkJoinPool.invoke(new RecursiveTask<>() {
            @Override
            protected String compute() {
                try {
                    if (Files.isDirectory(path)) {
                        Collection<Path> paths = Files.list(path).collect(Collectors.toSet());
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(path.getFileName());
                        for (Path file : paths) {
                            if (Files.isRegularFile(file)) {
                                stringBuilder.append(hasherMD5.hashMD5(new FileInputStream(file.toFile())));
                            } else if (Files.isDirectory(file)) {
                                stringBuilder.append(forkJoinPool.invoke(new RecursiveTask<String>() {
                                    @Override
                                    protected String compute() {
                                        return checkSum(file);
                                    }
                                }));
                            }
                        }
                        return hasherMD5.hashMD5(new ByteArrayInputStream(stringBuilder.toString().getBytes()));
                    }
                    if (Files.isRegularFile(path)) {
                        return hasherMD5.hashMD5(new FileInputStream(path.toFile()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }
        });
    }
}


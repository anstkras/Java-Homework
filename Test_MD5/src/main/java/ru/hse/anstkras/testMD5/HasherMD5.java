package ru.hse.anstkras.testMD5;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// package private class to compute md5 hash
class HasherMD5 {
    private HasherMD5() {}

    @NotNull
    static String hashMD5(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        var messageDigest = MessageDigest.getInstance("MD5");
        var digestInputStream = new DigestInputStream(inputStream, messageDigest);

        var reader = new BufferedReader(new InputStreamReader(digestInputStream));
        while ((reader.readLine()) != null) {
            ;
        }

        byte[] bytes = messageDigest.digest();
        var stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(Integer.toHexString(b & 0xff));
        }
        return stringBuilder.toString();
    }
}

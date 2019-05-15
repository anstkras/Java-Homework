package ru.hse.anstkras.testMD5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class hasherMD5 {
    private hasherMD5() {}

    static String hashMD5(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest);

        BufferedReader reader = new BufferedReader(new InputStreamReader(digestInputStream));
        while ((reader.readLine()) != null) {
            ;
        }

        byte[] bytes = messageDigest.digest();
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(Integer.toHexString(b & 0xff));
        }
        return stringBuilder.toString();
    }
}

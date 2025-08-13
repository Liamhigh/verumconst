package com.verum.omnis.core;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Locale;

public class HashUtil {
    public static String sha512(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(data);
        return toHex(md.digest());
    }

    public static String sha512File(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        FileInputStream fis = new FileInputStream(file);
        byte[] buf = new byte[8192];
        int r;
        while ((r = fis.read(buf)) != -1) {
            md.update(buf, 0, r);
        }
        fis.close();
        return toHex(md.digest());
    }

    public static String truncate(String fullHash, int chars) {
        if (fullHash == null) return "";
        return fullHash.length() <= chars ? fullHash : fullHash.substring(0, chars);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format(Locale.US, "%02x", b));
        return sb.toString();
    }
}
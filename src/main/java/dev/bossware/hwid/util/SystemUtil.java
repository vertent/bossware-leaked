package dev.bossware.hwid.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SystemUtil {
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String getSystemInfo() {
        return bytesToHex(generateHWID());
    }

    private static byte[] generateHWID() {
        try {
            MessageDigest hash = MessageDigest.getInstance("MD5");
            StringBuilder s = new StringBuilder();
            s.append(System.getProperty("os.name"));
            s.append(System.getProperty("os.arch"));
            s.append(System.getProperty("os.version"));
            s.append(Runtime.getRuntime().availableProcessors());
            s.append(System.getenv("PROCESSOR_IDENTIFIER"));
            s.append(System.getenv("PROCESSOR_ARCHITECTURE"));
            s.append(System.getenv("PROCESSOR_ARCHITEW6432"));
            s.append(System.getenv("NUMBER_OF_PROCESSORS"));
            return hash.digest(s.toString().getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Algorithm wasn't found.", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0xF];
        }
        return new String(hexChars);
    }
}
//this shit looks like a logger but isnt
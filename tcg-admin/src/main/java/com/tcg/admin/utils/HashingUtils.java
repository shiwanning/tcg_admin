package com.tcg.admin.utils;


import java.security.MessageDigest;

import com.tcg.admin.common.exception.AdminServiceBaseException;

public class HashingUtils {

    public static final String MD5 = "md5";
    public static final String SHA_1 = "SHA-1";

    private HashingUtils() {
    	throw new IllegalStateException("Utility class");
    }

    public static String encryptByMD5(String inputText) {
        return HashingUtils.encrypt(inputText, MD5);
    }

    public static String encryptBySHA1(String inputText) {
        return HashingUtils.encrypt(inputText, SHA_1);
    }


    private static String encrypt(String inputText, String algorithmName) {

        if (inputText == null || "".equals(inputText.trim())) {
            throw new IllegalArgumentException("String can't be empty");
        }

        if (algorithmName == null || "".equals(algorithmName.trim())) {
            throw new IllegalArgumentException("AlgorithmName can't be empty");
        }

        try {

            MessageDigest m = MessageDigest.getInstance(algorithmName);
            m.update(inputText.getBytes("UTF8"));
            byte[] s = m.digest();
            return bytesToHex(s);

        } catch (Exception e) {
            throw new AdminServiceBaseException("Encrypted text fail !!", e);
        }
    }

    private static String bytesToHex(byte[] arr) {
    	StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; ++i) {
            sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

}


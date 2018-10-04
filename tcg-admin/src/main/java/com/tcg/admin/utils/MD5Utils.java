package com.tcg.admin.utils;

/**
 * MD5 加密使用操作
 */
public class MD5Utils {

    private MD5Utils() {

    }

    /**
     * MessageDigest encrypt default md5
     * @param inputText
     * @return
     */
    public static String encrypt(String inputText) {
        return HashingUtils.encryptByMD5(inputText);
    }
    
    public static String encrypt(long inputText) {
        return HashingUtils.encryptByMD5(String.valueOf(inputText));
    }
    
}

package com.tcg.admin.utils;

import java.io.Serializable;
import java.util.Random;

public class RandomPasswordGenerator implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6607973731620906823L;
    
    private static final String SOURCE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    private static final int DIGIS_TO_GEN = 8;

    
    
    /**
     * 自动生成一个8位数的密码
     */
    public String create() {

        int range = SOURCE.length() - 1;

        Random random = new Random();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < DIGIS_TO_GEN; i++) {
            str.append(SOURCE.charAt(random.nextInt(range)));
        }

        return str.toString();
    }

    public String makeRandomPassword(){
        char charr[] = "abcdefghijklmnopqrstuvwxyz!@#$%^&*ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();


        String reg = "^(?:(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9])).{8,16}$";
        StringBuilder sb = null;
        Random r = new Random();
        while(true){
            sb = new StringBuilder();
            for (int x = 0; x < (Math.random()*8 + 8); ++x) {
                sb.append(charr[r.nextInt(charr.length)]);
            }
            if(sb.toString().matches(reg)){
                break;
            }
        }

        return sb.toString();
    }

    /**
     * 自动生成一个N位数的密码
     */
    public String create(int n) {

        int range = SOURCE.length() - 1;

        Random random = new Random();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < n; i++) {
            str.append(SOURCE.charAt(random.nextInt(range)));
        }
        return str.toString();
    }

}

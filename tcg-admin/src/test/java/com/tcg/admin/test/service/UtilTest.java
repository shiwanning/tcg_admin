package com.tcg.admin.test.service;




import org.junit.Test;

import java.util.Random;

public class UtilTest {



	@Test
	public void testDone() {

		char charr[] = "abcdefghijklmnopqrstuvwxyz!@#$%^&*ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();


		String reg = "^(?:(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9])).{8,16}$";
		StringBuilder sb = null;
		while(true){
			sb = new StringBuilder();
			Random r = new Random();
			for (int x = 0; x < (Math.random()*8 + 8); ++x) {
				sb.append(charr[r.nextInt(charr.length)]);
			}
			if(sb.toString().matches(reg)){
				break;
			}
		}
		System.out.println("Asd123.a".matches(reg));
	}



}

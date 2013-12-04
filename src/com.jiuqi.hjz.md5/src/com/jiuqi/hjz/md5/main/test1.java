package com.jiuqi.hjz.md5.main;

public class test1 {
	
	static final char digits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8',

        '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	public static void main(String[] args) {
		int Num = 255;// 要转换的数字

		int length = 32;

		char[] result = new char[length];

		do {

			result[--length] = digits[Num & 15];

			Num >>>= 4;

		} while (Num != 0);

		for (int i = length; i < result.length; i++) {

			System.out.println(result[i]);
		}
		
		int number[] = {0,0,0,0,0,0,0,0};
		int hex = 0xFFFFFF00;
		for(int i = 0;i<200;i++){
			System.out.println(String.format("%X", hex));
		}
	}
	
}

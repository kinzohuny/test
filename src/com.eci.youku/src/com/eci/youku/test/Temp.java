package com.eci.youku.test;

public class Temp {

	public static void main(String[] args) {
		int i = 0;
		try {
			i=2;
			throw new RuntimeException("Exception");
		} catch (Exception e) {
			System.out.println(i);
			System.out.println(e.getMessage());
		}
		System.out.println(i);
	}
}

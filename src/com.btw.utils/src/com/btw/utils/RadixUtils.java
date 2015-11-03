package com.btw.utils;

import java.util.Calendar;


public class RadixUtils {

	//字典
	private final static byte[] DICT= "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_-+=[]{}\\|;:'\",.<>/?".getBytes();
	private final static long MAX_RADIX = DICT.length;
	
	public static String changeRadix(long l, long radix){
		
		if(radix>MAX_RADIX){
			throw new IllegalArgumentException(radix + " is bigger than the MAX_RADIX="+MAX_RADIX);
		}else{
			int i=0;
			
			while(l>=pow(radix, i+1)||l<pow(radix, i)){
				i++;
			}
			byte[] result = new byte[i+1];	
			int j=0;
			while(i>0){
				result[j] = DICT[(int)(l/pow(radix, i))];
				l=l-(l/pow(radix, i)*pow(radix, i));
				i--;
				j++;
			}
			result[j] = DICT[(int)(l)];
			return new String(result);
		}
	}
	
	/**
	 * 计算x的y次方
	 * @param x
	 * @param y
	 * @return
	 */
	private static long pow(long x, long y){
		if(y<1){
			return 0;
		}else{
			long result = x;
			while(y>1){
				result *= x;
				y--;
			}
			return result;
		}
	}

	public static void main(String[] args) {
		Calendar date = Calendar.getInstance();
		long  time = date.getTimeInMillis();
		date.clear();
		date.set(2000, 1, 1);
		long basetime = date.getTimeInMillis();

		System.out.println(changeRadix(time, 62));
		System.out.println(changeRadix(time-basetime, 62));
	}
	
}


package com.eci.youku.util;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;

import com.alibaba.fastjson.JSON;

public class StringUtils {

	public static boolean isEmpty(String str){
		if(str==null||str.length()==0){
			return true;
		}else{
			return false;
		}
	}

	public static boolean isNotEmpty(String str){
		return !isEmpty(str);
	}
	
	public static String md5(String source) {

		StringBuffer sb = new StringBuffer(32);

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(source.getBytes("utf-8"));
			
			for (int i = 0; i < array.length; i++) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
		} catch (Exception e) {
			// logger.error(“Can not encode the string ‘” + source + “‘ to MD5!”, e);
			return null;
		}

		return sb.toString();
	}
	
	public static String toJSON(Object obj){
		return JSON.toJSONString(obj);
	}
	
	public static <T> T fromJSON(Class<T> clazz, String jsonObject){
		return JSON.parseObject(jsonObject, clazz);
	}
	
	public static String arrayToSting(Object[] objectArray){
		if(objectArray==null){
			return null;
		}
		if(objectArray.length==0){
			return "[]";
		}
		StringBuffer buffer = new StringBuffer("[");
		for(Object object : objectArray){
			buffer.append(object==null?"":String.valueOf(object)).append(",");
		}
		buffer.setLength(buffer.length()-1);
		buffer.append("]");
		return buffer.toString();
	}
	
	public static String toString(Object obj){
		if(obj==null){
			return null;
		}else{
			try {
				return String.valueOf(obj);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	/**
	 * if can not convert, return null;
	 * @param str
	 * @return
	 */
	public static Long toLong(String str){
		if(isEmpty(str)){
			return null;
		}else{
			try {
				return Long.valueOf(str.trim());
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	/**
	 * if can not convert, return null;
	 * @param str
	 * @return
	 */
	public static Integer toInteger(String str){
		if(isEmpty(str)){
			return null;
		}else{
			try {
				return Integer.valueOf(str.trim());
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	/**
	 * if can not convert, return 0;
	 * @param str
	 * @return
	 */
	public static int toInt(String str){
		Integer i = toInteger(str);
		return i==null?0:i;
	}
	
	public static BigDecimal toBigDecimal(String str){
		if(isEmpty(str)){
			return null;
		}else{
			try {
				return new BigDecimal(str.trim());
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	public static String formatDouble2(double d){
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		return decimalFormat.format(d);
	}
	
	public static boolean contentChinese(String str) {
		boolean res = false;
		if (str == null || str == "")
			return false;
		char[] c = str.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (!isLetter(c[i])) {
				res = true;
				break;
			}
		}
		return res;
	}
	
	public static boolean isLetter(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}
	
	public static boolean isLetter(String string) {
		if (isEmpty(string) == true)
			return false;
		byte[] tempbyte = string.getBytes();
		for (int i = 0; i < string.length(); i++) {
			if ((tempbyte[i] < 65) || (tempbyte[i] > 122)
					|| ((tempbyte[i] > 90) & (tempbyte[i] < 97))) {
				return false;
			}
		}
		return true;
	}
	
}

package com.eci.roy.util;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String dateToString(Date date){
		if(date==null){
			return "";
		}else{
			return sdf.format(date);
		}
	}
}

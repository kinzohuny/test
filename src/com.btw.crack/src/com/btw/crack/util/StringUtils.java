package com.btw.crack.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {

	/**
	 * format : yyyy-MM-dd HH:mm:ss:SSS
	 * @return
	 */
	public static String getNowStr(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		return format.format(new Date());
	}
	
	public static boolean isEmpty(String str){
		return str==null||str.length()==0;
	}
	
	public static boolean isNotEmpty(String str){
		return !isEmpty(str);
	}
}

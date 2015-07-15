package com.btw.crack.util;

public class SystemUtils {

	public static boolean isDebug = false;
	
	public static void stopWithTimeShow(){
		System.out.println("stop time : " + StringUtils.getNowStr());
		System.exit(1);
	}
	
	public static void stop(String reason){
		System.out.println(reason);
		System.exit(1);
	}
	
	public static void error(Exception e){
		if(isDebug){
			e.printStackTrace();
		}else{
			System.out.println("Error:" + e.getMessage());
		}
		System.exit(2);
	}
	
	public static void warn(Exception e){
		if(isDebug){
			e.printStackTrace();
		}else{
			System.out.println("Warnning:" + e.getMessage());
		}
	}
	
	public static void info(String info){
		if(isDebug){
			System.out.println(info);
		}
	}
}

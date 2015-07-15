package com.btw.query.taobao.shop.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Constants {

	public static Map<String, String> cookies;
	
	static{
		cookies = getCookies();
	}
	
	public static String getCookiesStr(){
		StringBuilder builder = new StringBuilder();
		for(String key : Constants.cookies.keySet()){
			builder.append(key).append("=").append(cookies.get(key)).append("; ");
		}
		String result = builder.toString();
		return result.substring(0, result.length()-2);
	}
	
	private static Map<String, String> getCookies(){
		
        StringBuffer sb = new StringBuffer();
        try {
        	//该cookies文件可由firebug中cookie页签下的导出本站cookies功能直接导出
                Reader r = new InputStreamReader(Constants.class.getResourceAsStream("/resources/cookies.txt"), "UTF-8");
                int length = 0;
                for (char[] c = new char[1024]; (length = r.read(c)) != -1;) {
                        sb.append(c, 0, length);
                }
                r.close();
        } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        String fileStr = sb.toString();
        
		String[] cookieStrs = fileStr.split("\r\n");
		Map<String, String> map = new HashMap<String, String>();
		for(String str : cookieStrs){
			String line = str.trim();
			String[] strs = line.split("\t");
			map.put(strs[strs.length-2], strs[strs.length-1]);
		}
		return map;
	}
	
	public static void main(String[] args) {
		System.out.println(getCookiesStr());
	}
}

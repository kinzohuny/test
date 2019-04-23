package com.btw.file.handle.main;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.btw.file.handle.utils.HttpUtils;

/**
 * 域名查询
 * @author Kinzo
 *
 */
public class QueryDomain {

	public static void main(String[] args) {
		String test = "aaaa";
		int i = 1;
		while(true){
			System.out.println(i++ +"  "+ test);
			String next = getNext(test);
			
			if(next.equals(test)){
				return;
			}else{
				test = next;
			}
			
		}
		/*for(char i : chars){
			for(char j : chars){
				for(char m : chars){
					for(char n : chars){
						char[] domain = {i,j,m,n};
						query(String.valueOf(domain), "com", false);
					}
				}
			}
		}*/
	}
	
	public static final String API_PANDA_URL = "http://panda.www.net.cn/cgi-bin/check.cgi";
	public static final String API_PANDA_PARA = "area_domain";
	public static final char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	
	public static void query(String domain, String suffix, boolean isOnlyAvailable){
		String para = API_PANDA_PARA+"="+domain+"."+suffix;
		String result = HttpUtils.sendPost(API_PANDA_URL, para);
		printResult(result, isOnlyAvailable, para);
	}
	
	public static void printResult(String xml, boolean isOnlyAvailable, String para){
		Document document = null;
		try {
			document = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			System.out.println(para);
			System.out.println(xml);
			System.exit(0);;
		}
		Element root = document.getRootElement();
		String returncode = root.element("returncode").getText();
		if("200".equals(returncode)){
			String key = root.element("key").getText();
			String original = root.element("original").getText();
			if(original.startsWith("210")){
				System.out.println(key+"\t"+original);
			}else if(!isOnlyAvailable){
				System.out.println(key+"\t"+original);
			}
		}
	}
	
	public static String getNext(String str){
		char[] charArray = str.toCharArray();
		charAdd(charArray, charArray.length-1);
		return String.valueOf(charArray);
	}

	private static boolean charAdd(char[] charArray, int i) {
		if(i<0){
			return false;
		}else if(charArray[i]+1<='z'){
			charArray[i]++;
			return true;
		}else if(i>0&&charArray[i]+1>'z'){
			if(charAdd(charArray, i-1)){
				charArray[i]='a';
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
		
	}
}

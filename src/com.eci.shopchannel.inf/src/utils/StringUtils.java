package utils;

import java.util.List;

import model.ItemModel;

import org.eclipse.jetty.util.security.Credential.MD5;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

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
	
	public static String md5(String str){
		return MD5.digest(str);
	}
	
	public static void main(String[] args) {
		System.out.println(md5("admin"));
	}
	
	public static String toJSON(List<ItemModel> list){
		SimplePropertyPreFilter filter = new SimplePropertyPreFilter(ItemModel.class, "long_title","identify","url","wap_url","img_url","price","cheap","site","site_url","tagid","tag","post");
		return JSON.toJSONString(list,filter);
	}
	
	/**
	 * 字符串转换unicode，仅转换非ACSII字符
	 */
	public static String string2Unicode(String string) {
	 
	    StringBuffer unicode = new StringBuffer();
	    for (int i = 0; i < string.length(); i++) {
	        // 取出每一个字符
	        char c = string.charAt(i);
	        // 转换为unicode
        	unicode.append(c>127?"\\u" + Integer.toHexString(c):c);
	    }
	    return unicode.toString();
	}
}

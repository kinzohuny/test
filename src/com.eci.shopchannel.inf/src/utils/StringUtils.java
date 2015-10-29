package utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import model.ItemModel;

import org.eclipse.jetty.util.security.Credential.MD5;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
	
	public static String toJSON(List<ItemModel> list){
		SimplePropertyPreFilter filter = new SimplePropertyPreFilter(ItemModel.class, "long_title","identify","url","wapurl","img_url","price","price_new","cheap","site","site_url","tagid","tag","post");
		return JSON.toJSONString(list,filter,SerializerFeature.WriteMapNullValue,SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteNullNumberAsZero,SerializerFeature.BrowserCompatible);
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
				return Long.valueOf(str);
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
				return Integer.valueOf(str);
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
				return new BigDecimal(str);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	public static String formatDouble2(double d){
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		return decimalFormat.format(d);
	}
	
	public static void main(String[] args) {
		int i = toInt(null);
		System.out.println(i);
		int j = toInt("123");
		System.out.println(j);
		int k = toInt("w");
		System.out.println(k);
//		System.out.println(toBigDecimal(null));
//		String str = "";
//		System.out.println(new BigDecimal(str));
	}
}

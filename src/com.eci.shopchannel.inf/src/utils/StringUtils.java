package utils;

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
	
	public static void main(String[] args) {
		System.out.println(md5("admin"));
	}
	
	public static String toJSON(List<ItemModel> list){
		SimplePropertyPreFilter filter = new SimplePropertyPreFilter(ItemModel.class, "long_title","identify","url","wapurl","img_url","price","cheap","site","site_url","tagid","tag","post");
		return JSON.toJSONString(list,filter,SerializerFeature.WriteMapNullValue,SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteNullNumberAsZero,SerializerFeature.BrowserCompatible);
	}
	
	public static String toSting(Object[] objectArray){
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
}

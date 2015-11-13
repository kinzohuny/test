package com.eci.youku.data.push.utils;

import java.util.List;

import com.alibaba.fastjson.JSON;

public class JsonUtils {

	public static String toJson(Object obj){
		return JSON.toJSONString(obj);
	}
	
	public static <T> T parseObject(Class<T> clazz, String jsonString){
		return JSON.parseObject(jsonString, clazz);
	}
	
	public static <T> List<T> parseList(Class<T> clazz, String jsonString){
		return JSON.parseArray(jsonString, clazz);
	}
}

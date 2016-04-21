package com.btw.utils.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.btw.utils.web.WebUtils;

public class Test {

	public static void main(String[] args) throws Exception {
//		try {
//			System.out.println(WebUtils.doPost("http://server.sfinx.cn/ip", null, 3000, 3000));
//			System.out.println(WebUtils.doPost("http://www.baidu.com", null, 3000, 3000));
//		} catch (IOException e) {
//			e.printStackTrace();
//		};
		Map<String,String> map = new HashMap<String,String>();
		map.put("a", "1");
		System.out.println("--POST");
		System.out.println(WebUtils.doPost(
				"http://localhost:9999/php/qualification/test/httptest.php",
				map));
		
		System.out.println("--GET1");
		System.out.println(WebUtils.doGet(
				"http://localhost:9999/php/qualification/test/httptest.php",
				map));
		System.out.println("--GET2");
		System.out.println(WebUtils.doGet(
				"http://localhost:9999/php/qualification/test/httptest.php?a=1"));
	}
}

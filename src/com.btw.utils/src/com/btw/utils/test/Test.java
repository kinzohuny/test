package com.btw.utils.test;

import java.io.IOException;

import com.btw.utils.web.WebUtils;

public class Test {

	public static void main(String[] args) {
		try {
			System.out.println(WebUtils.doPost("http://server.sfinx.cn/ip", null, 3000, 3000));
			System.out.println(WebUtils.doPost("http://www.baidu.com", null, 3000, 3000));
		} catch (IOException e) {
			e.printStackTrace();
		};
	}
}

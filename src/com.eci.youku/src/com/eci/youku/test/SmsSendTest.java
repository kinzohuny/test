package com.eci.youku.test;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.taobao.api.internal.util.WebUtils;

public class SmsSendTest {

	public static void main(String[] args) {
		
		try {
			String url = "http://127.0.0.1:8008/smsSend";
			Map<String, String> params = new HashMap<String, String>();
			String timestamp = getSerial();
			
			//必填参数
			params.put("timestamp", timestamp);//时间戳
			params.put("token", md5("pinkehudong"+timestamp));//鉴权码
			params.put("sid", "123");//商户ID
			params.put("mobile", "13601243558");//接收手机号
			params.put("tid", "640317480");//模板ID
			params.put("args_code", "111111");//模板参数，汉字需要URL编码，UTF-8字符集
			params.put("args_name", URLEncoder.encode("品客互动", "UTF-8"));//模板参数，汉字需要URL编码，UTF-8字符集
			
			//可为空参数
			params.put("signId", "82");//签名ID，为空时使用默认签名
			params.put("aid", "234");//活动ID
			params.put("nick", URLEncoder.encode("张三", "UTF-8"));//接收人昵称，，汉字需要URL编码，UTF-8字符集
			params.put("expandId", "345");//扩展码
			System.out.println(url+"?"+String.valueOf(params));
			System.out.println(WebUtils.doPost(url, params, 1000, 1000));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	private static int serialNum = 1;
	/**
	 * 获得带流水的时间戳
	 * @return
	 */
	private static String getSerial(){
		if(serialNum>9999){
			serialNum=1;
		}
		
		String serialStr = String.format("%04d", serialNum++);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		return formatter.format(date)+serialStr;
	}
	
	/**
	 * md5加密
	 * @param source
	 * @return
	 */
	public static String md5(String source) {

		StringBuffer sb = new StringBuffer(32);

		try {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] array = md.digest(source.getBytes("utf-8"));

		for (int i = 0; i < array.length; i++) {
		sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
		}
		} catch (Exception e) {
		//logger.error("Can not encode the string [" + source + "] to MD5!", e);
		return null;
		}

		return sb.toString();
	}
}

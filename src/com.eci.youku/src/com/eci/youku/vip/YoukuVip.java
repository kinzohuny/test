package com.eci.youku.vip;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.eci.youku.util.StringUtils;

public class YoukuVip {

	private static final String SIGN_TYPE = "MD5";
	private static final String URL = "https://premiumapi.youku.com/proxy/operators_buy_vip.json";
	private static final String TOKEN = "";
	
	private String mobile;
	private String timestamp;
	private String out_order_no;
	private YoukuVipResult result;
	
	public YoukuVip(String mobile){
		this.setMobile(mobile);
	}
	
	public YoukuVipResult create(){
		if(result==null){
			StringBuffer url = new StringBuffer(URL);
			url.append("?mobile=").append(mobile);
			url.append("&timestamp=").append(getTimestamp());
			url.append("&out_order_no=").append(getOut_order_no());
			url.append("&sign_type=").append(SIGN_TYPE);
			url.append("&sign=").append(getSign());
			System.out.println(url);
		}
		return result;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	private String getTimestamp() {
		if(timestamp==null){
			timestamp = String.valueOf(System.currentTimeMillis());
		}
		return timestamp;
	}

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private static String orderKey = null;
	private static long orderNum = 1;
	
	private synchronized String getOut_order_no() {
		if(out_order_no==null){
			String key = SDF.format(new Date());
			if(key.equals(orderKey)){
				orderNum++;
			}else{
				orderKey = key;
				orderNum = 1;
			}
			out_order_no = orderKey+String.format("%04d", orderNum);
		}
		return out_order_no;
	}

	private String getSign() {
		String[] token = {TOKEN,mobile,String.valueOf(getTimestamp()),getOut_order_no(),SIGN_TYPE} ;
		Arrays.sort(token);
		return StringUtils.md5(joinString(token));
	}
	
	private String joinString(String[] strArr){
		StringBuffer sb = new StringBuffer();
		for(String s : strArr){
			sb.append(s);
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		new YoukuVip("13601243558").create();
	}
}

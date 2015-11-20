package com.eci.youku.vip;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.eci.youku.util.StringUtils;
import com.eci.youku.util.WebUtils;

public class YoukuVip {

//	private static final String SIGN_TYPE = "md5";
	private static final String SIGN_TYPE = "sha1";
	private static final String URL = "https://premiumapi.youku.com/proxy/operators_buy_vip.json";
	
	//月卡：公钥2015182	私钥	9e3cfeeac733f7dbadaa1e909c7e290b
	private static final String MONTH_TOKEN = "K!U@JHF$V^MX";
	private static final String MONTH_PARTNER = "1443";
	
	//季卡： 公钥	2015182	私钥	9e3cfeeac733f7dbadaa1e909c7e290b
	private static final String QUARTER_TOKEN = "K^TIHPO&RGWY";
	private static final String QUARTER_PARTNER = "1447";
	
	private static final Logger logger = Logger.getLogger(YoukuVip.class);
	
	private String mobile;
	private String timestamp;
	private String out_order_no;
	private YoukuVipResult result;
	private String back;
	
	public YoukuVip(String mobile){
		this.mobile = mobile;
	}
	
	public String createMonth() throws IOException{
		return create(MONTH_TOKEN, MONTH_PARTNER);
	}
	
	public String createQuarter() throws IOException{
		return create(QUARTER_TOKEN, QUARTER_PARTNER);
	}
	
	private String create(String token, String partner) throws IOException{
		if(back==null){
			StringBuffer url = new StringBuffer(URL);
			url.append("?sign=").append(getSign(token, partner));
			url.append("&timestamp=").append(getTimestamp());
			url.append("&out_order_no=").append(getOut_order_no());
			url.append("&mobile=").append(mobile);
			url.append("&partner=").append(partner);
			url.append("&sign_type=").append(SIGN_TYPE);
			logger.debug(url);
			//真实发放
			back = WebUtils.doGet(url.toString(),null);
			//测试发放1
//			back= "{\"error\":1,\"cost\":0.000001123,\"password\":\"xxxxxxxxxx\",\"mobile\":\""+mobile+"\"}";
			//测试发放2
//			back= "{\"error\":1,\"cost\":0.000001123}";
			logger.info(back);
			
		}
		return back;
	}
	
	public String getBack(){
		return back;
	}
	
	public YoukuVipResult getResult(){
		if(result == null){
			result = JSON.parseObject(back, YoukuVipResult.class);
		}
		return result;
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

	private String getSign(String token, String partner) {
		String[] paras = {token,String.valueOf(getTimestamp()),getOut_order_no(),mobile,partner} ;
		Arrays.sort(paras);
		return StringUtils.SHA1(joinString(paras));
	}
	
	private String joinString(String[] strArr){
		StringBuffer sb = new StringBuffer();
		for(String s : strArr){
			sb.append(s);
		}
		return sb.toString();
	}
}

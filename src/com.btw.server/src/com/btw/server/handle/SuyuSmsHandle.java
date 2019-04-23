package com.btw.server.handle;

import org.apache.log4j.Logger;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.BmcResult;
import com.taobao.api.domain.SendMessageRequest;
import com.taobao.api.request.OpenSmsSendmsgRequest;
import com.taobao.api.response.OpenSmsSendmsgResponse;

public class SuyuSmsHandle {
	
	public static Logger log = Logger.getLogger(SuyuSmsHandle.class);

	/**
	 * 
	 * @param templateId 模板id
	 * @param contextString 模板上下文,JSON串
	 * @param mobile 接收手机号
	 * @return
	 * @throws ApiException
	 * @throws RuntimeException
	 */
	public static BmcResult send(Long templateId, String contextString, 
			String mobile) throws ApiException,RuntimeException{
		
		SendMessageRequest sendMessageRequest = new SendMessageRequest();
		sendMessageRequest.setTemplateId(templateId);
		sendMessageRequest.setSignatureId(SIGNATUREID);
		sendMessageRequest.setContextString(contextString);
//		sendMessageRequest.setExternalId(externalId);
		sendMessageRequest.setMobile(mobile);
//		sendMessageRequest.setMobileLimitInTime(60L);
		OpenSmsSendmsgRequest openSmsSendmsgRequest = new OpenSmsSendmsgRequest();
		openSmsSendmsgRequest.setSendMessageRequest(sendMessageRequest);
		
		//真实发送
		OpenSmsSendmsgResponse openSmsSendmsgResponse = initClient().execute(openSmsSendmsgRequest);
		
		//返回发送结果
		if(openSmsSendmsgResponse.isSuccess()){
			return openSmsSendmsgResponse.getResult();
		}else{
			throw new RuntimeException(openSmsSendmsgResponse.getSubMsg());
		}
	}
	
	//接口地址
	private static final String URL = "http://gw.api.taobao.com/router/rest";
	
	//大狼灰的小破店
	private final static String APP_KEY = "23256342";
	private final static String SECRET = "354b0a1bcba36a8f803ec7b54f02dfc8";
	private final static Long SIGNATUREID = 374L;
	
	// 淘宝API连接
	private static TaobaoClient client = null;
	/**
	 * 获得正式淘宝客户端
	 * @return
	 */
	private static synchronized TaobaoClient initClient() {
		if (client == null) {
				client = new DefaultTaobaoClient(URL, APP_KEY, SECRET);
		}
		return client;
	}
}

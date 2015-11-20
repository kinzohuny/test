package com.eci.youku.handle;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.BmcResult;
import com.taobao.api.domain.SendMessageRequest;
import com.taobao.api.request.OpenSmsSendmsgRequest;
import com.taobao.api.response.OpenSmsSendmsgResponse;

public class SuyuSmsHandle {
	
	private static Logger logger = Logger.getLogger(SuyuSmsHandle.class);

	/**
	 * 
	 * @param templateId 模板id
	 * @param signatureId 签名id
	 * @param contextString 模板上下文,JSON串
	 * @param externalId 外部id
	 * @param mobile 
	 * @return
	 * @throws ApiException
	 * @throws RuntimeException
	 */
	public static BmcResult send(Long templateId, String contextString, 
			String externalId, String mobile) throws ApiException,RuntimeException{
		
		SendMessageRequest sendMessageRequest = new SendMessageRequest();
		sendMessageRequest.setTemplateId(templateId);
		sendMessageRequest.setSignatureId(SIGNATUREID);
		sendMessageRequest.setContextString(contextString);
		sendMessageRequest.setExternalId(externalId);
		sendMessageRequest.setMobile(mobile);
		sendMessageRequest.setMobileLimitInTime(50L);
		OpenSmsSendmsgRequest openSmsSendmsgRequest = new OpenSmsSendmsgRequest();
		openSmsSendmsgRequest.setSendMessageRequest(sendMessageRequest);
		
		//真实发送
		OpenSmsSendmsgResponse openSmsSendmsgResponse = initClient().execute(openSmsSendmsgRequest);
		//测试发送---起
//		OpenSmsSendmsgResponse openSmsSendmsgResponse = new OpenSmsSendmsgResponse();
//		BmcResult result = new BmcResult();
//		result.setSuccessful(true);
//		result.setCode(1L);
//		result.setMessage("发送成功！");
//		SendMessageResult datas = new SendMessageResult();
//		datas.setTaskId(System.currentTimeMillis());
//		result.setDatas(datas);
//		openSmsSendmsgResponse.setResult(result);
		//测试发送---止
		
		//返回发送结果
		if(openSmsSendmsgResponse.isSuccess()){
			logger.info(JSON.toJSONString(openSmsSendmsgResponse));
			return openSmsSendmsgResponse.getResult();
		}else{
			throw new RuntimeException(openSmsSendmsgResponse.getSubMsg());
		}
	}
	
	//接口地址
	private static final String URL = "http://gw.api.taobao.com/router/rest";
	
	//客赞CRM_会员管理_二次营销专家
	private final static String APP_KEY = "21225052";
	private final static String SECRET = "80d3f73d1ce4320b882142cabc2995ef";
	private final static Long SIGNATUREID = 81L;
	
	//客赞CRM全容器
//	private final static String APP_KEY = "21631175";
//	private final static String SECRET = "98a4a3301e818c408f3ca7b26fa5bdbf";
//	private final static Long SIGNATUREID = 82L;
	
	//智慧门店
//	private final static String APP_KEY = "23256450";
//	private final static String SECRET = "86ff2c7c1d38e7745c99c03929f32c56";
//	private final static Long SIGNATUREID = 569L;
	
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

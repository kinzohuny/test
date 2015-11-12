package com.eci.youku.constant;

public enum Msg {

	ERR_WRONG_TOKEN(-1,"鉴权失败"),
	
	SEND_SUCCESSFUL(1,"ok"),
	SEND_FAILED(2,"发送失败"),
	
	VALIDATE_WRONG_SID(21,"sid参数不正确"),
	VALIDATE_WRONG_TEMPLATE(22,"tid参数不正确"),
	VALIDATE_WRONG_MOBILE(23,"mobile参数不正确"),
	VALIDATE_WRONG_SIGNID(24,"signId参数不正确"),
	
	COST_SUCCESSFUL(60,"扣费成功"),
	ERROR_OVERDUE(61,"账号余额不足"),
	ERROR_COST_FAILED(62,"扣费失败"),
	FREEZE_SUCCESSFUL(65,"费用冻结成功"),
	UNFREEZE_SUCCESSFUL(66,"费用解冻成功"),
	FREEZE_FAILED(67,"费用冻结失败"),
	UNFREEZE_FAILED(68,"费用解冻失败"),
	
	
	EXCEPTION_API(91,"API异常"),
	EXCEPTION_ARGS(92,"API参数错误"),
	EXCEPTION_SQL(93,"SQL错误"),
	EXCEPTION_UNKNOWN(99,"未知的异常");
	

	
	private int id;
	private String msg;
	
	Msg(int id,String msg){
		this.id = id;
		this.msg = msg;
	}
	
	public int getId(){
		return id;
	}
	
	public String getMsg(){
		return msg;
	}
	
	public String getAllMsg(){
		return id+":"+msg;
	}
	
	public String getJsonMsg(){
		return "{\"errcode\":"+id+",\"errmsg\":\""+msg+"\"}";
	}
}

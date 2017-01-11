package com.eci.roy.model;

import java.util.Calendar;

import com.eci.roy.constant.Constants;

public class ForbiddenModel {

	private String ip;
	private int times = 0;
	private Long timestamp;
	
	public ForbiddenModel(String ip){
		this.ip = ip;
		this.timestamp = Calendar.getInstance().getTimeInMillis(); 
	}
	
	public boolean isTimeout(){
		return Calendar.getInstance().getTimeInMillis() - timestamp > Constants.IP_FORBIDDEN_TIMEOUT_MS;
	}
	
	public int addTimes(){
		timestamp = Calendar.getInstance().getTimeInMillis(); 
		return ++times;
	}
	
	public boolean isForbid(){
		return times>Constants.IP_FORBIDDEN_TIMES;
	}
	
	public String getIp(){
		return ip;
	}
}

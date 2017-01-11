package com.eci.roy.model;

import java.util.Date;

import com.eci.roy.util.StringUtils;

public class LogModel {

	private Long id;
	private Date time;
	private Long user_id;
	private String content;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getTime() {
		return time;
	}
	public String getTimeStr() {
		return StringUtils.dateToString(time);
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}

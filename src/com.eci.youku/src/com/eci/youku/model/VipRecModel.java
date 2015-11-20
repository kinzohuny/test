package com.eci.youku.model;

import java.sql.Timestamp;

public class VipRecModel {

	private Long id;
	private String mobile;
	private Integer vip_type;
	private Integer vip_status;
	private String vip_back;
	private String vip_mobile;
	private String vip_password;
	private String vip_errmsg;
	private Timestamp vip_time;
	private Integer sms_status;
	private String sms_errormsg;
	private Timestamp sms_time;
	private Timestamp created;
	private Timestamp updated;
	private Integer isManual;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Integer getVip_type() {
		return vip_type;
	}
	public void setVip_type(Integer vip_type) {
		this.vip_type = vip_type;
	}
	public Integer getVip_status() {
		return vip_status;
	}
	public void setVip_status(Integer vip_status) {
		this.vip_status = vip_status;
	}
	public String getVip_back() {
		return vip_back;
	}
	public void setVip_back(String vip_back) {
		this.vip_back = vip_back;
	}
	public String getVip_mobile() {
		return vip_mobile;
	}
	public void setVip_mobile(String vip_mobile) {
		this.vip_mobile = vip_mobile;
	}
	public String getVip_password() {
		return vip_password;
	}
	public void setVip_password(String vip_password) {
		this.vip_password = vip_password;
	}
	public String getVip_errmsg() {
		return vip_errmsg;
	}
	public void setVip_errmsg(String vip_errmsg) {
		this.vip_errmsg = vip_errmsg;
	}
	public Timestamp getVip_time() {
		return vip_time;
	}
	public void setVip_time(Timestamp vip_time) {
		this.vip_time = vip_time;
	}
	public Integer getSms_status() {
		return sms_status;
	}
	public void setSms_status(Integer sms_status) {
		this.sms_status = sms_status;
	}
	public String getSms_errormsg() {
		return sms_errormsg;
	}
	public void setSms_errormsg(String sms_errormsg) {
		this.sms_errormsg = sms_errormsg;
	}
	public Timestamp getSms_time() {
		return sms_time;
	}
	public void setSms_time(Timestamp sms_time) {
		this.sms_time = sms_time;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	public Timestamp getUpdated() {
		return updated;
	}
	public void setUpdated(Timestamp updated) {
		this.updated = updated;
	}
	public Integer getIsManual() {
		return isManual;
	}
	public void setIsManual(Integer isManual) {
		this.isManual = isManual;
	}
	
}

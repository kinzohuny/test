package com.eci.youku.model;

import java.sql.Timestamp;

public class MobileModel {

	private String mobile;
	private Timestamp created;
	private Timestamp updated;
	private String shop_mobile;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	public String getShop_mobile() {
		return shop_mobile;
	}
	public void setShop_mobile(String shop_mobile) {
		this.shop_mobile = shop_mobile;
	}
	
	
}

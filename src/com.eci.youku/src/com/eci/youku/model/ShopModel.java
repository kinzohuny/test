package com.eci.youku.model;

import java.sql.Timestamp;

public class ShopModel {

	private Long sid;
	private String title;
	private String url;
	private String tk_url;
	private String logo_url;
	private String pic_url;
	private Long sort;
	private Integer status;
	private Timestamp created;
	private Timestamp updated;
	
	private Long item_num;
	private Long item_on_num;
	
	public Long getSid() {
		return sid;
	}
	public void setSid(Long sid) {
		this.sid = sid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTk_url() {
		return tk_url;
	}
	public void setTk_url(String tk_url) {
		this.tk_url = tk_url;
	}
	public String getLogo_url() {
		return logo_url;
	}
	public void setLogo_url(String logo_url) {
		this.logo_url = logo_url;
	}
	public String getPic_url() {
		return pic_url;
	}
	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}
	public Long getSort() {
		return sort;
	}
	public void setSort(Long sort) {
		this.sort = sort;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
	public Long getItem_num() {
		return item_num;
	}
	public void setItem_num(Long item_num) {
		this.item_num = item_num;
	}
	public Long getItem_on_num() {
		return item_on_num;
	}
	public void setItem_on_num(Long item_on_num) {
		this.item_on_num = item_on_num;
	}

}

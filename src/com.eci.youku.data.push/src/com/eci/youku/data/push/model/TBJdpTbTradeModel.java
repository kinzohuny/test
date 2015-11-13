package com.eci.youku.data.push.model;

import java.sql.Timestamp;

public class TBJdpTbTradeModel {

	private Long tid;
	private String STATUS;
	private String buyer_nick;
	private Timestamp created;
	private Timestamp modified;
	private String jdp_response;
	private Timestamp jdp_created;
	private Timestamp jdp_modified;
	
	public Long getTid() {
		return tid;
	}
	public void setTid(Long tid) {
		this.tid = tid;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getBuyer_nick() {
		return buyer_nick;
	}
	public void setBuyer_nick(String buyer_nick) {
		this.buyer_nick = buyer_nick;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	public Timestamp getModified() {
		return modified;
	}
	public void setModified(Timestamp modified) {
		this.modified = modified;
	}
	public String getJdp_response() {
		return jdp_response;
	}
	public void setJdp_response(String jdp_response) {
		this.jdp_response = jdp_response;
	}
	public Timestamp getJdp_created() {
		return jdp_created;
	}
	public void setJdp_created(Timestamp jdp_created) {
		this.jdp_created = jdp_created;
	}
	public Timestamp getJdp_modified() {
		return jdp_modified;
	}
	public void setJdp_modified(Timestamp jdp_modified) {
		this.jdp_modified = jdp_modified;
	}
	
	
}

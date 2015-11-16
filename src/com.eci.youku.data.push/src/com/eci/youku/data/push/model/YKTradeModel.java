package com.eci.youku.data.push.model;

import java.sql.Timestamp;

public class YKTradeModel {

	private Long tid;
	private Long sid;
	private String STATUS;
	private String seller_nick;
	private String seller_title;
	private String buyer_nick;
	private String payment;
	private String receiver_name;
	private String receiver_mobile;
	private Timestamp pay_time;
	private Timestamp end_time;
	private Timestamp trade_created;
	private Timestamp trade_modified;
	private Timestamp jdp_created;
	private Timestamp jdp_modified;
	private Timestamp created;
	private Timestamp updated;
	
	public Long getTid() {
		return tid;
	}
	public void setTid(Long tid) {
		this.tid = tid;
	}
	public Long getSid() {
		return sid;
	}
	public void setSid(Long sid) {
		this.sid = sid;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getSeller_nick() {
		return seller_nick;
	}
	public void setSeller_nick(String seller_nick) {
		this.seller_nick = seller_nick;
	}
	public String getSeller_title() {
		return seller_title;
	}
	public void setSeller_title(String seller_title) {
		this.seller_title = seller_title;
	}
	public String getBuyer_nick() {
		return buyer_nick;
	}
	public void setBuyer_nick(String buyer_nick) {
		this.buyer_nick = buyer_nick;
	}
	public String getPayment() {
		return payment;
	}
	public void setPayment(String payment) {
		this.payment = payment;
	}
	public String getReceiver_name() {
		return receiver_name;
	}
	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}
	public String getReceiver_mobile() {
		return receiver_mobile;
	}
	public void setReceiver_mobile(String receiver_mobile) {
		this.receiver_mobile = receiver_mobile;
	}
	public Timestamp getPay_time() {
		return pay_time;
	}
	public void setPay_time(Timestamp pay_time) {
		this.pay_time = pay_time;
	}
	public Timestamp getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Timestamp end_time) {
		this.end_time = end_time;
	}
	public Timestamp getTrade_created() {
		return trade_created;
	}
	public void setTrade_created(Timestamp trade_created) {
		this.trade_created = trade_created;
	}
	public Timestamp getTrade_modified() {
		return trade_modified;
	}
	public void setTrade_modified(Timestamp trade_modified) {
		this.trade_modified = trade_modified;
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
	
	
}

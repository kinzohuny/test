package com.eci.youku.model.virtual;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ToVerifyTradeVModel {

	private String mobile;
	private String shop_mobile;
	private String titles;
	private String tids;
	private Long trade_num;
	private Long unfinish_trade;
	private BigDecimal payment_sum;
	private Timestamp mobile_created;
	private Timestamp trade_min_created;
	private Timestamp trade_max_created;
	private Timestamp trade_finish_time;
	private Integer vip_type;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getShop_mobile() {
		return shop_mobile;
	}
	public void setShop_mobile(String shop_mobile) {
		this.shop_mobile = shop_mobile;
	}
	public String getTitles() {
		return titles;
	}
	public void setTitles(String titles) {
		this.titles = titles;
	}
	public String getTids() {
		return tids;
	}
	public void setTids(String tids) {
		this.tids = tids;
	}
	public Long getTrade_num() {
		return trade_num;
	}
	public void setTrade_num(Long trade_num) {
		this.trade_num = trade_num;
	}
	public Long getUnfinish_trade() {
		return unfinish_trade;
	}
	public void setUnfinish_trade(Long unfinish_trade) {
		this.unfinish_trade = unfinish_trade;
	}
	public BigDecimal getPayment_sum() {
		return payment_sum;
	}
	public void setPayment_sum(BigDecimal payment_sum) {
		this.payment_sum = payment_sum;
	}
	public Timestamp getMobile_created() {
		return mobile_created;
	}
	public void setMobile_created(Timestamp mobile_created) {
		this.mobile_created = mobile_created;
	}
	public Timestamp getTrade_min_created() {
		return trade_min_created;
	}
	public void setTrade_min_created(Timestamp trade_min_created) {
		this.trade_min_created = trade_min_created;
	}
	public Timestamp getTrade_max_created() {
		return trade_max_created;
	}
	public void setTrade_max_created(Timestamp trade_max_created) {
		this.trade_max_created = trade_max_created;
	}
	public Timestamp getTrade_finish_time() {
		return trade_finish_time;
	}
	public void setTrade_finish_time(Timestamp trade_finish_time) {
		this.trade_finish_time = trade_finish_time;
	}
	public Integer getVip_type() {
		return vip_type;
	}
	public void setVip_type(Integer vip_type) {
		this.vip_type = vip_type;
	}
	
}

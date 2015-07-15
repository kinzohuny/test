package com.btw.query.taobao.shop.bean;

import java.lang.reflect.Field;

public class ShopInfo {

	//返回值写入：2列：销量、3列：宝贝数、4列：查询店铺结果数、5列：查询链接
	private String title;//店铺名称
	private String saleNum;//销量
	private String itemNum;//宝贝数
	private String state;//所在地
	private String mainCat;//主营
	
	private String queryUrl;//查询链接
	private String queryNum;//查询结果数
	
	public String toString(){
		StringBuilder buidBuilder = new StringBuilder();
		for(Field field :ShopInfo.class.getFields()){
			try {
				buidBuilder.append(field.getName()).append("=").append(field.get(this)).append(";");
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return buidBuilder.toString();
	}
	
	public String toValueString(){
		StringBuilder buidBuilder = new StringBuilder();
		for(Field field :ShopInfo.class.getDeclaredFields()){
			try {
				buidBuilder.append(field.get(this)).append(",");
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return buidBuilder.toString();
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSaleNum() {
		return saleNum;
	}
	public void setSaleNum(String saleNum) {
		this.saleNum = saleNum;
	}
	public String getItemNum() {
		return itemNum;
	}
	public void setItemNum(String itemNum) {
		this.itemNum = itemNum;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getMainSale() {
		return mainCat;
	}
	public void setMainSale(String mainSale) {
		this.mainCat = mainSale;
	}
	public String getQueryUrl() {
		return queryUrl;
	}
	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}
	public String getQueryNum() {
		return queryNum;
	}
	public void setQueryNum(String queryNum) {
		this.queryNum = queryNum;
	}
	
	
}

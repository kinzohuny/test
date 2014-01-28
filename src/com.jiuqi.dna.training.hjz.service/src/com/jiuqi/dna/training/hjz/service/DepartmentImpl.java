package com.jiuqi.dna.training.hjz.service;

import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.training.hjz.service.intf.Department;

public class DepartmentImpl implements Department {
	
	private GUID id;
	private String name;
	private String master;
	private int num;
	private long date;
	private GUID parent;
	private String remark;
	private long order;
	private int hashCode;
	
	public GUID getId() {
		return id;
	}
	public void setId(GUID id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMaster() {
		return master;
	}
	public void setMaster(String master) {
		this.master = master;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public GUID getParent() {
		return parent;
	}
	public void setParent(GUID parent) {
		this.parent = parent;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public long getOrder() {
		return order;
	}
	public void setOrder(long order) {
		this.order = order;
	}
	public int getHashCode() {
		return hashCode;
	}
	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}


}
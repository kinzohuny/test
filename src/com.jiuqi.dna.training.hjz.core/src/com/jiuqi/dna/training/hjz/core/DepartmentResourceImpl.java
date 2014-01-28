package com.jiuqi.dna.training.hjz.core;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.training.hjz.core.intf.DepartmentResource;

@StructClass
public class DepartmentResourceImpl implements DepartmentResource{

	private GUID id;
	private String name;
	private String master;
	private int num;
	private long date;
	private GUID parent;
	private String remark;
	private long order;
	
	public void setId(GUID id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setMaster(String master) {
		this.master = master;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public void setParent(GUID parent) {
		this.parent = parent;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public void setOrder(long order) {
		this.order = order;
	}
	public GUID getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getMaster() {
		return master;
	}
	public int getNum() {
		return num;
	}
	public long getDate() {
		return date;
	}
	public GUID getParent() {
		return parent;
	}
	public String getRemark() {
		return remark;
	}
	public long getOrder() {
		return order;
	}
	
	
}

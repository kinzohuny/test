package com.jiuqi.dna.training.hjz.service.task;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.training.hjz.service.intf.Department;

public class CreateDepartmentTask extends SimpleTask {

	private String name;
	private String master;
	private int num;
	private long date;
	private GUID parent;
	private String remark;
	private Department result;

	public Department getResult() {
		return result;
	}

	public void setResult(Department result) {
		this.result = result;
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
	
}

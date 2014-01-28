package com.jiuqi.dna.training.hjz.service.key;

import com.jiuqi.dna.training.hjz.service.intf.Department;

public class GetDepartmentNext {

	public GetDepartmentNext(Department dept){
		this.dept = dept;
	}
	
	private Department dept;
	
	public Department getDept() {
		return dept;
	}
	public void setDept(Department dept) {
		this.dept = dept;
	}
	
}

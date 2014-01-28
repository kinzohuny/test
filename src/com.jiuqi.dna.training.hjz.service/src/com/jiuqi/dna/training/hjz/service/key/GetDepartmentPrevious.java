package com.jiuqi.dna.training.hjz.service.key;

import com.jiuqi.dna.training.hjz.service.intf.Department;

public class GetDepartmentPrevious {

	public GetDepartmentPrevious(Department dept){
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

package com.jiuqi.dna.training.hjz.service.task;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.training.hjz.service.intf.Department;

public class ChangeDepartmentOrderTask extends SimpleTask{

	public ChangeDepartmentOrderTask(Department dept1, Department dept2) {
		this.dept1 = dept1;
		this.dept2 = dept2;
	}
	
	private Department dept1;
	private Department dept2;
	
	public Department getDept1() {
		return dept1;
	}

	public void setDept1(Department dept1) {
		this.dept1 = dept1;
	}

	public Department getDept2() {
		return dept2;
	}

	public void setDept2(Department dept2) {
		this.dept2 = dept2;
	}
}

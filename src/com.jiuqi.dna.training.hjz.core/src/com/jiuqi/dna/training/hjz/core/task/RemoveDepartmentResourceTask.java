package com.jiuqi.dna.training.hjz.core.task;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public class RemoveDepartmentResourceTask extends SimpleTask {

	private GUID id;

	public RemoveDepartmentResourceTask(GUID id){
		this.setId(id);
	}

	public GUID getId() {
		return id;
	}

	public void setId(GUID id) {
		this.id = id;
	}
	
	
}

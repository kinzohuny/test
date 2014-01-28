package com.jiuqi.dna.training.hjz.service.task;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public class RemoveDepartmentTask extends SimpleTask {

	private GUID id;

	public RemoveDepartmentTask(GUID id){
		this.setId(id);
	}

	public GUID getId() {
		return id;
	}

	public void setId(GUID id) {
		this.id = id;
	}
	
	
}

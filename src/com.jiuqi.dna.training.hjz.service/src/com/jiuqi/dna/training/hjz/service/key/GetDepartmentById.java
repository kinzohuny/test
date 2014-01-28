package com.jiuqi.dna.training.hjz.service.key;

import com.jiuqi.dna.core.type.GUID;

public class GetDepartmentById {

	private GUID id;
	
	public GetDepartmentById(GUID id){
		this.id = id;
	}

	public GUID getId() {
		return id;
	}

	public void setId(GUID id) {
		this.id = id;
	}
}

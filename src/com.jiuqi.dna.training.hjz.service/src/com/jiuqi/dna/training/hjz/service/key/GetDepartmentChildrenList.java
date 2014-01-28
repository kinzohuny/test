package com.jiuqi.dna.training.hjz.service.key;

import com.jiuqi.dna.core.type.GUID;

public class GetDepartmentChildrenList {

	private GUID parentId;
	
	public GetDepartmentChildrenList(GUID parentId){
		this.parentId = parentId;
	}
	
	public GUID getParentId() {
		return parentId;
	}
	public void setParentId(GUID parentId) {
		this.parentId = parentId;
	}
}

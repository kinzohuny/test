package com.jiuqi.dna.training.hjz.service.key;

import com.jiuqi.dna.core.type.GUID;

public class GetDepartmentAllChildrenList {

	private GUID parentId;
	
	public GetDepartmentAllChildrenList(GUID parentId){
		this.parentId = parentId;
	}
	
	public GUID getParentId() {
		return parentId;
	}
	public void setParentId(GUID parentId) {
		this.parentId = parentId;
	}
}

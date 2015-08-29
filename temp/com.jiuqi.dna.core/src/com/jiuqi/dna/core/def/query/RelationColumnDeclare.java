package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDeclare;

/**
 * 关系列定义
 * 
 * @author houchunlei
 */
public interface RelationColumnDeclare extends RelationColumnDefine,
		NamedDeclare {

	public RelationDeclare getOwner();
}

package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDeclare;

/**
 * ��ϵ�ж���
 * 
 * @author houchunlei
 */
public interface RelationColumnDeclare extends RelationColumnDefine,
		NamedDeclare {

	public RelationDeclare getOwner();
}

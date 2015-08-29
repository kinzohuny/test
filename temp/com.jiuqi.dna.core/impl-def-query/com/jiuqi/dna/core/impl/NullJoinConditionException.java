package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.JoinedRelationRefDefine;
import com.jiuqi.dna.core.def.query.QuJoinedRelationRefDefine;
import com.jiuqi.dna.core.def.query.SelectDefine;

public final class NullJoinConditionException extends RuntimeException {

	private static final long serialVersionUID = -4907540339306929567L;

	public NullJoinConditionException(JoinedRelationRefDefine join) {
		super("��������[" + join.getName() + "]����������Ϊ��.");
	}

	public NullJoinConditionException(SelectDefine select,
			QuJoinedRelationRefDefine join) {
		super("��ѯ[" + select.getName() + "]�е���������[" + join.getName() + "]δ������������");
	}
}

package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.JoinedRelationRefDefine;
import com.jiuqi.dna.core.def.query.QuJoinedRelationRefDefine;
import com.jiuqi.dna.core.def.query.SelectDefine;

public final class NullJoinConditionException extends RuntimeException {

	private static final long serialVersionUID = -4907540339306929567L;

	public NullJoinConditionException(JoinedRelationRefDefine join) {
		super("连接引用[" + join.getName() + "]的连接条件为空.");
	}

	public NullJoinConditionException(SelectDefine select,
			QuJoinedRelationRefDefine join) {
		super("查询[" + select.getName() + "]中的连接引用[" + join.getName() + "]未定义连接条件");
	}
}

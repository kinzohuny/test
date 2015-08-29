package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.query.RelationColumnDefine;

interface SQLColumnProvider {
	public RelationColumnDefine findColumn(String name);

	public ValueExpression expOf(RelationColumnDefine c);
}

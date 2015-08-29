package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.exp.RelationColumnRefExpr;

/**
 * 关系列引用抽象类
 * 
 * @author houchunlei
 * 
 */
public abstract class RelationColumnRefImpl extends ValueExpr implements
		RelationColumnRefExpr {

	public abstract RelationRef getReference();

	public abstract RelationColumn getColumn();

	@Override
	protected final RelationColumnRefImpl clone(RelationRefDomain domain,
			ArgumentableDefine arguments) {
		RelationRef relationRef = domain.getRelationRefRecursively(this.getReference().getName());
		RelationColumn column = relationRef.getTarget().getColumn(this.getColumn().getName());
		return relationRef.expOf(column);
	}
}

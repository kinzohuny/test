package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.query.SubQueryDefine;
import com.jiuqi.dna.core.def.query.SubQueryExpression;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * 字查询表达式实现类
 * 
 * @author houchunlei
 * 
 */
public final class SubQueryExpr extends ValueExpr implements SubQueryExpression {

	@Override
	public final DataTypeInternal getType() {
		return this.subquery.columns.get(0).getType();
	}

	public final SubQueryDefine getSubQuery() {
		return this.subquery;
	}

	@Override
	public final String getXMLTagName() {
		return SubQueryExpr.xml_name_subquery;
	}

	@Override
	public final void render(SXElement element) {
		throw new UnsupportedOperationException();
	}

	static final String xml_name_subquery = "subquery";

	final SubQueryImpl subquery;

	SubQueryExpr(SubQueryImpl subquery) {
		this.subquery = subquery;
	}

	@Override
	protected final SubQueryExpr clone(RelationRefDomain domain,
			ArgumentableDefine args) {
		SubQueryImpl target = new SubQueryImpl(domain);
		this.subquery.cloneSelectTo(target, args);
		return target.newExpression();
	}

	@Override
	protected final ValueExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		throw new UnsupportedOperationException();
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitSubQueryExpr(this, context);
	}

	@Override
	public final void render(ISqlExprBuffer buffer, TableUsages usages) {
		ISqlSelectBuffer sb = buffer.subquery();
		this.subquery.renderFullSelect(sb, usages);
	}
}
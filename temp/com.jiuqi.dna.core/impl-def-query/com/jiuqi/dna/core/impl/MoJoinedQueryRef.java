package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.RelationColumnDefine;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedQueryRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedRelationRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;

/**
 * 更新语句使用的连接查询引用
 * 
 * @author houchunlei
 * 
 */
public final class MoJoinedQueryRef extends
		MoJoinedRelationRefImpl<DerivedQueryImpl> implements JoinedQueryRef {

	public final SelectColumnRefImpl expOf(RelationColumnDefine column) {
		if (column == null) {
			throw relationColumnNull();
		}
		if (column instanceof SelectColumnImpl<?, ?>) {
			return new SelectColumnRefImpl(this, (SelectColumnImpl<?, ?>) column);
		}
		throw notSupportedRelationColumnRefException(this, column);
	}

	public final SelectColumnRefImpl expOf(String columnName) {
		SelectColumnImpl<?, ?> column = this.target.columns.find(columnName);
		if (column == null) {
			throw notSupportedRelationColumnRefException(this, columnName);
		}
		return new SelectColumnRefImpl(this, column);
	}

	public final boolean isTableReference() {
		return false;
	}

	public final boolean isQueryReference() {
		return true;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "joined-queryref";

	MoJoinedQueryRef(ModifyStatementImpl statement, String name,
			DerivedQueryImpl target) {
		super(statement, name, target);
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitMoJoinedQueryRef(this, context);
	}

	@Override
	final ISqlJoinedRelationRefBuffer renderSelf(ISqlRelationRefBuffer buffer, ConditionalExpr condition,
			TableUsages usages) {
		if (this.target.isWith) {
			ISqlJoinedTableRefBuffer jrb = buffer.joinTable(this.target.name, this.name, this.getJoinType());
			condition.render(jrb.onCondition(), usages);
			return jrb;
		} else {
			ISqlJoinedQueryRefBuffer sq = buffer.joinQuery(this.name, this.getJoinType());
			this.target.renderFullSelect(sq.select(), usages);
			condition.render(sq.onCondition(), usages);
			return sq;
		}
	}

}

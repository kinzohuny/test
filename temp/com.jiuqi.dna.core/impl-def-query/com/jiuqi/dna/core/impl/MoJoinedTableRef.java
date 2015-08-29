package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.RelationColumnDefine;
import com.jiuqi.dna.core.internal.da.sql.render.Render;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;

/**
 * �������ʹ�õ����ӱ�����
 * 
 * @author houchunlei
 */
public final class MoJoinedTableRef extends
		MoJoinedRelationRefImpl<TableDefineImpl> implements JoinedTableRef {

	public final TableFieldRefImpl expOf(RelationColumnDefine column) {
		if (column == null) {
			throw relationColumnNull();
		}
		if (column instanceof TableFieldDefineImpl) {
			return new TableFieldRefImpl(this, (TableFieldDefineImpl) column);
		}
		throw notSupportedRelationColumnRefException(this, column);
	}

	public final TableFieldRefImpl expOf(String name) {
		TableFieldDefineImpl field = this.target.fields.find(name);
		if (field == null) {
			throw new IllegalArgumentException("ָ�����ƵĹ�ϵ�ж��岻����.");
		}
		return new TableFieldRefImpl(this, field);
	}

	public final boolean isTableReference() {
		return true;
	}

	public final boolean isQueryReference() {
		return false;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "joined-tableref";

	MoJoinedTableRef(ModifyStatementImpl statement, String name,
			TableDefineImpl target) {
		super(statement, name, target);
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitMoJoinedTableRef(this, context);
	}

	@Override
	final ISqlJoinedTableRefBuffer renderSelf(ISqlRelationRefBuffer buffer, ConditionalExpr condition,
			TableUsages usages) {
		return Render.renderJoinedTableRef(this, buffer, condition, usages);
	}

}

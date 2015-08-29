package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.query.QuJoinedTableRefDeclare;
import com.jiuqi.dna.core.def.query.RelationColumnDefine;
import com.jiuqi.dna.core.internal.da.sql.render.Render;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * 查询定义使用的连接表引用
 * 
 * @author houchunlei
 * 
 */
public final class QuJoinedTableRef extends
		QuJoinedRelationRefImpl<TableDefineImpl> implements QuTableRef,
		JoinedTableRef, QuJoinedTableRefDeclare {

	public final TableFieldRefImpl expOf(RelationColumnDefine column) {
		if (column == null) {
			throw relationColumnNull();
		}
		if (column instanceof TableFieldDefineImpl) {
			return new TableFieldRefImpl(this, (TableFieldDefineImpl) column);
		}
		throw notSupportedRelationColumnRefException(this, column);
	}

	public final TableFieldRefImpl expOf(String relationColumnName) {
		TableFieldDefineImpl field = this.target.fields.find(relationColumnName);
		if (field == null) {
			throw notSupportedRelationColumnRefException(this, relationColumnName);
		}
		return new TableFieldRefImpl(this, field);
	}

	public final boolean isTableReference() {
		return true;
	}

	public final boolean isQueryReference() {
		return false;
	}

	public final QuJoinedTableRef castAsTableRef() {
		return this;
	}

	public final QuJoinedQueryRef castAsQueryRef() {
		throw new ClassCastException();
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setString(xml_attr_table, this.target.name);
	}

	static final String xml_name = "joined-tableref";

	QuJoinedTableRef(SelectImpl<?, ?> owner, String name,
			TableDefineImpl target, QuRelationRef parent) {
		super(owner, name, target, parent);
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitQuJoinedTableRef(this, context);
	}

	@Override
	@Deprecated
	protected final QuJoinedRelationRef cloneSelfTo(QuRelationRef from,
			ArgumentableDefine arguments) {
		return from.newJoin0(this.name, this.target);
	}

	@Override
	final ISqlJoinedTableRefBuffer renderSelf(ISqlRelationRefBuffer buffer,
			TableUsages usages) {
		return Render.renderJoinedTableRef(this, buffer, condition, usages);
	}
}
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.query.RelationColumnDefine;
import com.jiuqi.dna.core.internal.da.sql.render.Render;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * ��ѯ������ʹ�õĸ���������
 * 
 * @author houchunlei
 * 
 */
public final class QuRootTableRef extends
		QuRootRelationRefImpl<TableDefineImpl> implements QuTableRef {

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

	public final QuRootTableRef castAsTableRef() {
		return this;
	}

	public final QuRootQueryRef castAsQueryRef() {
		throw new ClassCastException();
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "root-tableref";

	QuRootTableRef(SelectImpl<?, ?> owner, String name, TableDefineImpl target,
			QuRootRelationRef prev) {
		super(owner, name, target, prev);
	}

	public final QuTableRef asTableRef() {
		return this;
	}

	@Override
	protected final QuRootTableRef cloneSelfTo(SelectImpl<?, ?> owner,
			ArgumentableDefine args) {
		return owner.newTableRef(this.name, this.target);
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitQuRootTableRef(this, context);
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setString(xml_attr_table, this.target.name);
	}

	@Override
	final ISqlRelationRefBuffer renderSelf(ISqlSelectBuffer buffer,
			TableUsages usages) {
		return Render.renderTableRef(this, buffer, usages);
	}
}

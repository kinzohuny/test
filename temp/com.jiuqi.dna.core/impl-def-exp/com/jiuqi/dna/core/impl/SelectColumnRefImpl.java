package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.exp.SelectColumnRefExpr;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * 查询列引用表达式
 * 
 * @author houchunlei
 * 
 */
public final class SelectColumnRefImpl extends RelationColumnRefImpl implements
		SelectColumnRefExpr {

	@Override
	public final QueryRef getReference() {
		return this.queryRef;
	}

	@Override
	public final SelectColumnImpl<?, ?> getColumn() {
		return this.column;
	}

	@Override
	public final DataTypeInternal getType() {
		return this.column.getType();
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_columnref;
	}

	@Override
	public final void render(SXElement element) {
		element.setString(xml_attr_queryref, this.queryRef.getName());
		element.setString(xml_attr_column, this.column.name);
	}

	@Override
	public final String toString() {
		return this.getReference().getName() + "." + this.column.getName();
	}

	static final String xml_name_columnref = "query-column-ref";
	static final String xml_attr_queryref = "query-ref";
	static final String xml_attr_column = "column";

	public final QueryRef queryRef;

	public final SelectColumnImpl<?, ?> column;

	SelectColumnRefImpl(QueryRef reference, SelectColumnImpl<?, ?> column) {
		if (reference.getTarget() != column.owner) {
			throw new IllegalArgumentException("查询输出列定义[" + column.name + "]不属于查询引用的目标查询定义[" + reference.getTarget().name + "]");
		}
		this.queryRef = reference;
		this.column = column;
	}

	SelectColumnRefImpl(SXElement element, RelationRefOwner refOwner) {
		RelationRef relationRef = refOwner.findRelationRef(element.getString(xml_attr_queryref));
		if (relationRef == null) {
			throw new NullPointerException();
		}
		if (!(relationRef instanceof QueryRef)) {
			throw new IllegalArgumentException();
		}
		this.queryRef = (QueryRef) relationRef;
		this.column = this.queryRef.getTarget().columns.get(element.getShort(xml_attr_column));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitSelectColumnRef(this, context);
	}

	@Override
	protected final SelectColumnRefImpl clone(RelationRef fromSample,
			RelationRef from, RelationRef toSample, RelationRef to) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.loadColumnRef(this.queryRef.getName(), this.column.alias);
	}
}
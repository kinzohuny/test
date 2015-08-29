package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.misc.SXElement;

public final class QueryColumnRefExpr extends ValueExpr {

	@Override
	public final DataTypeInternal getType() {
		return this.column.getType();
	}

	@Override
	protected final ValueExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected final ValueExpr clone(RelationRefDomain domain,
			ArgumentableDefine args) {
		QueryStatementBase query = (QueryStatementBase) domain;
		return new QueryColumnRefExpr(query.getColumn(this.column.name));
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "querycolumn-ref";

	final QueryColumnImpl column;

	QueryColumnRefExpr(QueryColumnImpl column) {
		this.column = column;
	}

	@Override
	public final void render(ISqlExprBuffer buffer, TableUsages usages) {
		throw new UnsupportedOperationException();
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitQueryColumnRef(this, context);
	}

	@Override
	public final void render(SXElement element) {
		throw new UnsupportedOperationException();
	}
}
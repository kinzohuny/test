package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.obja.StructFieldDefine;
import com.jiuqi.dna.core.def.query.QueryColumnDeclare;
import com.jiuqi.dna.core.type.Digester;

/**
 * 查询语句定义的输出列
 * 
 * @author houchunlei
 * 
 */
public final class QueryColumnImpl extends
		SelectColumnImpl<QueryStatementBase, QueryColumnImpl> implements
		QueryColumnDeclare {

	public final void digestType(Digester digester) {
		this.digestAuthAndName(digester);
		this.value().getType().digestType(digester);
	}

	public final void setMapingField(StructFieldDefine value) {
		StructFieldDefineImpl sf = (StructFieldDefineImpl) value;
		if (sf != null && sf.owner != this.owner.mapping) {
			throw new IllegalArgumentException("无效的结构字段");
		}
		this.field = sf;

	}

	public final void setMapingField(String structFieldName) {
		this.field = this.owner.mapping.fields.get(structFieldName);

	}

	public final StructFieldDefineImpl getMapingField() {
		return this.field;
	}

	public final void setUsingBigDecimal(boolean usingBigDecimal) {
		this.usingBigDecimal = usingBigDecimal;
	}

	public boolean usingBigDecimal;

	static final String xml_attr_mapping_field = "m-field";

	StructFieldDefineImpl field;

	QueryColumnImpl(QueryStatementBase owner, String name, String alias,
			ValueExpr expr) {
		super(owner, name, alias, expr);
	}

	@Override
	final SelectColumnImpl<?, ?> cloneTo(SelectImpl<?, ?> owner,
			ArgumentableDefine args) {
		// toooooo disgusting !!!! you mei you !!!!!
		SelectColumnImpl<?, ?> column = super.cloneTo(owner, args);
		if (column instanceof QueryColumnImpl && owner instanceof MappingQueryStatementImpl) {
			((QueryColumnImpl) column).field = this.field;
		}
		return column;
	}
}
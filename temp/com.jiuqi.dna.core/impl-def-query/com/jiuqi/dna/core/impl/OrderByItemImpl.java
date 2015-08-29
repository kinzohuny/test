package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.query.OrderByItemDeclare;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * 排序规则实现类
 * 
 * @author houchunlei
 * 
 */
public final class OrderByItemImpl extends DefineBaseImpl implements
		OrderByItemDeclare {

	public final ValueExpression getExpression() {
		return this.value;
	}

	public final boolean isDesc() {
		return this.isDesc;
	}

	public final void setDesc(boolean value) {
		this.isDesc = value;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_orderby;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setBoolean(xml_attr_desc, this.isDesc);
		this.value.renderInto(element);
	}

	static final String xml_name_orderby = "orderby";
	static final String xml_attr_desc = "desc";

	final ValueExpr value;

	final SelectImpl<?, ?> owner;

	private boolean isDesc;

	OrderByItemImpl(SelectImpl<?, ?> owner, ValueExpr value) {
		super();
		if (value == null) {
			throw new NullPointerException();
		}
		this.owner = owner;
		// value域在构造前检查
		this.value = value;
	}

	final void cloneTo(QueryStatementBase query, ArgumentableDefine args) {
		query.newOrderBy(this.value.clone(query, args), this.isDesc);
	}

	final void render(ISqlQueryBuffer buffer, TableUsages usages) {
		if (this.value instanceof QueryColumnRefExpr) {
			QueryColumnRefExpr columnRef = (QueryColumnRefExpr) this.value;
			buffer.newOrder(columnRef.column.alias, this.isDesc);
		} else {
			this.value.render(buffer.newOrder(this.isDesc), usages);
		}
	}
}
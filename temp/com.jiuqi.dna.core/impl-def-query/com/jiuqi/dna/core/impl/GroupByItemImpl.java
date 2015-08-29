package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.query.GroupByItemDeclare;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * 分组查询实现类
 * 
 * @author houchunlei
 * 
 */
public final class GroupByItemImpl extends DefineBaseImpl implements
		GroupByItemDeclare {

	public final void setExpression(ValueExpression value) {
		if (value == null) {
			throw new NullArgumentException("分组表达式");
		}
		ValueExpr v = (ValueExpr) value;
		if (ContextVariableIntl.isStrictExprDomain()) {
			v.checkDomain(this.owner);
		}
		this.value = v;
	}

	public final ValueExpr getExpression() {
		return this.value;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_groupby;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		this.value.renderInto(element);
	}

	final static String xml_name_groupby = "groupby";

	final SelectImpl<?, ?> owner;

	private ValueExpr value;

	final ValueExpr value() {
		return this.value;
	}

	GroupByItemImpl(SelectImpl<?, ?> owner, ValueExpr value) {
		super();
		if (owner == null || value == null) {
			throw new NullPointerException();
		}
		this.owner = owner;
		// value域在构造前检查
		this.value = value;
	}

	final void cloneTo(SelectImpl<?, ?> owner, ArgumentableDefine args) {
		owner.newGroupBy(this.value.clone(owner, args));
	}
}
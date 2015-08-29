package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.query.SelectColumnDeclare;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * 查询输出列的实现类
 * 
 * <p>
 * 每个查询列表是select子句的一个输出
 * 
 * @author houchunlei
 * 
 */
public abstract class SelectColumnImpl<TSelect extends SelectImpl<TSelect, TColumn>, TColumn extends SelectColumnImpl<TSelect, TColumn>>
		extends NamedDefineImpl implements SelectColumnDeclare, RelationColumn {

	@Override
	protected final boolean isNameCaseSensitive() {
		return false;
	}

	public final ValueExpr getExpression() {
		return this.value;
	}

	public final void setExpression(ValueExpression value) {
		if (value == null) {
			this.value = NullExpr.NULL;
		} else {
			ValueExpr v = (ValueExpr) value;
			if (ContextVariableIntl.isStrictExprDomain()) {
				v.checkDomain(this.owner);
			}
			this.value = v;
		}
	}

	public final DataTypeInternal getType() {
		return this.value.getType();
	}

	final DataTypeInternal getRecordType() {
		if (this.value instanceof SelectColumnRefImpl) {
			SelectColumnRefImpl scr = (SelectColumnRefImpl) this.value;
			return scr.queryRef.getTarget().tryGetColumnFirstNonNullType(scr.queryRef.getTarget().columns.indexOf(scr.column));
		} else {
			return this.value.getType();
		}
	}

	public final TSelect getOwner() {
		return this.owner;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_column;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		this.value.renderInto(element);
	}

	static final String xml_name_column = "column";

	final TSelect owner;

	private ValueExpr value;

	public final ValueExpr value() {
		return this.value;
	}

	final String alias;

	SelectColumnImpl(TSelect owner, String name, String alias, ValueExpr value) {
		super(name);
		this.owner = owner;
		this.alias = alias;
		this.value = value;
	}

	public final TableFieldDefineImpl tryGetTableField() {
		try {
			return ((TableFieldRefImpl) this.value).field;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("当前查询列的表达式不为字段引用表达式");
		}
	}

	SelectColumnImpl<?, ?> cloneTo(SelectImpl<?, ?> owner,
			ArgumentableDefine args) {
		SelectColumnImpl<?, ?> column = owner.newColumn(this.name, this.value.clone(owner, args));
		column.title = this.title;
		column.description = this.description;
		return column;
	}
}
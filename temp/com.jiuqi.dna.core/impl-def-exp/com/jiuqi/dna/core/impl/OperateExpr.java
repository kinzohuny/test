package com.jiuqi.dna.core.impl;

import java.util.Iterator;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.exp.IllegalNullUsageException;
import com.jiuqi.dna.core.def.exp.OperateExpression;
import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * 运算表达式实现类
 * 
 * @author houchunlei
 * 
 */
public final class OperateExpr extends ValueExpr implements OperateExpression {

	public final ValueExpr get(int index) {
		return this.values[index];
	}

	@Deprecated
	public final int getCount() {
		return this.values.length;
	}

	public final int size() {
		return this.values.length;
	}

	public final OperatorIntrl getOperator() {
		return this.operator;
	}

	@Override
	public final DataTypeInternal getType() {
		return this.type;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_operate;
	}

	@Override
	public final void render(SXElement element) {
		element.setAttribute(xml_attr_operator, this.operator.toString());
		for (ValueExpr value : this.values) {
			value.renderInto(element);
		}
	}

	public static final OperateExpr COUNT_ASTERISK = new OperateExpr(AggregateFunction.COUNT_ASTERISK);

	public static final OperateExpr GET_DATE = new OperateExpr(ScalarFunction.GETDATE);

	public static final OperateExpr NEW_RECID = new OperateExpr(ScalarFunction.NEW_RECID);

	static final String xml_name_operate = "operate";
	static final String xml_attr_operator = "operator";

	/**
	 * 运算符
	 */
	public final OperatorIntrl operator;

	/**
	 * 运算结果类型
	 */
	public final DataTypeInternal type;

	/**
	 * 运算参数列表
	 */
	public final ValueExpr[] values;

	OperateExpr(OperatorIntrl operator, ValueExpr[] values) {
		this.operator = operator;
		this.values = values;
		this.checkNullUsage();
		this.type = operator.checkValues(values);
	}

	private final void checkNullUsage() {
		if (this.operator != CommonOperator.SIMPLE_CASE) {
			final int m = ContextVariableIntl.isStrictNullUsage();
			if (m > 0 && ExprUtl.containNullExpr(this.values)) {
				if (m == 1) {
					System.err.println(IllegalNullUsageException.message(this.operator));
				} else if (m == 2) {
					new IllegalNullUsageException(this.operator).printStackTrace();
				} else {
					throw new IllegalNullUsageException(this.operator);
				}
			}
		}
	}

	private OperateExpr(OperatorIntrl operator) {
		this(operator, ValueExpr.emptyArray);
	}

	public OperateExpr(OperatorIntrl operator, ValueExpr expr) {
		this(operator, new ValueExpr[] { expr });
	}

	public OperateExpr(OperatorIntrl operator, ValueExpr expr1, ValueExpr expr2) {
		this(operator, new ValueExpr[] { expr1, expr2 });
	}

	public OperateExpr(OperatorIntrl operator, ValueExpr expr1,
			ValueExpr expr2, ValueExpr expr3) {
		this(operator, new ValueExpr[] { expr1, expr2, expr3 });
	}

	OperateExpr(SXElement element, RelationRefOwner refOwner,
			ArgumentableDefine args) {
		this(valueOf(element.getString(xml_attr_operator)), ValueExpr.loadValues(element.firstChild(), refOwner, args));
	}

	static final OperatorIntrl valueOf(String operator) {
		return null;
	}

	@Override
	protected final OperateExpr clone(RelationRefDomain domain,
			ArgumentableDefine args) {
		ValueExpr[] values = new ValueExpr[this.values.length];
		for (int i = 0; i < this.values.length; i++) {
			values[i] = this.values[i].clone(domain, args);
		}
		return new OperateExpr(this.operator, values);
	}

	@Override
	protected final OperateExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		ValueExpr[] values = new ValueExpr[this.values.length];
		for (int i = 0; i < this.values.length; i++) {
			values[i] = this.values[i].clone(fromSample, from, toSample, to);
		}
		return new OperateExpr(this.operator, values);
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitOperateExpr(this, context);
	}

	public final boolean isNonDeterministic() {
		return this.operator.isNonDeterministic();
	}

	@Override
	public final void render(ISqlExprBuffer buffer, TableUsages usages) {
		this.operator.render(buffer, usages, this);
	}

	public final Iterator<ValueExpression> iterator() {
		return new Itr();
	}

	private final class Itr implements Iterator<ValueExpression> {

		private int next = 0;

		public final boolean hasNext() {
			return this.next < OperateExpr.this.size();
		}

		public final ValueExpression next() {
			if (this.next == OperateExpr.this.size()) {
				throw new IndexOutOfBoundsException();
			}
			final ValueExpression current = OperateExpr.this.get(this.next);
			this.next++;
			return current;
		}

		public final void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
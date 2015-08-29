package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.LinkedList;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.exp.ConditionalExpression;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.misc.SXElement;

public final class SearchedCaseExpr extends ValueExpr implements
		com.jiuqi.dna.core.def.exp.SearchedCaseExpr {

	public final int size() {
		return this.whens.length;
	}

	public final ConditionalExpr when() {
		return this.whens[0];
	}

	public final ConditionalExpr when(int index) {
		if (index < 0 || index >= this.whens.length) {
			throw new IllegalArgumentException("指定表达式序号越界。");
		}
		return this.whens[index];
	}

	public final ValueExpr then() {
		return this.thens[0];
	}

	public final ValueExpr then(int index) {
		if (index < 0 || index >= this.whens.length) {
			throw new IllegalArgumentException("指定表达式序号越界。");
		}
		return this.thens[index];
	}

	public final ValueExpr other() {
		return this.other;
	}

	@Override
	public final String toString() {
		return "Searched-Case";
	}

	@Override
	public final DataTypeInternal getType() {
		return this.retype;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_searched_case;
	}

	@Override
	public final void render(SXElement element) {
		for (int i = 0; i < this.whens.length; i++) {
			this.whens[i].renderInto(element.append(xml_element_when));
			this.thens[i].renderInto(element.append(xml_element_return));
		}
		if (this.other != null) {
			this.other.renderInto(element.append(xml_element_default));
		}
	}

	static final String xml_name_searched_case = "searched-case";
	static final String xml_element_when = "when-condition";
	static final String xml_element_return = "return-value";
	static final String xml_element_default = "default-value";

	final ConditionalExpr[] whens;
	final ValueExpr[] thens;
	final ValueExpr other;
	final DataTypeInternal retype;

	private SearchedCaseExpr(ConditionalExpr[] whens, ValueExpr[] thens,
			ValueExpr other) {
		this.whens = whens;
		this.thens = thens;
		this.other = other;
		DataTypeInternal retype = thens[0].getType();
		if (thens.length > 1) {
			for (int i = 1; i < thens.length; i++) {
				retype = ExprUtl.checkSameCategory(this, retype, thens[i].getType());
			}
		}
		if (other != null) {
			retype = ExprUtl.checkSameCategory(this, retype, other.getType());
		}
		this.retype = retype;
	}

	private static final ConditionalExpr convert(Object obj) {
		if (obj == null || obj instanceof ConditionalExpr) {
			return (ConditionalExpr) obj;
		}
		throw new IllegalArgumentException("对象不是条件表达式。");

	}

	static final SearchedCaseExpr newSearchedCase(Object when, Object then,
			Object[] others) {
		if (others == null || others.length == 0) {
			return new SearchedCaseExpr(new ConditionalExpr[] { convert(when) }, new ValueExpr[] { ValueExpr.expOf(then) }, null);
		} else {
			final int ol = others.length / 2;
			ConditionalExpr[] whens = new ConditionalExpr[1 + ol];
			ValueExpr[] thens = new ValueExpr[1 + ol];
			whens[0] = convert(when);
			thens[0] = ValueExpr.expOf(then);
			for (int i = 0; i < ol; i++) {
				whens[i + 1] = convert(others[i * 2]);
				thens[i + 1] = ValueExpr.expOf(others[2 * i + 1]);
			}
			ValueExpr other = others.length % 2 == 0 ? null : ValueExpr.expOf(others[others.length - 1]);
			return new SearchedCaseExpr(whens, thens, other);
		}
	}

	static final SearchedCaseExpr newSearchedCase(SXElement element,
			RelationRefOwner refOwner, ArgumentableDefine args) {
		LinkedList<ConditionalExpr> whens = new LinkedList<ConditionalExpr>();
		LinkedList<ValueExpr> returns = new LinkedList<ValueExpr>();
		for (SXElement whenElement = element.firstChild(xml_element_when); whenElement != null; whenElement = whenElement.nextSibling(xml_element_when)) {
			SXElement returnElement = whenElement.nextSibling();
			whens.add(ConditionalExpr.loadCondition(whenElement.firstChild(), refOwner, args));
			if (returnElement == null || !returnElement.name.equals(xml_element_return)) {
				throw new NullPointerException("错误的xml元素");
			}
			returns.add(ValueExpr.loadValue(returnElement.firstChild(), refOwner, args));
		}
		ValueExpr other = null;
		SXElement otherElement = element.firstChild(xml_element_default);
		if (otherElement != null) {
			other = ValueExpr.loadValue(otherElement.firstChild(), refOwner, args);
		}
		int c = whens.size();
		if (c == 0) {
			throw new IllegalArgumentException();
		} else {
			whens.remove(0);
			returns.remove(0);
			return new SearchedCaseExpr(whens.toArray(new ConditionalExpr[whens.size()]), returns.toArray(new ValueExpr[returns.size()]), other);
		}
	}

	static final SearchedCaseExpr newSearchedCase(SearchedCaseExpr sample,
			RelationRef from, RelationRef to, RelationRef fromSample,
			RelationRef toSample) {
		ValueExpr other = null;
		if (sample.other != null) {
			other = sample.other.clone(fromSample, from, toSample, to);
		}
		ConditionalExpr[] whens = new ConditionalExpr[sample.whens.length];
		ValueExpr[] thens = new ValueExpr[sample.thens.length];
		for (int i = 0; i < sample.whens.length; i++) {
			whens[i] = sample.whens[i].clone(fromSample, from, toSample, to);
			thens[i] = sample.thens[i].clone(fromSample, from, toSample, to);
		}
		return new SearchedCaseExpr(whens, thens, other);
	}

	public static final class BuilderImpl implements Builder {

		private final ArrayList<ConditionalExpr> whens = new ArrayList<ConditionalExpr>();
		private final ArrayList<ValueExpr> thens = new ArrayList<ValueExpr>();

		public final Builder when(ConditionalExpression when, Object then) {
			this.whens.add(convert(when));
			try {
				this.thens.add(ValueExpr.expOf(then));
			} catch (Throwable e) {
				this.whens.remove(this.whens.size() - 1);
			}
			return this;
		}

		public final SearchedCaseExpr other(Object other) {
			return new SearchedCaseExpr(this.whens.toArray(new ConditionalExpr[this.whens.size()]), this.thens.toArray(new ValueExpr[this.thens.size()]), ValueExpr.expOf(other));
		}

		public final SearchedCaseExpr build() {
			return new SearchedCaseExpr(this.whens.toArray(new ConditionalExpr[this.whens.size()]), this.thens.toArray(new ValueExpr[this.thens.size()]), null);
		}

	}

	@Override
	protected final ValueExpr clone(RelationRefDomain domain,
			ArgumentableDefine args) {
		ValueExpr other = null;
		if (this.other != null) {
			other = this.other.clone(domain, args);
		}
		ConditionalExpr[] whens = new ConditionalExpr[this.whens.length];
		ValueExpr[] thens = new ValueExpr[this.thens.length];
		for (int i = 0; i < this.whens.length; i++) {
			whens[i] = this.whens[i].clone(domain, args);
			thens[i] = this.thens[i].clone(domain, args);
		}
		return new SearchedCaseExpr(whens, thens, other);
	}

	@Override
	protected final SearchedCaseExpr clone(RelationRef fromSample,
			RelationRef from, RelationRef toSample, RelationRef to) {
		return newSearchedCase(this, from, to, fromSample, toSample);
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitSearchedCase(this, context);
	}

	@Override
	public final void render(ISqlExprBuffer buffer, TableUsages usages) {
		int c = 0;
		for (int i = 0; i < this.whens.length; i++) {
			this.whens[i].render(buffer, usages);
			this.thens[i].render(buffer, usages);
			c += 2;
		}
		if (this.other != null) {
			this.other.render(buffer, usages);
			c++;
		}
		buffer.searchedCase(c);
	}
}
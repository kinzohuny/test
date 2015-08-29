package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.exp.ConditionalExpression;
import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedRelationRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * 查询中使用的连接引用的基类
 * 
 * @param <TRelation>
 * 
 * @author houchunlei
 */
abstract class QuJoinedRelationRefImpl<TRelation extends Relation> extends
		QuRelationRefImpl<TRelation, QuJoinedRelationRef, QuJoinedRelationRef>
		implements QuJoinedRelationRef {

	public final ConditionalExpr getJoinCondition() {
		return this.condition;
	}

	public final TableJoinType getJoinType() {
		return this.type;
	}

	public final void setJoinCondition(ConditionalExpression condition) {
		if (condition == null) {
			throw new NullJoinConditionException(this);
		}
		ConditionalExpr jc = (ConditionalExpr) condition;
		if (ContextVariableIntl.isStrictExprDomain()) {
			jc.checkDomain(this.owner);
		}
		this.condition = jc;
	}

	public final void setJoinType(TableJoinType type) {
		if (type == null) {
			throw new NullArgumentException("连接类型");
		}
		this.type = type;
	}

	static final String xml_attr_join_type = "join-type";
	static final String xml_element_join_condition = "join-condition";

	final QuRelationRef parent;

	ConditionalExpr condition;

	TableJoinType type = TableJoinType.INNER;

	QuJoinedRelationRefImpl(SelectImpl<?, ?> owner, String name,
			TRelation target, QuRelationRef parent) {
		super(owner, name, target);
		this.parent = parent;
	}

	public final QuRelationRef parent() {
		return this.parent;
	}

	public final void cloneTo(QuRelationRef from, ArgumentableDefine args) {
		QuJoinedRelationRef selfClone = this.cloneSelfTo(from, args);
		selfClone.setJoinType(this.type);
		if (this.condition == null) {
			throw new NullJoinConditionException(this);
		}
		selfClone.setJoinCondition(this.condition.clone(from.getOwner(), args));
		QuJoinedRelationRef join = this.getJoins();
		if (join != null) {
			join.cloneTo(selfClone, args);
		}
		QuJoinedRelationRef next = this.next();
		if (next != null) {
			next.cloneTo(from, args);
		}
	}

	/**
	 * 只负责调用newJoin方法构建相应的连接对象,不设置条件及连接类型
	 * 
	 * @param from
	 *            连接的最左边,即调用newJoin的对象
	 * @param args
	 * @return
	 */
	protected abstract QuJoinedRelationRef cloneSelfTo(QuRelationRef from,
			ArgumentableDefine args);

	@Override
	public void render(SXElement element) {
		super.render(element);
		element.setEnum(xml_attr_join_type, this.type);
		this.condition.renderInto(element.append(xml_element_join_condition));
	}

	@Override
	public void validate() {
		if (this.condition == null) {
			throw new NullJoinConditionException(this);
		}
	}

	public void render(ISqlRelationRefBuffer buffer, TableUsages usages) {
		ISqlJoinedRelationRefBuffer self = this.renderSelf(buffer, usages);
		QuJoinedRelationRef join = this.getJoins();
		if (join != null) {
			join.render(self, usages);
		}
		QuJoinedRelationRef next = this.next();
		if (next != null) {
			next.render(buffer, usages);
		}
	}

	abstract ISqlJoinedRelationRefBuffer renderSelf(
			ISqlRelationRefBuffer buffer, TableUsages usages);

}

package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;
import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedRelationRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;

/**
 * �����������ӹ�ϵ����
 * 
 * @param <TRelation>
 *            ����Ŀ���ϵ����
 * 
 * @author houchunlei
 */
public abstract class MoJoinedRelationRefImpl<TRelation extends Relation>
		extends
		MoRelationRefImpl<TRelation, MoJoinedRelationRef, MoJoinedRelationRef>
		implements MoJoinedRelationRef {

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
		this.condition = (ConditionalExpr) condition;

	}

	public final void setJoinType(TableJoinType type) {
		if (type == null) {
			throw new NullArgumentException("��������");
		}
		this.type = type;
	}

	/**
	 * ��������
	 */
	private ConditionalExpr condition;

	/**
	 * ��������
	 */
	private TableJoinType type = TableJoinType.INNER;

	MoJoinedRelationRefImpl(ModifyStatementImpl statement, String name,
			TRelation target) {
		super(statement, name, target);
	}

	public final void render(ISqlRelationRefBuffer buffer, TableUsages usages) {
		ISqlJoinedRelationRefBuffer self = this.renderSelf(buffer, this.condition, usages);
//		this.condition.render(self.onCondition(), usages);
		MoJoinedRelationRef join = this.getJoins();
		if (join != null) {
			join.render(self, usages);
		}
		MoJoinedRelationRef next = this.next();
		if (next != null) {
			next.render(buffer, usages);
		}
	}

	abstract ISqlJoinedRelationRefBuffer renderSelf(
			ISqlRelationRefBuffer buffer, ConditionalExpr condition, TableUsages usages);
}

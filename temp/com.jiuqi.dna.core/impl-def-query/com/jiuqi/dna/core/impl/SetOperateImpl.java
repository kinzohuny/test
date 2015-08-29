package com.jiuqi.dna.core.impl;

import java.util.BitSet;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.query.SetOperateDeclare;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;

/**
 * ��ѯ�ļ�������
 * 
 * @author houchunlei
 * 
 */
final class SetOperateImpl extends MetaBase implements SetOperateDeclare,
		OMVisitable {

	public final SetOperatorImpl getOperator() {
		return this.all ? SetOperatorImpl.UNION_ALL : SetOperatorImpl.UNION;
	}

	public final DerivedQueryImpl getTarget() {
		return this.target;
	}

	@Override
	final String getDescription() {
		return "union�Ӿ�";
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "set-operate";

	/**
	 * union���ڵ�select�ṹ.
	 */
	final SelectImpl<?, ?> owner;

	/**
	 * union�Ĳ�ѯ�ṹ.
	 */
	final DerivedQueryImpl target;

	/**
	 * �Ƿ�union all.
	 */
	final boolean all;

	SetOperateImpl(SelectImpl<?, ?> owner, DerivedQueryImpl target, boolean all) {
		this.owner = owner;
		if (target.isWith) {
			throw new UnsupportedOperationException("With���岻��union��");
		}
		this.target = target;
		this.all = all;
	}

	final void cloneTo(SelectImpl<?, ?> select, ArgumentableDefine args) {
		DerivedQueryImpl dq = select.newDerivedQuery();
		this.target.cloneSelectTo(dq, args);
		if (this.all) {
			select.unionAll(dq);
		} else {
			select.union(dq);
		}
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitSetOperate(this, context);
	}

	final void render(ISqlSelectBuffer buffer, TableUsages usages,
			BitSet filtered) {
		this.target.renderFrom(buffer, usages);
		this.target.renderWhere(buffer, usages);
		this.target.renderGroupby(buffer, usages);
		this.target.renderHaving(buffer, usages);
		if (this.target.distinct) {
			buffer.distinct();
		}
		for (int i = 0, c = this.target.columns.size(); i < c; i++) {
			if (filtered == null || !filtered.get(i)) {
				SelectColumnImpl<?, ?> column = this.target.columns.get(i);
				if (column.value() == NullExpr.NULL) {
					buffer.newColumn(column.alias).loadNull(this.target.getUnionTopSelect().tryGetColumnFirstNonNullType(i));
				} else {
					column.value().render(buffer.newColumn(column.alias), usages);
				}
			}
		}
		this.target.renderUnion(buffer, usages, filtered);
	}

}

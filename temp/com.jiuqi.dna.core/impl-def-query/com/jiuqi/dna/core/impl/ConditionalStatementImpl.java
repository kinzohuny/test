package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.exp.ConditionalExpression;
import com.jiuqi.dna.core.def.query.DerivedQueryDefine;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableRelationDefine;
import com.jiuqi.dna.core.internal.da.sql.render.Render;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;

/**
 * 带条件的更新语句
 * 
 * @author houchunlei
 * 
 */
abstract class ConditionalStatementImpl extends ModifyStatementImpl implements
		RelationJoinableIntrl {

	public final ConditionalExpr getCondition() {
		return this.condition;
	}

	public final void setCondition(ConditionalExpression condition) {
		if (condition == null) {
			this.condition = null;
		} else {
			ConditionalExpr c = (ConditionalExpr) condition;
			if (ContextVariableIntl.isStrictExprDomain()) {
				c.checkDomain(this);
			}
			this.condition = (ConditionalExpr) condition;
		}
	}

	public final MoJoinedTableRef newJoin(TableDefine table) {
		return this.moTableRef.newJoin(table);
	}

	public final MoJoinedTableRef newJoin(TableDefine table, String name) {
		return this.moTableRef.newJoin(table, name);
	}

	public final MoJoinedTableRef newJoin(TableDeclarator table) {
		return this.moTableRef.newJoin(table);
	}

	public final MoJoinedTableRef newJoin(TableDeclarator table, String name) {
		return this.moTableRef.newJoin(table, name);
	}

	public final MoJoinedTableRef newJoin(TableRelationDefine sample) {
		return this.moTableRef.newJoin(sample);
	}

	public final MoJoinedTableRef newJoin(TableRelationDefine sample,
			String name) {
		return this.moTableRef.newJoin(sample);
	}

	public final MoJoinedQueryRef newJoin(DerivedQueryDefine query) {
		return this.moTableRef.newJoin(query);
	}

	public final MoJoinedQueryRef newJoin(DerivedQueryDefine query, String name) {
		return this.moTableRef.newJoin(query, name);
	}

	ConditionalExpr condition;

	ConditionalStatementImpl(String name, String alias, TableDefineImpl table) {
		super(name, alias, table);
	}

	ConditionalStatementImpl(String name, String alias, TableDefineImpl table,
			StructDefineImpl arguments) {
		super(name, alias, table, arguments);
	}

	/**
	 * 转换当前更新语句的目标关系,以select的结果关系表示.即更新目标关系转换成from子句中的关系引用,
	 * 更新条件转换到select的where子句中.
	 * 
	 * @param buffer
	 * @param usages
	 */
	public final void renderUpdateRelationIntoSelect(ISqlSelectBuffer buffer,
			TableUsages usages) {
		ISqlTableRefBuffer tableRef = Render.renderTableRef(this.moTableRef, buffer, usages);
		MoJoinedRelationRef join = this.moTableRef.getJoins();
		if (join != null) {
			join.render(tableRef, usages);
		}
		if (this.condition != null) {
			this.condition.render(buffer.where(), usages);
		}
	}
}
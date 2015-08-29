package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

abstract class DmSourceBuffer extends SqlBuffer implements
		ISqlRelationRefBuffer {

	final String alias;
	DmExprBuffer condition;
	ArrayList<DmSourceBuffer> joins;
	TableJoinType joinType;
	protected String targetAlias;
	protected String alternateAlias;

	DmSourceBuffer(String alias) {
		this.alias = DmExprBuffer.quote(alias);
	}

	DmSourceBuffer(String alias, TableJoinType type) {
		this.alias = DmExprBuffer.quote(alias);
		this.joinType = type;
	}

	public void replace(String targetAlias, String alternateAlias) {
		this.targetAlias = targetAlias;
		this.alternateAlias = alternateAlias;
	}

	private final ArrayList<DmSourceBuffer> ensureJoins() {
		if (this.joins == null) {
			this.joins = new ArrayList<DmSourceBuffer>();
		}
		return this.joins;
	}

	public DmTableSourceBuffer joinTable(String table, String alias,
			TableJoinType type) {
		if (this.targetAlias != null && alias.equals(this.targetAlias)) {
			alias = this.alternateAlias;
		}
		DmTableSourceBuffer j = new DmTableSourceBuffer(table, alias, type);
		j.replace(this.targetAlias, this.alternateAlias);
		this.ensureJoins().add(j);
		return j;
	}

	public DmSubqueryRefBuffer joinQuery(String alias, TableJoinType type) {
		if (this.targetAlias != null && alias.equals(this.targetAlias)) {
			alias = this.alternateAlias;
		}
		DmSubqueryRefBuffer j = new DmSubqueryRefBuffer(alias, type);
		j.replace(this.targetAlias, this.alternateAlias);
		this.ensureJoins().add(j);
		return j;
	}

	public DmWithSourceBuffer joinWith(String target, String alias,
			TableJoinType type) {
		if (this.targetAlias != null && alias.equals(this.targetAlias)) {
			alias = this.alternateAlias;
		}
		DmWithSourceBuffer j = new DmWithSourceBuffer(target, alias, type);
		j.replace(this.targetAlias, this.alternateAlias);
		this.ensureJoins().add(j);
		return j;
	}

	public DmExprBuffer onCondition() {
		if (this.condition == null) {
			this.condition = new DmExprBuffer();
			this.condition.replace(this.targetAlias, this.alternateAlias);
		}
		return this.condition;
	}

	public final void writeTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		if (this.condition != null) {
			if (this.joinType == TableJoinType.INNER) {
				sql.append("inner join ");
			} else if (this.joinType == TableJoinType.FULL) {
				sql.append("full join ");
			} else if (this.joinType == TableJoinType.RIGHT) {
				sql.append("right join ");
			} else {
				sql.append("left join ");
			}
			if (this.joins != null) {
				sql.append('(');
			}
		}
		this.writeRefTextTo(sql, args);
		sql.append(' ').append(this.alias);
		if (this.joins != null) {
			for (DmSourceBuffer j : this.joins) {
				sql.append(' ');
				j.writeTo(sql, args);
			}
		}
		if (this.condition != null) {
			if (this.joins != null) {
				sql.append(')');
			}
			sql.append(" on ");
			this.condition.writeTo(sql, args);
		}
	}

	/**
	 * 不输出主表，把第一个连接表当做主表输出连接语句。
	 * 
	 * @param sql
	 * @param args
	 */
	public final void writeJoinsTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		if (joins == null) {
			return;
		}
		DmSourceBuffer join = this.joins.get(0);
		join.writeRefTextTo(sql, args);
		sql.append(' ').append(join.alias);
		if (join.joins != null) {
			for (DmSourceBuffer j : join.joins) {
				sql.append(' ');
				j.writeTo(sql, args);
			}
		}
		if (this.joins.size() > 0) {
			for (int i = 1; i < this.joins.size(); i++) {
				DmSourceBuffer j = this.joins.get(i);
				sql.append(' ');
				j.writeTo(sql, args);
			}
		}
	}

	protected abstract void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args);
}
package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedQueryRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedWithRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public abstract class OracleRelationRefBuffer extends SqlBuffer implements
		ISqlRelationRefBuffer {

	final String alias;
	OracleExprBuffer condition;
	ArrayList<OracleRelationRefBuffer> joins;
	TableJoinType joinType;
	protected String targetAlias;
	protected String alternateAlias;

	public OracleRelationRefBuffer(String alias) {
		this.alias = OracleExprBuffer.quote(alias);
	}

	public OracleRelationRefBuffer(String alias, TableJoinType type) {
		this.alias = OracleExprBuffer.quote(alias);
		this.joinType = type;
	}

	public void replace(String targetAlias, String alternateAlias) {
		this.targetAlias = targetAlias;
		this.alternateAlias = alternateAlias;
	}

	private final ArrayList<OracleRelationRefBuffer> ensureJoins() {
		if (this.joins == null) {
			this.joins = new ArrayList<OracleRelationRefBuffer>();
		}
		return this.joins;
	}

	public ISqlJoinedTableRefBuffer joinTable(String table, String alias,
			TableJoinType type) {
		if (this.targetAlias != null && alias.equals(this.targetAlias)) {
			alias = this.alternateAlias;
		}
		OracleTableRefBuffer j = new OracleTableRefBuffer(table, alias, type);
		j.replace(this.targetAlias, this.alternateAlias);
		this.ensureJoins().add(j);
		return j;
	}

	public ISqlJoinedQueryRefBuffer joinQuery(String alias, TableJoinType type) {
		if (this.targetAlias != null && alias.equals(this.targetAlias)) {
			alias = this.alternateAlias;
		}
		OracleSubQueryRefBuffer j = new OracleSubQueryRefBuffer(alias, type);
		j.replace(this.targetAlias, this.alternateAlias);
		this.ensureJoins().add(j);
		return j;
	}

	public ISqlJoinedWithRefBuffer joinWith(String target, String alias,
			TableJoinType type) {
		if (this.targetAlias != null && alias.equals(this.targetAlias)) {
			alias = this.alternateAlias;
		}
		OracleTableRefBuffer j = new OracleTableRefBuffer(target, alias, type);
		j.replace(this.targetAlias, this.alternateAlias);
		this.ensureJoins().add(j);
		return j;
	}

	public ISqlExprBuffer onCondition() {
		if (this.condition == null) {
			this.condition = new OracleExprBuffer();
			this.condition.replace(this.targetAlias, this.alternateAlias);
		}
		return this.condition;
	}

	protected abstract void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args);

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
			for (OracleRelationRefBuffer j : this.joins) {
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
		if (this.joins == null) {
			return;
		}
		OracleRelationRefBuffer join = this.joins.get(0);
		join.writeRefTextTo(sql, args);
		sql.append(' ').append(join.alias);
		if (join.joins != null) {
			for (OracleRelationRefBuffer j : join.joins) {
				sql.append(' ');
				j.writeTo(sql, args);
			}
		}
		if (this.joins.size() > 1) {
			for (int i = 1; i < this.joins.size(); i++) {
				sql.append(' ');
				this.joins.get(i).writeTo(sql, args);
			}
		}
	}
}

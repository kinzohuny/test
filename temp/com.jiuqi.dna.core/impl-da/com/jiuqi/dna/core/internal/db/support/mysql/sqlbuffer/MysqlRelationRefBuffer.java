package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public abstract class MysqlRelationRefBuffer extends SqlBuffer implements
		ISqlRelationRefBuffer {

	final String alias;
	MysqlExprBuffer condition;
	ArrayList<MysqlRelationRefBuffer> joins;
	TableJoinType joinType;

	final MysqlCommandBuffer command;

	MysqlRelationRefBuffer(MysqlCommandBuffer command, String alias) {
		this.command = command;
		this.alias = MysqlExprBuffer.quote(alias);
	}

	MysqlRelationRefBuffer(MysqlCommandBuffer command, String alias,
			TableJoinType type) {
		this.command = command;
		this.alias = MysqlExprBuffer.quote(alias);
		this.joinType = type;
	}

	private final ArrayList<MysqlRelationRefBuffer> ensureJoins() {
		if (this.joins == null) {
			this.joins = new ArrayList<MysqlRelationRefBuffer>();
		}
		return this.joins;
	}

	public final ISqlJoinedTableRefBuffer joinTable(String table, String alias,
			TableJoinType type) {
		MysqlTableRefBuffer j = new MysqlTableRefBuffer(this.command, table, alias, type);
		this.ensureJoins().add(j);
		return j;
	}

	public final MysqlQueryRefBuffer joinQuery(String alias, TableJoinType type) {
		MysqlQueryRefBuffer j = new MysqlQueryRefBuffer(this.command, alias, type);
		this.ensureJoins().add(j);
		return j;
	}

	public final MysqlWithRefBuffer joinWith(String target, String alias,
			TableJoinType type) {
		MysqlWithRefBuffer j = new MysqlWithRefBuffer(this.command, target, alias, type);
		this.ensureJoins().add(j);
		return j;
	}

	public final ISqlExprBuffer onCondition() {
		if (this.condition == null) {
			this.condition = new MysqlExprBuffer(this.command);
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
			for (MysqlRelationRefBuffer j : this.joins) {
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
}

package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

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
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

public abstract class SQLServerSourceBuffer extends SqlBuffer implements
		ISqlRelationRefBuffer {

	final SqlserverMetadata metadata;
	final SQLServerCommandBuffer command;
	final String alias;
	SQLServerExprBuffer condition;
	ArrayList<SQLServerSourceBuffer> joins;
	TableJoinType joinType;

	public SQLServerSourceBuffer(SqlserverMetadata metadata,
			SQLServerCommandBuffer command, String alias) {
		this.metadata = metadata;
		this.command = command;
		this.alias = SQLServerExprBuffer.quote(alias);
	}

	public SQLServerSourceBuffer(SqlserverMetadata metadata,
			SQLServerCommandBuffer command, String alias, TableJoinType type) {
		this.metadata = metadata;
		this.command = command;
		this.alias = SQLServerExprBuffer.quote(alias);
		this.joinType = type;
	}

	private final ArrayList<SQLServerSourceBuffer> ensureJoins() {
		if (this.joins == null) {
			this.joins = new ArrayList<SQLServerSourceBuffer>();
		}
		return this.joins;
	}

	public boolean findAlias(String name) {
		if (name.equals(this.alias)) {
			return true;
		}
		if (this.joins != null) {
			for (ISqlRelationRefBuffer j : this.joins) {
				if (j instanceof SQLServerTableSourceBuffer) {
					if (((SQLServerTableSourceBuffer) j).findAlias(name)) {
						return true;
					}
				} else {
					((SQLServerQuerySourceBuffer) j).findAlias(name);
				}
			}
		}
		return false;
	}

	public ISqlJoinedTableRefBuffer joinTable(String table, String alias,
			TableJoinType type) {
		SQLServerTableSourceBuffer j = new SQLServerTableSourceBuffer(this.metadata, this.command, table, alias, type);
		this.ensureJoins().add(j);
		return j;
	}

	public ISqlJoinedQueryRefBuffer joinQuery(String alias, TableJoinType type) {
		SQLServerQuerySourceBuffer j = new SQLServerQuerySourceBuffer(this.metadata, this.command, alias, type);
		this.ensureJoins().add(j);
		return j;
	}

	public ISqlJoinedWithRefBuffer joinWith(String target, String alias,
			TableJoinType type) {
		SQLServerWithSourceBuffer j = new SQLServerWithSourceBuffer(this.metadata, this.command, target, alias, type);
		this.ensureJoins().add(j);
		return j;
	}

	public ISqlExprBuffer onCondition() {
		if (this.condition == null) {
			this.condition = new SQLServerExprBuffer(this.metadata, this.command);
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
			for (SQLServerSourceBuffer j : this.joins) {
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

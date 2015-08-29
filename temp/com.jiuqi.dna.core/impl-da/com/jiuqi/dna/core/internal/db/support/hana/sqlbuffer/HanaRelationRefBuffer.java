package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

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

public abstract class HanaRelationRefBuffer extends SqlBuffer implements
		ISqlRelationRefBuffer {

	final HanaCommandBuffer command;

	final String alias;
	HanaExprBuffer condition;
	ArrayList<HanaRelationRefBuffer> joins;
	TableJoinType joinType;

	HanaRelationRefBuffer(HanaCommandBuffer command, String alias) {
		this.command = command;
		this.alias = HanaExprBuffer.quote(alias);
	}

	HanaRelationRefBuffer(HanaCommandBuffer command, String alias,
			TableJoinType type) {
		this.command = command;
		this.alias = HanaExprBuffer.quote(alias);
		this.joinType = type;
	}

	private final ArrayList<HanaRelationRefBuffer> ensureJoins() {
		if (this.joins == null) {
			this.joins = new ArrayList<HanaRelationRefBuffer>();
		}
		return this.joins;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
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
			for (HanaRelationRefBuffer j : this.joins) {
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

	protected abstract void writeRefTextTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args);

	public ISqlJoinedTableRefBuffer joinTable(String table, String alias,
			TableJoinType type) {
		HanaTableRefBuffer j = new HanaTableRefBuffer(this.command, table, alias, type);
		this.ensureJoins().add(j);
		return j;
	}

	public ISqlJoinedQueryRefBuffer joinQuery(String alias, TableJoinType type) {
		HanaQueryRefBuffer j = new HanaQueryRefBuffer(this.command, alias, type);
		this.ensureJoins().add(j);
		return j;
	}

	public ISqlJoinedWithRefBuffer joinWith(String target, String alias,
			TableJoinType type) {
		HanaWithRefBuffer j = new HanaWithRefBuffer(this.command, target, alias, type);
		this.ensureJoins().add(j);
		return j;
	}

	public final ISqlExprBuffer onCondition() {
		if (this.condition == null) {
			this.condition = new HanaExprBuffer(this.command);
		}
		return this.condition;
	}
}
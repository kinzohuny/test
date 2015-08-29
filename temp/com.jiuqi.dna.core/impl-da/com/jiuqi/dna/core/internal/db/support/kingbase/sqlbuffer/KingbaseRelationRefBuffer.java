package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.ArrayList;
import java.util.HashMap;
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

public abstract class KingbaseRelationRefBuffer extends SqlBuffer implements
		ISqlRelationRefBuffer {
	final String alias;
	KingbaseExprBuffer condition;
	ArrayList<KingbaseRelationRefBuffer> joins;
	TableJoinType joinType;
	protected String targetAlias;
	protected String alternateAlias;

	public KingbaseRelationRefBuffer(String alias) {
		this.alias = KingbaseExprBuffer.quote(alias);
	}

	public KingbaseRelationRefBuffer(String alias, TableJoinType type) {
		this.alias = KingbaseExprBuffer.quote(alias);
		this.joinType = type;
	}

	public void replace(String targetAlias, String alternateAlias) {
		this.targetAlias = targetAlias;
		this.alternateAlias = alternateAlias;
	}

	private final ArrayList<KingbaseRelationRefBuffer> ensureJoins() {
		if (this.joins == null) {
			this.joins = new ArrayList<KingbaseRelationRefBuffer>();
		}
		return this.joins;
	}

	public ISqlJoinedTableRefBuffer joinTable(String table, String alias,
			TableJoinType type) {
		if (this.targetAlias != null && alias.equals(this.targetAlias)) {
			alias = this.alternateAlias;
		}
		KingbaseTableRefBuffer j = new KingbaseTableRefBuffer(table, alias, type);
		j.replace(this.targetAlias, this.alternateAlias);
		this.ensureJoins().add(j);
		return j;
	}

	public ISqlJoinedQueryRefBuffer joinQuery(String alias, TableJoinType type) {
		if (this.targetAlias != null && alias.equals(this.targetAlias)) {
			alias = this.alternateAlias;
		}
		KingbaseSubQueryRefBuffer j = new KingbaseSubQueryRefBuffer(alias, type);
		j.replace(this.targetAlias, this.alternateAlias);
		this.ensureJoins().add(j);
		return j;
	}

	public ISqlJoinedWithRefBuffer joinWith(String target, String alias,
			TableJoinType type) {
		if (this.targetAlias != null && alias.equals(this.targetAlias)) {
			alias = this.alternateAlias;
		}
		KingbaseTableRefBuffer j = new KingbaseTableRefBuffer(target, alias, type);
		j.replace(this.targetAlias, this.alternateAlias);
		this.ensureJoins().add(j);
		return j;
	}

	public ISqlExprBuffer onCondition() {
		if (this.condition == null) {
			this.condition = new KingbaseExprBuffer();
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
			for (KingbaseRelationRefBuffer j : this.joins) {
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

	public final void writeWithTo(SqlStringBuffer sql, HashMap hmsql,
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
		SqlStringBuffer sqlName = new SqlStringBuffer();
		this.writeRefTextTo(sqlName, args);
		if (hmsql.get(sqlName.toString()) != null) {
			sql.append(hmsql.get(sqlName.toString()).toString());
		} else {
			sql.append(sqlName.toString());
		}
		sql.append(' ').append(this.alias);
		if (this.joins != null) {
			for (KingbaseRelationRefBuffer j : this.joins) {
				sql.append(' ');
				j.writeWithTo(sql, hmsql, args);
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

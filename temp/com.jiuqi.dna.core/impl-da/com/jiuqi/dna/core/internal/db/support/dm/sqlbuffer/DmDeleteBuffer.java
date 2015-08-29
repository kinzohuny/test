package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.impl.ContextVariableIntl;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class DmDeleteBuffer extends DmCommandBuffer implements ISqlDeleteBuffer {

	final DmTableSourceBuffer target;
	DmExprBuffer where;

	public DmDeleteBuffer(String table, String alias) {
		super(null);
		this.target = new DmTableSourceBuffer(table, alias);
	}

	public final DmTableSourceBuffer target() {
		return this.target;
	}

	public final DmExprBuffer where() {
		if (this.where == null) {
			this.where = new DmExprBuffer();
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		throw new UnsupportedOperationException();
	}
	
	private boolean isOmitTargetSource() {
		for (DmSourceBuffer join : this.target.joins) {
			if (join.joinType != TableJoinType.INNER) {
				return false;
			}
		}
		return ContextVariableIntl.OPTIMIZE_MODIFY_SQL_OMIT_TARGET_SOURCE.getBoolean();
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("delete from ");
		if (this.target.joins != null) {
			// 多表
			if (isOmitTargetSource()) {
				sql.append(this.target.table).append(' ').append(this.target.alias).append(" where exists(select 1 from ");
				this.target.writeJoinsTo(sql, args);
				sql.append(" where (");
				this.where.writeTo(sql, args);
				sql.append(") and (");
				this.target.joins.get(0).condition.writeTo(sql, args);
				sql.append("))");
			} else {
				String alias = "\"$T\"";
				sql.append(this.target.table).append(' ').append(alias).append(" where exists(select 1 from ");
				this.target.writeTo(sql, args);
				sql.append(" where (");
				this.where.writeTo(sql, args);
				sql.append(") and ").append(this.target.alias).append(".recid=").append(alias).append(".recid)");
			}
		} else if (this.where != null) {
			// 单表
			this.target.writeTo(sql, args);
			sql.append(" where ");
			this.where.writeTo(sql, args);
		} else {
			// 没有where
			sql.append(this.target.table);
		}
	}
}
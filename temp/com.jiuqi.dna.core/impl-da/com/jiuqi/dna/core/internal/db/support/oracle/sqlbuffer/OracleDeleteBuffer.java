package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.impl.ContextVariableIntl;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlCommandBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class OracleDeleteBuffer extends SqlCommandBuffer implements ISqlDeleteBuffer {

	final OracleTableRefBuffer table;
	OracleExprBuffer where;
	String cursor;

	public OracleDeleteBuffer(OracleSegmentBuffer scope, String table,
			String alias) {
		super(scope);
		this.table = new OracleTableRefBuffer(table, alias);
	}

	public ISqlTableRefBuffer target() {
		return this.table;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new OracleExprBuffer();
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		this.cursor = cursor;
	}
	
	private boolean isOmitTargetSource() {
		for (OracleRelationRefBuffer join : this.table.joins) {
			if (join.joinType != TableJoinType.INNER) {
				return false;
			}
		}
		return ContextVariableIntl.OPTIMIZE_MODIFY_SQL_OMIT_TARGET_SOURCE.getBoolean();
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("delete from ");
		if (this.table.joins != null) {
			// 多表
			if (isOmitTargetSource()) {
				sql.append(this.table.name).append(' ').append(this.table.alias).append(" where exists(select 1 from ");
				this.table.writeJoinsTo(sql, args);
				sql.append(" where (");
				this.where.writeTo(sql, args);
				sql.append(") and (");
				this.table.joins.get(0).condition.writeTo(sql, args);
				sql.append("))");
			} else {
				String alias = "\"$T\"";
				sql.append(this.table.name).append(' ').append(alias).append(" where exists(select 1 from ");
				this.table.writeTo(sql, args);
				sql.append(" where (");
				this.where.writeTo(sql, args);
				sql.append(") and ").append(this.table.alias).append(".recid=").append(alias).append(".recid)");
			}
		} else if (this.cursor != null) {
			// 游标
			this.table.writeTo(sql, args);
			sql.append(" where current of ").append(this.cursor);
		} else if (this.where != null) {
			// 单表
			this.table.writeTo(sql, args);
			sql.append(" where ");
			this.where.writeTo(sql, args);
		} else {
			// 没有where
			sql.append(this.table.name);
		}
		if (this.scope != null) {
			sql.append(';');
		}
	}
}
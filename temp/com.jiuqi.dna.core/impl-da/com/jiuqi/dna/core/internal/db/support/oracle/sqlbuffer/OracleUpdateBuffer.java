package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.impl.ContextVariableIntl;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlCommandBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class OracleUpdateBuffer extends SqlCommandBuffer implements ISqlUpdateBuffer {

	static class OracleUpdateValueBuffer extends OracleExprBuffer {

		final String field;

		public OracleUpdateValueBuffer(String field) {
			this.field = OracleExprBuffer.quote(field);
		}
	}

	final OracleTableRefBuffer table;
	final ArrayList<OracleUpdateValueBuffer> values = new ArrayList<OracleUpdateValueBuffer>();
	final boolean assignFromSlaveTable;
	OracleExprBuffer where;
	String cursor;
	private static final String alias = "\"$T\"";

	public OracleUpdateBuffer(OracleSegmentBuffer scope, String table,
			String alias, boolean assignFromSlaveTable) {
		super(scope);
		this.assignFromSlaveTable = assignFromSlaveTable;
		this.table = new OracleTableRefBuffer(table, alias);
	}

	public ISqlTableRefBuffer target() {
		return this.table;
	}

	public ISqlExprBuffer newValue(String field) {
		OracleUpdateValueBuffer val = new OracleUpdateValueBuffer(field);
		if (this.table.joins != null && !isOmitTargetSource()) {
			val.replace(this.table.alias, alias);
		}
		this.values.add(val);
		return val;
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
		if (this.table.joins != null) {
			if (isOmitTargetSource()) {
				sql.append("update ").append(this.table.name).append(' ').append(this.table.alias).append(" set ");
				if (this.assignFromSlaveTable) {
					sql.append('(');
					Iterator<OracleUpdateValueBuffer> iter = this.values.iterator();
					OracleUpdateValueBuffer val = iter.next();
					sql.append(val.field);
					while (iter.hasNext()) {
						sql.append(',').append(iter.next().field);
					}
					sql.append(")=(select ");
					iter = this.values.iterator();
					iter.next().writeTo(sql, args);
					while (iter.hasNext()) {
						sql.append(',');
						iter.next().writeTo(sql, args);
					}
					sql.append(" from ");
					this.table.writeJoinsTo(sql, args);
					sql.append(" where ");
					if (this.where != null) {
						sql.append('(');
						this.where.writeTo(sql, args);
						sql.append(") and ");
					}
					sql.append('(');
					this.table.joins.get(0).condition.writeTo(sql, args);
					sql.append(") and rownum <= 1)");
				} else {
					Iterator<OracleUpdateValueBuffer> iter = this.values.iterator();
					OracleUpdateValueBuffer val = iter.next();
					sql.append(val.field).append('=');
					val.writeTo(sql, args);
					while (iter.hasNext()) {
						val = iter.next();
						sql.append(',').append(val.field).append('=');
						val.writeTo(sql, args);
					}
				}
				sql.append(" where exists(select 1 from ");
				this.table.writeJoinsTo(sql, args);
				sql.append(" where ");
				if (this.where != null) {
					sql.append('(');
					this.where.writeTo(sql, args);
					sql.append(") and (");
					this.table.joins.get(0).condition.writeTo(sql, args);
					sql.append("))");
				} else {
					this.table.joins.get(0).condition.writeTo(sql, args);
					sql.append(")");
				}
			} else {
				sql.append("update ").append(this.table.name).append(' ').append(alias).append(" set ");
				if (this.assignFromSlaveTable) {
					sql.append('(');
					Iterator<OracleUpdateValueBuffer> iter = this.values.iterator();
					OracleUpdateValueBuffer val = iter.next();
					sql.append(val.field);
					while (iter.hasNext()) {
						sql.append(',').append(iter.next().field);
					}
					sql.append(")=(select ");
					iter = this.values.iterator();
					iter.next().writeTo(sql, args);
					while (iter.hasNext()) {
						sql.append(',');
						iter.next().writeTo(sql, args);
					}
					sql.append(" from ");
					this.table.writeTo(sql, args);
					sql.append(" where ");
					if (this.where != null) {
						sql.append('(');
						this.where.writeTo(sql, args);
						sql.append(") and ");
					}
					sql.append(this.table.alias).append(".recid=").append(alias).append(".recid and rownum<=1)");
				} else {
					Iterator<OracleUpdateValueBuffer> iter = this.values.iterator();
					OracleUpdateValueBuffer val = iter.next();
					sql.append(val.field).append('=');
					val.writeTo(sql, args);
					while (iter.hasNext()) {
						val = iter.next();
						sql.append(',').append(val.field).append('=');
						val.writeTo(sql, args);
					}
				}
				sql.append(" where exists(select 1 from ");
				this.table.writeTo(sql, args);
				sql.append(" where ");
				if (this.where != null) {
					sql.append('(');
					this.where.writeTo(sql, args);
					sql.append(") and ");
				}
				sql.append(this.table.alias).append(".recid=").append(alias).append(".recid)");
			}
		} else {
			sql.append("update ");
			this.table.writeTo(sql, args);
			sql.append(" set ");
			Iterator<OracleUpdateValueBuffer> iter = this.values.iterator();
			OracleUpdateValueBuffer val = iter.next();
			sql.append(val.field).append('=');
			val.writeTo(sql, args);
			while (iter.hasNext()) {
				val = iter.next();
				sql.append(',').append(val.field).append('=');
				val.writeTo(sql, args);
			}
			if (this.cursor != null) {
				sql.append(" where current of ").append(this.cursor);
			} else if (this.where != null) {
				sql.append(" where ");
				this.where.writeTo(sql, args);
			}
		}
		if (this.scope != null) {
			sql.append(';');
		}
	}
}

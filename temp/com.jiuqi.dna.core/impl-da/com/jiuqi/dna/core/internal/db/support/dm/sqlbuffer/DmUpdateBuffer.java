package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.impl.ContextVariableIntl;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class DmUpdateBuffer extends DmCommandBuffer implements ISqlUpdateBuffer {

	static class DmUpdateValueBuffer extends DmExprBuffer {

		final String field;

		public DmUpdateValueBuffer(String field) {
			this.field = DmExprBuffer.quote(field);
		}
	}

	DmUpdateBuffer(String table, String alias, boolean assignFromSlaveTable) {
		super(null);
		this.target = new DmTableSourceBuffer(table, alias);
		this.assignFromSlaveTable = assignFromSlaveTable;
	}

	final DmTableSourceBuffer target;
	final ArrayList<DmUpdateValueBuffer> values = new ArrayList<DmUpdateValueBuffer>();
	final boolean assignFromSlaveTable;
	DmExprBuffer where;

	public DmTableSourceBuffer target() {
		return this.target;
	}

	public ISqlExprBuffer newValue(String field) {
		DmUpdateValueBuffer val = new DmUpdateValueBuffer(field);
		if (this.target.joins != null && !isOmitTargetSource()) {
			val.replace(this.target.alias, alias);
		}
		this.values.add(val);
		return val;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new DmExprBuffer();
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		throw new UnsupportedOperationException();
	}

	private static final String alias = "\"$T\"";
	
	private boolean isOmitTargetSource() {
		for (DmSourceBuffer join : this.target.joins) {
			if (join.joinType != TableJoinType.INNER) {
				return false;
			}
		}
		return ContextVariableIntl.OPTIMIZE_MODIFY_SQL_OMIT_TARGET_SOURCE.getBoolean();
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		if (this.target.joins != null) {
			if (isOmitTargetSource()) {
				sql.append("update ").append(this.target.table).append(' ').append(this.target.alias).append(" set ");
				if (this.assignFromSlaveTable) {
					sql.append('(');
					Iterator<DmUpdateValueBuffer> iter = this.values.iterator();
					DmUpdateValueBuffer val = iter.next();
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
					this.target.writeJoinsTo(sql, args);
					sql.append(" where ");
					if (this.where != null) {
						sql.append('(');
						this.where.writeTo(sql, args);
						sql.append(") and ");
					}
					sql.append('(');
					this.target.joins.get(0).condition.writeTo(sql, args);
					sql.append(") and rownum <= 1)");
				} else {
					Iterator<DmUpdateValueBuffer> iter = this.values.iterator();
					DmUpdateValueBuffer val = iter.next();
					sql.append(val.field).append('=');
					val.writeTo(sql, args);
					while (iter.hasNext()) {
						val = iter.next();
						sql.append(',').append(val.field).append('=');
						val.writeTo(sql, args);
					}
				}
				sql.append(" where exists(select 1 from ");
				this.target.writeJoinsTo(sql, args);
				sql.append(" where ");
				if (this.where != null) {
					sql.append('(');
					this.where.writeTo(sql, args);
					sql.append(") and (");
					this.target.joins.get(0).condition.writeTo(sql, args);
					sql.append("))");
				} else {
					this.target.joins.get(0).condition.writeTo(sql, args);
					sql.append(')');
				}
			} else {
				sql.append("update ").append(this.target.table).append(' ').append(alias).append(" set ");
				if (this.assignFromSlaveTable) {
					sql.append('(');
					Iterator<DmUpdateValueBuffer> iter = this.values.iterator();
					DmUpdateValueBuffer val = iter.next();
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
					this.target.writeTo(sql, args);
					sql.append(" where ");
					if (this.where != null) {
						sql.append('(');
						this.where.writeTo(sql, args);
						sql.append(") and ");
					}
					sql.append(this.target.alias).append(".recid=").append(alias).append(".recid and rownum<=1)");
				} else {
					Iterator<DmUpdateValueBuffer> iter = this.values.iterator();
					DmUpdateValueBuffer val = iter.next();
					sql.append(val.field).append('=');
					val.writeTo(sql, args);
					while (iter.hasNext()) {
						val = iter.next();
						sql.append(',').append(val.field).append('=');
						val.writeTo(sql, args);
					}
				}
				sql.append(" where exists(select 1 from ");
				this.target.writeTo(sql, args);
				sql.append(" where ");
				if (this.where != null) {
					sql.append('(');
					this.where.writeTo(sql, args);
					sql.append(") and ");
				}
				sql.append(this.target.alias).append(".recid=").append(alias).append(".recid)");
			}
		} else {
			sql.append("update ");
			this.target.writeTo(sql, args);
			sql.append(" set ");
			Iterator<DmUpdateValueBuffer> iter = this.values.iterator();
			DmUpdateValueBuffer val = iter.next();
			sql.append(val.field).append('=');
			val.writeTo(sql, args);
			while (iter.hasNext()) {
				val = iter.next();
				sql.append(',').append(val.field).append('=');
				val.writeTo(sql, args);
			}
			if (this.where != null) {
				sql.append(" where ");
				this.where.writeTo(sql, args);
			}
		}
	}
}
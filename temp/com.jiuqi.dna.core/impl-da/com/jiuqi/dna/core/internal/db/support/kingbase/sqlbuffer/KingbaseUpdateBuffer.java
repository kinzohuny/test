package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlCommandBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class KingbaseUpdateBuffer extends SqlCommandBuffer implements ISqlUpdateBuffer {
	final KingbaseTableRefBuffer table;
	final ArrayList<KingbaseUpdateValueBuffer> values = new ArrayList<KingbaseUpdateValueBuffer>();
	final boolean assignFromSlaveTable;
	KingbaseExprBuffer where;
	String cursor;
	private static final String alias = "\"$T\"";

	static class KingbaseUpdateValueBuffer extends KingbaseExprBuffer {
		final String field;

		public KingbaseUpdateValueBuffer(String field) {
			this.field = KingbaseExprBuffer.quote(field);
		}
	}

	public KingbaseUpdateBuffer(KingbaseSegmentBuffer scope, String table,
			String alias, boolean assignFromSlaveTable) {
		super(scope);
		this.assignFromSlaveTable = assignFromSlaveTable;
		this.table = new KingbaseTableRefBuffer(table, alias);
	}

	public ISqlTableRefBuffer target() {
		return this.table;
	}

	public ISqlExprBuffer newValue(String field) {
		KingbaseUpdateValueBuffer val = new KingbaseUpdateValueBuffer(field);
		if (this.table.joins != null) {
			val.replace(this.table.alias, alias);
		}
		this.values.add(val);
		return val;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new KingbaseExprBuffer();
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		this.cursor = cursor;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		if (this.table.joins != null) {
			final String otherAN = "\"$TE\"";
			sql.append("update ").append(this.table.name).append(' ').append(alias).append(" set ");
			if (this.assignFromSlaveTable) {
				Iterator<KingbaseUpdateValueBuffer> iter = this.values.iterator();
				Iterator<KingbaseUpdateValueBuffer> iterValue = this.values.iterator();
				KingbaseUpdateValueBuffer val = iter.next();
				sql.append(val.field);
				sql.append("=(select ");
				iterValue.next().writeTo(sql, args);
				sql.append(" from ");
				this.table.writeTo(sql, args);
				sql.append(" where ");
				if (this.where != null) {
					sql.append('(');
					this.where.writeTo(sql, args);
					sql.append(") and ");
				}
				sql.append(this.table.alias).append(".recid=").append(alias).append(".recid and rownum<=1)");
				while (iter.hasNext()) {
					val = iter.next();
					sql.append(',');
					sql.append(val.field);
					sql.append("=(select ");
					iterValue.next().writeTo(sql, args);
					sql.append(" from ");
					this.table.writeTo(sql, args);
					sql.append(" where ");
					if (this.where != null) {
						sql.append('(');
						this.where.writeTo(sql, args);
						sql.append(") and ");
					}
					sql.append(this.table.alias).append(".recid=").append(alias).append(".recid and rownum<=1)");
				}
			} else {
				Iterator<KingbaseUpdateValueBuffer> iter = this.values.iterator();
				KingbaseUpdateValueBuffer val = iter.next();
				sql.append(val.field).append('=');
				val.writeTo(sql, args);
				while (iter.hasNext()) {
					val = iter.next();
					sql.append(',').append(val.field).append('=');
					val.writeTo(sql, args);
				}
			}
			sql.append(" where not exists (select 1 from ");
			sql.append(this.table.name).append(' ').append(otherAN).append(" where not exists (select 1 from ");
			this.table.writeTo(sql, args);
			sql.append(" where ");
			if (this.where != null) {
				sql.append('(');
				this.where.writeTo(sql, args);
				sql.append(") and ");
			}
			sql.append(alias).append(".recid=").append(otherAN).append(".recid))");
		} else {
			sql.append("update ");
			this.table.writeTo(sql, args);
			sql.append(" set ");
			Iterator<KingbaseUpdateValueBuffer> iter = this.values.iterator();
			KingbaseUpdateValueBuffer val = iter.next();
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

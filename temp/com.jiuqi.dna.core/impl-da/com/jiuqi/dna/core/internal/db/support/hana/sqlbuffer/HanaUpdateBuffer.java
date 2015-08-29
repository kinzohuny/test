package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class HanaUpdateBuffer extends HanaCommandBuffer implements
		ISqlUpdateBuffer {

	final HanaTableRefBuffer target;
	final boolean assignFromSlaveTable;
	final ArrayList<HanaUpdateAssignBuffer> values = new ArrayList<HanaUpdateAssignBuffer>();
	HanaExprBuffer where;

	public HanaUpdateBuffer(String table, String alias,
			boolean assignFromSlaveTable) {
		super(null);
		this.target = new HanaTableRefBuffer(this, table, alias);
		this.assignFromSlaveTable = assignFromSlaveTable;
	}

	static class HanaUpdateAssignBuffer extends HanaExprBuffer {

		final String field;

		public HanaUpdateAssignBuffer(HanaUpdateBuffer update, String field) {
			super(update);
			this.field = HanaExprBuffer.quote(field);
		}
	}

	public HanaTableRefBuffer target() {
		return this.target;
	}

	public HanaUpdateAssignBuffer newValue(String field) {
		HanaUpdateAssignBuffer assign = new HanaUpdateAssignBuffer(this, field);
		this.values.add(assign);
		return assign;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new HanaExprBuffer(this);
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		throw new UnsupportedOperationException();
	}

	private static final String alias = "\"$ZM\"";

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		if (this.target.joins != null) {
			sql.append("update ").append(this.target.table).append(' ').append(alias).append(" set ");
			Iterator<HanaUpdateAssignBuffer> it = this.values.iterator();
			HanaUpdateAssignBuffer val = it.next();
			sql.append(val.field).append('=');
			val.writeTo(sql, args);
			while (it.hasNext()) {
				val = it.next();
				sql.append(',').append(val.field).append('=');
				val.writeTo(sql, args);
			}
			sql.append(" from ");
			this.target.writeTo(sql, args);
			sql.append(" where ");
			if (this.where != null) {
				sql.append('(');
				this.where.writeTo(sql, args);
				sql.append(") and ");
			}
			sql.append(this.target.alias).append(".recid=").append(alias).append(".recid");
		} else {
			sql.append("update ");
			this.target.writeTo(sql, args);
			sql.append(" set ");
			Iterator<HanaUpdateAssignBuffer> it = this.values.iterator();
			HanaUpdateAssignBuffer val = it.next();
			sql.append(val.field).append('=');
			val.writeTo(sql, args);
			while (it.hasNext()) {
				val = it.next();
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
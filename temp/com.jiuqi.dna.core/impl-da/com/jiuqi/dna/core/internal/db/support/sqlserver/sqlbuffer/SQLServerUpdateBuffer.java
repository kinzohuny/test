package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

class SQLServerUpdateBuffer extends SQLServerCommandBuffer implements
		ISqlUpdateBuffer {

	final SqlserverMetadata metadata;

	static class SQLServerUpdateValueBuffer extends SQLServerExprBuffer {

		final String field;

		public SQLServerUpdateValueBuffer(SqlserverMetadata metadata,
				SQLServerUpdateBuffer update, String field) {
			super(metadata, update);
			this.field = SQLServerExprBuffer.quote(field);
		}
	}

	final SQLServerTableSourceBuffer table;
	final ArrayList<SQLServerUpdateValueBuffer> values = new ArrayList<SQLServerUpdateValueBuffer>();
	SQLServerExprBuffer where;
	String cursor;

	public SQLServerUpdateBuffer(SqlserverMetadata metadata,
			SQLServerSegmentBuffer scope, String table, String alias) {
		super(scope);
		this.metadata = metadata;
		this.table = new SQLServerTableSourceBuffer(metadata, this, table, alias);
	}

	public ISqlTableRefBuffer target() {
		return this.table;
	}

	public ISqlExprBuffer newValue(String field) {
		SQLServerUpdateValueBuffer val = new SQLServerUpdateValueBuffer(this.metadata, this, field);
		this.values.add(val);
		return val;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new SQLServerExprBuffer(this.metadata, this);
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		this.cursor = cursor;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("update ").append(this.table.table).append(" set ");
		Iterator<SQLServerUpdateValueBuffer> iter = this.values.iterator();
		SQLServerUpdateValueBuffer val = iter.next();
		sql.append(val.field).append('=');
		val.writeTo(sql, args);
		while (iter.hasNext()) {
			val = iter.next();
			sql.append(',').append(val.field).append('=');
			val.writeTo(sql, args);
		}
		if (this.table.joins != null || this.table.alias != null && !this.table.table.equals(this.table.alias)) {
			sql.append(" from ");
			this.table.writeTo(sql, args);
		}
		if (this.cursor != null) {
			sql.append(" where current of ").append(this.cursor);
		} else if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
		sql.append(';');
	}
}

package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

class SQLServerDeleteBuffer extends SQLServerCommandBuffer implements
		ISqlDeleteBuffer {

	final SqlserverMetadata metadata;
	final SQLServerTableSourceBuffer table;
	SQLServerExprBuffer where;
	String cursor;

	public SQLServerDeleteBuffer(SqlserverMetadata metadata,
			SQLServerSegmentBuffer scope, String table, String alias) {
		super(scope);
		this.metadata = metadata;
		this.table = new SQLServerTableSourceBuffer(metadata, this, table, alias);
	}

	public ISqlTableRefBuffer target() {
		return this.table;
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
		sql.append("delete from ");
		sql.append(this.table.table);
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

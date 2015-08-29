package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class MysqlDeleteBuffer extends MysqlCommandBuffer implements ISqlDeleteBuffer {

	final MysqlTableRefBuffer table;
	MysqlExprBuffer where;

	MysqlDeleteBuffer(ISqlSegmentBuffer scope, String table, String alias) {
		super(scope);
		this.table = new MysqlTableRefBuffer(this, table, alias);
	}

	public final MysqlTableRefBuffer target() {
		return this.table;
	}

	public final ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new MysqlExprBuffer(this);
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		throw new UnsupportedOperationException();
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("delete from ");
		sql.append(this.table.alias);
		sql.append(" using ");
		this.table.writeTo(sql, args);
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
	}

}

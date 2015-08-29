package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class MysqlUpdateBuffer extends MysqlCommandBuffer implements ISqlUpdateBuffer {

	static class MySqlUpdateAssignBuffer extends MysqlExprBuffer {

		final String field;

		public MySqlUpdateAssignBuffer(MysqlCommandBuffer command, String field) {
			super(command);
			this.field = quote(field);
		}
	}

	final MysqlTableRefBuffer target;

	final ArrayList<MySqlUpdateAssignBuffer> values = new ArrayList<MySqlUpdateAssignBuffer>();

	MysqlExprBuffer where;

	MysqlUpdateBuffer(ISqlSegmentBuffer scope, String table, String alias,
			boolean assignFromSlaveTable) {
		super(scope);
		this.target = new MysqlTableRefBuffer(this, table, alias);
	}

	public final MysqlTableRefBuffer target() {
		return this.target;
	}

	public MysqlExprBuffer newValue(String field) {
		MySqlUpdateAssignBuffer value = new MySqlUpdateAssignBuffer(this, field);
		this.values.add(value);
		return value;
	}

	public MysqlExprBuffer where() {
		if (this.where == null) {
			this.where = new MysqlExprBuffer(this);
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		throw new UnsupportedOperationException();
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("update ");
		this.target.writeTo(sql, args);
		sql.append(" set ");
		for (int i = 0, c = this.values.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',');
			}
			sql.append(this.target.alias);
			sql.append('.');
			MySqlUpdateAssignBuffer value = this.values.get(i);
			sql.append(value.field);
			sql.append('=');
			value.writeTo(sql, args);
		}
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
	}

}

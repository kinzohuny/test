package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateMultiBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class MysqlUpdateMultiBuffer extends MysqlCommandBuffer implements
		ISqlUpdateMultiBuffer {

	final MysqlTableRefBuffer target;

	static class MySqlUpdateMultiAssignBuffer extends MysqlExprBuffer {

		final String alias;
		final String field;

		public MySqlUpdateMultiAssignBuffer(MysqlUpdateMultiBuffer command,
				String alias, String field) {
			super(command);
			this.alias = quote(alias);
			this.field = quote(field);
		}

	}

	final ArrayList<MySqlUpdateMultiAssignBuffer> values = new ArrayList<MySqlUpdateMultiAssignBuffer>();
	MysqlExprBuffer where;

	MysqlUpdateMultiBuffer(ISqlSegmentBuffer scope, String table, String alias) {
		super(scope);
		this.target = new MysqlTableRefBuffer(this, table, alias);
	}

	public final MysqlTableRefBuffer target() {
		return this.target;
	}

	public final MysqlExprBuffer newValue(String alias, String field) {
		MySqlUpdateMultiAssignBuffer value = new MySqlUpdateMultiAssignBuffer(this, alias, field);
		this.values.add(value);
		return value;
	}

	public final MysqlExprBuffer where() {
		if (this.where == null) {
			this.where = new MysqlExprBuffer(this);
		}
		return this.where;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("update ");
		this.target.writeTo(sql, args);
		sql.append(" set ");
		for (int i = 0, c = this.values.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',');
			}
			MySqlUpdateMultiAssignBuffer value = this.values.get(i);
			sql.append(value.alias);
			sql.append('.');
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

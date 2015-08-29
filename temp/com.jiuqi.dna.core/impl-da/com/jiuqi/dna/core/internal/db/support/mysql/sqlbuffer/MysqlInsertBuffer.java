package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class MysqlInsertBuffer extends MysqlCommandBuffer implements ISqlInsertBuffer {

	final String table;
	ArrayList<String> fields = new ArrayList<String>();
	MysqlSelectBuffer select;
	ArrayList<MysqlExprBuffer> values;

	MysqlInsertBuffer(ISqlSegmentBuffer scope, String table) {
		super(scope);
		this.table = MysqlExprBuffer.quote(table);
	}

	public final void newField(String name) {
		this.fields.add(MysqlExprBuffer.quote(name));
	}

	public final MysqlExprBuffer newValue() {
		if (this.values == null) {
			this.values = new ArrayList<MysqlExprBuffer>();
		}
		MysqlExprBuffer e = new MysqlExprBuffer(this);
		this.values.add(e);
		return e;
	}

	public final MysqlSelectBuffer select() {
		if (this.select == null) {
			this.select = new MysqlSelectBuffer(this);
		}
		return this.select;
	}

	public final void writeTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append("insert into ").append(this.table).append('(');
		for (int i = 0, c = this.fields.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',');
			}
			sql.append(this.fields.get(i));
		}
		sql.append(')').append(' ');
		if (this.values != null) {
			sql.append("values (");
			for (int i = 0, c = this.values.size(); i < c; i++) {
				if (i > 0) {
					sql.append(",");
				}
				this.values.get(i).writeTo(sql, args);
			}
			sql.append(')');
		} else {
			this.select.writeTo(sql, args);
		}
	}

}

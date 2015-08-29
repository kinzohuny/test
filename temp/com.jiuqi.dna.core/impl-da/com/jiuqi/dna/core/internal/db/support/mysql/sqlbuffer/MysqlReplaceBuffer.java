package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlReplaceBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class MysqlReplaceBuffer extends MysqlCommandBuffer implements
		ISqlReplaceBuffer {

	final String table;

	final ArrayList<String> fields = new ArrayList<String>();
	final ArrayList<MysqlExprBuffer> values = new ArrayList<MysqlExprBuffer>();

	MysqlReplaceBuffer(ISqlSegmentBuffer scope, String table) {
		super(scope);
		this.table = MysqlExprBuffer.quote(table);
	}

	public final void newField(String name) {
		this.fields.add(MysqlExprBuffer.quote(name));
	}

	public final ISqlExprBuffer newValue() {
		MysqlExprBuffer expr = new MysqlExprBuffer(this);
		this.values.add(expr);
		return expr;
	}

	public final void writeTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append("replace into ").append(this.table);
		sql.append(" (");
		for (int i = 0, c = this.fields.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',');
			}
			sql.append(this.fields.get(i));
		}
		sql.append(") values (");
		for (int i = 0, c = this.values.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',');
			}
			this.values.get(i).writeTo(sql, args);
		}
		sql.append(')');
	}

}

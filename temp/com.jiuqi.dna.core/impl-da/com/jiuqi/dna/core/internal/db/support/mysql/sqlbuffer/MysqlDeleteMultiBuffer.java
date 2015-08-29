package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteMultiBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class MysqlDeleteMultiBuffer extends MysqlCommandBuffer implements
		ISqlDeleteMultiBuffer {

	final MysqlTableRefBuffer table;
	final ArrayList<String> froms;
	MysqlExprBuffer where;

	MysqlDeleteMultiBuffer(ISqlSegmentBuffer scope, String table, String alias) {
		super(scope);
		this.table = new MysqlTableRefBuffer(this, table, alias);
		this.froms = new ArrayList<String>();
		this.froms.add(MysqlExprBuffer.quote(alias));
	}

	public MysqlTableRefBuffer target() {
		return this.table;
	}

	public MysqlExprBuffer where() {
		if (this.where == null) {
			this.where = new MysqlExprBuffer(this);
		}
		return this.where;
	}

	public void from(String alias) {
		this.froms.add(MysqlExprBuffer.quote(alias));
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("delete from ");
		for (int i = 0, c = this.froms.size(); i < c; i++) {
			if (i > 0) {
				sql.append(',');
			}
			sql.append(this.froms.get(i));
		}
		sql.append(" using ");
		this.table.writeTo(sql, args);
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
	}

}

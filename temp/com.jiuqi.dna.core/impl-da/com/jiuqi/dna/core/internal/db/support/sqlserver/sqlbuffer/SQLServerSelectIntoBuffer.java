package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectIntoBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

class SQLServerSelectIntoBuffer implements ISqlSelectIntoBuffer {

	final SqlserverMetadata metadata;

	public SQLServerSelectIntoBuffer(SqlserverMetadata metadata) {
		this.metadata = metadata;
	}

	ArrayList<ISqlRelationRefBuffer> source = new ArrayList<ISqlRelationRefBuffer>();
	ArrayList<SQLServerSelectColumnBuffer> columns = new ArrayList<SQLServerSelectColumnBuffer>();
	SQLServerExprBuffer where;

	public ISqlTableRefBuffer newTable(String table, String alias) {
		ISqlTableRefBuffer t = new SQLServerTableSourceBuffer(this.metadata, null, table, alias);
		this.source.add(t);
		return t;
	}

	public ISqlQueryRefBuffer newSubquery(String alias) {
		ISqlQueryRefBuffer q = new SQLServerQuerySourceBuffer(this.metadata, null, alias);
		this.source.add(q);
		return q;
	}

	public ISqlExprBuffer newColumn(String var) {
		SQLServerSelectColumnBuffer expr = new SQLServerSelectColumnBuffer(this.metadata, null, "@" + var);
		this.columns.add(expr);
		return expr;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new SQLServerExprBuffer(this.metadata, null);
		}
		return this.where;
	}

	private void writeColumns(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		Iterator<SQLServerSelectColumnBuffer> iter = this.columns.iterator();
		SQLServerSelectColumnBuffer c = iter.next();
		sql.append(c.alias).append('=');
		c.writeTo(sql, args);
		while (iter.hasNext()) {
			c = iter.next();
			sql.append(',').append(c.alias).append('=');
			c.writeTo(sql, args);
		}
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("select ");
		this.writeColumns(sql, args);
		sql.append(" from ");
		Iterator<ISqlRelationRefBuffer> iter = this.source.iterator();
		iter.next().writeTo(sql, args);
		while (iter.hasNext()) {
			sql.append(',');
			iter.next().writeTo(sql, args);
		}
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
	}
}

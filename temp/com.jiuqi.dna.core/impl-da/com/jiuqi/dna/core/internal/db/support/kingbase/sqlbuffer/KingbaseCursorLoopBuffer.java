package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCursorLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class KingbaseCursorLoopBuffer extends KingbaseSegmentBuffer implements
		ISqlCursorLoopBuffer {

	KingbaseQueryBuffer query;
	final String cursor;
	final boolean forUpdate;

	public KingbaseCursorLoopBuffer(KingbaseSegmentBuffer scope, String cursor,
			boolean forUpdate) {
		super(scope);
		this.cursor = cursor;
		this.forUpdate = forUpdate;
	}

	public ISqlQueryBuffer query() {
		if (this.query == null) {
			this.query = new KingbaseQueryBuffer();
		}
		return this.query;
	}

	private void writeFetch(SqlStringBuffer sql) {
		sql.append("fetch ").append(this.cursor);
		if (this.vars != null) {
			Iterator<Variable> iter = this.vars.iterator();
			sql.append(" into ").append(iter.next().name);
			while (iter.hasNext()) {
				sql.append(',').append(iter.next().name);
			}
		}
		sql.append(';');
	}

	@Override
	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("declare cursor ").append(this.cursor).append(" is ");
		this.query.writeTo(sql, args);
		if (this.forUpdate) {
			sql.append(" for update");
		}
		sql.append(';');
		if (this.vars != null) {
			for (Variable var : this.vars) {
				var.writeTo(sql);
				sql.append(';');
			}
		}
		sql.append("begin; ");
		sql.append("open ").append(this.cursor).append(";loop ");
		writeFetch(sql);
		sql.append("exit when ").append(this.cursor).append("%NOTFOUND;");
		writeStmts(sql, args);
		sql.append(" end loop;close ").append(this.cursor).append(";end;");
	}
}

package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

class SQLServerInsertBuffer extends SQLServerCommandBuffer implements
		ISqlInsertBuffer {

	final SqlserverMetadata metadata;
	final String table;
	ArrayList<String> fields = new ArrayList<String>();
	SQLServerSelectBuffer select;
	ArrayList<SQLServerExprBuffer> values;

	public SQLServerInsertBuffer(SqlserverMetadata metadata,
			SQLServerSegmentBuffer scope, String table) {
		super(scope);
		this.metadata = metadata;
		this.table = table;
	}

	public void newField(String name) {
		this.fields.add(SQLServerExprBuffer.quote(name));
	}

	public ISqlExprBuffer newValue() {
		if (this.values == null) {
			this.values = new ArrayList<SQLServerExprBuffer>();
		}
		SQLServerExprBuffer e = new SQLServerExprBuffer(this.metadata, this);
		this.values.add(e);
		return e;
	}

	public ISqlSelectBuffer select() {
		if (this.select == null) {
			this.select = new SQLServerSelectBuffer(this.metadata, this);
		}
		return this.select;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("insert into ").append(this.table).append('(');
		Iterator<String> iter = this.fields.iterator();
		sql.append(iter.next());
		while (iter.hasNext()) {
			sql.append(',').append(iter.next());
		}
		sql.append(')').append(' ');
		if (this.values != null) {
			sql.append("values(");
			Iterator<SQLServerExprBuffer> itval = this.values.iterator();
			itval.next().writeTo(sql, args);
			while (itval.hasNext()) {
				sql.append(',');
				itval.next().writeTo(sql, args);
			}
			sql.append(')');
		} else {
			this.select.writeTo(sql, args);
		}
		sql.append(';');
	}
}

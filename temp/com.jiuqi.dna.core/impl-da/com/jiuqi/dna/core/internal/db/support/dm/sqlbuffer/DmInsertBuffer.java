package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

import static com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer.DmExprBuffer.quote;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class DmInsertBuffer extends DmCommandBuffer implements ISqlInsertBuffer {

	final String table;

	ArrayList<String> fields = new ArrayList<String>();
	DmSelectBuffer select;
	ArrayList<DmExprBuffer> values;

	DmInsertBuffer(String table) {
		super(null);
		this.table = quote(table);
	}

	public void newField(String name) {
		this.fields.add(DmExprBuffer.quote(name));
	}

	public ISqlExprBuffer newValue() {
		if (this.values == null) {
			this.values = new ArrayList<DmExprBuffer>();
		}
		DmExprBuffer e = new DmExprBuffer();
		this.values.add(e);
		return e;
	}

	public ISqlSelectBuffer select() {
		if (this.select == null) {
			this.select = new DmSelectBuffer();
		}
		return this.select;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("insert into ").append(this.table).append(' ').append('(');
		Iterator<String> iter = this.fields.iterator();
		sql.append(iter.next());
		while (iter.hasNext()) {
			sql.append(',').append(iter.next());
		}
		sql.append(')').append(' ');
		if (this.values != null) {
			sql.append("values (");
			Iterator<DmExprBuffer> itval = this.values.iterator();
			itval.next().writeTo(sql, args);
			while (itval.hasNext()) {
				sql.append(',');
				itval.next().writeTo(sql, args);
			}
			sql.append(')');
		} else {
			this.select.writeTo(sql, args);
		}
	}
}
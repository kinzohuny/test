package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class HanaInsertBuffer extends HanaCommandBuffer implements
		ISqlInsertBuffer {

	public HanaInsertBuffer(String table) {
		super(null);
		this.table = HanaExprBuffer.quote(table);
	}

	final String table;
	ArrayList<String> fields = new ArrayList<String>();
	ArrayList<HanaExprBuffer> values;
	HanaSelectBuffer select;

	public void newField(String name) {
		this.fields.add(HanaExprBuffer.quote(name));
	}

	public HanaExprBuffer newValue() {
		if (this.values == null) {
			this.values = new ArrayList<HanaExprBuffer>();
		}
		HanaExprBuffer e = new HanaExprBuffer(this);
		this.values.add(e);
		return e;
	}

	public HanaSelectBuffer select() {
		if (this.select == null) {
			this.select = new HanaSelectBuffer(this);
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
			Iterator<HanaExprBuffer> itval = this.values.iterator();
			itval.next().writeTo(sql, args);
			while (itval.hasNext()) {
				sql.append(',');
				itval.next().writeTo(sql, args);
			}
			sql.append(')');
		} else {
			this.select.writeTo(sql, args);
		}
		if (this.scope != null) {
			sql.append(';');
		}
	}
}
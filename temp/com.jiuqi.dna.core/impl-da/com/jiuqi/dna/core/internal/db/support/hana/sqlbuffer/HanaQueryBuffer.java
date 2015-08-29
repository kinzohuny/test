package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class HanaQueryBuffer extends HanaCommandBuffer implements
		ISqlQueryBuffer {

	public HanaQueryBuffer() {
		super(null);
	}

	ArrayList<HanaWithBuffer> withs;
	final HanaSelectBuffer select = new HanaSelectBuffer(this);
	ArrayList<HanaOrderExprBuffer> orders;
	HanaExprBuffer limit;
	HanaExprBuffer offset;

	final HanaWithBuffer getWith(String name) {
		if (this.withs == null) {
			throw new UnsupportedOperationException();
		}
		final String e = HanaExprBuffer.quote(name);
		for (int i = 0, c = this.withs.size(); i < c; i++) {
			HanaWithBuffer with = this.withs.get(i);
			if (with.name.equals(e)) {
				return with;
			}
		}
		throw new UnsupportedOperationException();
	}

	public ISqlSelectBuffer newWith(String alias) {
		if (this.withs == null) {
			this.withs = new ArrayList<HanaWithBuffer>();
		}
		HanaWithBuffer w = new HanaWithBuffer(this, alias);
		this.withs.add(w);
		return w;
	}

	public ISqlSelectBuffer select() {
		return this.select;
	}

	public ISqlExprBuffer limit() {
		if (this.limit == null) {
			this.limit = new HanaExprBuffer(this);
		}
		return this.limit;
	}

	public ISqlExprBuffer offset() {
		if (this.offset == null) {
			this.offset = new HanaExprBuffer(this);
		}
		return this.offset;
	}

	public ISqlExprBuffer newOrder(boolean desc) {
		if (this.orders == null) {
			this.orders = new ArrayList<HanaOrderExprBuffer>();
		}
		HanaOrderExprBuffer o = new HanaOrderExprBuffer(this, desc);
		this.orders.add(o);
		return o;
	}

	public void newOrder(String column, boolean desc) {
		if (this.orders == null) {
			this.orders = new ArrayList<HanaOrderExprBuffer>();
		}
		HanaOrderExprBuffer o = new HanaOrderExprBuffer(this, column, desc);
		this.orders.add(o);
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		if (this.select.unions == null || this.limit == null) {
			this.select.writeTo(sql, args);
			HanaSelectBuffer.writeOrderbyLimit(sql, args, this.orders, this.limit, this.offset);
		} else {
			sql.append("select * from (");
			this.select.writeTo(sql, args);
			sql.append(") \"N\"");
			HanaSelectBuffer.writeOrderbyLimit(sql, args, this.orders, this.limit, this.offset);
		}
	}
}
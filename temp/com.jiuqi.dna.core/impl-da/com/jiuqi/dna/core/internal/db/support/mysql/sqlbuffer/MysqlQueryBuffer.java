package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

final class MysqlQueryBuffer extends MysqlCommandBuffer implements
		ISqlQueryBuffer {

	MysqlQueryBuffer() {
		super(null);
	}

	public final MysqlWithBuffer newWith(String alias) {
		if (this.withs == null) {
			this.withs = new ArrayList<MysqlWithBuffer>();
		}
		MysqlWithBuffer w = new MysqlWithBuffer(this, MysqlExprBuffer.quote(alias));
		this.withs.add(w);
		return w;
	}

	final MysqlWithBuffer getWith(String name) {
		if (this.withs == null) {
			throw new UnsupportedOperationException();
		}
		final String e = MysqlExprBuffer.quote(name);
		for (int i = 0, c = this.withs.size(); i < c; i++) {
			MysqlWithBuffer with = this.withs.get(i);
			if (with.name.equals(e)) {
				return with;
			}
		}
		throw new UnsupportedOperationException();
	}

	public final MysqlSelectBuffer select() {
		return this.select;
	}

	public final MysqlExprBuffer limit() {
		if (this.limit == null) {
			this.limit = new MysqlExprBuffer(this);
		}
		return this.limit;
	}

	public final MysqlExprBuffer offset() {
		if (this.offset == null) {
			this.offset = new MysqlExprBuffer(this);
		}
		return this.offset;
	}

	public final MysqlOrderExprBuffer newOrder(boolean desc) {
		if (this.orders == null) {
			this.orders = new ArrayList<MysqlOrderExprBuffer>();
		}
		MysqlOrderExprBuffer o = new MysqlOrderExprBuffer(this, desc);
		this.orders.add(o);
		return o;
	}

	public final void newOrder(String column, boolean desc) {
		if (this.orders == null) {
			this.orders = new ArrayList<MysqlOrderExprBuffer>();
		}
		MysqlOrderExprBuffer o = new MysqlOrderExprBuffer(this, column, desc);
		this.orders.add(o);
	}

	ArrayList<MysqlWithBuffer> withs;
	final MysqlSelectBuffer select = new MysqlSelectBuffer(this);
	ArrayList<MysqlOrderExprBuffer> orders;
	MysqlExprBuffer limit;
	MysqlExprBuffer offset;

	public final void writeTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		if (this.select.unions == null || this.limit == null) {
			this.select.writeTo(sql, args);
			MysqlSelectBuffer.writeOrderbyLimit(sql, args, this.orders, this.limit, this.offset);
		} else {
			sql.append("select * from (");
			this.select.writeTo(sql, args);
			sql.append(") `N`");
			MysqlSelectBuffer.writeOrderbyLimit(sql, args, this.orders, this.limit, this.offset);
		}
	}
}

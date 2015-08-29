package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class DmQueryBuffer extends DmCommandBuffer implements ISqlQueryBuffer {

	static class DmOrderExprBuffer extends DmExprBuffer {

		final boolean desc;

		public DmOrderExprBuffer(boolean desc) {
			this.desc = desc;
		}

		public DmOrderExprBuffer(String column, boolean desc) {
			this.push(quote(column));
			this.desc = desc;
		}
	}

	ArrayList<DmWithBuffer> with;
	final DmSelectBuffer select = new DmSelectBuffer();
	ArrayList<DmOrderExprBuffer> order;
	DmExprBuffer limit;
	DmExprBuffer offset;

	DmQueryBuffer() {
		super(null);
	}

	public DmWithBuffer newWith(String alias) {
		if (this.with == null) {
			this.with = new ArrayList<DmWithBuffer>();
		}
		DmWithBuffer w = new DmWithBuffer(alias);
		this.with.add(w);
		return w;
	}

	public DmSelectBuffer select() {
		return this.select;
	}

	public DmExprBuffer limit() {
		if (this.limit == null) {
			this.limit = new DmExprBuffer();
		}
		return this.limit;
	}

	public DmExprBuffer offset() {
		if (this.offset == null) {
			this.offset = new DmExprBuffer();
		}
		return this.offset;
	}

	public DmOrderExprBuffer newOrder(boolean desc) {
		if (this.order == null) {
			this.order = new ArrayList<DmOrderExprBuffer>();
		}
		DmOrderExprBuffer expr = new DmOrderExprBuffer(desc);
		this.order.add(expr);
		return expr;
	}

	public void newOrder(String column, boolean desc) {
		if (this.order == null) {
			this.order = new ArrayList<DmOrderExprBuffer>();
		}
		DmOrderExprBuffer expr = new DmOrderExprBuffer(column, desc);
		this.order.add(expr);
	}

	private void writeSelect(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		this.select.writeTo(sql, args);
		if (this.order != null) {
			sql.append(" order by ");
			Iterator<DmOrderExprBuffer> it = this.order.iterator();
			DmOrderExprBuffer e = it.next();
			e.writeTo(sql, args);
			if (e.desc) {
				sql.append(" desc nulls last");
			} else {
				sql.append(" asc nulls first");
			}
			while (it.hasNext()) {
				e = it.next();
				sql.append(',');
				e.writeTo(sql, args);
				if (e.desc) {
					sql.append(" desc nulls last");
				} else {
					sql.append(" asc nulls first");
				}
			}
		}
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		if (this.with != null) {
			sql.append("with ");
			Iterator<DmWithBuffer> iter = this.with.iterator();
			iter.next().writeTo(sql, args);
			while (iter.hasNext()) {
				sql.append(',');
				iter.next().writeTo(sql, args);
			}
			sql.append(' ');
		}
		if (this.limit != null) {
			if (this.offset != null) {
				sql.append("select * from (select \"$T0\".*,rownum \"$F\"" + " from (");
				this.writeSelect(sql, args);
				sql.append(") \"$T0\") where \"$F\">");
				this.offset.writeTo(sql, args);
				sql.append(" and rownum<=");
				this.limit.writeTo(sql, args);
			} else {
				sql.append("select * from (");
				this.writeSelect(sql, args);
				sql.append(") where rownum<=");
				this.limit.writeTo(sql, args);
			}
		} else {
			this.writeSelect(sql, args);
		}
	}
}
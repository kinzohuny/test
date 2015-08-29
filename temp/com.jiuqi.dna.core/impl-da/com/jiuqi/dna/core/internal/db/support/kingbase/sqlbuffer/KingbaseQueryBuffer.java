package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer.KingbaseSelectBuffer.KingbaseOrderExprBuffer;

final class KingbaseQueryBuffer extends KingbaseCommandBuffer implements
		ISqlQueryBuffer {
	ArrayList<KingbasePredefinedSubQueryBuffer> with;
	final KingbaseSelectBuffer select = new KingbaseSelectBuffer();
	ArrayList<KingbaseOrderExprBuffer> order;
	KingbaseExprBuffer limit;
	KingbaseExprBuffer offset;

	public KingbaseQueryBuffer() {
		super(null);
	}

	public ISqlSelectBuffer select() {
		return this.select;
	}

	public ISqlSelectBuffer newWith(String alias) {
		if (this.with == null) {
			this.with = new ArrayList<KingbasePredefinedSubQueryBuffer>();
		}
		KingbasePredefinedSubQueryBuffer w = new KingbasePredefinedSubQueryBuffer(KingbaseExprBuffer.quote(alias));
		this.with.add(w);
		return w;
	}

	public ISqlExprBuffer newOrder(boolean desc) {
		if (this.order == null) {
			this.order = new ArrayList<KingbaseOrderExprBuffer>();
		}
		KingbaseOrderExprBuffer expr = new KingbaseOrderExprBuffer(desc);
		this.order.add(expr);
		return expr;
	}

	public void newOrder(String column, boolean desc) {
		if (this.order == null) {
			this.order = new ArrayList<KingbaseOrderExprBuffer>();
		}
		KingbaseOrderExprBuffer expr = new KingbaseOrderExprBuffer(column, desc);
		this.order.add(expr);
	}

	public ISqlExprBuffer limit() {
		if (this.limit == null) {
			this.limit = new KingbaseExprBuffer();
		}
		return this.limit;
	}

	public ISqlExprBuffer offset() {
		if (this.offset == null) {
			this.offset = new KingbaseExprBuffer();
		}
		return this.offset;
	}

	private void writeSelect(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		this.select.writeTo(sql, args);
		if (this.order != null) {
			sql.append(" order by ");
			Iterator<KingbaseOrderExprBuffer> it = this.order.iterator();
			KingbaseOrderExprBuffer e = it.next();
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

	private void writeWithSelect(SqlStringBuffer sql, HashMap hmsql,
			List<ParameterPlaceholder> args) {
		this.select.writeWithTo(sql, hmsql, args);
		if (this.order != null) {
			sql.append(" order by ");
			Iterator<KingbaseOrderExprBuffer> it = this.order.iterator();
			KingbaseOrderExprBuffer e = it.next();
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
		HashMap<String, String> hmsql = new HashMap<String, String>();
		final boolean withNoNull = this.with != null;
		if (withNoNull) {
			SqlStringBuffer firstsql = new SqlStringBuffer();
			Iterator<KingbasePredefinedSubQueryBuffer> iter = this.with.iterator();
			String firstsqlName = iter.next().addSQL(firstsql, args);
			hmsql.put(firstsqlName, firstsql.toString());
			while (iter.hasNext()) {
				SqlStringBuffer othersql = new SqlStringBuffer();
				String othersqlName = iter.next().addSQLWith(othersql, hmsql, args);
				hmsql.put(othersqlName, othersql.toString());
			}
		}
		if (this.limit != null) {
			if (this.offset != null) {
				sql.append("select * from (select \"$T0\".*,rownum \"$F\"" + " from (");
				if (withNoNull) {
					this.writeWithSelect(sql, hmsql, args);
				} else {
					this.writeSelect(sql, args);
				}
				sql.append(") \"$T0\") where \"$F\">");
				this.offset.writeTo(sql, args);
				sql.append(" and rownum<=");
				this.limit.writeTo(sql, args);
			} else {
				sql.append("select * from (");
				if (withNoNull) {
					this.writeWithSelect(sql, hmsql, args);
				} else {
					this.writeSelect(sql, args);
				}
				sql.append(") where rownum<=");
				this.limit.writeTo(sql, args);
			}
		} else {
			if (withNoNull) {
				this.writeWithSelect(sql, hmsql, args);
			} else {
				this.writeSelect(sql, args);
			}
		}
	}
}

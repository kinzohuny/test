package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlWithRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class KingbaseSelectBuffer implements ISqlSelectBuffer {
	ArrayList<ISqlRelationRefBuffer> source = new ArrayList<ISqlRelationRefBuffer>();
	ArrayList<KingbaseSelectColumnBuffer> columns = new ArrayList<KingbaseSelectColumnBuffer>();
	KingbaseExprBuffer where;
	ArrayList<KingbaseExprBuffer> group;
	KingbaseExprBuffer having;
	ArrayList<KingbaseUnionedSelectBuffer> union;
	GroupMethod groupMethod = GroupMethod.NONE;
	boolean distinct;
	ArrayList<KingbaseOrderExprBuffer> order;
	private boolean dummy;
	private String targetAlias;
	private String alternateAlias;

	enum GroupMethod {
		ROLLUP, CUBE, NONE
	}

	static class KingbaseOrderExprBuffer extends KingbaseExprBuffer {
		final boolean desc;

		public KingbaseOrderExprBuffer(boolean desc) {
			this.desc = desc;
		}

		public KingbaseOrderExprBuffer(String column, boolean desc) {
			this.push(quote(column));
			this.desc = desc;
		}
	}

	public void replace(String targetAlias, String alternateAlias) {
		this.targetAlias = targetAlias;
		this.alternateAlias = alternateAlias;
	}

	public ISqlTableRefBuffer newTableRef(String table, String alias) {
		KingbaseTableRefBuffer t = new KingbaseTableRefBuffer(table, alias);
		t.replace(this.targetAlias, this.alternateAlias);
		this.source.add(t);
		return t;
	}

	public ISqlQueryRefBuffer newQueryRef(String alias) {
		KingbaseSubQueryRefBuffer q = new KingbaseSubQueryRefBuffer(alias);
		q.replace(this.targetAlias, this.alternateAlias);
		this.source.add(q);
		return q;
	}

	public ISqlWithRefBuffer newWithRef(String target, String alias) {
		KingbaseTableRefBuffer t = new KingbaseTableRefBuffer(target, alias);
		t.replace(this.targetAlias, this.alternateAlias);
		this.source.add(t);
		return t;
	}

	public void fromDummy() {
		this.dummy = true;
	}

	public ISqlExprBuffer newColumn(String alias) {
		KingbaseSelectColumnBuffer expr = new KingbaseSelectColumnBuffer(alias);
		expr.replace(this.targetAlias, this.alternateAlias);
		this.columns.add(expr);
		return expr;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new KingbaseExprBuffer();
			this.where.replace(this.targetAlias, this.alternateAlias);
		}
		return this.where;
	}

	public ISqlExprBuffer groupby() {
		if (this.group == null) {
			this.group = new ArrayList<KingbaseExprBuffer>();
		}
		KingbaseExprBuffer expr = new KingbaseExprBuffer();
		expr.replace(this.targetAlias, this.alternateAlias);
		this.group.add(expr);
		return expr;
	}

	public void distinct() {
		this.distinct = true;
	}

	public void rollup() {
		this.groupMethod = GroupMethod.ROLLUP;
	}

	public void cube() {
		this.groupMethod = GroupMethod.CUBE;
	}

	public ISqlExprBuffer having() {
		if (this.having == null) {
			this.having = new KingbaseExprBuffer();
			this.having.replace(this.targetAlias, this.alternateAlias);
		}
		return this.having;
	}

	public ISqlExprBuffer orderby(boolean desc) {
		if (this.order == null) {
			this.order = new ArrayList<KingbaseOrderExprBuffer>();
		}
		KingbaseOrderExprBuffer expr = new KingbaseOrderExprBuffer(desc);
		expr.replace(this.targetAlias, this.alternateAlias);
		this.order.add(expr);
		return expr;
	}

	public ISqlSelectBuffer union(boolean all) {
		if (this.union == null) {
			this.union = new ArrayList<KingbaseUnionedSelectBuffer>();
		}
		KingbaseUnionedSelectBuffer q = new KingbaseUnionedSelectBuffer(all);
		q.replace(this.targetAlias, this.alternateAlias);
		this.union.add(q);
		return q;
	}

	private void writeSelect(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append("select ");
		if (this.distinct) {
			sql.append("distinct ");
		}
		Iterator<KingbaseSelectColumnBuffer> iter = this.columns.iterator();
		KingbaseSelectColumnBuffer c = iter.next();
		c.writeTo(sql, args);
		sql.append(' ').append(c.alias);
		while (iter.hasNext()) {
			c = iter.next();
			sql.append(',');
			c.writeTo(sql, args);
			sql.append(' ').append(c.alias);
		}
		if (this.dummy) {
			sql.append(" from dual");
		} else {
			sql.append(" from ");
			Iterator<ISqlRelationRefBuffer> it = this.source.iterator();
			it.next().writeTo(sql, args);
			while (it.hasNext()) {
				sql.append(',');
				it.next().writeTo(sql, args);
			}
		}
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
		if (this.group != null) {
			switch (this.groupMethod) {
			case ROLLUP:
				sql.append(" group by rollup(");
				break;
			case CUBE:
				sql.append(" group by cube(");
				break;
			default:
				sql.append(" group by ");
				break;
			}
			Iterator<KingbaseExprBuffer> it = this.group.iterator();
			it.next().writeTo(sql, args);
			while (it.hasNext()) {
				sql.append(',');
				it.next().writeTo(sql, args);
			}
			switch (this.groupMethod) {
			case ROLLUP:
			case CUBE:
				sql.append(')');
				break;
			default:
				break;
			}
		}
		if (this.having != null) {
			sql.append(" having ");
			this.having.writeTo(sql, args);
		}
		if (this.order != null) {
			sql.append(" order by ");
			Iterator<KingbaseOrderExprBuffer> it = this.order.iterator();
			KingbaseOrderExprBuffer e = it.next();
			e.writeTo(sql, args);
			if (e.desc) {
				sql.append(" desc");
			}
			while (it.hasNext()) {
				e = it.next();
				sql.append(',');
				e.writeTo(sql, args);
				if (e.desc) {
					sql.append(" desc");
				}
			}
		}
	}

	private void writeWithSelect(SqlStringBuffer sql, HashMap hmsql,
			List<ParameterPlaceholder> args) {
		sql.append("select ");
		if (this.distinct) {
			sql.append("distinct ");
		}
		Iterator<KingbaseSelectColumnBuffer> iter = this.columns.iterator();
		KingbaseSelectColumnBuffer c = iter.next();
		c.writeTo(sql, args);
		sql.append(' ').append(c.alias);
		while (iter.hasNext()) {
			c = iter.next();
			sql.append(',');
			c.writeTo(sql, args);
			sql.append(' ').append(c.alias);
		}
		if (this.dummy) {
			sql.append(" from dual");
		} else {
			sql.append(" from ");
			Iterator<ISqlRelationRefBuffer> it = this.source.iterator();
			// it.next().writeTo(sql, args);
			KingbaseRelationRefBuffer krrb = (KingbaseRelationRefBuffer) it.next();
			krrb.writeWithTo(sql, hmsql, args);
			while (it.hasNext()) {
				sql.append(',');
				krrb = (KingbaseRelationRefBuffer) it.next();
				krrb.writeWithTo(sql, hmsql, args);
				// it.next().writeTo(sql, args);
			}
		}
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
		if (this.group != null) {
			switch (this.groupMethod) {
			case ROLLUP:
				sql.append(" group by rollup(");
				break;
			case CUBE:
				sql.append(" group by cube(");
				break;
			default:
				sql.append(" group by ");
				break;
			}
			Iterator<KingbaseExprBuffer> it = this.group.iterator();
			it.next().writeTo(sql, args);
			while (it.hasNext()) {
				sql.append(',');
				it.next().writeTo(sql, args);
			}
			switch (this.groupMethod) {
			case ROLLUP:
			case CUBE:
				sql.append(')');
				break;
			default:
				break;
			}
		}
		if (this.having != null) {
			sql.append(" having ");
			this.having.writeTo(sql, args);
		}
		if (this.order != null) {
			sql.append(" order by ");
			Iterator<KingbaseOrderExprBuffer> it = this.order.iterator();
			KingbaseOrderExprBuffer e = it.next();
			e.writeTo(sql, args);
			if (e.desc) {
				sql.append(" desc");
			}
			while (it.hasNext()) {
				e = it.next();
				sql.append(',');
				e.writeTo(sql, args);
				if (e.desc) {
					sql.append(" desc");
				}
			}
		}
		String sqlR = sql.toString();
		for (int i = 0; i < hmsql.size(); i++) {
			hmsql.values();
		}
		final Object[] obj = hmsql.keySet().toArray();
		for (int i = 0; i < obj.length; i++) {
			sqlR = sqlR.replace(obj[i].toString(), hmsql.get(obj[i].toString()).toString());
		}
		sql.clear();
		sql.append(sqlR);
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		this.writeSelect(sql, args);
		if (this.union != null) {
			for (KingbaseUnionedSelectBuffer u : this.union) {
				u.writeTo(sql, args);
			}
		}
	}

	public void writeWithTo(SqlStringBuffer sql, HashMap hmsql,
			List<ParameterPlaceholder> args) {
		this.writeWithSelect(sql, hmsql, args);
		if (this.union != null) {
			for (KingbaseUnionedSelectBuffer u : this.union) {
				u.writeTo(sql, args);
			}
		}
	}
}

package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.GroupMethod;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlWithRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class DmSelectBuffer implements ISqlSelectBuffer {

	ArrayList<DmSourceBuffer> source = new ArrayList<DmSourceBuffer>();
	ArrayList<DmSelectColumnBuffer> columns = new ArrayList<DmSelectColumnBuffer>();
	DmExprBuffer where;
	ArrayList<DmExprBuffer> group;
	DmExprBuffer having;
	ArrayList<DmUnionSelectBuffer> union;
	GroupMethod groupMethod = GroupMethod.NONE;
	boolean distinct;
	boolean dummy;
	private String targetAlias;
	private String alternateAlias;

	public void replace(String targetAlias, String alternateAlias) {
		this.targetAlias = targetAlias;
		this.alternateAlias = alternateAlias;
	}

	public DmTableSourceBuffer newTableRef(String table, String alias) {
		DmTableSourceBuffer t = new DmTableSourceBuffer(table, alias);
		t.replace(this.targetAlias, this.alternateAlias);
		this.source.add(t);
		return t;
	}

	public ISqlQueryRefBuffer newQueryRef(String alias) {
		DmSubqueryRefBuffer q = new DmSubqueryRefBuffer(alias);
		q.replace(this.targetAlias, this.alternateAlias);
		this.source.add(q);
		return q;
	}

	public ISqlWithRefBuffer newWithRef(String target, String alias) {
		DmWithSourceBuffer t = new DmWithSourceBuffer(target, alias);
		t.replace(this.targetAlias, this.alternateAlias);
		this.source.add(t);
		return t;
	}

	public void fromDummy() {
		this.dummy = true;
	}

	public void distinct() {
		this.distinct = true;
	}

	public DmSelectColumnBuffer newColumn(String alias) {
		DmSelectColumnBuffer expr = new DmSelectColumnBuffer(alias);
		expr.replace(this.targetAlias, this.alternateAlias);
		this.columns.add(expr);
		return expr;
	}

	public DmExprBuffer where() {
		if (this.where == null) {
			this.where = new DmExprBuffer();
			this.where.replace(this.targetAlias, this.alternateAlias);
		}
		return this.where;
	}

	public ISqlExprBuffer groupby() {
		if (this.group == null) {
			this.group = new ArrayList<DmExprBuffer>();
		}
		DmExprBuffer expr = new DmExprBuffer();
		expr.replace(this.targetAlias, this.alternateAlias);
		this.group.add(expr);
		return expr;
	}

	public void rollup() {
		this.groupMethod = GroupMethod.ROLLUP;
	}

	public DmExprBuffer having() {
		if (this.having == null) {
			this.having = new DmExprBuffer();
			this.having.replace(this.targetAlias, this.alternateAlias);
		}
		return this.having;
	}

	public DmUnionSelectBuffer union(boolean all) {
		if (this.union == null) {
			this.union = new ArrayList<DmUnionSelectBuffer>();
		}
		DmUnionSelectBuffer q = new DmUnionSelectBuffer(all);
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
		Iterator<DmSelectColumnBuffer> iter = this.columns.iterator();
		DmSelectColumnBuffer c = iter.next();
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
			Iterator<DmSourceBuffer> it = this.source.iterator();
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
			Iterator<DmExprBuffer> it = this.group.iterator();
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
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		this.writeSelect(sql, args);
		if (this.union != null) {
			for (DmUnionSelectBuffer u : this.union) {
				u.writeTo(sql, args);
			}
		}
	}
}
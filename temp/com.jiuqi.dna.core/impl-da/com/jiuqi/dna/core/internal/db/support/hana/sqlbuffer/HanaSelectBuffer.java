package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.GroupMethod;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlWithRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class HanaSelectBuffer implements ISqlSelectBuffer {

	static final class HanaSelectColumnBuffer extends HanaExprBuffer {

		public HanaSelectColumnBuffer(HanaCommandBuffer command, String alias) {
			super(command);
			this.alias = HanaExprBuffer.quote(alias);
		}

		final String alias;
	}

	HanaSelectBuffer(HanaCommandBuffer command) {
		this.command = command;
	}

	final HanaCommandBuffer command;
	ArrayList<HanaRelationRefBuffer> sources = new ArrayList<HanaRelationRefBuffer>();
	ArrayList<HanaSelectColumnBuffer> columns = new ArrayList<HanaSelectColumnBuffer>();
	HanaExprBuffer where;
	ArrayList<HanaExprBuffer> groups;
	HanaExprBuffer having;
	ArrayList<HanaUnionBuffer> unions;
	GroupMethod groupMethod = GroupMethod.NONE;
	boolean distinct;
	ArrayList<HanaOrderExprBuffer> orders;
	private boolean dummy;

	public HanaTableRefBuffer newTableRef(String table, String alias) {
		HanaTableRefBuffer t = new HanaTableRefBuffer(this.command, table, alias);
		this.sources.add(t);
		return t;
	}

	public HanaQueryRefBuffer newQueryRef(String alias) {
		HanaQueryRefBuffer t = new HanaQueryRefBuffer(this.command, alias);
		this.sources.add(t);
		return t;
	}

	public ISqlWithRefBuffer newWithRef(String target, String alias) {
		HanaWithRefBuffer r = new HanaWithRefBuffer(this.command, target, alias);
		this.sources.add(r);
		return r;
	}

	public void fromDummy() {
		this.dummy = true;
	}

	public void distinct() {
		this.distinct = true;
	}

	public ISqlExprBuffer newColumn(String alias) {
		HanaSelectColumnBuffer column = new HanaSelectColumnBuffer(this.command, alias);
		this.columns.add(column);
		return column;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new HanaExprBuffer(this.command);
		}
		return this.where;
	}

	public HanaExprBuffer groupby() {
		if (this.groups == null) {
			this.groups = new ArrayList<HanaExprBuffer>();
		}
		HanaExprBuffer g = new HanaExprBuffer(this.command);
		this.groups.add(g);
		return g;
	}

	public void rollup() {
		this.groupMethod = GroupMethod.ROLLUP;
	}

	public HanaExprBuffer having() {
		if (this.having == null) {
			this.having = new HanaExprBuffer(this.command);
		}
		return this.having;
	}

	public ISqlSelectBuffer union(boolean all) {
		if (this.unions == null) {
			this.unions = new ArrayList<HanaUnionBuffer>();
		}
		HanaUnionBuffer u = new HanaUnionBuffer(this.command, all);
		this.unions.add(u);
		return u;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		this.writeSelectTo(sql, args);
	}

	final void writeSelectTo(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append("select ");
		if (this.distinct) {
			sql.append("distinct ");
		}
		for (int i = 0, c = this.columns.size(); i < c; i++) {
			if (i > 0) {
				sql.append(", ");
			}
			HanaSelectColumnBuffer column = this.columns.get(i);
			column.writeTo(sql, args);
			sql.append(' ').append(column.alias);
		}
		if (this.dummy) {
			sql.append(" from dummy");
		} else {
			sql.append(" from ");
			for (int i = 0, c = this.sources.size(); i < c; i++) {
				if (i > 0) {
					sql.append(", ");
				}
				HanaRelationRefBuffer s = this.sources.get(i);
				s.writeTo(sql, args);
			}
		}
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
		if (this.groups != null) {
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
			Iterator<HanaExprBuffer> it = this.groups.iterator();
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
		writeOrderbyLimit(sql, args, this.orders, null, null);
		if (this.unions != null) {
			for (int i = 0, c = this.unions.size(); i < c; i++) {
				this.unions.get(i).writeTo(sql, args);
			}
		}
	}

	static final void writeOrderbyLimit(SqlStringBuffer sql,
			List<ParameterPlaceholder> args,
			ArrayList<HanaOrderExprBuffer> orders, HanaExprBuffer limit,
			HanaExprBuffer offset) {
		if (orders != null) {
			sql.append(" order by ");
			for (int i = 0, c = orders.size(); i < c; i++) {
				if (i > 0) {
					sql.append(", ");
				}
				HanaOrderExprBuffer o = orders.get(i);
				o.writeTo(sql, args);
			}
		}
		if (limit != null) {
			sql.append(" limit ");
			limit.writeTo(sql, args);
			if (offset != null) {
				sql.append(" offset ");
				offset.writeTo(sql, args);
			}
		}
	}
}
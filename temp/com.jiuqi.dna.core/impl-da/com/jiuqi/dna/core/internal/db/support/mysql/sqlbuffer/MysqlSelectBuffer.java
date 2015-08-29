package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.GroupMethod;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

class MysqlSelectBuffer implements ISqlSelectBuffer {

	public final MysqlTableRefBuffer newTableRef(String table, String alias) {
		MysqlTableRefBuffer t = new MysqlTableRefBuffer(this.command, table, alias);
		this.sources.add(t);
		return t;
	}

	public final MysqlQueryRefBuffer newQueryRef(String alias) {
		MysqlQueryRefBuffer t = new MysqlQueryRefBuffer(this.command, alias);
		this.sources.add(t);
		return t;
	}

	public final MysqlWithRefBuffer newWithRef(String target, String alias) {
		MysqlWithRefBuffer r = new MysqlWithRefBuffer(this.command, target, alias);
		this.sources.add(r);
		return r;
	}

	public final void fromDummy() {
		this.dummy = true;
	}

	public final MysqlExprBuffer where() {
		if (this.where == null) {
			this.where = new MysqlExprBuffer(this.command);
		}
		return this.where;
	}

	public final MysqlExprBuffer groupby() {
		if (this.groups == null) {
			this.groups = new ArrayList<MysqlExprBuffer>();
		}
		MysqlExprBuffer g = new MysqlExprBuffer(this.command);
		this.groups.add(g);
		return g;
	}

	public final void distinct() {
		this.distinct = true;
	}

	public final void rollup() {
		this.groupMethod = GroupMethod.ROLLUP;
	}

	public final void cube() {
		this.groupMethod = GroupMethod.CUBE;
	}

	public final MysqlExprBuffer having() {
		if (this.having == null) {
			this.having = new MysqlExprBuffer(this.command);
		}
		return this.having;
	}

	public final MySqlSelectColumnBuffer newColumn(String alias) {
		MySqlSelectColumnBuffer column = new MySqlSelectColumnBuffer(this.command, alias);
		this.columns.add(column);
		return column;
	}

	public final ISqlExprBuffer limit() {
		if (this.limit == null) {
			this.limit = new MysqlExprBuffer(this.command);
		}
		return this.limit;
	}

	public final ISqlExprBuffer offset() {
		if (this.offset == null) {
			this.offset = new MysqlExprBuffer(this.command);
		}
		return this.offset;
	}

	public final MysqlOrderExprBuffer orderby(boolean desc) {
		if (this.orders == null) {
			this.orders = new ArrayList<MysqlOrderExprBuffer>();
		}
		MysqlOrderExprBuffer o = new MysqlOrderExprBuffer(this.command, desc);
		this.orders.add(o);
		return o;
	}

	public final MySqlUnionedSelectBuffer union(boolean all) {
		if (this.unions == null) {
			this.unions = new ArrayList<MySqlUnionedSelectBuffer>();
		}
		MySqlUnionedSelectBuffer u = new MySqlUnionedSelectBuffer(this.command, all);
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
			MySqlSelectColumnBuffer col = this.columns.get(i);
			col.writeTo(sql, args);
			sql.append(' ').append(col.alias);
		}
		if (this.dummy) {
			sql.append(" from dual");
		} else {
			sql.append(" from ");
			for (int i = 0, c = this.sources.size(); i < c; i++) {
				if (i > 0) {
					sql.append(", ");
				}
				MysqlRelationRefBuffer s = this.sources.get(i);
				s.writeTo(sql, args);
			}
		}
		if (this.where != null) {
			sql.append(" where ");
			this.where.writeTo(sql, args);
		}
		if (this.groups != null) {
			sql.append("  group by ");
			for (int i = 0, c = this.groups.size(); i < c; i++) {
				if (i > 0) {
					sql.append(", ");
				}
				MysqlExprBuffer g = this.groups.get(i);
				g.writeTo(sql, args);
			}
			switch (this.groupMethod) {
			case ROLLUP:
				sql.append(" with rollup");
				break;
			case CUBE:
				throw new UnsupportedOperationException();
			default:
				break;
			}
		}
		if (this.having != null) {
			sql.append(" having ");
			this.having.writeTo(sql, args);
		}
		writeOrderbyLimit(sql, args, this.orders, this.limit, this.offset);
		if (this.unions != null) {
			for (int i = 0, c = this.unions.size(); i < c; i++) {
				this.unions.get(i).writeTo(sql, args);
			}
		}
	}

	static final void writeOrderbyLimit(SqlStringBuffer sql,
			List<ParameterPlaceholder> args,
			ArrayList<MysqlOrderExprBuffer> orders, MysqlExprBuffer limit,
			MysqlExprBuffer offset) {
		if (orders != null) {
			sql.append(" order by ");
			for (int i = 0, c = orders.size(); i < c; i++) {
				if (i > 0) {
					sql.append(", ");
				}
				MysqlOrderExprBuffer o = orders.get(i);
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

	private boolean dummy;
	ArrayList<MysqlRelationRefBuffer> sources = new ArrayList<MysqlRelationRefBuffer>();
	MysqlExprBuffer where;
	ArrayList<MysqlExprBuffer> groups;
	GroupMethod groupMethod = GroupMethod.NONE;
	MysqlExprBuffer having;
	boolean distinct;
	ArrayList<MySqlSelectColumnBuffer> columns = new ArrayList<MySqlSelectColumnBuffer>();
	ArrayList<MysqlOrderExprBuffer> orders;
	MysqlExprBuffer limit;
	MysqlExprBuffer offset;
	ArrayList<MySqlUnionedSelectBuffer> unions;

	final MysqlCommandBuffer command;

	MysqlSelectBuffer(MysqlCommandBuffer command) {
		this.command = command;
	}

	static final class MySqlSelectColumnBuffer extends MysqlExprBuffer {

		final String alias;

		MySqlSelectColumnBuffer(MysqlCommandBuffer command, String alias) {
			super(command);
			this.alias = MysqlExprBuffer.quote(alias);
		}
	}

	static final class MySqlUnionedSelectBuffer extends MysqlSelectBuffer {

		final boolean unionAll;

		MySqlUnionedSelectBuffer(MysqlCommandBuffer command, boolean unionAll) {
			super(command);
			this.unionAll = unionAll;
		}

		@Override
		public final void writeTo(SqlStringBuffer sql,
				List<ParameterPlaceholder> args) {
			if (this.unionAll) {
				sql.append(" union ");
			} else {
				sql.append(" union all ");
			}
			if (this.unions != null) {
				sql.append('(');
				super.writeSelectTo(sql, args);
				sql.append(')');
			} else {
				super.writeTo(sql, args);
			}
		}

	}

}

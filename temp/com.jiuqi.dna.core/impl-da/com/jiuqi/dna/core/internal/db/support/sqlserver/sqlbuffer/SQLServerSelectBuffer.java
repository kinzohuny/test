package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import static com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer.SQLServerExprBuffer.quote;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.GroupMethod;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlWithRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

class SQLServerSelectBuffer implements ISqlSelectBuffer {

	final SqlserverMetadata metadata;

	public SQLServerSelectBuffer(SqlserverMetadata metadata,
			SQLServerCommandBuffer command) {
		this.metadata = metadata;
		this.command = command;
	}

	final SQLServerCommandBuffer command;

	ArrayList<ISqlRelationRefBuffer> source = new ArrayList<ISqlRelationRefBuffer>();
	SQLServerExprBuffer where;
	GroupMethod groupMethod = GroupMethod.NONE;
	ArrayList<SQLServerExprBuffer> group;
	SQLServerExprBuffer having;
	boolean distinct;
	ISqlBuffer top;
	ArrayList<SQLServerSelectColumnBuffer> columns = new ArrayList<SQLServerSelectColumnBuffer>();

	ArrayList<SQLServerUnionedBuffer> union;

	// boolean counting;
	private boolean dummy;

	public ISqlTableRefBuffer newTableRef(String table, String alias) {
		ISqlTableRefBuffer t = new SQLServerTableSourceBuffer(this.metadata, this.command, table, alias);
		this.source.add(t);
		return t;
	}

	public ISqlQueryRefBuffer newQueryRef(String alias) {
		ISqlQueryRefBuffer q = new SQLServerQuerySourceBuffer(this.metadata, this.command, alias);
		this.source.add(q);
		return q;
	}

	public ISqlWithRefBuffer newWithRef(String target, String alias) {
		ISqlWithRefBuffer t = new SQLServerWithSourceBuffer(this.metadata, this.command, target, alias);
		this.source.add(t);
		return t;
	}

	public void fromDummy() {
		this.dummy = true;
	}

	public ISqlExprBuffer newColumn(String alias) {
		SQLServerSelectColumnBuffer expr = new SQLServerSelectColumnBuffer(this.metadata, this.command, alias);
		this.columns.add(expr);
		return expr;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new SQLServerExprBuffer(this.metadata, this.command);
		}
		return this.where;
	}

	public ISqlExprBuffer groupby() {
		if (this.group == null) {
			this.group = new ArrayList<SQLServerExprBuffer>();
		}
		SQLServerExprBuffer expr = new SQLServerExprBuffer(this.metadata, this.command);
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
			this.having = new SQLServerExprBuffer(this.metadata, this.command);
		}
		return this.having;
	}

	public ISqlSelectBuffer union(boolean all) {
		if (this.union == null) {
			this.union = new ArrayList<SQLServerUnionedBuffer>();
		}
		SQLServerUnionedBuffer q = new SQLServerUnionedBuffer(this.metadata, this.command, all);
		this.union.add(q);
		return q;
	}

	static final void writeTop(SqlStringBuffer sql,
			List<ParameterPlaceholder> args, ISqlBuffer top,
			SqlserverMetadata metadata) {
		sql.append("top ");
		if (metadata.beforeYukon()) {
			top.writeTo(sql, args);
		} else {
			// sql.append('(');
			top.writeTo(sql, args);
			// sql.append(')');
		}
	}

	final void writeSelect(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("select ");
		if (this.distinct) {
			sql.append("distinct ");
		}
		if (this.top != null) {
			writeTop(sql, args, this.top, this.metadata);
			sql.append(' ');
		}
		for (int i = 0; i < this.columns.size(); i++) {
			SQLServerSelectColumnBuffer c = this.columns.get(i);
			if (i > 0) {
				sql.append(',');
			}
			c.writeTo(sql, args);
			sql.append(' ');
			sql.append(c.alias);
		}
		switch (this.injection) {
		case None:
			break;
		case Lietral:
			sql.append(",1 ").append(this.injectionAlias);
			break;
		case RowNumber:
			sql.append(",row_number() over (");
			SQLServerQueryBuffer.writeOrderby(sql, args, this.order, false);
			sql.append(") ").append(this.injectionAlias);
			break;
		case Orderbys:
			for (int i = 0; i < this.order.size(); i++) {
				sql.append(',');
				SQLServerOrderExprBuffer e = this.order.get(i);
				e.writeTo(sql, args);
				sql.append(' ');
				sql.append(quote(this.injectionAlias + i));
			}
			break;
		}
		if (!this.dummy) {
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
			sql.append(" group by ");
			Iterator<SQLServerExprBuffer> it = this.group.iterator();
			it.next().writeTo(sql, args);
			while (it.hasNext()) {
				sql.append(',');
				it.next().writeTo(sql, args);
			}
			switch (this.groupMethod) {
			case ROLLUP:
				sql.append(" with rollup");
				break;
			case CUBE:
				sql.append(" with cube");
				break;
			default:
				break;
			}
		}
		if (this.having != null) {
			sql.append(" having ");
			this.having.writeTo(sql, args);
		}
		// if (this.order != null) {
		// sql.append(" order by ");
		// Iterator<SQLServerOrderExprBuffer> it = this.order.iterator();
		// SQLServerOrderExprBuffer e = it.next();
		// e.writeTo(sql, args);
		// if (e.desc) {
		// sql.append(" desc");
		// }
		// while (it.hasNext()) {
		// e = it.next();
		// sql.append(',');
		// e.writeTo(sql, args);
		// if (e.desc) {
		// sql.append(" desc");
		// }
		// }
		// }
	}

	private enum Injection {

		None, Lietral, RowNumber, Orderbys
	}

	private Injection injection = Injection.None;
	private String injectionAlias;

	/**
	 * 在select子句追加常量1的输出
	 * 
	 * @param alias
	 *            输出列的别名
	 */
	final void injectSelectLiteral(String alias) {
		this.injection = Injection.Lietral;
		this.injectionAlias = SQLServerExprBuffer.quote(alias);
	}

	private ArrayList<SQLServerOrderExprBuffer> order;

	/**
	 * 在select子句追加row_number()函数的输出
	 * 
	 * @param alias
	 *            输出列的别名
	 * @param order
	 *            使用的排序规则
	 */
	final void injectRowNumber(String alias,
			ArrayList<SQLServerOrderExprBuffer> order) {
		this.injection = Injection.RowNumber;
		this.injectionAlias = SQLServerExprBuffer.quote(alias);
		this.order = order;
	}

	/**
	 * 在select子句追加order by的各表达式输出列
	 * 
	 * @param alias
	 *            输出别名的前缀
	 * @param order
	 *            排序规则列表
	 */
	final void injectOrderbys(String alias,
			ArrayList<SQLServerOrderExprBuffer> order) {
		this.injection = Injection.Orderbys;
		this.injectionAlias = alias;
		this.order = order;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		this.writeSelect(sql, args);
		if (this.union != null) {
			for (SQLServerUnionedBuffer u : this.union) {
				u.writeTo(sql, args);
			}
		}
	}
}
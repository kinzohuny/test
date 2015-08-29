package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import static com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer.SQLServerExprBuffer.quote;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sql.render.DatabaseCompatibleException;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlQueryBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

final class SQLServerQueryBuffer extends SQLServerCommandBuffer implements
		ISqlQueryBuffer {

	final SqlserverMetadata metadata;

	ArrayList<SQLServerWithBuffer> with;
	final SQLServerSelectBuffer select;
	ArrayList<SQLServerOrderExprBuffer> order;

	// unique
	SQLServerExprBuffer offset;
	SQLServerExprBuffer limit;

	public SQLServerQueryBuffer(SqlserverMetadata metadata,
			SQLServerSegmentBuffer scope) {
		super(scope);
		this.metadata = metadata;
		this.select = new SQLServerSelectBuffer(metadata, this);
	}

	public ISqlSelectBuffer select() {
		return this.select;
	}

	public ISqlSelectBuffer newWith(String alias) {
		// if (this.metadata.beforeYukon()) {
		// throw new DatabaseCompatibleException(this.metadata, "不支持with语法。");
		// }
		if (this.with == null) {
			this.with = new ArrayList<SQLServerWithBuffer>();
		}
		SQLServerWithBuffer w = new SQLServerWithBuffer(this.metadata, this, alias);
		this.with.add(w);
		return w;
	}

	final SQLServerWithBuffer getWith(String name) {
		if (this.with == null) {
			throw new UnsupportedOperationException();
		}
		final String e = SQLServerExprBuffer.quote(name);
		for (int i = 0, c = this.with.size(); i < c; i++) {
			SQLServerWithBuffer with = this.with.get(i);
			if (with.alias.equals(e)) {
				return with;
			}
		}
		throw new UnsupportedOperationException();
	}

	private final void writeWith(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		if (this.with != null) {
			sql.append("with ");
			for (int i = 0; i < this.with.size(); i++) {
				if (i > 0) {
					sql.append(',');
				}
				this.with.get(i).writeTo(sql, args);
			}
			sql.append(' ');
		}
	}

	public ISqlExprBuffer newOrder(boolean desc) {
		if (this.order == null) {
			this.order = new ArrayList<SQLServerOrderExprBuffer>();
		}
		SQLServerOrderExprBuffer expr = new SQLServerOrderExprBuffer(this.metadata, this, desc);
		this.order.add(expr);
		return expr;
	}

	public void newOrder(String column, boolean desc) {
		if (this.order == null) {
			this.order = new ArrayList<SQLServerOrderExprBuffer>();
		}
		SQLServerOrderExprBuffer expr = new SQLServerOrderExprBuffer(this.metadata, this, column, desc);
		this.order.add(expr);
	}

	public ISqlExprBuffer limit() {
		if (this.limit == null) {
			this.limit = new SQLServerExprBuffer(this.metadata, this);
		}
		return this.limit;
	}

	public ISqlExprBuffer offset() {
		if (this.offset == null) {
			this.offset = new SQLServerExprBuffer(this.metadata, this);
		}
		return this.offset;
	}

	static final void writeOrderby(SqlStringBuffer sql,
			List<ParameterPlaceholder> args,
			ArrayList<SQLServerOrderExprBuffer> order, boolean reverse) {
		sql.append("order by ");
		for (int i = 0; i < order.size(); i++) {
			final SQLServerOrderExprBuffer e = order.get(i);
			if (i > 0) {
				sql.append(',');
			}
			e.writeTo(sql, args);
			if (reverse != e.desc) {
				sql.append(" desc");
			}
		}
	}

	static final void writeOrderbyOutside(SqlStringBuffer sql,
			ArrayList<SQLServerOrderExprBuffer> order, String iq,
			String prefix, boolean reverse) {
		sql.append("order by ");
		for (int i = 0; i < order.size(); i++) {
			final SQLServerOrderExprBuffer e = order.get(i);
			if (i > 0) {
				sql.append(',');
			}
			sql.append(quote(iq)).append('.').append(quote(prefix + i));
			if (reverse != e.desc) {
				sql.append(" desc");
			}
		}
	}

	/**
	 * 重新输出
	 * 
	 * @param sql
	 * @param iq
	 * @param oldPrefix
	 * @param newPrefix
	 */
	private final void plusOrderbyOutput(SqlStringBuffer sql, String iq,
			String oldPrefix, String newPrefix) {
		for (int i = 0; i < this.order.size(); i++) {
			sql.append(',');
			sql.append(quote(iq)).append('.').append(quote(oldPrefix + i));
			sql.append(' ');
			sql.append(quote(newPrefix + i));
		}
	}

	// void wirteToDeprecated(SqlStringBuffer sql, List<ParameterPlaceholder>
	// args) {
	// if (this.limit != null) {
	// if (this.offset != null) {
	// if (this.select.union != null) {
	// sql.append("select * from (select top (");
	// this.limit.writeTo(sql, args);
	// sql.append("+");
	// this.offset.writeTo(sql, args);
	// sql.append("-1) *,ROW_NUMBER() over "
	// + "(order by [$FC]) [$FR] from (");
	// // this.select.counting = true;
	// this.select.writeTo(sql, args);
	// sql.append(") [$T0]) [$T0] where [$FR]>=");
	// this.offset.writeTo(sql, args);
	// if (this.order != null) {
	// sql.append(" order by ");
	// writeOrderby(sql, args, this.order, false);
	// }
	// } else {
	// sql.append("select * from (select *,ROW_NUMBER() over "
	// + "(order by [$FC]) [$FR] from (");
	// this.select.top = new ISqlBuffer() {
	// public void writeTo(SqlStringBuffer sql,
	// List<ParameterPlaceholder> args) {
	// sql.append("(");
	// SQLServerQueryBuffer.this.limit.writeTo(sql, args);
	// sql.append(")+(");
	// SQLServerQueryBuffer.this.offset.writeTo(sql, args);
	// sql.append(")");
	// }
	// };
	// // this.select.counting = true;
	// this.select.writeTo(sql, args);
	// if (this.order != null) {
	// sql.append(" order by ");
	// writeOrderby(sql, args, this.order, false);
	// }
	// sql.append(") [$T0]) [$T0] where [$FR]>");
	// this.offset.writeTo(sql, args);
	// }
	// } else {
	// if (this.select.union != null) {
	// sql.append("select top ");
	// this.limit.writeTo(sql, args);
	// sql.append(" * from (");
	// this.select.writeTo(sql, args);
	// sql.append(") [$T0]");
	// if (this.order != null) {
	// sql.append(" order by ");
	// writeOrderby(sql, args, this.order, false);
	// }
	// } else {
	// this.select.top = new ISqlBuffer() {
	// public void writeTo(SqlStringBuffer sql,
	// List<ParameterPlaceholder> args) {
	// SQLServerQueryBuffer.this.limit.writeTo(sql, args);
	// }
	// };
	// this.select.writeTo(sql, args);
	// if (this.order != null) {
	// sql.append(" order by ");
	// writeOrderby(sql, args, this.order, false);
	// }
	// }
	// }
	// } else {
	// this.select.writeTo(sql, args);
	// if (this.order != null) {
	// sql.append(" order by ");
	// writeOrderby(sql, args, this.order, false);
	// }
	// }
	// sql.append(';');
	// }

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		if (!this.metadata.beforeYukon()) {
			this.writeWith(sql, args);
		}
		if (this.limit == null) {
			// 没有任何限定
			this.writeNoLimitOrOffset(sql, args);
		} else if (this.select.union == null) {
			// 没有union
			if (this.offset == null) {
				// 限定top的查询，直接在select中注入top子句，limit作为输出行数
				this.injectTop(sql, args);
			} else if (this.metadata.beforeYukon()) {
				// limit，offset的限定
				// sqlserver2000版本
				this.twiceTopSelectThenReverseForShiloh(sql, args);
			} else if (this.select.distinct) {
				// sqlserver 2005及之后的版本
				// distinct的，不能注入row_number()，会影响distinct的过滤。
				this.injectTopAndLiteralThenRownumThenFilter(sql, args);
			} else if (this.order != null) {
				// 带order且不是distinct，可以注入row_number()。
				this.injectTopAndRownumThenFilter(sql, args);
			} else {
				this.injectTopAndLiteralThenRownumThenFilter(sql, args);
			}
		} else if (this.offset == null) {
			// 带union的top查询
			this.wrapTopOutside(sql, args);
		} else if (this.metadata.beforeYukon()) {
			throw new DatabaseCompatibleException(this.metadata, "不支持带union子句的限定查询");
		} else {
			this.wrapTopAndLiteralThenRownumThenFilter(sql, args);
		}
	}

	private final void writeNoLimitOrOffset(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		this.select.writeTo(sql, args);
		if (this.order != null) {
			sql.append(' ');
			writeOrderby(sql, args, this.order, false);
		}
	}

	private final void injectTop(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		this.select.top = this.limit;
		this.select.writeTo(sql, args);
		if (this.order != null) {
			sql.append(' ');
			writeOrderby(sql, args, this.order, false);
		}
	}

	private final void writeNoneUnionWithOrder(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {

	}

	private final void twiceTopSelectThenReverseForShiloh(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		if (this.order == null || this.order.size() == 0) {
			throw new DatabaseCompatibleException(this.metadata, "不支持不带order by子句的限定查询");
		}
		final String iq = "iq";
		final String iqp = "$Z";
		final String oq = "oq";
		final String oqp = "$Y";
		// limit作为第二层嵌套的top，正确的limit含义
		sql.append("select * from (select top ");
		this.limit.writeTo(sql, args);
		sql.append(" *");
		this.plusOrderbyOutput(sql, iq, iqp, oqp);
		sql.append(" from (");
		// offset作为（limit+offset）使用
		this.select.top = this.offset;
		this.select.injectOrderbys(iqp, this.order);
		this.select.writeTo(sql, args);
		sql.append(' ');
		writeOrderby(sql, args, this.order, false);
		sql.append(") ").append(quote(iq)).append(' ');
		writeOrderbyOutside(sql, this.order, iq, iqp, true);
		sql.append(") ").append(quote(oq)).append(' ');
		writeOrderbyOutside(sql, this.order, oq, oqp, false);
	}

	private final void injectTopAndLiteralThenRownumThenFilter(
			SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("select * from (select *,row_number() over (order by x.[$r]) [$rn] from (");
		this.select.top = this.limit;
		this.select.injectSelectLiteral("$r");
		this.select.writeTo(sql, args);
		if (this.order != null) {
			sql.append(' ');
			writeOrderby(sql, args, this.order, false);
		}
		sql.append(")x)y where y.[$rn]>");
		this.offset.writeTo(sql, args);
	}

	private final void injectTopAndRownumThenFilter(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		this.select.top = this.limit;
		this.select.injectRowNumber("$rn", this.order);
		sql.append("select * from (");
		this.select.writeTo(sql, args);
		sql.append(") iq where iq.[$rn]>");
		this.offset.writeTo(sql, args);
	}

	private final void wrapTopOutside(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		sql.append("select ");
		SQLServerSelectBuffer.writeTop(sql, args, this.limit, this.metadata);
		sql.append(" * from (");
		this.select.writeTo(sql, args);
		sql.append(") zm");
	}

	private final void wrapTopAndLiteralThenRownumThenFilter(
			SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("select * from (select y.*, row_number() over (order by y.[$l]) [$r] from (select ");
		SQLServerSelectBuffer.writeTop(sql, args, this.limit, this.metadata);
		sql.append(" x.*, 1 [$l] from (");
		this.select.writeTo(sql, args);
		sql.append(")x)y)z where z.[$r]>");
		this.offset.writeTo(sql, args);
	}
}
package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class HanaDeleteBuffer extends HanaCommandBuffer implements
		ISqlDeleteBuffer {

	final HanaTableRefBuffer target;
	HanaExprBuffer where;

	public HanaDeleteBuffer(String table, String alias) {
		super(null);
		this.target = new HanaTableRefBuffer(this, table, alias);
	}

	public HanaTableRefBuffer target() {
		return this.target;
	}

	public HanaExprBuffer where() {
		if (this.where == null) {
			this.where = new HanaExprBuffer(this);
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		throw new UnsupportedOperationException();
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("delete from ");
		if (this.target.joins != null) {
			// 多表
			String alias = "\"$ZM\"";
			sql.append(this.target.table).append(' ').append(alias).append(" where exists(select 1 from ");
			this.target.writeTo(sql, args);
			sql.append(" where (");
			this.where.writeTo(sql, args);
			sql.append(") and ").append(this.target.alias).append(".recid=").append(alias).append(".recid)");
		} else if (this.where != null) {
			// 单表
			this.target.writeTo(sql, args);
			sql.append(" where ");
			this.where.writeTo(sql, args);
		} else {
			// 没有where
			sql.append(this.target.table);
		}
		if (this.scope != null) {
			sql.append(';');
		}
	}
}

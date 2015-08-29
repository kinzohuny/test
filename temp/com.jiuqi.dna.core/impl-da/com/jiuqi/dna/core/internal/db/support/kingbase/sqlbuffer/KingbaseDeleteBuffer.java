package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlCommandBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class KingbaseDeleteBuffer extends SqlCommandBuffer implements
		ISqlDeleteBuffer {
	final KingbaseTableRefBuffer table;
	KingbaseExprBuffer where;
	String cursor;

	public KingbaseDeleteBuffer(KingbaseSegmentBuffer scope, String table,
			String alias) {
		super(scope);
		this.table = new KingbaseTableRefBuffer(table, alias);
	}

	public ISqlTableRefBuffer target() {
		return this.table;
	}

	public ISqlExprBuffer where() {
		if (this.where == null) {
			this.where = new KingbaseExprBuffer();
		}
		return this.where;
	}

	public void whereCurrentOf(String cursor) {
		this.cursor = cursor;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("delete from ");
		if (this.table.joins != null) {
			// 多表
			String alias = "\"$T\"";
			sql.append(this.table.name).append(" \"$TE\"");
			sql.append(" where not exists (select 1 from ").append(this.table.name);
			sql.append(' ').append(alias).append(" where not exists (select 1 from ");
			// To change
			// sql.append(this.table.name).append(' ').append(alias)
			// .append(" where exists(select 1 from ");
			this.table.writeTo(sql, args);
			sql.append(" where (");
			this.where.writeTo(sql, args);
			sql.append(") and ").append(this.table.alias).append(".recid=").append(alias).append(".recid)");
			// To change
			sql.append(')');
		} else if (this.cursor != null) {
			// 游标
			this.table.writeTo(sql, args);
			sql.append(" where current of ").append(this.cursor);
		} else if (this.where != null) {
			// 单表
			this.table.writeTo(sql, args);
			sql.append(" where ");
			this.where.writeTo(sql, args);
		} else {
			// 没有where
			sql.append(this.table.name);
		}
		if (this.scope != null) {
			sql.append(';');
		}
	}
}

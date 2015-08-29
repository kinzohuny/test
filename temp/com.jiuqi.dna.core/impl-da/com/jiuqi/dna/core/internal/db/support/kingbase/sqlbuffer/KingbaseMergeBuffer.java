package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlMergeBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlCommandBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class KingbaseMergeBuffer extends SqlCommandBuffer implements
		ISqlMergeBuffer {
	final String table;
	final String alias;
	String srcTable;
	KingbaseSelectBuffer srcQuery;
	String srcAlias;
	ArrayList<KingbaseMergeValueBuffer> newValues;
	ArrayList<KingbaseMergeValueBuffer> setValues;
	KingbaseExprBuffer condition;

	static class KingbaseMergeValueBuffer extends KingbaseExprBuffer {
		final String field;

		public KingbaseMergeValueBuffer(String field) {
			this.field = field;
		}
	}

	/**
	 * @param scope
	 * @param table
	 *            unquoted
	 * @param alias
	 *            unquoted
	 */
	public KingbaseMergeBuffer(KingbaseSegmentBuffer scope, String table,
			String alias) {
		super(scope);
		this.table = KingbaseExprBuffer.quote(table);
		this.alias = KingbaseExprBuffer.quote(alias);
	}

	public void usingDummy() {
		this.srcTable = "dual";
		this.srcAlias = "dual";
	}

	public void usingTable(String table, String alias) {
		this.srcTable = KingbaseExprBuffer.quote(table);
		this.srcAlias = KingbaseExprBuffer.quote(alias);
	}

	public ISqlSelectBuffer usingSubquery(String alias) {
		this.srcQuery = new KingbaseSelectBuffer();
		this.srcAlias = KingbaseExprBuffer.quote(alias);
		return this.srcQuery;
	}

	public ISqlExprBuffer insert(String field) {
		if (this.newValues == null) {
			this.newValues = new ArrayList<KingbaseMergeValueBuffer>();
		}
		KingbaseMergeValueBuffer e = new KingbaseMergeValueBuffer(field);
		this.newValues.add(e);
		return e;
	}

	public ISqlExprBuffer update(String field) {
		if (this.setValues == null) {
			this.setValues = new ArrayList<KingbaseMergeValueBuffer>();
		}
		KingbaseMergeValueBuffer e = new KingbaseMergeValueBuffer(field);
		this.setValues.add(e);
		return e;
	}

	public ISqlExprBuffer onCondition() {
		if (this.condition == null) {
			this.condition = new KingbaseExprBuffer();
		}
		return this.condition;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		sql.append("merge into ").append(this.table);
		if (this.alias != null) {
			sql.append(' ').append(this.alias);
		}
		sql.append(" using ");
		if (this.srcTable != null) {
			sql.append(this.srcTable);
		} else {
			sql.append('(');
			this.srcQuery.writeTo(sql, args);
			sql.append(')');
		}
		if (this.srcAlias != null) {
			sql.append(' ').append(this.srcAlias);
		}
		sql.append(" on (");
		this.condition.writeTo(sql, args);
		sql.append(')');
		if (this.setValues != null) {
			sql.append(" when matched then update set ");
			Iterator<KingbaseMergeValueBuffer> iter = this.setValues.iterator();
			KingbaseMergeValueBuffer val = iter.next();
			sql.append(KingbaseExprBuffer.quote(val.field)).append('=');
			val.writeTo(sql, args);
			while (iter.hasNext()) {
				val = iter.next();
				sql.append(',').append(KingbaseExprBuffer.quote(val.field)).append('=');
				val.writeTo(sql, args);
			}
		}
		if (this.newValues != null) {
			sql.append(" when not matched then insert (");
			Iterator<KingbaseMergeValueBuffer> iter = this.newValues.iterator();
			sql.append(KingbaseExprBuffer.quote(iter.next().field));
			while (iter.hasNext()) {
				sql.append(',').append(iter.next().field);
			}
			sql.append(") values(");
			iter = this.newValues.iterator();
			iter.next().writeTo(sql, args);
			while (iter.hasNext()) {
				sql.append(',');
				iter.next().writeTo(sql, args);
			}
			sql.append(')');
		}
		if (this.scope != null) {
			sql.append(';');
		}
	}
}

package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlMergeBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlCommandBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;

public class OracleMergeBuffer extends SqlCommandBuffer implements
		ISqlMergeBuffer {

	static class OracleMergeValueBuffer extends OracleExprBuffer {

		final String field;

		public OracleMergeValueBuffer(String field) {
			this.field = OracleExprBuffer.quote(field);
		}
	}

	final String table;
	final String alias;
	String srcTable;
	OracleSelectBuffer srcQuery;
	String srcAlias;
	OracleExprBuffer condition;
	ArrayList<OracleMergeValueBuffer> insertValues;
	ArrayList<OracleMergeValueBuffer> updateValues;

	/**
	 * @param scope
	 * @param table
	 *            unquoted
	 * @param alias
	 *            unquoted
	 */
	public OracleMergeBuffer(OracleSegmentBuffer scope, String table,
			String alias) {
		super(scope);
		this.table = OracleExprBuffer.quote(table);
		this.alias = OracleExprBuffer.quote(alias);
	}

	public void usingDummy() {
		this.srcTable = "dual";
		this.srcAlias = "dual";
	}

	public void usingTable(String table, String alias) {
		this.srcTable = OracleExprBuffer.quote(table);
		this.srcAlias = OracleExprBuffer.quote(alias);
	}

	public ISqlSelectBuffer usingSubquery(String alias) {
		this.srcQuery = new OracleSelectBuffer();
		this.srcAlias = OracleExprBuffer.quote(alias);
		return this.srcQuery;
	}

	public ISqlExprBuffer insert(String field) {
		if (this.insertValues == null) {
			this.insertValues = new ArrayList<OracleMergeValueBuffer>();
		}
		OracleMergeValueBuffer e = new OracleMergeValueBuffer(field);
		this.insertValues.add(e);
		return e;
	}

	public ISqlExprBuffer update(String field) {
		if (this.updateValues == null) {
			this.updateValues = new ArrayList<OracleMergeValueBuffer>();
		}
		OracleMergeValueBuffer e = new OracleMergeValueBuffer(field);
		this.updateValues.add(e);
		return e;
	}

	public ISqlExprBuffer onCondition() {
		if (this.condition == null) {
			this.condition = new OracleExprBuffer();
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
		if (this.updateValues != null) {
			sql.append(" when matched then update set ");
			Iterator<OracleMergeValueBuffer> iter = this.updateValues.iterator();
			OracleMergeValueBuffer val = iter.next();
			sql.append(val.field).append('=');
			val.writeTo(sql, args);
			while (iter.hasNext()) {
				val = iter.next();
				sql.append(',').append(val.field).append('=');
				val.writeTo(sql, args);
			}
		}
		if (this.insertValues != null) {
			sql.append(" when not matched then insert (");
			Iterator<OracleMergeValueBuffer> iter = this.insertValues.iterator();
			sql.append(iter.next().field);
			while (iter.hasNext()) {
				sql.append(',').append(iter.next().field);
			}
			sql.append(") values(");
			iter = this.insertValues.iterator();
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

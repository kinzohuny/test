package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlConditionBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCursorLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlMergeBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlMergeCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectIntoBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlCommandBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.oracle.OracleMetadata;
import com.jiuqi.dna.core.type.DataType;

class OracleSegmentBuffer extends SqlCommandBuffer implements
		ISqlSegmentBuffer, ISqlMergeCommandFactory {

	static class Variable {

		final String name;
		final DataType type;

		public Variable(String name, DataType type) {
			this.name = OracleExprBuffer.quote(name);
			this.type = type;
		}

		public void writeTo(SqlStringBuffer sql) {
			sql.append(this.name).append(' ');
			this.type.detect(OracleMetadata.formatter, sql);
		}
	}

	final ArrayList<ISqlBuffer> stmts = new ArrayList<ISqlBuffer>();
	ArrayList<Variable> vars;

	public OracleSegmentBuffer(OracleSegmentBuffer scope) {
		super(scope);
	}

	public void declare(String name, DataType type) {
		if (this.vars == null) {
			this.vars = new ArrayList<Variable>();
		}
		this.vars.add(new Variable(name, type));
	}

	public ISqlInsertBuffer insert(String table) {
		OracleInsertBuffer i = new OracleInsertBuffer(this, table);
		this.stmts.add(i);
		return i;
	}

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		OracleUpdateBuffer u = new OracleUpdateBuffer(this, table, alias, assignFromSlaveTable);
		this.stmts.add(u);
		return u;
	}

	public ISqlDeleteBuffer delete(String table, String alias) {
		OracleDeleteBuffer d = new OracleDeleteBuffer(this, table, alias);
		this.stmts.add(d);
		return d;
	}

	public ISqlExprBuffer assign(String var) {
		OracleAssignBuffer a = new OracleAssignBuffer(var);
		this.stmts.add(a);
		return a;
	}

	public ISqlSelectIntoBuffer selectInto() {
		OracleSelectIntoBuffer s = new OracleSelectIntoBuffer();
		this.stmts.add(s);
		return s;
	}

	public ISqlConditionBuffer ifThenElse() {
		OracleConditionBuffer c = new OracleConditionBuffer(this);
		this.stmts.add(c);
		return c;
	}

	public ISqlLoopBuffer loop() {
		OracleLoopBuffer l = new OracleLoopBuffer(this);
		this.stmts.add(l);
		return l;
	}

	public ISqlCursorLoopBuffer cursorLoop(String cursor, boolean forUpdate) {
		OracleCursorLoopBuffer l = new OracleCursorLoopBuffer(this, cursor, forUpdate);
		this.stmts.add(l);
		return l;
	}

	public void breakLoop() {
		this.stmts.add(OracleSimpleBuffer.BREAK);
	}

	public void exit() {
		this.stmts.add(OracleSimpleBuffer.EXIT);
	}

	public ISqlExprBuffer returnValue() {
		OracleReturnBuffer r = new OracleReturnBuffer();
		this.stmts.add(r);
		return r;
	}

	public ISqlExprBuffer print() {
		OraclePrintBuffer p = new OraclePrintBuffer();
		this.stmts.add(p);
		return p;
	}

	public ISqlMergeBuffer merge(String table, String alias) {
		OracleMergeBuffer m = new OracleMergeBuffer(this, table, alias);
		this.stmts.add(m);
		return m;
	}

	@SuppressWarnings("unchecked")
	public <T> T getFeature(Class<T> clazz) {
		if (clazz == ISqlMergeCommandFactory.class) {
			return (T) this;
		}
		return null;
	}

	protected void writeDeclare(SqlStringBuffer sql) {
		sql.append("declare ");
		for (Variable var : this.vars) {
			var.writeTo(sql);
			sql.append(';');
		}
	}

	protected void writeStmts(SqlStringBuffer sql,
			List<ParameterPlaceholder> args) {
		for (ISqlBuffer b : this.stmts) {
			b.writeTo(sql, args);
		}
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		if (this.vars != null) {
			this.writeDeclare(sql);
			sql.append("begin ");
			this.writeStmts(sql, args);
			sql.append(" end;");
		} else {
			this.writeStmts(sql, args);
		}
	}
}

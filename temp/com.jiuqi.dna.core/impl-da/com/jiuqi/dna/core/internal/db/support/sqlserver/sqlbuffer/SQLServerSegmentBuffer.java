package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlConditionBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCursorLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectIntoBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;
import com.jiuqi.dna.core.type.DataType;

class SQLServerSegmentBuffer extends SQLServerCommandBuffer implements
		ISqlSegmentBuffer {

	final SqlserverMetadata metadata;

	static class Variable {
		final String name;
		final DataType type;

		public Variable(String name, DataType type) {
			this.name = name;
			this.type = type;
		}

		public void writeTo(SqlStringBuffer sql) {
			sql.append('@').append(this.name).append(' ');
			this.type.detect(SqlserverMetadata.formatter, sql);
		}

		public void writeRefTo(SqlStringBuffer sql) {
			sql.append('@').append(this.name);
		}
	}

	final ArrayList<ISqlBuffer> stmts = new ArrayList<ISqlBuffer>();
	ArrayList<Variable> vars;

	public SQLServerSegmentBuffer(SqlserverMetadata metadata,
			SQLServerSegmentBuffer scope) {
		super(scope);
		this.metadata = metadata;
	}

	public void declare(String name, DataType type) {
		if (this.vars == null) {
			this.vars = new ArrayList<Variable>();
		}
		this.vars.add(new Variable(name, type));
	}

	public ISqlInsertBuffer insert(String table) {
		SQLServerInsertBuffer i = new SQLServerInsertBuffer(this.metadata, this, table);
		this.stmts.add(i);
		return i;
	}

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		SQLServerUpdateBuffer u = new SQLServerUpdateBuffer(this.metadata, this, table, alias);
		this.stmts.add(u);
		return u;
	}

	public ISqlDeleteBuffer delete(String table, String alias) {
		SQLServerDeleteBuffer d = new SQLServerDeleteBuffer(this.metadata, this, table, alias);
		this.stmts.add(d);
		return d;
	}

	public ISqlExprBuffer assign(String var) {
		SQLServerAssignBuffer a = new SQLServerAssignBuffer(this.metadata, var);
		this.stmts.add(a);
		return a;
	}

	public ISqlSelectIntoBuffer selectInto() {
		SQLServerSelectIntoBuffer s = new SQLServerSelectIntoBuffer(this.metadata);
		this.stmts.add(s);
		return s;
	}

	public ISqlConditionBuffer ifThenElse() {
		SQLServerConditionBuffer c = new SQLServerConditionBuffer(this.metadata, this);
		this.stmts.add(c);
		return c;
	}

	public ISqlLoopBuffer loop() {
		SQLServerLoopBuffer l = new SQLServerLoopBuffer(this.metadata, this);
		this.stmts.add(l);
		return l;
	}

	public ISqlCursorLoopBuffer cursorLoop(String cursor, boolean forUpdate) {
		SQLServerCursorLoopBuffer l = new SQLServerCursorLoopBuffer(this.metadata, this, cursor);
		this.stmts.add(l);
		return l;
	}

	public void breakLoop() {
		this.stmts.add(SQLServerSimpleBuffer.BREAK);
	}

	public void exit() {
		this.stmts.add(SQLServerSimpleBuffer.EXIT);
	}

	public ISqlExprBuffer returnValue() {
		SQLServerReturnBuffer r = new SQLServerReturnBuffer(this.metadata);
		this.stmts.add(r);
		return r;
	}

	public ISqlExprBuffer print() {
		SQLServerPrintBuffer p = new SQLServerPrintBuffer(this.metadata);
		this.stmts.add(p);
		return p;
	}

	public <T> T getFeature(Class<T> clazz) {
		return null;
	}

	protected void writeDeclare(SqlStringBuffer sql) {
		sql.append("declare ");
		Iterator<Variable> iter = this.vars.iterator();
		iter.next().writeTo(sql);
		while (iter.hasNext()) {
			sql.append(',');
			iter.next().writeTo(sql);
		}
		sql.append(';');
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

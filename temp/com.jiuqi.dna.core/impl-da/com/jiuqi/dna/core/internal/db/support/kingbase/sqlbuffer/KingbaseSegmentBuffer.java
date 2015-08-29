package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

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
import com.jiuqi.dna.core.internal.db.support.kingbase.KingbaseMetadata;
import com.jiuqi.dna.core.type.DataType;

class KingbaseSegmentBuffer extends SqlCommandBuffer implements
		ISqlSegmentBuffer, ISqlMergeCommandFactory {

	static class Variable {

		final String name;
		final DataType type;

		public Variable(String name, DataType type) {
			this.name = KingbaseExprBuffer.quote(name);
			this.type = type;
		}

		public void writeTo(SqlStringBuffer sql) {
			sql.append(this.name).append(' ');
			this.type.detect(KingbaseMetadata.formatter, sql);
		}
	}

	final ArrayList<ISqlBuffer> stmts = new ArrayList<ISqlBuffer>();
	ArrayList<Variable> vars;

	public KingbaseSegmentBuffer(KingbaseSegmentBuffer scope) {
		super(scope);
	}

	public void declare(String name, DataType type) {
		if (this.vars == null) {
			this.vars = new ArrayList<Variable>();
		}
		this.vars.add(new Variable(name, type));
	}

	public ISqlInsertBuffer insert(String table) {
		KingbaseInsertBuffer i = new KingbaseInsertBuffer(this, table);
		this.stmts.add(i);
		return i;
	}

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		KingbaseUpdateBuffer u = new KingbaseUpdateBuffer(this, table, alias, assignFromSlaveTable);
		this.stmts.add(u);
		return u;
	}

	public ISqlDeleteBuffer delete(String table, String alias) {
		KingbaseDeleteBuffer d = new KingbaseDeleteBuffer(this, table, alias);
		this.stmts.add(d);
		return d;
	}

	public ISqlExprBuffer assign(String var) {
		KingbaseAssignBuffer a = new KingbaseAssignBuffer(var);
		this.stmts.add(a);
		return a;
	}

	public ISqlSelectIntoBuffer selectInto() {
		KingbaseSelectIntoBuffer s = new KingbaseSelectIntoBuffer();
		this.stmts.add(s);
		return s;
	}

	public ISqlConditionBuffer ifThenElse() {
		KingbaseConditionBuffer c = new KingbaseConditionBuffer(this);
		this.stmts.add(c);
		return c;
	}

	public ISqlLoopBuffer loop() {
		KingbaseLoopBuffer l = new KingbaseLoopBuffer(this);
		this.stmts.add(l);
		return l;
	}

	public ISqlCursorLoopBuffer cursorLoop(String cursor, boolean forUpdate) {
		KingbaseCursorLoopBuffer l = new KingbaseCursorLoopBuffer(this, cursor, forUpdate);
		this.stmts.add(l);
		return l;
	}

	public void breakLoop() {
		this.stmts.add(KingbaseSimpleBuffer.BREAK);
	}

	public void exit() {
		this.stmts.add(KingbaseSimpleBuffer.EXIT);
	}

	public ISqlExprBuffer returnValue() {
		KingbaseReturnBuffer r = new KingbaseReturnBuffer();
		this.stmts.add(r);
		return r;
	}

	public ISqlExprBuffer print() {
		KingbasePrintBuffer p = new KingbasePrintBuffer();
		this.stmts.add(p);
		return p;
	}

	public ISqlMergeBuffer merge(String table, String alias) {
		KingbaseMergeBuffer m = new KingbaseMergeBuffer(this, table, alias);
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
			sql.append("begin; ");
			this.writeStmts(sql, args);
			sql.append(" end;");
		} else {
			this.writeStmts(sql, args);
		}
	}
}

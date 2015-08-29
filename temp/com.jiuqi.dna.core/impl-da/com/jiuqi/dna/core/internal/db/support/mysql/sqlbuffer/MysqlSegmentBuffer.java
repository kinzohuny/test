package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlConditionBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCursorLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteMultiBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteMultiCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlReplaceCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectIntoBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateMultiBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateMultiCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.type.DataType;

class MysqlSegmentBuffer extends MysqlCommandBuffer implements
		ISqlSegmentBuffer, ISqlReplaceCommandFactory,
		ISqlUpdateMultiCommandFactory, ISqlDeleteMultiCommandFactory {

	final ArrayList<ISqlBuffer> stmts = new ArrayList<ISqlBuffer>();

	MysqlSegmentBuffer(ISqlSegmentBuffer scope) {
		super(scope);
	}

	public void declare(String name, DataType type) {
		throw new UnsupportedOperationException();
	}

	public MysqlInsertBuffer insert(String table) {
		MysqlInsertBuffer i = new MysqlInsertBuffer(this, table);
		this.stmts.add(i);
		return i;
	}

	public MysqlDeleteBuffer delete(String table, String alias) {
		MysqlDeleteBuffer d = new MysqlDeleteBuffer(this, table, alias);
		this.stmts.add(d);
		return d;
	}

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable) {
		MysqlUpdateBuffer i = new MysqlUpdateBuffer(this, table, alias, assignFromSlaveTable);
		this.stmts.add(i);
		return i;
	}

	public ISqlUpdateMultiBuffer updateMultiple(String table, String alias) {
		MysqlUpdateMultiBuffer u = new MysqlUpdateMultiBuffer(this, table, alias);
		this.stmts.add(u);
		return u;
	}

	public ISqlDeleteMultiBuffer deleteMulti(String table, String alias) {
		MysqlDeleteMultiBuffer d = new MysqlDeleteMultiBuffer(this, table, alias);
		this.stmts.add(d);
		return d;
	}

	public MysqlReplaceBuffer replace(String table) {
		MysqlReplaceBuffer r = new MysqlReplaceBuffer(this, table);
		this.stmts.add(r);
		return r;
	}

	public ISqlExprBuffer assign(String var) {
		throw new UnsupportedOperationException();
	}

	public ISqlSelectIntoBuffer selectInto() {
		throw new UnsupportedOperationException();
	}

	public ISqlConditionBuffer ifThenElse() {
		throw new UnsupportedOperationException();
	}

	public ISqlLoopBuffer loop() {
		throw new UnsupportedOperationException();
	}

	public ISqlCursorLoopBuffer cursorLoop(String cursor, boolean forUpdate) {
		throw new UnsupportedOperationException("mysql does not supported cursor loop.");
	}

	public void breakLoop() {
		throw new UnsupportedOperationException();
	}

	public ISqlExprBuffer print() {
		throw new UnsupportedOperationException();
	}

	public void exit() {
		throw new UnsupportedOperationException();
	}

	public ISqlExprBuffer returnValue() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public <T> T getFeature(Class<T> clazz) {
		if (clazz == ISqlReplaceCommandFactory.class) {
			return (T) this;
		} else if (clazz == ISqlUpdateMultiCommandFactory.class) {
			return (T) this;
		} else if (clazz == ISqlDeleteMultiCommandFactory.class) {
			return (T) this;
		}
		return null;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		for (int i = 0, c = this.stmts.size(); i < c; i++) {
			this.stmts.get(i).writeTo(sql, args);
			sql.append(';');
		}
	}

}

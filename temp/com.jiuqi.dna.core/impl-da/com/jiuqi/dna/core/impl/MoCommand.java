package com.jiuqi.dna.core.impl;

import java.sql.ResultSet;

import com.jiuqi.dna.core.da.RecordIterateAction;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlModifier;

final class MoCommand extends DBCommandImpl {

	@Override
	final int executeUpdate() {
		return this.updater.update(this.argValueObj);
	}

	@Override
	final RecordSet[] executeProcedure() {
		throw this.notProcedureStatement();
	}

	@Override
	final ResultSet executeQuery(Object argValueObj) {
		throw this.notQueryStatement();
	}

	@Override
	final RecordSetImpl executeQuery() {
		throw this.notQueryStatement();
	}

	@Override
	final RecordSetImpl executeQueryTop(long limit) {
		throw this.notQueryStatement();
	}

	@Override
	final RecordSetImpl executeQueryLimit(long limit, long offset) {
		throw this.notQueryStatement();
	}

	@Override
	final void iterateQuery(RecordIterateAction action) {
		throw this.notQueryStatement();
	}

	@Override
	final void iterateQueryTop(RecordIterateAction action, long limit) {
		throw this.notQueryStatement();
	}

	@Override
	final void iterateQueryLimit(RecordIterateAction action, long limit,
			long offset) {
		throw this.notQueryStatement();
	}

	@Override
	final long rowCountOf() {
		throw this.notQueryStatement();
	}

	@Override
	final Object executeScalar() {
		throw this.notQueryStatement();
	}

	@Override
	final IStatement getStatement() {
		return this.modify;
	}

	@Override
	protected final void unuse() {
		this.updater.unuse();
	}

	final ModifyStatementImpl modify;

	MoCommand(ContextImpl<?, ?, ?> context, ModifyStatementImpl modify,
			DBCommandProxy proxy) {
		super(context, modify, proxy);
		this.modify = modify;
		this.updater = modify.getSql(this.adapter).newExecutor(this.adapter, this);
	}

	private final SqlModifier updater;
}
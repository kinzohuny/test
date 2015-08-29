package com.jiuqi.dna.core.impl;

import java.sql.ResultSet;

import com.jiuqi.dna.core.da.RecordIterateAction;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.internal.da.sql.execute.SpExecutor;

final class SpCommand extends DBCommandImpl {

	@Override
	final int executeUpdate() {
		throw this.notModifyStatement();
	}

	@Override
	final RecordSet[] executeProcedure() {
		if (this.procedure.isInvalid()) {
			throw new RoutineInvalidException(this.procedure);
		}
		this.adapter.checkAccessible();
		return this.executor.executeProcedure(this.argValueObj);
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
	final StoredProcedureDefineImpl getStatement() {
		return this.procedure;
	}

	@Override
	protected final void unuse() {
		if (this.executor != null) {
			this.executor.unuse();
		}
	}

	final StoredProcedureDefineImpl procedure;

	SpCommand(ContextImpl<?, ?, ?> context,
			StoredProcedureDefineImpl procedure, DBCommandProxy proxy) {
		super(context, procedure, proxy);
		if (procedure.isInvalid()) {
			throw new RoutineInvalidException(procedure);
		}
		this.procedure = procedure;
		this.executor = procedure.getSql(this.adapter).newExecutor(this.adapter, this);
	}

	private SpExecutor executor;
}
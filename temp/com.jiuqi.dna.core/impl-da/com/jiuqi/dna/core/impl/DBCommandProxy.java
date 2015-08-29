package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.da.DBCommand;
import com.jiuqi.dna.core.da.RecordIterateAction;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.def.arg.ArgumentDefine;
import com.jiuqi.dna.core.def.obja.DynamicObject;

final class DBCommandProxy implements DBCommand, TransientProxy<DBCommandImpl> {

	public final IStatement getStatement() {
		return this.command.getStatement();
	}

	public final RecordSetImpl executeQuery() {
		return this.command.executeQuery();
	}

	public final RecordSetImpl executeQueryTop(long rowCount) {
		return this.command.executeQueryTop(rowCount);
	}

	public final RecordSetImpl executeQueryLimit(long offset, long rowCount) {
		return this.command.executeQueryLimit(rowCount, offset);
	}

	public final void iterateQuery(RecordIterateAction action) {
		this.command.iterateQuery(action);
	}

	public final void iterateQueryTop(long rowCount, RecordIterateAction action) {
		this.command.iterateQueryTop(action, rowCount);
	}

	public final void iterateQueryLimit(RecordIterateAction action,
			long offset, long rowCount) {
		this.command.iterateQueryLimit(action, offset, rowCount);
	}

	public final Object executeScalar() {
		return this.command.executeScalar();
	}

	public final int rowCountOf() {
		return (int) this.command.rowCountOf();
	}

	public final long rowCountOfL() {
		return this.command.rowCountOf();
	}

	public final int executeUpdate() {
		return this.command.executeUpdate();
	}

	public final RecordSet[] executeProcedure() {
		return this.command.executeProcedure();
	}

	public final DynamicObject getArgumentsObj() {
		return this.command.getArgumentsObj();
	}

	public final void setArgumentValue(int argIndex, Object argValue) {
		this.command.setArgumentValue(argIndex, argValue);
	}

	public final void setArgumentValue(ArgumentDefine arg, Object argValue) {
		this.command.setArgumentValue(arg, argValue);
	}

	public final void setArgumentValues(Object... argValues) {
		this.command.setArgumentValues(argValues);
	}

	public final Object getArgumentValue(int index) {
		return this.command.getArgumentValue(index);
	}

	public final Object getArgumentValue(ArgumentDefine arg) {
		return this.command.getArgumentValue(arg);
	}

	public final void unuse() {
		this.command.unuse();
	}

	final DBCommandImpl command;

	public final DBCommandImpl getProvider() {
		return this.command;
	}

	DBCommandProxy(ContextImpl<?, ?, ?> context, IStatement statement) {
		if (statement instanceof QueryStatementImpl) {
			this.command = new QuCommand(context, (QueryStatementImpl) statement, this);
		} else if (statement instanceof StoredProcedureDefineImpl) {
			this.command = new SpCommand(context, (StoredProcedureDefineImpl) statement, this);
		} else {
			this.command = new MoCommand(context, (ModifyStatementImpl) statement, this);
		}
	}
}
package com.jiuqi.dna.core.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.jiuqi.dna.core.da.RecordIterateAction;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.da.sql.execute.LimitQuerier;
import com.jiuqi.dna.core.internal.da.sql.execute.Querier;
import com.jiuqi.dna.core.internal.da.sql.execute.RowCountQuerier;
import com.jiuqi.dna.core.internal.da.sql.execute.TopQuerier;
import com.jiuqi.dna.core.type.DataType;

final class QuCommand extends DBCommandImpl {

	@Override
	final int executeUpdate() {
		throw this.notModifyStatement();
	}

	@Override
	final RecordSet[] executeProcedure() {
		throw this.notProcedureStatement();
	}

	@Override
	final ResultSet executeQuery(Object argValueObj) {
		return this.ensureQuerier().query(argValueObj);
	}

	@Override
	final RecordSetImpl executeQuery() {
		this.adapter.checkAccessible();
		RecordSetImpl rs = new RecordSetImpl(this.query);
		try {
			rs.loadRecordSet(this.ensureQuerier().query(this.argValueObj));
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
		return rs;
	}

	@Override
	final RecordSetImpl executeQueryTop(long limit) {
		RecordSetImpl rs = new RecordSetImpl(this.query);
		try {
			rs.loadRecordSet(this.ensureTopQuerier().query(this.argValueObj, limit));
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
		return rs;
	}

	@Override
	final RecordSetImpl executeQueryLimit(long limit, long offset) {
		RecordSetImpl rs = new RecordSetImpl(this.query);
		try {
			rs.loadRecordSet(this.ensureLimitQuerier().query(this.argValueObj, limit, offset));
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
		return rs;
	}

	@Override
	final void iterateQuery(RecordIterateAction action) {
		this.adapter.checkAccessible();
		if (action == null) {
			throw new NullArgumentException("查询迭代操作");
		}
		final RecordSetImpl rs = new RecordSetImpl(this.query);
		try {
			rs.iterateResultSet(this.context, this.ensureQuerier().query(this.argValueObj), action);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	final void iterateQueryTop(RecordIterateAction action, long limit) {
		this.adapter.checkAccessible();
		if (action == null) {
			throw new NullArgumentException("查询迭代操作");
		}
		final RecordSetImpl rs = new RecordSetImpl(this.query);
		try {
			rs.iterateResultSet(this.context, this.ensureTopQuerier().query(this.argValueObj, limit), action);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	final void iterateQueryLimit(RecordIterateAction action, long limit,
			long offset) {
		this.adapter.checkAccessible();
		if (action == null) {
			throw new NullArgumentException("查询迭代操作");
		}
		final RecordSetImpl rs = new RecordSetImpl(this.query);
		try {
			rs.iterateResultSet(this.context, this.ensureLimitQuerier().query(this.argValueObj, limit, offset), action);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	final long rowCountOf() {
		this.adapter.checkAccessible();
		return this.ensureRowCountQuerier().longScalar(this.argValueObj);
	}

	@Override
	final Object executeScalar() {
		this.adapter.checkAccessible();
		try {
			ResultSet rs = this.ensureQuerier().query(this.argValueObj);
			try {
				if (rs.next()) {
					DataType dt = this.query.columns.get(0).value().getType();
					return dt.detect(ResultSetScalarReader.reader, rs);
				}
				return null;
			} finally {
				rs.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	final QueryStatementImpl getStatement() {
		return this.query;
	}

	@Override
	protected final void unuse() {
		if (this.querier != null) {
			this.querier.unuse();
		}
		if (this.topQuerier != null) {
			this.topQuerier.unuse();
		}
		if (this.limitQuerier != null) {
			this.limitQuerier.unuse();
		}
		if (this.rowCountQuerier != null) {
			this.rowCountQuerier.unuse();
		}
	}

	final QueryStatementImpl query;

	QuCommand(ContextImpl<?, ?, ?> context, QueryStatementImpl query,
			DBCommandProxy proxy) {
		super(context, query, proxy);
		this.query = query;
	}

	private Querier querier;
	private TopQuerier topQuerier;
	private LimitQuerier limitQuerier;
	private RowCountQuerier rowCountQuerier;

	final Querier ensureQuerier() {
		if (this.querier == null) {
			this.querier = this.query.getSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.querier;
	}

	private final TopQuerier ensureTopQuerier() {
		if (this.topQuerier == null) {
			this.topQuerier = this.query.getQueryTopSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.topQuerier;
	}

	private final LimitQuerier ensureLimitQuerier() {
		if (this.limitQuerier == null) {
			this.limitQuerier = this.query.getQueryLimitSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.limitQuerier;
	}

	private final RowCountQuerier ensureRowCountQuerier() {
		if (this.rowCountQuerier == null) {
			this.rowCountQuerier = this.query.getQueryRowCountSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.rowCountQuerier;
	}

}

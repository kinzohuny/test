package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.impl.TransientContainer.TransientProvider;

/**
 * 通过代理接口提供开发使用的数据库资源的访问器
 * 
 * <p>
 * 该对象会使用数据库资源,在对象被销毁前必须释放使用的资源.
 * 
 * <p>
 * TProxy为提供用户使用的Holder的接口,组合了真正使用(占有)数据库资源的Holder,并通过Holder来提供接口的实现.
 * 当TProxy对象不再为强可及对象时,即表示其组合Holder可以被释放了.
 * 
 * 
 * @author houchunlei
 * 
 * @param <TProxy>
 *            提供用户使用的Holder的接口
 */
abstract class StatementHolder<TProxy extends TransientProxy<?>> extends
		TransientProvider implements ActiveChangable {

	final ContextImpl<?, ?, ?> context;

	final DBAdapterImpl adapter;

	@Override
	final Object getOwner() {
		return this.adapter;
	}

	StatementHolder(ContextImpl<?, ?, ?> context, TProxy referent) {
		super(referent, context.getDBAdapter().transaction.getTransientContainer(), context.getDepth());
		this.context = context;
		this.adapter = context.getDBAdapter();
	}

	/**
	 * 当前容器所拥有的语句数量
	 */
	private int statements;

	public final void activeChanged(boolean active) {
		if (active) {
			if (this.statements++ == 0) {
				this.transientActive();
			}
		} else {
			if (--this.statements == 0) {
				this.transientInactive();
			}
		}
	}

	@Override
	protected abstract void unuse();

	static final void setArgumentValues(Object argValueObj,
			IStatement statement, Object... argValues) {
		final ArrayList<StructFieldDefineImpl> args = statement.getArguments();
		for (int i = 0, c = Math.min(args.size(), argValues.length); i < c; i++) {
			args.get(i).setFieldValueAsObject(argValueObj, argValues[i]);
		}
	}

	// static abstract class HoldedExecutor<TSql extends Sql> extends
	// PsExecutor<TSql> {
	//
	// final PsHolder<?> holder;
	//
	// HoldedExecutor<?> next;
	//
	// HoldedExecutor(PsHolder<?> holder, TSql sql) {
	// super(holder.adapter, sql);
	// this.holder = holder;
	// }
	//
	// @Override
	// protected void activeChanged(boolean active) {
	// this.holder.activeChanged(active);
	// }
	//
	// }
	//
	// static final class StatementExecutor extends HoldedExecutor<Sql> {
	//
	// StatementExecutor(PsHolder<?> holder, IStatement statement) {
	// super(holder, statement.getSql(holder.adapter));
	// }
	//
	// }
	//
	// static final class TopQuerier extends HoldedExecutor<QueryTopSql> {
	//
	// TopQuerier(PsHolder<?> holder, QueryStatementBase query) {
	// super(holder, query.getQueryTopSql(holder.adapter));
	// }
	//
	// final ResultSet executeQuery(Object argsObj, long limit) {
	// try {
	// super.use(false);
	// this.flushParameters(argsObj);
	// this.sql.top.setLong(this.ps, limit);
	// return this.adapter.jdbcQuery(this);
	// } catch (SQLException e) {
	// throw Utils.tryThrowException(e);
	// }
	// }
	//
	// }
	//
	// static final class LimitQuerier extends HoldedExecutor<QueryLimitSql> {
	//
	// LimitQuerier(PsHolder<?> holder, QueryStatementBase query) {
	// super(holder, query.getQueryLimitSql(holder.adapter));
	// }
	//
	// final ResultSet executeQuery(Object argsObj, long limit, long offset) {
	// try {
	// super.use(false);
	// this.flushParameters(argsObj);
	// this.sql.limit.setLong(this.ps, limit);
	// this.sql.offset.setLong(this.ps, offset);
	// return this.adapter.jdbcQuery(this);
	// } catch (SQLException e) {
	// throw Utils.tryThrowException(e);
	// }
	// }
	//
	// }
	//
	// static final class RowCountQuerier extends
	// HoldedExecutor<QueryRowCountSql> {
	//
	// RowCountQuerier(PsHolder<?> holder, QueryStatementBase statement) {
	// super(holder, statement.getQueryRowCountSql(holder.adapter));
	// }
	//
	// }

}

package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.impl.TransientContainer.TransientProvider;

/**
 * ͨ������ӿ��ṩ����ʹ�õ����ݿ���Դ�ķ�����
 * 
 * <p>
 * �ö����ʹ�����ݿ���Դ,�ڶ�������ǰ�����ͷ�ʹ�õ���Դ.
 * 
 * <p>
 * TProxyΪ�ṩ�û�ʹ�õ�Holder�Ľӿ�,���������ʹ��(ռ��)���ݿ���Դ��Holder,��ͨ��Holder���ṩ�ӿڵ�ʵ��.
 * ��TProxy������Ϊǿ�ɼ�����ʱ,����ʾ�����Holder���Ա��ͷ���.
 * 
 * 
 * @author houchunlei
 * 
 * @param <TProxy>
 *            �ṩ�û�ʹ�õ�Holder�Ľӿ�
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
	 * ��ǰ������ӵ�е��������
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

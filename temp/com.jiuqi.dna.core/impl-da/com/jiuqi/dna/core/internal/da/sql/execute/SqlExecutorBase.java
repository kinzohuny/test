package com.jiuqi.dna.core.internal.da.sql.execute;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.internal.da.sql.render.ESql;

public abstract class SqlExecutorBase<TSql extends ESql> implements SqlExecutor {

	public final DBAdapterImpl adapter;

	public final TSql sql;

	public final ActiveChangable notify;

	public SqlExecutorBase(DBAdapterImpl adapter, TSql sql,
			ActiveChangable notify) {
		if (adapter == null) {
			throw new NullPointerException("数据库适配器为空");
		}
		if (sql == null) {
			throw new NullPointerException("sql语句为空.");
		}
		this.adapter = adapter;
		this.sql = sql;
		this.notify = notify;
	}

	/**
	 * 当前包装类状态改变通知
	 * 
	 * @param active
	 *            true表示申请数据库资源,false表示释放数据库资源
	 */
	protected void activeChanged(boolean active) {
		if (this.notify != null) {
			this.notify.activeChanged(active);
		}
	}
}
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
			throw new NullPointerException("���ݿ�������Ϊ��");
		}
		if (sql == null) {
			throw new NullPointerException("sql���Ϊ��.");
		}
		this.adapter = adapter;
		this.sql = sql;
		this.notify = notify;
	}

	/**
	 * ��ǰ��װ��״̬�ı�֪ͨ
	 * 
	 * @param active
	 *            true��ʾ�������ݿ���Դ,false��ʾ�ͷ����ݿ���Դ
	 */
	protected void activeChanged(boolean active) {
		if (this.notify != null) {
			this.notify.activeChanged(active);
		}
	}
}
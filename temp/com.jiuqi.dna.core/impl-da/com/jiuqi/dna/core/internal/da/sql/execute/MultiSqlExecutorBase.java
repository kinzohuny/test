package com.jiuqi.dna.core.internal.da.sql.execute;

import java.sql.SQLException;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.internal.da.sql.render.ESql;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;

abstract class MultiSqlExecutorBase<TSql extends ESql> extends
		SqlExecutorBase<TSql> {

	PreparedStatementWrap[] pss;

	MultiSqlExecutorBase(DBAdapterImpl adapter, TSql sql, ActiveChangable notify) {
		super(adapter, sql, notify);
	}

	public void use(boolean forUpdate) throws SQLException {
		if (this.pss == null) {
			this.prepare();
			this.activeChanged(true);
		}
		this.adapter.updateTrans(forUpdate);
	}

	abstract void prepare() throws SQLException;

	public void unuse() {
		if (this.pss != null) {
			PreparedStatementWrap[] pss = this.pss;
			this.pss = null;
			for (int i = 0; i < pss.length; i++) {
				PreparedStatementWrap ps = pss[i];
				pss[i] = null;
				this.adapter.freeStatement(ps);
			}
			this.activeChanged(false);
		}
	}
}

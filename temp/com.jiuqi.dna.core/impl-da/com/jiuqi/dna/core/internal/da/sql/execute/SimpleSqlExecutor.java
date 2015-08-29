package com.jiuqi.dna.core.internal.da.sql.execute;

import java.sql.SQLException;
import java.util.ArrayList;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DynObj;
import com.jiuqi.dna.core.impl.ParameterSetter;
import com.jiuqi.dna.core.internal.da.sql.render.SimpleSql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;

public abstract class SimpleSqlExecutor<TSql extends SimpleSql<TSql, TExecutor>, TExecutor extends SqlExecutor>
		extends SqlExecutorBase<TSql> {

	public void use(boolean forUpdate) throws SQLException {
		if (this.pstmt == null) {
			this.pstmt = this.adapter.prepareStatement(this.sql.text());
			this.activeChanged(true);
		}
		this.adapter.updateTrans(forUpdate);
	}

	public void unuse() {
		if (this.pstmt != null) {
			final PreparedStatementWrap ps = this.pstmt;
			this.pstmt = null;
			this.adapter.freeStatement(ps);
			this.activeChanged(false);
		}
	}

	final ParameterSetter setter = new ParameterSetter();

	public PreparedStatementWrap pstmt;

	public SimpleSqlExecutor(DBAdapterImpl adapter, TSql sql,
			ActiveChangable notify) {
		super(adapter, sql, notify);
	}

	public final void flushParameters(Object argValueObj,
			ArrayList<ParameterPlaceholder> parameters) throws SQLException {
		if (this.pstmt == null) {
			throw new NullPointerException();
		}
		if (argValueObj instanceof DynObj) {
			this.setter.flushArgumentValues(this.pstmt, parameters, (DynObj) argValueObj);
		} else {
			this.setter.flushEntityValues(this.pstmt, parameters, argValueObj);
		}
	}

	public final void flushParameters(Object argValueObj) throws SQLException {
		this.flushParameters(argValueObj, this.sql.parameters);
	}
}
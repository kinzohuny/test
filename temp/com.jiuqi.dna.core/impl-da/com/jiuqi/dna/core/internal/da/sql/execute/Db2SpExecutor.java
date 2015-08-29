package com.jiuqi.dna.core.internal.da.sql.execute;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.BytesConstExpr;
import com.jiuqi.dna.core.impl.ConstExpr;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.internal.da.sql.render.SpCallSql;

public final class Db2SpExecutor extends SpExecutor {

	public Db2SpExecutor(DBAdapterImpl adapter, SpCallSql sql,
			ActiveChangable notify) {
		super(adapter, sql, notify);
	}

	@Override
	public final RecordSet[] executeProcedure(Object argValueObj) {
		return this.loadUsingJdbcMoreResultSet(argValueObj);
	}

	@Override
	protected final ConstExpr constOf(ResultSetMetaData rsmd, int j)
			throws SQLException {
		final String className = rsmd.getColumnClassName(j);
		ConstExpr c = defaults.get(className);
		if (c != null) {
			return c;
		} else {
			if (className.equals("byte[]")) {
				return BytesConstExpr.EMPTY;
			}
			return null;
		}
	}
}
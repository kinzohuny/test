package com.jiuqi.dna.core.internal.da.sql.execute;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.BytesConstExpr;
import com.jiuqi.dna.core.impl.ConstExpr;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.internal.da.sql.render.SpCallSql;

public final class DMSpExecutor extends SpExecutor {

	public DMSpExecutor(DBAdapterImpl adapter, SpCallSql sql,
			ActiveChangable notify) {
		super(adapter, sql, notify);
	}

	@Override
	public final RecordSet[] executeProcedure(Object argValueObj) {
		return this.loadUsingJdbcMoreResultSet(argValueObj);
	}

	@Override
	final ConstExpr constOf(ResultSetMetaData rsmd, int j) throws SQLException {
		final String className = rsmd.getColumnClassName(j);
		final ConstExpr c = defaults.get(className);
		if (c != null) {
			return c;
		} else {
			final int type = rsmd.getColumnType(j);
			if (type == Types.BINARY || type == Types.VARBINARY
					|| type == Types.LONGVARBINARY) {
				return BytesConstExpr.EMPTY;
			}
			return null;
		}
	}

}

package com.jiuqi.dna.core.internal.da.sql.execute;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sql.render.QuerySql;

public final class Querier extends SimpleSqlExecutor<QuerySql, Querier> {

	public Querier(DBAdapterImpl adapter, QuerySql sql, ActiveChangable notify) {
		super(adapter, sql, notify);
	}

	public final ResultSet query(Object argValueObj) {
		try {
			this.use(false);
			this.flushParameters(argValueObj);
			return this.pstmt.executeQuery();
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final long longScalar(Object argValueObj) {
		try {
			ResultSet rs = this.query(argValueObj);
			try {
				if (rs.next()) {
					return rs.getLong(1);
				}
			} finally {
				rs.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
		return 0L;
	}
}
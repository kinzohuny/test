package com.jiuqi.dna.core.internal.da.sql.execute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sql.render.QueryRowCountSql;

public final class RowCountQuerier extends
		SimpleSqlExecutor<QueryRowCountSql, RowCountQuerier> {

	public RowCountQuerier(DBAdapterImpl adapter, QueryRowCountSql sql,
			ActiveChangable notify) {
		super(adapter, sql, notify);
	}

	public final long executeLongScalar(ArrayList<Object> paramValues) {
		try {
			for (int i = 0, parameterIndex = 1, c = paramValues.size(); i < c; i++) {
				this.pstmt.setObject(parameterIndex++, paramValues.get(i));
			}
			ResultSet rs = this.pstmt.executeQuery();
			try {
				if (rs.next()) {
					return rs.getLong(1);
				}
				return 0;
			} finally {
				rs.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
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
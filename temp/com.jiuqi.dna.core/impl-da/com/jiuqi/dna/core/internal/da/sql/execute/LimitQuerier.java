package com.jiuqi.dna.core.internal.da.sql.execute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sql.render.QueryLimitSql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;

public final class LimitQuerier extends
		SimpleSqlExecutor<QueryLimitSql, LimitQuerier> {

	public LimitQuerier(DBAdapterImpl adapter, QueryLimitSql sql,
			ActiveChangable notify) {
		super(adapter, sql, notify);
	}

	public final ResultSet query(Object argValueObj, long limit, long offset) {
		try {
			if (this.adapter.dbMetadata.product() == DbProduct.SQLServer) {
				if (SqlserverMetadata.beforeYukon(this.adapter.dbMetadata)) {
					this.unuse();
					final long rc;
					final RowCountQuerier rcq = this.sql.query.getQueryRowCountSql(this.adapter).newExecutor(this.adapter, null);
					try {
						rc = rcq.longScalar(argValueObj);
					} finally {
						rcq.unuse();
					}
					final SqlReplacer replacer = new SqlReplacer(this.sql);
					if (limit + offset <= rc) {
						replacer.replaceLong(this.sql.limit, limit);
						replacer.replaceLong(this.sql.offset, limit + offset);
					} else {
						replacer.replaceLong(this.sql.limit, rc - offset);
						replacer.replaceLong(this.sql.offset, rc);
					}
					this.pstmt = this.adapter.prepareStatement(replacer.sqltext());
					this.activeChanged(true);
					this.adapter.updateTrans(false);
					this.flushParameters(argValueObj, replacer.paramters);
					return this.pstmt.executeQuery();
				} else {
					// offset是lowValue=offset，limit是highValue=limit+offset
					this.unuse();
					final SqlReplacer replacer = new SqlReplacer(this.sql);
					replacer.replaceLong(this.sql.limit, limit + offset);
					replacer.replaceLong(this.sql.offset, offset);
					this.pstmt = this.adapter.prepareStatement(replacer.sqltext());
					this.activeChanged(true);
					this.adapter.updateTrans(false);
					this.flushParameters(argValueObj, replacer.paramters);
					return this.pstmt.executeQuery();
				}
			} else {
				super.use(false);
				this.flushParameters(argValueObj);
				this.setLimitAndOffset(limit, offset);
				return this.pstmt.executeQuery();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final ResultSet query(ArrayList<Object> paramValues, long limit,
			long offset) {
		try {
			// HCL 有问题
			int parameterIndex = 1;
			for (int i = 0, c = paramValues.size(); i < c; i++) {
				this.pstmt.setObject(parameterIndex++, paramValues.get(i));
			}
			this.setLimitAndOffset(limit, offset);
			return this.pstmt.executeQuery();
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final void setLimitAndOffset(long limit, long offset)
			throws SQLException {
		for (int i = 0; i < this.sql.parameters.size(); i++) {
			ParameterPlaceholder pp = this.sql.parameters.get(i);
			if (pp == this.sql.limit) {
				this.pstmt.setLong(i + 1, limit);
			} else if (pp == this.sql.offset) {
				this.pstmt.setLong(i + 1, offset);
			}
		}
	}
}
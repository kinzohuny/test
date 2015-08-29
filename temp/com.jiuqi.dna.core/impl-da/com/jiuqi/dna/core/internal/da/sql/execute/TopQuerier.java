package com.jiuqi.dna.core.internal.da.sql.execute;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sql.render.QueryTopSql;

public final class TopQuerier extends
		SimpleSqlExecutor<QueryTopSql, TopQuerier> {

	public TopQuerier(DBAdapterImpl adapter, QueryTopSql sql,
			ActiveChangable notify) {
		super(adapter, sql, notify);
	}

	public final ResultSet query(Object argValueObj, long limit) {
		try {
			if (this.adapter.dbMetadata.product() == DbProduct.SQLServer) {
				this.unuse();
				SqlReplacer replacer = new SqlReplacer(this.sql);
				replacer.replaceLong(this.sql.top, limit);
				this.pstmt = this.adapter.prepareStatement(replacer.sqltext());
				this.activeChanged(true);
				this.adapter.updateTrans(false);
				this.flushParameters(argValueObj, replacer.paramters);
				return this.pstmt.executeQuery();
			} else {
				super.use(false);
				this.flushParameters(argValueObj);
				this.sql.top.setLong(this.pstmt, this.sql.parameters, limit);
				return this.pstmt.executeQuery();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}
}
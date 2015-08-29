package com.jiuqi.dna.core.internal.da.sql.execute;

import java.sql.SQLException;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DynObj;
import com.jiuqi.dna.core.impl.Utils;

public class SimpleSqlModifier extends
		SimpleSqlExecutor<SimpleModifySql, SimpleSqlModifier> implements
		SqlModifier {

	public SimpleSqlModifier(DBAdapterImpl adapter, SimpleModifySql sql,
			ActiveChangable notify) {
		super(adapter, sql, notify);
	}

	public final int update(Object argValueObj) {
		try {
			this.use(true);
			this.flushParameters(argValueObj);
			return this.pstmt.executeUpdate();
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final int update(DynObj argObj1, DynObj argObj2) {
		try {
			this.use(true);
			this.setter.flushParameters(this.pstmt, this.sql.parameters, argObj1, argObj2);
			return this.pstmt.executeUpdate();
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final boolean updateRow(Object argValueObj) {
		return this.update(argValueObj) > 0;
	}
}
package com.jiuqi.dna.core.internal.da.sql.execute;

import java.sql.SQLException;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DynObj;
import com.jiuqi.dna.core.impl.ParameterSetter;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sql.render.MultiSql;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;

public class KingbaseMultipleSqlModifier extends MultiSqlExecutorBase<MultiSql>
		implements SqlModifier {

	public KingbaseMultipleSqlModifier(DBAdapterImpl adapter, MultiSql sql,
			ActiveChangable notify) {
		super(adapter, sql, notify);
		if (sql.std == null) {
			throw new IllegalArgumentException("»ù×¼sqlÎª¿Õ.");
		}
	}

	final ParameterSetter setter = new ParameterSetter();

	public int update(Object argValueObj) {
		try {
			this.use(true);
			int updateCount = 0;
			for (int i = 0, c = this.sql.sqls.size(); i < c; i++) {
				this.flushParameters(argValueObj, i);
				if (this.sql.sqls.get(i) == this.sql.std) {
					updateCount = this.pss[i].executeUpdate();
				} else {
					this.pss[i].executeUpdate();
				}
			}
			return updateCount;
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	final void flushParameters(Object argValueObj, int i) throws SQLException {
		if (argValueObj instanceof DynObj) {
			this.setter.flushArgumentValues(this.pss[i], this.sql.sqls.get(i).parameters, (DynObj) argValueObj);
		} else {
			this.setter.flushEntityValues(this.pss[i], this.sql.sqls.get(i).parameters, argValueObj);
		}
	}

	public final int update(DynObj argObj1, DynObj argObj2) {
		throw new UnsupportedOperationException();
	}

	public final boolean updateRow(Object argValueObj) {
		return this.update(argValueObj) > 0;
	}

	@Override
	final void prepare() throws SQLException {
		final int c = this.sql.sqls.size();
		this.pss = new PreparedStatementWrap[c];
		for (int i = 0; i < c; i++) {
			this.pss[i] = this.adapter.prepareStatement(this.sql.sqls.get(i).text());
		}
	}
}

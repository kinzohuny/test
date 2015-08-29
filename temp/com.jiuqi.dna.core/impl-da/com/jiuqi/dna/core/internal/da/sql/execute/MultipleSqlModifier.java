package com.jiuqi.dna.core.internal.da.sql.execute;

import java.sql.SQLException;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DynObj;
import com.jiuqi.dna.core.impl.ParameterSetter;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sql.render.MultipleSql;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;

public class MultipleSqlModifier extends SqlExecutorBase<MultipleSql> implements
		SqlModifier {

	public MultipleSqlModifier(DBAdapterImpl adapter, MultipleSql sql,
			ActiveChangable notify) {
		super(adapter, sql, notify);
	}

	private final ParameterSetter setter = new ParameterSetter();

	private PreparedStatementWrap pstmt;
	private PreparedStatementWrap[] others;

	public void use(boolean forUpdate) throws SQLException {
		if (this.pstmt == null) {
			this.pstmt = this.adapter.prepareStatement(this.sql.text());
			int c = this.sql.others.size();
			this.others = new PreparedStatementWrap[c];
			for (int i = 0; i < c; i++) {
				this.others[i] = this.adapter.prepareStatement(this.sql.others.get(i).text());
			}
			this.activeChanged(true);
		}
		this.adapter.updateTrans(forUpdate);
	}

	public void unuse() {
		if (this.pstmt != null) {
			final PreparedStatementWrap pstmt = this.pstmt;
			final PreparedStatementWrap[] others = this.others;
			this.pstmt = null;
			this.others = null;
			this.adapter.freeStatement(pstmt);
			for (PreparedStatementWrap o : others) {
				this.adapter.freeStatement(o);
			}
			this.activeChanged(false);
		}
	}

	public int update(Object argValueObj) {
		try {
			int r = 0;
			this.use(true);
			this.setter.flushArgumentValues(this.pstmt, this.sql.parameters, (DynObj) argValueObj);
			r = this.pstmt.executeUpdate();
			if (r > 0) {
				for (int i = 0; i < this.others.length; i++) {
					this.setter.flushArgumentValues(this.others[i], this.sql.others.get(i).parameters, (DynObj) argValueObj);
					this.others[i].executeUpdate();
				}
			}
			return r;
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public boolean updateRow(Object argValueObj) {
		return this.update(argValueObj) > 0;
	}

	public int update(DynObj argObj1, DynObj argObj2) {
		return this.update(argObj1);
	}
}
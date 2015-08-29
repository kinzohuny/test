package com.jiuqi.dna.core.internal.da.report;

import java.sql.SQLException;
import java.util.ArrayList;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DynObj;
import com.jiuqi.dna.core.impl.ParameterSetter;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlExecutorBase;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlModifier;
import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;

final class MysqlRptRecordSaver extends SqlExecutorBase<MysqlRptRecordSaveSql>
		implements SqlModifier {

	public MysqlRptRecordSaver(DBAdapterImpl adapter,
			MysqlRptRecordSaveSql sql, ActiveChangable notify) {
		super(adapter, sql, notify);
	}

	public final void use(boolean forUpdate) throws SQLException {
		if (this.updatePrimary == null) {
			this.updatePrimary = this.adapter.prepareStatement(this.sql.text());
		}
		this.adapter.updateTrans(forUpdate);
		if (this.insertPrimary == null) {
			this.insertPrimary = this.adapter.prepareStatement(this.sql.insertPrimary.text());
		}
		if (this.updateSlaves == null) {
			this.updateSlaves = new ArrayList<PreparedStatementWrap>();
			for (int i = 0; i < this.sql.updateSlaves.size(); i++) {
				this.updateSlaves.add(this.adapter.prepareStatement(this.sql.updateSlaves.get(i).text()));
			}
		}
		if (this.insertSlaves == null) {
			this.insertSlaves = new ArrayList<PreparedStatementWrap>();
			for (int i = 0; i < this.sql.insertSlaves.size(); i++) {
				this.insertSlaves.add(this.adapter.prepareStatement(this.sql.insertSlaves.get(i).text()));
			}
		}
		this.activeChanged(true);
		this.adapter.updateTrans(true);
	}

	public final void unuse() {
		if (this.updatePrimary != null) {
			PreparedStatementWrap updatePrimary = this.updatePrimary;
			this.updatePrimary = null;
			this.adapter.freeStatement(updatePrimary);
		}
		if (this.updateSlaves != null) {
			ArrayList<PreparedStatementWrap> updateSlaves = this.updateSlaves;
			this.updateSlaves = null;
			for (PreparedStatementWrap psw : updateSlaves) {
				this.adapter.freeStatement(psw);
			}
		}
		if (this.insertSlaves != null) {
			ArrayList<PreparedStatementWrap> insertSlaves = this.insertSlaves;
			this.insertSlaves = null;
			for (PreparedStatementWrap psw : insertSlaves) {
				this.adapter.freeStatement(psw);
			}
		}
		this.activeChanged(false);
	}

	PreparedStatementWrap updatePrimary;
	ArrayList<PreparedStatementWrap> updateSlaves;
	PreparedStatementWrap insertPrimary;
	ArrayList<PreparedStatementWrap> insertSlaves;

	public int update(Object argValueObj) {
		throw new UnsupportedOperationException();
	}

	public int update(DynObj argObj1, DynObj argObj2) {
		throw new UnsupportedOperationException();
	}

	private final ParameterSetter setter = new ParameterSetter();

	public boolean updateRow(Object argValueObj) {
		try {
			DynObj obj = (DynObj) argValueObj;
			this.use(true);
			this.setter.flushArgumentValues(this.updatePrimary, this.sql.parameters, obj);
			if (this.updatePrimary.executeUpdate() == 1) {
				for (int i = 0; i < this.updateSlaves.size(); i++) {
					PreparedStatementWrap psw = this.updateSlaves.get(i);
					this.setter.flushArgumentValues(psw, this.sql.updateSlaves.get(i).parameters, obj);
					psw.executeUpdate();
				}
			} else {
				this.setter.flushArgumentValues(this.insertPrimary, this.sql.insertPrimary.parameters, obj);
				this.insertPrimary.executeUpdate();
				for (int i = 0; i < this.insertSlaves.size(); i++) {
					PreparedStatementWrap psw = this.insertSlaves.get(i);
					this.setter.flushArgumentValues(psw, this.sql.insertSlaves.get(i).parameters, obj);
					psw.executeUpdate();
				}
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
		return false;
	}
}
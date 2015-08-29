package com.jiuqi.dna.core.internal.da.report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.PlainSql;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.render.SimpleSql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class MysqlRptRecordSaveSql extends
		SimpleSql<MysqlRptRecordSaveSql, MysqlRptRecordSaver> implements
		RptRecordSaveSql {

	private static final String _T = "T";

	MysqlRptRecordSaveSql(DbMetadata metadata, RPTRecordSetTableInfo tableInfo) {
		this.insertPrimary = new PlainSql();
		final ISqlCommandFactory factory = metadata.sqlbuffers();
		final LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields = new LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl>();
		for (int i = 0; i < tableInfo.table.dbTables.size(); i++) {
			DBTableDefineImpl dbTable = tableInfo.table.dbTables.get(i);
			RPTRecordSetDBTableInfo dbTableInfo = tableInfo.find(dbTable);
			if (dbTable.isPrimary()) {
				if (dbTableInfo == null) {
					this.build(updateEmptyPrimary(factory, tableInfo));
					this.insertPrimary.build(insertEmptyPrimary(factory, tableInfo));
				} else {
					this.build(updatePrimary(factory, tableInfo, dbTableInfo, fields));
					this.insertPrimary.build(insertPrimary(factory, tableInfo, dbTableInfo, fields));
				}
			} else {
				if (dbTableInfo == null) {
					// insert empty slave
					ISqlInsertBuffer insert = factory.insert(dbTable.namedb());
					SimpleRptRecordSaveSql.insertEmptySlave(insert, tableInfo);
					PlainSql s = new PlainSql();
					s.build(insert);
					this.insertSlaves.add(s);
				} else {
					// update slave
					ISqlUpdateBuffer update = factory.update(dbTable.namedb(), _T, false);
					SimpleRptRecordSaveSql.fillUpdateFieldReturnWhetherUpdateKey(dbTableInfo, fields);
					SimpleRptRecordSaveSql.updateNormalFields(update, fields);
					SimpleRptRecordSaveSql.updateSlaveBuildWhere(update, tableInfo, fields);
					PlainSql s1 = new PlainSql();
					s1.build(update);
					this.updateSlaves.add(s1);
					// insert slave
					ISqlInsertBuffer insert = factory.insert(dbTable.namedb());
					SimpleRptRecordSaveSql.insertRecidAndRecver(insert, tableInfo, dbTableInfo);
					SimpleRptRecordSaveSql.fillUpdateFieldReturnWhetherUpdateKey(dbTableInfo, fields);
					SimpleRptRecordSaveSql.insertNormalFields(insert, fields);
					PlainSql s = new PlainSql();
					s.build(insert);
					this.insertSlaves.add(s);
				}
			}
		}
	}

	private static final ISqlUpdateBuffer updateEmptyPrimary(
			ISqlCommandFactory factory, RPTRecordSetTableInfo tableInfo) {
		ISqlUpdateBuffer update = factory.update(tableInfo.table.primary.namedb(), _T, false);
		SimpleRptRecordSaveSql.buildKeyCondition(update.where(), _T, tableInfo);
		String recver = tableInfo.table.f_RECVER().namedb();
		update.newValue(recver).loadColumnRef(null, recver);
		return update;
	}

	private static final ISqlUpdateBuffer updatePrimary(
			ISqlCommandFactory factory, RPTRecordSetTableInfo tableInfo,
			RPTRecordSetDBTableInfo dbTableInfo,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		ISqlUpdateBuffer update = factory.update(tableInfo.table.primary.namedb(), _T, false);
		SimpleRptRecordSaveSql.buildKeyCondition(update.where(), _T, tableInfo);
		SimpleRptRecordSaveSql.fillUpdateFieldReturnWhetherUpdateKey(dbTableInfo, fields);
		for (Entry<TableFieldDefineImpl, RPTRecordSetFieldImpl> e : fields.entrySet()) {
			update.newValue(e.getKey().namedb()).loadParam(SimpleRptRecordSaveSql.paramOf(e.getValue()));
		}
		return update;
	}

	private static final ISqlInsertBuffer insertEmptyPrimary(
			ISqlCommandFactory factory, RPTRecordSetTableInfo tableInfo) {
		ISqlInsertBuffer insert = factory.insert(tableInfo.table.primary.namedb());
		if (!SimpleRptRecordSaveSql.recidIsKey(tableInfo)) {
			TableFieldDefineImpl recid = tableInfo.table.f_recid;
			insert.newField(recid.namedb());
			insert.newValue().loadParam(arg(tableInfo.recidSf, recid.getType()));
		}
		insert.newField(tableInfo.table.f_recver.namedb());
		insert.newValue().loadParam(arg(tableInfo.recverSf, tableInfo.table.f_recver.getType()));
		SimpleRptRecordSaveSql.insertLKeyFields(insert, tableInfo);
		return insert;
	}

	private static final ISqlInsertBuffer insertPrimary(
			ISqlCommandFactory factory, RPTRecordSetTableInfo tableInfo,
			RPTRecordSetDBTableInfo dbTableInfo,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		ISqlInsertBuffer insert = factory.insert(dbTableInfo.dbTable.namedb());
		SimpleRptRecordSaveSql.insertRecidAndRecver(insert, tableInfo, dbTableInfo);
		SimpleRptRecordSaveSql.insertLKeyFields(insert, tableInfo);
		SimpleRptRecordSaveSql.fillUpdateFieldReturnWhetherUpdateKey(dbTableInfo, fields);
		SimpleRptRecordSaveSql.removeRecverAndKeys(tableInfo, fields);
		SimpleRptRecordSaveSql.insertNormalFields(insert, fields);
		return insert;
	}

	PlainSql insertPrimary = new PlainSql();
	ArrayList<PlainSql> updateSlaves = new ArrayList<PlainSql>();
	ArrayList<PlainSql> insertSlaves = new ArrayList<PlainSql>();

	@Override
	public MysqlRptRecordSaver newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new MysqlRptRecordSaver(adapter, this, null);
	}
}
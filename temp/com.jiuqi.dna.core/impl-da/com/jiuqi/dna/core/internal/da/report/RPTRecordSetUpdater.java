package com.jiuqi.dna.core.internal.da.report;

import java.util.ArrayList;

import com.jiuqi.dna.core.da.DBAdapter;
import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DynObj;
import com.jiuqi.dna.core.internal.da.report.RPTRecordSetRecordDefine.RPTRecord;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlExecutor;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlModifier;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

final class RPTRecordSetUpdater {

	final DBAdapter context;
	final DBAdapterImpl adapter;
	final RPTRecordSetImpl recordSet;

	private final RPTRecordSetTableInfo[] tables;
	private SqlModifier[] savers;
	private SqlModifier[] deleters;

	RPTRecordSetUpdater(DBAdapter context, RPTRecordSetImpl recordSet)
			throws Throwable {
		this.context = context;
		this.adapter = DBAdapterImpl.toDBAdapter(context);
		this.recordSet = recordSet;
		ArrayList<RPTRecordSetTableInfo> tables = new ArrayList<RPTRecordSetTableInfo>();
		for (RPTRecordSetRestrictionImpl r : recordSet.restrictions) {
			tables.addAll(r.tables);
		}
		this.tables = tables.toArray(new RPTRecordSetTableInfo[tables.size()]);
	}

	final void update(RPTRecordSetRestrictionImpl rstr, RPTRecord record) {
		switch (record.getRecordState()) {
		case DynObj.r_new_modified:
		case DynObj.r_new:
		case DynObj.r_db_modifing:
			if (this.savers == null) {
				ArrayList<SqlExecutor> list = new ArrayList<SqlExecutor>();
				for (RPTRecordSetTableInfo tableInfo : rstr.tables) {
					list.add(saveSqlFor(this.adapter.dbMetadata, tableInfo).newExecutor(this.adapter, null));
				}
				this.savers = list.toArray(new SqlModifier[list.size()]);
			}
			for (int i = 0, c = this.savers.length; i < c; i++) {
				RPTRecordSetTableInfo table = rstr.tables.get(i);
				if ((record.mask & (1 << table.restriction.index)) == 0) {
					continue;
				}
				aTable: {
					for (RPTRecordSetKeyImpl key : table.keys) {
						if (!table.restriction.tryUpdateKeyFieldValueIfNull(record, key.index)) {
							break aTable;
						}
					}
					if (table.recidSf.isFieldValueNull(record)) {
						table.recidSf.setFieldValueAsGUID(record, this.context.newRECID());
					}
					table.recverSf.setFieldValueAsLong(record, this.context.newRECVER());
					this.savers[i].updateRow(record);
				}
			}
			break;
		case DynObj.r_db_deleting:
			if (this.deleters == null) {
				ArrayList<SqlExecutor> list = new ArrayList<SqlExecutor>();
				for (RPTRecordSetTableInfo tableInfo : rstr.tables) {
					list.add(new RPTRecordDeleteSql(this.adapter.dbMetadata, tableInfo).newExecutor(this.adapter, null));
				}
				this.deleters = list.toArray(new SqlModifier[list.size()]);
			}
			for (int i = 0, c = this.deleters.length; i < c; i++) {
				RPTRecordSetTableInfo table = rstr.tables.get(i);
				if (table.recidSf.isFieldValueNull(record)) {
					return;
				}
				this.deleters[i].updateRow(record);
			}
			break;
		}
	}

	final void update(RPTRecord record) {
		switch (record.getRecordState()) {
		case DynObj.r_new_modified:
		case DynObj.r_new:
		case DynObj.r_db_modifing:
			// if (this.savers == null) {
			// ArrayList<SqlExecutor> list = new ArrayList<SqlExecutor>();
			// for (RPTRecordSetTableInfo tableInfo : this.tables) {
			// list.add(saveSqlFor(this.adapter.dbMetadata, tableInfo)
			// .newExecutor(this.adapter, null));
			// }
			// this.savers = list.toArray(new SqlModifier[list.size()]);
			// }
			for (int i = 0, c = this.tables.length; i < c; i++) {
				RPTRecordSetTableInfo table = this.tables[i];
				if ((record.mask & (1 << table.restriction.index)) == 0) {
					continue;
				}
				aTable: {
					for (RPTRecordSetKeyImpl key : table.keys) {
						if (!table.restriction.tryUpdateKeyFieldValueIfNull(record, key.index)) {
							break aTable;
						}
					}
					if (table.recidSf.isFieldValueNull(record)) {
						table.recidSf.setFieldValueAsGUID(record, this.context.newRECID());
					}
					table.recverSf.setFieldValueAsLong(record, this.context.newRECVER());
					SqlModifier saver = saveSqlFor(this.adapter.dbMetadata, table).newExecutor(this.adapter, null);
					try {
						saver.updateRow(record);
					} finally {
						saver.unuse();
					}
					// this.savers[i].updateRow(record);
				}
			}
			record.setRecordState(DynObj.r_db);
			break;
		case DynObj.r_db_deleting:
			// if (this.deleters == null) {
			// ArrayList<SqlExecutor> list = new ArrayList<SqlExecutor>();
			// for (RPTRecordSetTableInfo tableInfo : this.tables) {
			// list.add(new RPTRecordDeleteSql(this.adapter.dbMetadata,
			// tableInfo).newExecutor(this.adapter, null));
			// }
			// this.deleters = list.toArray(new SqlModifier[list.size()]);
			// }
			for (int i = 0, c = this.tables.length; i < c; i++) {
				RPTRecordSetTableInfo table = this.tables[i];
				if (table.recidSf.isFieldValueNull(record)) {
					return;
				}
				SqlModifier deleter = new RPTRecordDeleteSql(this.adapter.dbMetadata, table).newExecutor(this.adapter, null);
				try {
					deleter.updateRow(record);
				} finally {
					deleter.unuse();
				}
				// this.deleters[i].updateRow(record);
			}
			break;
		}
	}

	final void unuse() {
		if (this.savers != null) {
			for (SqlExecutor p : this.savers) {
				p.unuse();
			}
			this.savers = null;
		}
		if (this.deleters != null) {
			for (SqlExecutor p : this.deleters) {
				p.unuse();
			}
			this.deleters = null;
		}
	}

	private static final RptRecordSaveSql saveSqlFor(DbMetadata metadata,
			RPTRecordSetTableInfo tableInfo) {
		if (metadata.product() == DbProduct.MySQL) {
			return new MysqlRptRecordSaveSql(metadata, tableInfo);
		} else {
			return new SimpleRptRecordSaveSql(metadata, tableInfo);
		}
	}
}
package com.jiuqi.dna.core.internal.da.report;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.NumericDBType;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlConditionBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlMergeBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlMergeCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlReplaceBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlReplaceCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlPredicate;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.type.TypeFactory;

final class SimpleRptRecordSaveSql extends SimpleModifySql implements
		RptRecordSaveSql {

	SimpleRptRecordSaveSql(DbMetadata dbMetadata,
			RPTRecordSetTableInfo tableInfo) {
		ISqlSegmentBuffer segment = dbMetadata.sqlbuffers().segment();
		final ISqlMergeCommandFactory mf = segment.getFeature(ISqlMergeCommandFactory.class);
		final ISqlReplaceCommandFactory rf = segment.getFeature(ISqlReplaceCommandFactory.class);
		LinkedHashMap<DBTableDefineImpl, RPTRecordSetDBTableInfo> tables = new LinkedHashMap<DBTableDefineImpl, RPTRecordSetDBTableInfo>();
		for (DBTableDefineImpl dbTable : tableInfo.table.dbTables) {
			tables.put(dbTable, null);
		}
		for (RPTRecordSetDBTableInfo dbTableInfo : tableInfo) {
			tables.put(dbTableInfo.dbTable, dbTableInfo);
		}
		if (mf != null && dbMetadata.dbMajorVer > 9) {
			this.saveUsingMerge(segment, mf, tableInfo, tables);
		} else if (rf != null) {
			this.saveUsingReplace(segment, rf, tableInfo, tables);
		} else {
			this.saveUsingUpsert(segment, tableInfo, tables);
		}
	}

	/**
	 * 构造key等于参数的表达式
	 * 
	 * @param where
	 * @param alias
	 * @param tableInfo
	 */
	static final void buildKeyCondition(ISqlExprBuffer where, String alias,
			RPTRecordSetTableInfo tableInfo) {
		final int c = tableInfo.keyFields.length;
		for (int ki = 0; ki < c; ki++) {
			// !!! important!
			StructFieldDefineImpl keySf = tableInfo.getKeyValueField(ki);
			TableFieldDefineImpl field = tableInfo.keyFields[ki];
			where.loadColumnRef(alias, field.namedb());
			where.loadParam(arg(keySf, field.getType()));
			where.eq();
		}
		where.and(c);
	}

	/**
	 * recid是否作为了连接的字段,一般是B0608_UNIT或MD_ORG表.
	 * 
	 * @param tableInfo
	 * @return
	 */
	static final boolean recidIsKey(RPTRecordSetTableInfo tableInfo) {
		for (int i = 0; i < tableInfo.keyFields.length; i++) {
			if (tableInfo.keyFields[i].isRECID()) {
				return true;
			}
		}
		return false;
	}

	static final ArgumentPlaceholder paramOf(RPTRecordSetFieldImpl field) {
		if (field.usingBigDecimal && field.tableField.getType() instanceof NumericDBType) {
			return new ArgumentPlaceholder(field.structField, TypeFactory.BIG_DECIMAL());
		}
		return new ArgumentPlaceholder(field.structField, field.tableField.getType());
	}

	/**
	 * 使用merge来保存.
	 * 
	 * @param segment
	 *            和mf是相同对象.
	 * @param mf
	 * @param tableInfo
	 * @param tables
	 */
	private final void saveUsingMerge(ISqlSegmentBuffer segment,
			ISqlMergeCommandFactory mf, RPTRecordSetTableInfo tableInfo,
			LinkedHashMap<DBTableDefineImpl, RPTRecordSetDBTableInfo> tables) {
		LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields = new LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl>();
		for (Entry<DBTableDefineImpl, RPTRecordSetDBTableInfo> entry : tables.entrySet()) {
			DBTableDefineImpl dbTable = entry.getKey();
			RPTRecordSetDBTableInfo dbTableInfo = entry.getValue();
			if (dbTableInfo == null) {
				if (dbTable.isPrimary()) {
					mergeEmptyPrimary(mf, tableInfo);
				} else {
					mergeEmptySlavery(mf, tableInfo, dbTable);
				}
			} else if (fillUpdateFieldReturnWhetherUpdateKey(dbTableInfo, fields)) {
				upsert(segment, tableInfo, dbTableInfo, fields);
			} else {
				merge(mf, tableInfo, dbTableInfo, fields);
			}
		}
		this.build(segment);
		return;
	}

	private static final String TARGET_ALIAS = "T";
	private static final String USING_ALIAS = "S";
	private static final String USING_FROM = "SF";
	private static final String USING_VAL = "SV";

	private static void mergeEmptyPrimary(ISqlMergeCommandFactory mf,
			RPTRecordSetTableInfo tableInfo) {
		ISqlMergeBuffer merge = mf.merge(tableInfo.table.primary.namedb(), TARGET_ALIAS);
		merge.usingDummy();
		buildKeyCondition(merge.onCondition(), TARGET_ALIAS, tableInfo);
		if (!recidIsKey(tableInfo)) {
			TableFieldDefineImpl recid = tableInfo.table.f_recid;
			merge.insert(recid.namedb()).loadParam(arg(tableInfo.recidSf, recid.getType()));
		}
		TableFieldDefineImpl recver = tableInfo.table.f_recver;
		merge.insert(recver.namedb()).loadParam(arg(tableInfo.recverSf, recver.getType()));
		for (int i = 0; i < tableInfo.keyFields.length; i++) {
			TableFieldDefineImpl key = tableInfo.keyFields[i];
			merge.insert(key.namedb()).loadParam(arg(tableInfo.getKeyValueField(i), key.getType()));
		}
	}

	private static void mergeEmptySlavery(ISqlMergeCommandFactory mf,
			RPTRecordSetTableInfo tableInfo, DBTableDefineImpl dbTable) {
		ISqlMergeBuffer merge = mf.merge(dbTable.namedb(), TARGET_ALIAS);
		ISqlSelectBuffer using = merge.usingSubquery(USING_ALIAS);
		DBTableDefineImpl primary = tableInfo.table.primary;
		String recid = tableInfo.table.f_recid.namedb();
		using.newTableRef(primary.namedb(), USING_FROM);
		using.newColumn(USING_VAL).loadColumnRef(USING_FROM, recid);
		buildKeyCondition(using.where(), USING_FROM, tableInfo);
		merge.onCondition().loadColumnRef(TARGET_ALIAS, recid).loadColumnRef(USING_ALIAS, USING_VAL).eq();
		merge.insert(recid).loadColumnRef(USING_ALIAS, USING_VAL);
	}

	/**
	 * 装填针对指定物理表需要更新的字段.如果有重复字段,使用第一个更新值.RECID不会被装填.
	 * 
	 * @param dbTableInfo
	 *            物理表信息
	 * @param fields
	 *            装填到
	 * @return 更新字段是否包含了键
	 */
	static final boolean fillUpdateFieldReturnWhetherUpdateKey(
			RPTRecordSetDBTableInfo dbTableInfo,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		boolean r = false;
		fields.clear();
		for (int tfIndex = 0, tfCount = dbTableInfo.size(); tfIndex < tfCount; tfIndex++) {
			RPTRecordSetFieldImpl rsf = dbTableInfo.get(tfIndex);
			// 必须先判断recid
			if (rsf.tableField.isRECID()) {
				continue;
			}
			if (rsf.tableField.isPrimaryKey()) {
				r = true;
			}
			if (!fields.containsKey(rsf.tableField)) {
				fields.put(rsf.tableField, rsf);
			}
		}
		return r;
	}

	/**
	 * 尝试update,没有更新时则insert.主表辅表都可以.
	 * 
	 * @param segment
	 * @param tableInfo
	 * @param dbTableInfo
	 * @param fields
	 *            需要更新的字段,可能包含key和recver,不会包含recid.
	 */
	private static final void upsert(ISqlSegmentBuffer segment,
			RPTRecordSetTableInfo tableInfo,
			RPTRecordSetDBTableInfo dbTableInfo,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		update(segment, tableInfo, dbTableInfo, fields);
		ISqlConditionBuffer ifs = segment.ifThenElse();
		rowCountEqZero(ifs.newWhen());
		removeRecverAndKeys(tableInfo, fields);
		insert(ifs.newThen(), tableInfo, dbTableInfo, fields);
	}

	/**
	 * 更新指定物理表的指定字段.
	 * 
	 * @param segment
	 * @param tableInfo
	 * @param dbTableInfo
	 * @param fields
	 *            更新字段列表
	 */
	private static final void update(ISqlSegmentBuffer segment,
			RPTRecordSetTableInfo tableInfo,
			RPTRecordSetDBTableInfo dbTableInfo,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		ISqlUpdateBuffer update = segment.update(dbTableInfo.dbTable.namedb(), TARGET_ALIAS, false);
		updateNormalFields(update, fields);
		if (dbTableInfo.dbTable.isPrimary()) {
			buildKeyCondition(update.where(), TARGET_ALIAS, tableInfo);
		} else {
			updateSlaveBuildWhere(update, tableInfo, fields);
		}
	}

	static final void updateSlaveBuildWhere(ISqlUpdateBuffer update,
			RPTRecordSetTableInfo tableInfo,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		final TableFieldDefineImpl recid = tableInfo.table.f_recid;
		ISqlExprBuffer where = update.where();
		where.loadColumnRef(TARGET_ALIAS, recid.namedb());
		ISqlSelectBuffer subquery = where.subquery();
		subquery.newTableRef(tableInfo.table.primary.name, USING_FROM);
		subquery.newColumn(USING_VAL).loadColumnRef(USING_FROM, recid.namedb());
		buildKeyCondition(subquery.where(), USING_FROM, tableInfo);
		where.predicate(SqlPredicate.IN, 2);
	}

	private static final void rowCountEqZero(ISqlExprBuffer expr) {
		expr.rowcount();
		expr.load(0);
		expr.eq();
	}

	private static final void insert(ISqlSegmentBuffer segment,
			RPTRecordSetTableInfo tableInfo,
			RPTRecordSetDBTableInfo dbTableInfo,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		ISqlInsertBuffer insert = segment.insert(dbTableInfo.dbTable.namedb());
		insertRecidAndRecver(insert, tableInfo, dbTableInfo);
		if (dbTableInfo.dbTable.isPrimary()) {
			insertLKeyFields(insert, tableInfo);
		}
		insertNormalFields(insert, fields);
	}

	private static final void merge(ISqlMergeCommandFactory mf,
			RPTRecordSetTableInfo tableInfo,
			RPTRecordSetDBTableInfo dbTableInfo,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		ISqlMergeBuffer merge = mf.merge(dbTableInfo.dbTable.namedb(), TARGET_ALIAS);
		mergeUsingAndOn(merge, tableInfo, dbTableInfo);
		mergeWhenMatched(merge, fields);
		removeRecverAndKeys(tableInfo, fields);
		mergeWhenNotMatched(merge, tableInfo, dbTableInfo, fields);
	}

	private static final void mergeUsingAndOn(ISqlMergeBuffer merge,
			RPTRecordSetTableInfo tableInfo, RPTRecordSetDBTableInfo dbTableInfo) {
		if (dbTableInfo.dbTable.isPrimary()) {
			merge.usingDummy();
			buildKeyCondition(merge.onCondition(), TARGET_ALIAS, tableInfo);
		} else {
			TableFieldDefineImpl recid = tableInfo.table.f_recid;
			ISqlSelectBuffer using = merge.usingSubquery(USING_ALIAS);
			using.newTableRef(tableInfo.table.primary.namedb(), USING_FROM);
			using.newColumn(USING_VAL).loadColumnRef(USING_FROM, recid.namedb());
			buildKeyCondition(using.where(), USING_FROM, tableInfo);
			merge.onCondition().loadColumnRef(TARGET_ALIAS, recid.namedb()).loadColumnRef(USING_ALIAS, USING_VAL).eq();
		}
	}

	private static final void mergeWhenMatched(ISqlMergeBuffer merge,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		for (Entry<TableFieldDefineImpl, RPTRecordSetFieldImpl> e : fields.entrySet()) {
			TableFieldDefineImpl field = e.getKey();
			merge.update(field.namedb()).loadParam(paramOf(e.getValue()));
		}
	}

	private static final void mergeWhenNotMatched(ISqlMergeBuffer merge,
			RPTRecordSetTableInfo tableInfo,
			RPTRecordSetDBTableInfo dbTableInfo,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		if (dbTableInfo.dbTable.isPrimary()) {
			if (!recidIsKey(tableInfo)) {
				TableFieldDefineImpl recid = tableInfo.table.f_recid;
				merge.insert(recid.namedb()).loadParam(arg(tableInfo.recidSf, recid.getType()));
			}
		} else {
			merge.insert(tableInfo.table.f_recid.namedb()).loadColumnRef(USING_ALIAS, USING_VAL);
		}
		if (dbTableInfo.dbTable.isPrimary()) {
			TableFieldDefineImpl recver = tableInfo.table.f_recver;
			merge.insert(recver.namedb()).loadParam(arg(tableInfo.recverSf, recver.getType()));
			for (int i = 0; i < tableInfo.keyFields.length; i++) {
				TableFieldDefineImpl key = tableInfo.keyFields[i];
				merge.insert(key.namedb()).loadParam(arg(tableInfo.getKeyValueField(i), key.getType()));
			}
		}
		for (Entry<TableFieldDefineImpl, RPTRecordSetFieldImpl> e : fields.entrySet()) {
			TableFieldDefineImpl field = e.getKey();
			merge.insert(field.namedb()).loadParam(paramOf(e.getValue()));
		}
	}

	static final void removeRecverAndKeys(RPTRecordSetTableInfo tableInfo,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		fields.remove(tableInfo.table.f_recver);
		for (int i = 0; i < tableInfo.keyFields.length; i++) {
			fields.remove(tableInfo.keyFields[i]);
		}
	}

	/**
	 * @param segment
	 *            和rf是相同对象.
	 * @param rf
	 * @param tableInfo
	 * @param tables
	 */
	private final void saveUsingReplace(ISqlSegmentBuffer segment,
			ISqlReplaceCommandFactory rf, RPTRecordSetTableInfo tableInfo,
			LinkedHashMap<DBTableDefineImpl, RPTRecordSetDBTableInfo> tables) {
		final LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields = new LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl>();
		for (Entry<DBTableDefineImpl, RPTRecordSetDBTableInfo> entry : tables.entrySet()) {
			final DBTableDefineImpl dbTable = entry.getKey();
			final RPTRecordSetDBTableInfo dbTableInfo = entry.getValue();
			if (dbTableInfo == null) {
				if (dbTable.isPrimary()) {
					replaceEmptyPrimary(rf, tableInfo);
				} else {
					replaceEmptySalvery(rf, tableInfo, dbTable);
				}
			} else {
				replace(rf, tableInfo, dbTableInfo, fields);
			}
		}
		this.build(segment);
	}

	private static final void replaceEmptyPrimary(ISqlReplaceCommandFactory rf,
			RPTRecordSetTableInfo tableInfo) {
		TableFieldDefineImpl recid = tableInfo.table.f_recid;
		DBTableDefineImpl primary = tableInfo.table.primary;
		ISqlReplaceBuffer replace = rf.replace(primary.namedb());
		if (!recidIsKey(tableInfo)) {
			replace.newField(recid.namedb());
			replace.newValue().loadParam(arg(tableInfo.recidSf, recid.getType()));
		}
		TableFieldDefineImpl recver = tableInfo.table.f_recver;
		replace.newField(recver.namedb());
		replace.newValue().loadParam(arg(tableInfo.recverSf, recver.getType()));
		for (int i = 0; i < tableInfo.keyFields.length; i++) {
			TableFieldDefineImpl key = tableInfo.keyFields[i];
			replace.newField(key.namedb());
			replace.newValue().loadParam(arg(tableInfo.getKeyValueField(i), key.getType()));
		}
	}

	private static final void replaceEmptySalvery(ISqlReplaceCommandFactory rf,
			RPTRecordSetTableInfo tableInfo, DBTableDefineImpl dbTable) {
		String recid = tableInfo.table.f_recid.namedb();
		ISqlReplaceBuffer replace = rf.replace(dbTable.namedb());
		replace.newField(recid);
		ISqlSelectBuffer sq = replace.newValue().subquery();
		sq.newTableRef(tableInfo.table.primary.namedb(), USING_FROM);
		sq.newColumn(USING_VAL).loadColumnRef(USING_FROM, recid);
		buildKeyCondition(sq.where(), USING_FROM, tableInfo);
	}

	private static final void replace(ISqlReplaceCommandFactory rf,
			RPTRecordSetTableInfo tableInfo,
			RPTRecordSetDBTableInfo dbTableInfo,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		fillUpdateFieldReturnWhetherUpdateKey(dbTableInfo, fields);
		ISqlReplaceBuffer replace = rf.replace(dbTableInfo.dbTable.namedb());
		if (dbTableInfo.dbTable.isPrimary()) {
			replace.newField(tableInfo.table.f_recid.namedb());
			replace.newValue().loadParam(arg(tableInfo.recidSf, tableInfo.table.f_recid.getType()));
			for (int ki = 0; ki < tableInfo.keyFields.length; ki++) {
				if (fields.containsKey(tableInfo.keyFields[ki])) {
					continue;
				}
				replace.newField(tableInfo.keyFields[ki].namedb());
				replace.newValue().loadParam(arg(tableInfo.getKeyValueField(ki), tableInfo.keyFields[ki].getType()));
			}
		} else {
			replace.newField(tableInfo.table.f_recid.namedb());
			ISqlSelectBuffer select = replace.newValue().subquery();
			select.newTableRef(tableInfo.table.primary.namedb(), "T");
			select.newColumn("V").loadColumnRef("T", tableInfo.table.f_recid.namedb());
			buildKeyCondition(select.where(), "T", tableInfo);
		}
		for (Entry<TableFieldDefineImpl, RPTRecordSetFieldImpl> e : fields.entrySet()) {
			TableFieldDefineImpl field = e.getKey();
			replace.newField(field.namedb());
			replace.newValue().loadParam(paramOf(e.getValue()));
		}
	}

	private final void saveUsingUpsert(ISqlSegmentBuffer segment,
			RPTRecordSetTableInfo tableInfo,
			LinkedHashMap<DBTableDefineImpl, RPTRecordSetDBTableInfo> tables) {
		LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields = new LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl>();
		for (Entry<DBTableDefineImpl, RPTRecordSetDBTableInfo> entry : tables.entrySet()) {
			DBTableDefineImpl dbTable = entry.getKey();
			RPTRecordSetDBTableInfo dbTableInfo = entry.getValue();
			if (dbTableInfo == null) {
				if (dbTable.isPrimary()) {
					upsertEmptyPrimary(segment, tableInfo);
				} else {
					upsertEmptySlavery(segment, tableInfo, dbTable);
				}
			} else {
				fillUpdateFieldReturnWhetherUpdateKey(dbTableInfo, fields);
				upsert(segment, tableInfo, dbTableInfo, fields);
			}
		}
		this.build(segment);
	}

	private static final void upsertEmptyPrimary(ISqlSegmentBuffer segment,
			RPTRecordSetTableInfo tableInfo) {
		TableDefineImpl table = tableInfo.table;
		ISqlUpdateBuffer update = segment.update(table.primary.namedb(), TARGET_ALIAS, false);
		buildKeyCondition(update.where(), TARGET_ALIAS, tableInfo);
		update.newValue(table.f_recid.namedb()).loadColumnRef(TARGET_ALIAS, table.f_recid.namedb());
		ISqlConditionBuffer ifs = segment.ifThenElse();
		rowCountEqZero(ifs.newWhen());
		ISqlInsertBuffer insert = ifs.newThen().insert(table.primary.namedb());
		if (!recidIsKey(tableInfo)) {
			TableFieldDefineImpl recid = table.f_recid;
			insert.newField(recid.namedb());
			insert.newValue().loadParam(arg(tableInfo.recidSf, recid.getType()));
		}
		TableFieldDefineImpl recver = table.f_recver;
		insert.newField(recver.namedb());
		insert.newValue().loadParam(arg(tableInfo.recverSf, recver.getType()));
		for (int i = 0; i < tableInfo.keyFields.length; i++) {
			TableFieldDefineImpl key = tableInfo.keyFields[i];
			insert.newField(key.namedb());
			insert.newValue().loadParam(arg(tableInfo.getKeyValueField(i), key.getType()));
		}
	}

	private static final void upsertEmptySlavery(ISqlSegmentBuffer segment,
			RPTRecordSetTableInfo tableInfo, DBTableDefineImpl dbTable) {
		final String recid = tableInfo.table.f_recid.namedb();
		ISqlUpdateBuffer update = segment.update(dbTable.namedb(), TARGET_ALIAS, false);
		update.newValue(recid).loadColumnRef(TARGET_ALIAS, recid);
		ISqlExprBuffer where = update.where();
		where.loadColumnRef(TARGET_ALIAS, recid);
		ISqlSelectBuffer sq = where.subquery();
		sq.newTableRef(tableInfo.table.primary.namedb(), USING_FROM);
		sq.newColumn(USING_VAL).loadColumnRef(USING_FROM, recid);
		buildKeyCondition(sq.where(), USING_FROM, tableInfo);
		where.predicate(SqlPredicate.IN, 2);
		ISqlConditionBuffer ifs = segment.ifThenElse();
		rowCountEqZero(ifs.newWhen());
		ISqlInsertBuffer insert = ifs.newThen().insert(dbTable.namedb());
		insertEmptySlave(insert, tableInfo);
	}

	static final void insertEmptySlave(ISqlInsertBuffer insert,
			RPTRecordSetTableInfo tableInfo) {
		insert.newField(tableInfo.table.f_recid.namedb());
		ISqlSelectBuffer val = insert.newValue().subquery();
		val.newTableRef(tableInfo.table.primary.namedb(), USING_FROM);
		val.newColumn(USING_VAL).loadColumnRef(USING_FROM, tableInfo.table.f_recid.namedb());
		buildKeyCondition(val.where(), USING_FROM, tableInfo);
	}

	static final void insertNormalFields(ISqlInsertBuffer insert,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		for (Entry<TableFieldDefineImpl, RPTRecordSetFieldImpl> e : fields.entrySet()) {
			TableFieldDefineImpl column = e.getKey();
			insert.newField(column.namedb());
			insert.newValue().loadParam(paramOf(e.getValue()));
		}
	}

	static final void updateNormalFields(ISqlUpdateBuffer update,
			LinkedHashMap<TableFieldDefineImpl, RPTRecordSetFieldImpl> fields) {
		for (Entry<TableFieldDefineImpl, RPTRecordSetFieldImpl> e : fields.entrySet()) {
			TableFieldDefineImpl field = e.getKey();
			update.newValue(field.namedb()).loadParam(paramOf(e.getValue()));
		}
	}

	static final void insertRecidAndRecver(ISqlInsertBuffer insert,
			RPTRecordSetTableInfo tableInfo, RPTRecordSetDBTableInfo dbTableInfo) {
		if (dbTableInfo.dbTable.isPrimary() && !recidIsKey(tableInfo) || (!dbTableInfo.dbTable.isPrimary())) {
			TableFieldDefineImpl recid = tableInfo.table.f_recid;
			insert.newField(recid.namedb());
			insert.newValue().loadParam(arg(tableInfo.recidSf, recid.getType()));
		}
		if (dbTableInfo.dbTable.isPrimary()) {
			TableFieldDefineImpl recver = tableInfo.table.f_recver;
			insert.newField(recver.namedb());
			insert.newValue().loadParam(arg(tableInfo.recverSf, recver.getType()));
		}
	}

	static final void insertLKeyFields(ISqlInsertBuffer insert,
			RPTRecordSetTableInfo tableInfo) {
		for (int i = 0; i < tableInfo.keyFields.length; i++) {
			TableFieldDefineImpl key = tableInfo.keyFields[i];
			insert.newField(key.namedb());
			insert.newValue().loadParam(arg(tableInfo.getKeyValueField(i), key.getType()));
		}
	}
}
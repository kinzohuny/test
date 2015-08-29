/**
 * 
 */
package com.jiuqi.dna.core.internal.da.report;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.jiuqi.dna.core.impl.CombinedExpr;
import com.jiuqi.dna.core.impl.ConditionalExpr;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.DynObj;
import com.jiuqi.dna.core.impl.GUIDType;
import com.jiuqi.dna.core.impl.QuRootTableRef;
import com.jiuqi.dna.core.impl.QuTableRef;
import com.jiuqi.dna.core.impl.QueryStatementImpl;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldRefImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.Querier;
import com.jiuqi.dna.core.internal.da.sql.render.QuerySql;
import com.jiuqi.dna.core.type.GUID;

final class RPTRecordSetTableInfo extends ArrayList<RPTRecordSetDBTableInfo> {

	@Override
	public final String toString() {
		return this.table.name;
	}

	private static final long serialVersionUID = 1L;
	final StructFieldDefineImpl recidSf;
	final StructFieldDefineImpl recverSf;
	final TableDefineImpl table;
	final TableFieldDefineImpl[] keyFields;
	final RPTRecordSetKeyImpl[] keys;
	final RPTRecordSetRestrictionImpl restriction;

	final boolean hasKey(RPTRecordSetKeyImpl key) {
		for (RPTRecordSetKeyImpl k : this.keys) {
			if (k == key) {
				return true;
			}
		}
		return false;
	}

	final TableFieldDefineImpl getTableField(RPTRecordSetKeyImpl key) {
		for (int i = 0; i < this.keys.length; i++) {
			if (this.keys[i] == key) {
				return this.keyFields[i];
			}
		}
		throw new IllegalArgumentException();
	}

	final StructFieldDefineImpl getKeyValueField(int index) {
		RPTRecordSetKeyImpl key = this.keys[index];
		StructFieldDefineImpl sf = this.restriction.tryGetKeyRestrictionField(key.index);
		if (sf != null) {
			return sf;
		}
		return key.structField;
	}

	final RPTRecordSetFieldImpl newField(TableFieldDefineImpl tableField,
			boolean usingBigDecimal) {
		DBTableDefineImpl dbTable = tableField.dbTable;
		RPTRecordSetDBTableInfo dbTableInfo = this.findDBTableInfo(dbTable);
		if (dbTableInfo == null) {
			this.add(dbTableInfo = new RPTRecordSetDBTableInfo(dbTable));
		}
		RPTRecordSetFieldImpl field = new RPTRecordSetFieldImpl(this.restriction.recordSet, tableField, this.restriction, usingBigDecimal);
		dbTableInfo.add(field);
		field.recordSet.fields.add(field);
		return field;
	}

	private final QuRootTableRef newPrimaryQuery(RPTRecordSetRecordReader reader) {
		reader.resetQuery();
		final RPTRecordSetImpl owner = this.restriction.recordSet;
		final QueryStatementImpl query = new QueryStatementImpl("rpt-m", owner.recordStruct);
		final QuRootTableRef tr = query.newReference(this.table);
		final ArrayList<ConditionalExpr> conditions = new ArrayList<ConditionalExpr>();
		for (int i = 0; i < this.keyFields.length; i++) {
			final TableFieldDefineImpl tf = this.keyFields[i];
			final TableFieldRefImpl fre = new TableFieldRefImpl(tr, tf);
			query.newColumn(fre, tf.name);
			RPTRecordSetKeyRestrictionImpl kr = this.restriction.useKeyRestriction(this.keys[i].index, reader);
			if (kr == null) {
				continue;
			}
			final int paramCount = kr.getMatchValueCount();
			if (paramCount > 0) {
				reader.paramCache.ensureCapacity(paramCount);
				kr.fillAsSqlParams(reader.paramCache);
				conditions.add(reader.buildInCondition(fre, paramCount));
			}
		}
		if (conditions.size() == 1) {
			query.setCondition(conditions.get(0));
		} else if (conditions.size() > 1) {
			ConditionalExpr[] ands = conditions.toArray(new ConditionalExpr[conditions.size()]);
			query.setCondition(new CombinedExpr(false, true, ands));
		}
		int orderbyCount = this.restriction.recordSet.getOrderByCount();
		if (orderbyCount > 0) {
			for (int i = 0; i < orderbyCount; i++) {
				RPTRecordSetOrderByImpl orderby = this.restriction.recordSet.getOrderBy(i);
				RPTRecordSetColumnImpl column = orderby.column;
				if (column instanceof RPTRecordSetFieldImpl) {
					TableFieldDefineImpl field = ((RPTRecordSetFieldImpl) column).tableField;
					if (field.owner == this.table) {
						query.newOrderBy(field, orderby.isDesc);
					}
				} else if (column instanceof RPTRecordSetKeyImpl) {
					RPTRecordSetKeyImpl k = (RPTRecordSetKeyImpl) column;
					for (int keyIdx = 0; keyIdx < this.keys.length; keyIdx++) {
						if (this.keys[keyIdx] == k) {
							if (this.keyFields[i].owner == this.table) {
								query.newOrderBy(this.keyFields[i], orderby.isDesc);
							}
							break;
						}
					}
				} else {
					throw new UnsupportedOperationException();
				}
			}
		}
		tr.newColumn(this.table.f_recver);
		tr.newColumn(this.table.f_recid);
		return tr;
	}

	private final QuRootTableRef newSlaveQuery(RPTRecordSetRecordReader reader) {
		reader.resetQuery();
		final HashMap<Object, DynObj> recidMap = reader.getRecidMap();
		final int paramCount = recidMap.size();
		if (paramCount == 0) {
			throw new IllegalStateException();
		}
		RPTRecordSetImpl owner = this.restriction.recordSet;
		QueryStatementImpl query = new QueryStatementImpl("rpt-r", owner.recordStruct);
		QuRootTableRef tr = query.newReference(this.table);
		reader.paramCache.ensureCapacity(paramCount);
		for (Object param : recidMap.keySet()) {
			reader.paramCache.add(((GUID) param).toBytes());
		}
		final TableFieldRefImpl fre = new TableFieldRefImpl(tr, this.table.f_recid);
		query.setCondition(reader.buildInCondition(fre, paramCount));
		query.newColumn(fre, "RECID");
		return tr;
	}

	private int loadPart(final DBAdapterImpl adapter, QuTableRef ref,
			RPTRecordSetRecordReader reader, boolean isFirstPart,
			boolean isLastPart) throws SQLException {
		QuerySql sql = new QuerySql(adapter.dbMetadata, (QueryStatementImpl) ref.getOwner());
		Querier querier = sql.newExecutor(adapter, null);
		querier.use(false);
		try {
			int resultCount = 0;
			final int paramCount = reader.paramCache.size();
			int condisUsed = 0;
			do {
				if (condisUsed != 0) {
					querier.pstmt.clearParameters();
				}
				for (int i = 1, c = sql.parameters.size(); i <= c && condisUsed < paramCount; i++, condisUsed++) {
					querier.pstmt.setObject(i, reader.paramCache.get(condisUsed));
				}
				reader.resultSet = querier.pstmt.executeQuery();
				try {
					resultCount += reader.readTablePart(this, isFirstPart, isLastPart);
				} finally {
					reader.resultSet.close();
				}
			} while (condisUsed < paramCount);
			return resultCount;
		} finally {
			querier.unuse();
			reader.paramCache.clear();
		}
	}

	final void load(DBAdapterImpl dbAdapter, RPTRecordSetRecordReader reader)
			throws SQLException {
		final int maxColumnsInSelect = dbAdapter.dbMetadata.getMaxColumnsInSelect() * 9 / 10;
		boolean isFirstPart = true;
		QuRootTableRef ref = this.newPrimaryQuery(reader);
		int curCols = ref.getOwner().columns.size();
		for (int i = 0, c = this.size(); i < c; i++) {
			RPTRecordSetDBTableInfo dbTableInfo = this.get(i);
			int colInDBTable = dbTableInfo.size();
			for (int j = 0; j < colInDBTable; j++) {
				if (curCols >= maxColumnsInSelect) {
					this.loadPart(dbAdapter, ref, reader, isFirstPart, false);
					if (reader.getRecidMap().isEmpty()) {
						return;
					}
					isFirstPart = false;
					ref = this.newSlaveQuery(reader);
					curCols = ref.getOwner().columns.size();
				}
				RPTRecordSetFieldImpl field = dbTableInfo.get(j);
				ref.newColumn(field.tableField);
				reader.addDataFieldToCache(field.structField);
				curCols++;
			}
		}
		this.loadPart(dbAdapter, ref, reader, isFirstPart, true);
	}

	private final RPTRecordSetDBTableInfo findDBTableInfo(
			DBTableDefineImpl dbTable) {
		for (int i = 0, c = this.size(); i < c; i++) {
			RPTRecordSetDBTableInfo tb = this.get(i);
			if (tb.dbTable == dbTable) {
				return tb;
			}
		}
		return null;
	}

	private static final String MD_ORG = "MD_ORG";
	private static final String UNITID = "UNITID";

	private static final void joinUsingRecidReplaceUnitid(
			RPTRecordSetImpl owner, TableDefineImpl table,
			ArrayList<TableFieldDefineImpl> fields,
			ArrayList<RPTRecordSetKeyImpl> keys) {
		fields.add(table.f_recid);
		RPTRecordSetKeyImpl key = owner.findKey(UNITID);
		if (key == null) {
			owner.keys.add(key = new RPTRecordSetKeyImpl(owner, UNITID, GUIDType.TYPE));
		}
		keys.add(key);
	}

	RPTRecordSetTableInfo(RPTRecordSetRestrictionImpl restriction,
			TableDefineImpl table) {
		RPTRecordSetImpl recordSet = restriction.recordSet;
		this.restriction = restriction;
		int rollbackField = 0;
		int rollbackKey = 0;
		ArrayList<TableFieldDefineImpl> keyFields = new ArrayList<TableFieldDefineImpl>();
		ArrayList<RPTRecordSetKeyImpl> keys = new ArrayList<RPTRecordSetKeyImpl>();
		if (table.name.equals(MD_ORG) || table.name.equals("B0608_UNIT")) {
			joinUsingRecidReplaceUnitid(recordSet, table, keyFields, keys);
		} else {
			for (int i = 0, c = table.fields.size(); i < c; i++) {
				TableFieldDefineImpl fd = table.fields.get(i);
				if (fd.isPrimaryKey()) {
					rollbackField = recordSet.recordStruct.getFields().size();
					rollbackKey = recordSet.keys.size();
					keys.add(recordSet.ensureKey(fd, rollbackField, rollbackKey));
					keyFields.add(fd);
				}
			}
		}
		if (keyFields.size() == 0 || keys.size() == 0) {
			throw new IllegalArgumentException("±í[" + table.name + "]Î´°üº¬Ö÷¼ü");
		}
		int keyCount = keyFields.size();
		this.keyFields = keyFields.toArray(new TableFieldDefineImpl[keyCount]);
		this.keys = keys.toArray(new RPTRecordSetKeyImpl[keyCount]);
		this.table = table;
		this.recidSf = recordSet.recordStruct.newField(table.f_recid.getType());
		this.recverSf = recordSet.recordStruct.newField(table.f_recver.getType());
	}

	final RPTRecordSetDBTableInfo find(DBTableDefineImpl dbTable) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).dbTable == dbTable) {
				return this.get(i);
			}
		}
		return null;
	}
}
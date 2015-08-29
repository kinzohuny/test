package com.jiuqi.dna.core.internal.da.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.jiuqi.dna.core.da.DBAdapter;
import com.jiuqi.dna.core.da.ext.RPTRecordSet;
import com.jiuqi.dna.core.da.ext.RPTRecordSetColumn;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.ArgumentRefExpr;
import com.jiuqi.dna.core.impl.CombinedExpr;
import com.jiuqi.dna.core.impl.ConditionalExpr;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DynObj;
import com.jiuqi.dna.core.impl.MappingQueryStatementImpl;
import com.jiuqi.dna.core.impl.PredicateExpr;
import com.jiuqi.dna.core.impl.PredicateImpl;
import com.jiuqi.dna.core.impl.QuJoinedTableRef;
import com.jiuqi.dna.core.impl.QuRootTableRef;
import com.jiuqi.dna.core.impl.QuTableRef;
import com.jiuqi.dna.core.impl.ResultSetDynObjReader;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.impl.ValueExpr;
import com.jiuqi.dna.core.internal.da.report.RPTRecordSetRecordDefine.RPTRecord;
import com.jiuqi.dna.core.internal.da.sql.execute.LimitQuerier;
import com.jiuqi.dna.core.internal.da.sql.execute.RowCountQuerier;
import com.jiuqi.dna.core.misc.MissingObjectException;

/**
 * 报表专用数据集
 * 
 * @author houchunlei
 * 
 */
public final class RPTRecordSetImpl implements RPTRecordSet {

	public static class FactoryImpl implements Factory {
		public RPTRecordSet newRPTRecordSet() {
			return new RPTRecordSetImpl();
		}
	}

	RPTRecordSetImpl() {
		this.resetRestrictions();
	}

	final void checkCurrentRecordValid() {
		if (this.current == null) {
			throw new NullPointerException("当前记录为空");
		}
	}

	final RPTRecord getRecordRead() {
		this.checkCurrentRecordValid();
		return this.current;
	}

	final RPTRecord getRecordWrite() {
		this.checkCurrentRecordValid();
		switch (this.current.getRecordState()) {
		case DynObj.r_db:
			this.current.setRecordState(DynObj.r_db_modifing);
			this.addModifiedRecord(this.current);
			break;
		case DynObj.r_new:
			this.current.setRecordState(DynObj.r_new_modified);
			break;
		}
		return this.current;
	}

	final void updateRecordMask(int index) {
		if (index < 0) {
			throw new IllegalArgumentException();
		}
		RPTRecord record = this.current;
		record.mask |= (1 << index);
	}

	int generation;

	private RPTRecord current;

	final RPTRecordSetRecordDefine recordStruct = new RPTRecordSetRecordDefine();

	final ArrayList<RPTRecordSetFieldImpl> fields = new ArrayList<RPTRecordSetFieldImpl>();

	final ArrayList<RPTRecordSetKeyImpl> keys = new ArrayList<RPTRecordSetKeyImpl>();

	final RPTRecordSetKeyImpl ensureKey(TableFieldDefineImpl tableField,
			int rollbackFieldCount, int rollbackKeyCount) {
		RPTRecordSetKeyImpl key = this.findKey(tableField.name);
		if (key == null) {
			this.keys.add(key = new RPTRecordSetKeyImpl(this, tableField));
		} else if (key.structField.getType().getRootType() != tableField.getType().getRootType()) {
			for (int j = this.keys.size() - 1; j >= rollbackKeyCount; j--) {
				this.keys.remove(j);
			}
			for (int j = this.recordStruct.getFields().size() - 1; j >= rollbackFieldCount; j--) {
				this.recordStruct.getFields().remove(j);
			}
			throw new IllegalArgumentException("表[" + tableField.owner.name + "]的键[" + tableField.name + "]的类型与先前追加的表的键类型不符");
		}
		return key;
	}

	// //////////////////////////////////
	// 数据集定义
	// //////////////////////////////////
	/**
	 * 清空定义
	 */
	public final void reset() {
		this.generation++;
		this.keys.clear();
		this.fields.clear();
		this.records.clear();
		this.recordStruct.reset();
		this.currentRecordIndex = 0;
		this.current = null;
		if (this.orderbys != null) {
			this.orderbys.clear();
		}
		this.resetRestrictions();
	}

	private void resetRestrictions() {
		this.restrictions.clear();
		this.firstRestriction = new RPTRecordSetRestrictionImpl(this);
		this.restrictions.add(this.firstRestriction);
	}

	private final void ensurePrepared() {
		for (int i = 0, c = this.restrictions.size(); i < c; i++) {
			this.restrictions.get(i).ensurePrepared();
		}
		this.recordStruct.prepareAccessInfo();
	}

	RPTRecordSetRestrictionImpl firstRestriction;

	final ArrayList<RPTRecordSetRestrictionImpl> restrictions = new ArrayList<RPTRecordSetRestrictionImpl>(1);

	public final RPTRecordSetRestrictionImpl newRestriction() {
		RPTRecordSetRestrictionImpl restriction = new RPTRecordSetRestrictionImpl(this);
		this.restrictions.add(restriction);
		return restriction;
	}

	public final RPTRecordSetFieldImpl newField(TableFieldDefine tableField) {
		return this.internalNewField(tableField, this.firstRestriction, false);
	}

	public final RPTRecordSetFieldImpl newField(TableFieldDefine tableField,
			boolean usingBigDecimal) {
		return this.internalNewField(tableField, this.firstRestriction, usingBigDecimal);
	}

	private final RPTRecordSetFieldImpl internalNewField(
			TableFieldDefine tableField,
			RPTRecordSetRestrictionImpl restriction, boolean usingBigDecimal) {
		if (tableField == null) {
			throw new NullArgumentException("tableField");
		}
		if (restriction == null) {
			throw new NullArgumentException("restriction");
		}
		if (restriction.recordSet != this) {
			throw new IllegalArgumentException("restriction 对象已经失效");
		}
		TableFieldDefineImpl tf = (TableFieldDefineImpl) tableField;
		return restriction.internalNewField(tf, usingBigDecimal);
	}

	/**
	 * 返回字段个数
	 */
	public final int getFieldCount() {
		return this.fields.size();
	}

	/**
	 * 获得某位置的字段
	 */
	public final RPTRecordSetFieldImpl getField(int index) {
		return this.fields.get(index);
	}

	// /////////////////////////////////////
	// // 键相关
	// ////////////////////////////////////
	/**
	 * 获取键个数
	 */
	public final int getKeyCount() {
		return this.keys.size();
	}

	/**
	 * 获取键
	 */
	public final RPTRecordSetKeyImpl getKey(int index) {
		return this.keys.get(index);
	}

	/**
	 * 根据键名称查找键
	 */
	public final RPTRecordSetKeyImpl findKey(String keyName) {
		if (keyName == null || keyName.length() == 0) {
			throw new NullArgumentException("keyName");
		}
		for (int i = 0, c = this.keys.size(); i < c; i++) {
			RPTRecordSetKeyImpl key = this.keys.get(i);
			if (key.structField.name.equals(keyName)) {
				return key;
			} else
			// !!!
			if (keyName.equals("RECID") && key.structField.name.equals("UNITID")) {
				return key;
			}
		}
		return null;
	}

	public final RPTRecordSetKeyImpl getKey(String keyName) {
		RPTRecordSetKeyImpl key = this.findKey(keyName);
		if (key == null) {
			throw new MissingObjectException("找不到名为[" + keyName + "]的键");
		}
		return key;
	}

	// /////////////////////////////////////
	// // 数据相关
	// ////////////////////////////////////
	/**
	 * 装载数据集
	 * 
	 * @return 返回记录个数
	 */
	public final int load(DBAdapter dbAdapter) {
		if (dbAdapter == null) {
			throw new NullArgumentException("dbAdapter");
		}
		try {
			DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(dbAdapter);
			this.ensurePrepared();
			this.records.clear();
			this.currentRecordIndex = 0;
			this.current = null;
			final RPTRecordSetRecordReader reader = new RPTRecordSetRecordReader(this);
			for (int i = 0, c = this.restrictions.size(); i < c; i++) {
				RPTRecordSetRestrictionImpl r = this.restrictions.get(i);
				r.load(adapter, reader);
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
		int recordCount = this.records.size();
		if (recordCount > 0) {
			this.current = this.records.get(0);
		}
		return recordCount;
	}

	public final int load(DBAdapter context, int offset, int rowCount) {
		if (context == null) {
			throw new NullArgumentException("数据库适配器");
		}
		if (offset < 0 || rowCount <= 0) {
			throw new IllegalArgumentException("错误的offset或rowCount");
		}
		this.ensurePrepared();
		ArrayList<Object> paramValues = new ArrayList<Object>();
		MappingQueryStatementImpl query = this.buildQuery(paramValues);
		try {
			DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
			LimitQuerier querier = query.getQueryLimitSql(adapter).newExecutor(adapter, null);
			try {
				querier.use(false);
				ResultSet rs = querier.query(paramValues, rowCount, offset);
				try {
					ResultSetDynObjReader reader = new ResultSetDynObjReader(rs);
					while (rs.next()) {
						reader.readRecord(this.newRecord(DynObj.r_db), query);
					}
					return this.records.size();
				} finally {
					rs.close();
				}
			} finally {
				querier.unuse();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final int getRecordCountInDB(DBAdapter context) {
		if (context == null) {
			throw new NullArgumentException("数据库适配器");
		}
		ArrayList<Object> paramValues = new ArrayList<Object>();
		MappingQueryStatementImpl qu = this.buildQuery(paramValues);
		DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
		RowCountQuerier querier = qu.getQueryRowCountSql(adapter).newExecutor(adapter, null);
		try {
			return (int) querier.executeLongScalar(paramValues);
		} finally {
			querier.unuse();
		}
	}

	private final MappingQueryStatementImpl buildQuery(
			ArrayList<Object> paramValues) {
		MappingQueryStatementImpl qu = new MappingQueryStatementImpl("rpt", this.recordStruct);
		final int kc = this.keys.size();
		final ArgumentRefExpr dummy = new ArgumentRefExpr(this.recordStruct.getFields().get(0));
		ArrayList<PredicateExpr> inConditions = null;
		for (RPTRecordSetRestrictionImpl rstr : this.restrictions) {
			final HashMap<RPTRecordSetTableInfo, QuTableRef> ti2tr = new HashMap<RPTRecordSetTableInfo, QuTableRef>();
			final HashMap<RPTRecordSetKeyImpl, RPTRecordSetTableInfo> key2ti = new HashMap<RPTRecordSetKeyImpl, RPTRecordSetTableInfo>();
			QuRootTableRef tr = null;
			for (RPTRecordSetTableInfo ti : rstr.tables) {
				if (tr == null) {
					tr = qu.newReference(ti.table);
					ti2tr.put(ti, tr);
				} else {
					QuJoinedTableRef join = tr.newJoin(ti.table);
					for (RPTRecordSetKeyImpl key : ti.keys) {
						if (key2ti.containsKey(key)) {
							RPTRecordSetTableInfo otherTi = key2ti.get(key);
							QuTableRef otherTr = ti2tr.get(otherTi);
							ConditionalExpr condition = join.expOf(ti.getTableField(key)).xEq(otherTr.expOf(otherTi.getTableField(key)));
							if (join.getJoinCondition() == null) {
								join.setJoinCondition(condition);
							} else {
								join.setJoinCondition(join.getJoinCondition().and(condition));
							}
						}
					}
					if (join.getJoinCondition() == null) {
						throw new UnsupportedOperationException("表[" + ti.table.name + "]无法与其它表连接.");
					}
					ti2tr.put(ti, join);
				}
				qu.newColumn(ti.table.f_recid, ti.recidSf.name);
				qu.newColumn(ti.table.f_recver, ti.recverSf.name);
				for (int i = 0; i < ti.keyFields.length; i++) {
					qu.newColumn(ti.keyFields[i], ti.keys[i].structField.name);
				}
				for (RPTRecordSetKeyImpl key : ti.keys) {
					if (!key2ti.containsKey(key)) {
						key2ti.put(key, ti);
					}
				}
			}
			for (int ki = 0; ki < kc; ki++) {
				if (rstr.isKeySupported(ki)) {
					RPTRecordSetKeyRestrictionImpl kr = rstr.useKeyRestriction(ki);
					if (kr != null && kr.getMatchValueCount() > 0) {
						RPTRecordSetKeyImpl key = kr.key;
						RPTRecordSetTableInfo ti = key2ti.get(key);
						QuTableRef tableref = ti2tr.get(ti);
						ValueExpr[] exprs = new ValueExpr[kr.getMatchValueCount() + 1];
						exprs[0] = tableref.expOf(ti.getTableField(key));
						for (int i = 1; i < exprs.length; i++) {
							exprs[i] = dummy;
						}
						PredicateExpr predicate = new PredicateExpr(false, PredicateImpl.IN, exprs);
						if (inConditions == null) {
							inConditions = new ArrayList<PredicateExpr>();
						}
						inConditions.add(predicate);
						kr.fillAsSqlParams(paramValues);
					}
				}
			}
		}
		if (inConditions != null && inConditions.size() > 0) {
			if (inConditions.size() == 1) {
				qu.setCondition(inConditions.get(0));
			} else {
				qu.setCondition(new CombinedExpr(false, true, inConditions.toArray(new ConditionalExpr[inConditions.size()])));
			}
		}
		for (RPTRecordSetFieldImpl f : this.fields) {
			qu.newColumn(f.tableField, f.structField.name);
		}
		if (this.orderbys != null) {
			for (RPTRecordSetOrderByImpl orderby : this.orderbys) {
				RPTRecordSetColumnImpl column = orderby.column;
				if (column instanceof RPTRecordSetFieldImpl) {
					// 按输出字段排序,直接增加排序
					RPTRecordSetFieldImpl rsf = (RPTRecordSetFieldImpl) column;
					qu.newOrderBy(rsf.tableField, orderby.isDesc);
				} else if (column instanceof RPTRecordSetKeyImpl) {
					// 按主键排序
					RPTRecordSetKeyImpl key = (RPTRecordSetKeyImpl) column;
					// 按约束顺序,为每个包含该主键的约束增加排序
					restrictionNewOrderby: for (int i = 0, c = this.restrictions.size(); i < c; i++) {
						RPTRecordSetRestrictionImpl rstr = this.restrictions.get(i);
						if (rstr.isKeySupported(key)) {
							for (RPTRecordSetTableInfo ti : rstr.tables) {
								if (ti.hasKey(key)) {
									qu.newOrderBy(ti.getTableField(key), orderby.isDesc);
								}
								continue restrictionNewOrderby;
							}
							// 不应该走到的代码块.约束支持该键,则必在某一TableInfo中增加上排序,continue下一个约束.
							throw new IllegalArgumentException();
						}
					}
				} else {
					throw new IllegalArgumentException("不支持的排序列.");
				}
			}
		}
		return qu;
	}

	public ArrayList<RPTRecord> records = new ArrayList<RPTRecord>(0);
	private int currentRecordIndex;

	private ArrayList<RPTRecord> modifiedRecords;

	final void addModifiedRecord(RPTRecord record) {
		if (this.modifiedRecords == null) {
			this.modifiedRecords = new ArrayList<RPTRecord>();
		}
		this.modifiedRecords.add(record);
	}

	public final int getRecordCount() {
		return this.records.size();
	}

	public final int getCurrentRecordIndex() {
		return this.currentRecordIndex;
	}

	public final void setCurrentRecordIndex(int recordIndex) {
		this.current = this.records.get(recordIndex);
		this.currentRecordIndex = recordIndex;
	}

	final RPTRecord newRecord(int state) {
		RPTRecord rp = this.recordStruct.newRecord(state);
		this.records.add(rp);
		return rp;
	}

	public final int newRecord() {
		RPTRecord record = this.newRecord(DynObj.r_new);
		this.addModifiedRecord(record);
		this.current = record;
		return this.currentRecordIndex = this.records.size() - 1;
	}

	public final void remove(int recordIndex) {
		if (recordIndex < 0 || recordIndex >= this.records.size()) {
			throw new IllegalArgumentException("错误的序号");
		}
		DynObj obj = this.records.get(recordIndex);
		obj.setRecordState(DynObj.r_db_deleting);
		this.addModifiedRecord(this.records.remove(recordIndex));
		if (this.records.size() > this.currentRecordIndex) {
			this.current = this.records.get(this.currentRecordIndex);
		} else {
			this.current = null;
		}
	}

	public final void removeCurrentRecord() {
		this.remove(this.currentRecordIndex);
	}

	public int update(DBAdapter context) {
		if (this.modifiedRecords == null) {
			return 0;
		}
		int resCount = 0;
		int modCount = this.modifiedRecords.size();
		try {
			if (modCount > 0) {
				RPTRecordSetUpdater updater = new RPTRecordSetUpdater(context, this);
				try {
					// for (RPTRecordSetRestrictionImpl rstr :
					// this.restrictions) {
					// try {
					// for (int recIndex = modCount - 1; recIndex >= 0;
					// recIndex--) {
					// RPTRecord record = this.modifiedRecords
					// .get(recIndex);
					// updater.update(rstr, record);
					// }
					// } finally {
					// updater.unuse();
					// }
					// }
					// resCount = this.modifiedRecords.size();
					// for (RPTRecord record : this.modifiedRecords) {
					// record.setRecordState(DynObj.r_db);
					// }
					// this.modifiedRecords.clear();
					try {
						for (int recIndex = modCount - 1; recIndex >= 0; recIndex--) {
							RPTRecord record = this.modifiedRecords.get(recIndex);
							updater.update(record);
							this.modifiedRecords.remove(recIndex);
							resCount++;
						}
					} finally {
						updater.unuse();
					}
				} finally {
					updater.unuse();
				}
			}
			return resCount;
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	private ArrayList<RPTRecordSetOrderByImpl> orderbys;

	public final RPTRecordSetOrderByImpl newOrderBy(RPTRecordSetColumn column,
			boolean isDesc, boolean isNullAsMIN) {
		if (column == null) {
			throw new NullArgumentException("column");
		}
		RPTRecordSetColumnImpl col = (RPTRecordSetColumnImpl) column;
		if (col.recordSet != this || col.generation != this.generation) {
			throw new IllegalArgumentException("column");
		}
		RPTRecordSetOrderByImpl orderBy = new RPTRecordSetOrderByImpl(col, isDesc, isNullAsMIN);
		if (this.orderbys == null) {
			this.orderbys = new ArrayList<RPTRecordSetOrderByImpl>();
		}
		this.orderbys.add(orderBy);
		return orderBy;
	}

	public final RPTRecordSetOrderByImpl newOrderBy(RPTRecordSetColumn column,
			boolean isDesc) {
		return this.newOrderBy(column, isDesc, true);
	}

	public final RPTRecordSetOrderByImpl newOrderBy(RPTRecordSetColumn column) {
		return this.newOrderBy(column, false, true);
	}

	public final RPTRecordSetOrderByImpl getOrderBy(int index) {
		if (this.orderbys != null) {
			return this.orderbys.get(index);
		}
		throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
	}

	public final int getOrderByCount() {
		return this.orderbys != null ? this.orderbys.size() : 0;
	}

	public final RPTRecordSetRestrictionImpl getFirstRestriction() {
		return this.firstRestriction;
	}

}

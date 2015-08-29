package com.jiuqi.dna.core.internal.da.report;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.jiuqi.dna.core.impl.ArgumentRefExpr;
import com.jiuqi.dna.core.impl.CombinedExpr;
import com.jiuqi.dna.core.impl.ConditionalExpr;
import com.jiuqi.dna.core.impl.DynObj;
import com.jiuqi.dna.core.impl.GUIDType;
import com.jiuqi.dna.core.impl.PredicateExpr;
import com.jiuqi.dna.core.impl.PredicateImpl;
import com.jiuqi.dna.core.impl.ResultSetDynObjReader;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldRefImpl;
import com.jiuqi.dna.core.impl.ValueExpr;
import com.jiuqi.dna.core.internal.da.report.RPTRecordSetRecordDefine.RPTRecord;
import com.jiuqi.dna.core.type.DataType;

final class RPTRecordSetRecordReader extends ResultSetDynObjReader {

	/**
	 * 针对每个TableInfo的查询，需要使用的键约束值的缓存。
	 */
	final ArrayList<Object> paramCache = new ArrayList<Object>();

	RPTRecordSetRecordReader(RPTRecordSetImpl recordSet) {
		super(null);
		this.dummyArg = new ArgumentRefExpr(recordSet.recordStruct.getFields().get(0));
		int keyL = recordSet.keys.size();
		this.keyValuesCache = new Object[keyL];
		this.keyStoreFields = new StructFieldDefineImpl[keyL];
		this.hashKeyMatchFields = new StructFieldDefineImpl[keyL];
	}

	private final ArgumentRefExpr dummyArg;

	final ConditionalExpr buildInCondition(TableFieldRefImpl fre, int inCount) {
		if (inCount <= 1000) {
			ValueExpr[] values = new ValueExpr[inCount + 1];
			values[0] = fre;
			for (int k = 1; k <= inCount; k++) {
				values[k] = this.dummyArg;
			}
			return new PredicateExpr(false, PredicateImpl.IN, values);
		} else {
			ValueExpr[] values = new ValueExpr[1001];
			values[0] = fre;
			for (int k = 1; k <= 1000; k++) {
				values[k] = this.dummyArg;
			}
			PredicateExpr predicate = new PredicateExpr(false, PredicateImpl.IN, values);
			int j = inCount / 1000;
			PredicateExpr[] ps = new PredicateExpr[j + 1];
			for (int i = 0; i < j; i++) {
				ps[i] = predicate;
			}
			int k = inCount % 1000;
			ValueExpr[] rest = new ValueExpr[k + 1];
			rest[0] = fre;
			for (int i = 1; i <= k; i++) {
				rest[i] = this.dummyArg;
			}
			ps[j] = new PredicateExpr(false, PredicateImpl.IN, rest);
			return new CombinedExpr(false, false, ps);
		}
	}

	final void readField(StructFieldDefineImpl field) {
		this.targetField = field;
		field.getType().detect(this, null);
		this.columnIndex++;
	}

	final Object readFieldReturn(StructFieldDefineImpl field) {
		this.targetField = field;
		Object o = field.getType().detect(this, Boolean.TRUE);
		this.columnIndex++;
		return o;
	}

	final Object readOnlyReturn(DataType type) {
		Object o = type.detect(this, read_only_return);
		this.columnIndex++;
		return o;
	}

	final void readRecidFieldAndPutToMap(StructFieldDefineImpl field) {
		this.targetField = field;
		Object o = field.getType().detect(this, Boolean.TRUE);
		HashMap<Object, DynObj> recidMap = this.recidMap;
		if (recidMap == null) {
			this.recidMap = recidMap = new HashMap<Object, DynObj>();
		}
		recidMap.put(o, this.obj);
		this.columnIndex++;
	}

	final boolean readRecidAndLocateWithMap() {
		Object o = GUIDType.TYPE.detect(this, read_only_return);
		this.columnIndex++;
		if (o == null) {
			return false;
		}
		return (this.obj = this.recidMap.get(o)) != null;
	}

	private HashMap<Object, DynObj> recidMap;

	final HashMap<Object, DynObj> getRecidMap() {
		if (this.recidMap == null) {
			this.recidMap = new HashMap<Object, DynObj>();
		}
		return this.recidMap;
	}

	/**
	 * RPTRecordSetField对应的结构字段
	 */
	private final ArrayList<StructFieldDefineImpl> fieldsCache = new ArrayList<StructFieldDefineImpl>();

	final void addDataFieldToCache(StructFieldDefineImpl field) {
		this.fieldsCache.add(field);
	}

	private int keyFieldSize;

	final void addKeyFieldToCache(StructFieldDefineImpl storeField,
			StructFieldDefineImpl matchField) {
		int keyFieldSize = this.keyFieldSize;
		if (this.hashKeyMatchFields[keyFieldSize] != matchField) {
			this.hashKeyMatchFields[keyFieldSize] = matchField;
			this.needRebuildHash = true;
		}
		this.keyStoreFields[keyFieldSize] = storeField;
		this.keyFieldSize = keyFieldSize + 1;
	}

	final void resetQuery() {
		this.fieldsCache.clear();
		this.keyFieldSize = 0;
	}

	final void resetLoadTable() {
		if (this.recidMap != null) {
			this.recidMap.clear();
		}
	}

	private int recordHashSize;
	private RPTRecord[] recordHashTable = new RPTRecord[4];
	private final Object[] keyValuesCache;
	private RPTRecordSetTableInfo recordHashTableInfo;
	private final StructFieldDefineImpl[] hashKeyMatchFields;
	private boolean needRebuildHash;
	private final StructFieldDefineImpl[] keyStoreFields;

	private final void locateOrNewRecord(RPTRecordSetTableInfo tableInfo) {
		final RPTRecordSetKeyImpl[] keys = tableInfo.keys;
		final int keyCount = keys.length;
		final StructFieldDefineImpl[] hashKeyMatchFields = this.hashKeyMatchFields;
		RPTRecord[] recordHashTable = this.recordHashTable;
		int oldHashLen = recordHashTable.length;
		// 1. 如果表信息发生变化，则判断是否需要重建键哈西
		if (this.recordHashTableInfo != tableInfo) {
			boolean needRebuildHash = this.needRebuildHash;
			for (int i = keyCount, c = hashKeyMatchFields.length; i < c; i++) {
				if (hashKeyMatchFields[i] != null) {
					hashKeyMatchFields[i] = null;
					needRebuildHash = true;
				}
			}
			// 重建哈西
			if (needRebuildHash) {
				RPTRecord[] newHashTable = new RPTRecord[oldHashLen];
				final int hashHigh = oldHashLen - 1;
				for (int i = 0; i < oldHashLen; i++) {
					for (RPTRecord record = recordHashTable[i]; record != null;) {
						int hash = 0;
						for (int ki = 0; ki < keyCount; ki++) {
							StructFieldDefineImpl keyField = hashKeyMatchFields[ki];
							if (keyField != null) {
								Object keyValue = keyField.getFieldValueAsObject(record);
								if (keyValue == null) {
									hash *= 31;
								} else {
									hash = hash * 31 + keyValue.hashCode();
								}
							}
						}
						RPTRecord next = record.nextSameHash;
						int newIndex = hash & hashHigh;
						record.hash = hash;
						record.nextSameHash = newHashTable[newIndex];
						newHashTable[newIndex] = record;
						record = next;
					}
				}
				this.recordHashTable = recordHashTable = newHashTable;
				this.needRebuildHash = false;
			}
			this.recordHashTableInfo = tableInfo;
		}
		// 2. 装载键值，并计算Hash值
		int hash = 0;
		final Object[] keyValuesCache = this.keyValuesCache;
		for (int i = 0; i < keyCount; i++) {
			Object keyValue = this.readOnlyReturn(keys[i].structField.getType());
			keyValuesCache[i] = keyValue;
			StructFieldDefineImpl matchKeyField = hashKeyMatchFields[i];// 匹配字段
			if (matchKeyField != null) {
				if (keyValue == null) {
					hash *= 31;
				} else {
					hash = hash * 31 + keyValue.hashCode();
				}
			}
		}
		// 3. 查找哈西表，没有找到则新建记录并放入哈西表
		boolean newRecord;
		RPTRecord record;
		hashLocate: {
			record = recordHashTable[hash & (oldHashLen - 1)];
			while (record != null) {
				if (record.hash == hash) {
					recordMatch: {
						for (int i = 0; i < keyCount; i++) {
							StructFieldDefineImpl matchKeyField = hashKeyMatchFields[i];// 匹配字段
							if (matchKeyField != null) {
								Object matchValue = matchKeyField.getFieldValueAsObject(record);
								Object keyValue = keyValuesCache[i];
								if (!(matchValue == keyValue || matchValue != null && keyValue != null && keyValue.equals(matchValue))) {
									break recordMatch;
								}
							}
						}
						// 找到对应记录
						newRecord = false;
						this.obj = record;
						break hashLocate;
					}
				}
				record = record.nextSameHash;
			}
			// 没有找到，需要创建新纪录
			this.obj = record = tableInfo.restriction.recordSet.newRecord(DynObj.r_db);
			newRecord = true;
			record.hash = hash;
			// 将新纪录加入到哈西表中
			if (++this.recordHashSize > oldHashLen * 0.75) {
				int newLen = oldHashLen * 2;
				RPTRecord[] newTable = new RPTRecord[newLen];
				for (int j = 0; j < oldHashLen; j++) {
					for (RPTRecord e = recordHashTable[j], next; e != null; e = next) {
						int index = e.hash & (newLen - 1);
						next = e.nextSameHash;
						e.nextSameHash = newTable[index];
						newTable[index] = e;
					}
				}
				this.recordHashTable = recordHashTable = newTable;
				oldHashLen = newLen;
			}
			int index = hash & (oldHashLen - 1);
			record.nextSameHash = recordHashTable[index];
			recordHashTable[index] = record;
		}
		final StructFieldDefineImpl[] keyStoreFields = this.keyStoreFields;
		// 4. 将临时键值写入记录
		for (int i = 0; i < keyCount; i++) {
			StructFieldDefineImpl keyField = keyStoreFields[i];// 存储字段
			if (keyField == null) {
				if (newRecord) {
					keyField = hashKeyMatchFields[i];// 匹配字段
					if (keyField == null) {
						continue;
					}
				} else {
					continue;
				}
			}
			keyField.setFieldValueAsObject(record, keyValuesCache[i]);
		}
	}

	final int readTablePart(RPTRecordSetTableInfo tableInfo,
			boolean isFirstPart, boolean isLastPart) throws SQLException {
		final ArrayList<StructFieldDefineImpl> fieldsCache = this.fieldsCache;
		final int fieldCount = fieldsCache.size();
		int count = 0;
		while (this.resultSet.next()) {
			this.columnIndex = 1;
			if (isFirstPart) {
				// 装载键值，定位或新建记录，保存键值
				this.locateOrNewRecord(tableInfo);
				// RECVER
				this.readField(tableInfo.recverSf);
				// RECID
				if (isLastPart) {
					this.readField(tableInfo.recidSf);
				} else {
					// 不是最后一张物理表时，为后续物理表记录RECID
					this.readRecidFieldAndPutToMap(tableInfo.recidSf);
				}
			} else {
				if (!this.readRecidAndLocateWithMap()) {
					// 未找到相应的记录
					continue;
				}
			}
			// 装载数据字段
			for (int i = 0; i < fieldCount; i++) {
				this.readField(fieldsCache.get(i));
			}
			count++;
		}
		return count;
	}
}
package com.jiuqi.dna.core.internal.da.report;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.jiuqi.dna.core.da.ext.RPTRecordSetKeyRestriction;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.DateType;
import com.jiuqi.dna.core.impl.GUIDType;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.report.RPTRecordSetRecordDefine.RPTRecord;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;

final class RPTRecordSetKeyRestrictionImpl extends RPTRecordSetColumnImpl
		implements RPTRecordSetKeyRestriction {

	@Override
	public final String toString() {
		return "KeyRstr:".concat(this.structField.name);
	}

	final RPTRecordSetKeyImpl key;

	private RPTRecordSetKeyRestrictionImpl matchKeyRestriction;

	final RPTRecordSetKeyRestrictionImpl useKeyRestriction(
			RPTRecordSetRecordReader reader) {
		RPTRecordSetKeyRestrictionImpl dkr = this.matchKeyRestriction;
		final StructFieldDefineImpl matchField;
		StructFieldDefineImpl storeField = this.structField;
		if (dkr != null) {
			matchField = dkr.structField;
			if (matchField == storeField) {
				storeField = null;
			}
		} else {
			matchField = null;
		}
		reader.addKeyFieldToCache(storeField, matchField);
		return this.getMatchValueCount() > 0 ? this : null;
	}

	private boolean tryUpdateKeyFieldValue(RPTRecord record,
			StructFieldDefineImpl field) {
		if (this.valueMapSize == 1) {
			// 只在匹配单值时使用匹配值!
			for (Entry e : this.valueMapTable) {
				if (e != null) {
					field.setFieldValueAsObject(record, e.value);
					break;
				}
			}
			return true;
		} else {
			final RPTRecordSetKeyRestrictionImpl dkr = this.matchKeyRestriction;
			return dkr != null && dkr.structField == field && dkr.tryUpdateKeyFieldValue(record, field);
		}
	}

	final boolean tryUpdateKeyFieldValueIfNull(RPTRecord record) {
		StructFieldDefineImpl field = this.structField;
		// 如果为空,则尝试设值
		return !field.isFieldValueNull(record) || this.tryUpdateKeyFieldValue(record, field);
	}

	/**
	 * 将当前键约束的值以SQL语句参数值的形式准备到参数值的缓存列表中
	 * 
	 * @param params
	 */
	final void fillAsSqlParams(ArrayList<Object> params) {
		final DataType valueType = this.structField.getType();
		if (valueType == GUIDType.TYPE) {
			for (Entry e : this.valueMapTable) {
				while (e != null) {
					params.add(((GUID) e.value).toBytes());
					e = e.next;
				}
			}
		} else if (valueType == DateType.TYPE) {
			for (Entry e : this.valueMapTable) {
				while (e != null) {
					params.add(new Timestamp((Long) e.value));
					e = e.next;
				}
			}
		} else {
			for (Entry e : this.valueMapTable) {
				while (e != null) {
					params.add(e.value);
					e = e.next;
				}
			}
		}
	}

	final void ensurePrepared() {
		final RPTRecordSetKeyRestrictionImpl keyDefaultKr = this.key.defaultKeyRestriction;
		if (this == keyDefaultKr) {
			// 即当前约束没有覆盖定义键约束
			return;
		}
		final StructFieldDefineImpl oldField = this.structField;
		final RPTRecordSetKeyRestrictionImpl oldKr = this.matchKeyRestriction;
		if (this.valueMapSize == 0) {
			RPTRecordSetKeyRestrictionImpl newKr;
			// 如果为空
			if (oldKr == null) {
				this.matchKeyRestriction = newKr = keyDefaultKr;
			} else {
				newKr = oldKr;
				newKr.ensurePrepared();
			}
			StructFieldDefineImpl newField = newKr.structField;
			if (newField != oldField) {
				if (newField == this.key.structField && (oldKr == null || oldField != oldKr.structField)) {
					this.recordSet.recordStruct.getFields().remove(oldField);
				}
				this.structField = newField;
			}
		} else if (oldField == this.key.structField) {
			// 即表示对Restriction的KeyRestriction单独设置了匹配参数!
			// 原本默认的match为keyDefaultKr,则置空.
			if (oldKr == keyDefaultKr) {
				this.matchKeyRestriction = null;
			}
			// 并且建立独立的存储
			this.structField = this.recordSet.recordStruct.newField(this.key.getType());
		}
	}

	RPTRecordSetKeyRestrictionImpl(RPTRecordSetKeyImpl key) {
		super(key.recordSet, key.index, key.structField);
		this.key = key;
		this.matchKeyRestriction = this.key.defaultKeyRestriction;
	}

	public final RPTRecordSetKeyImpl getKey() {
		return this.key;
	}

	public final int addMatchValue(Object keyValue) {
		return this.addMatchValue(keyValue, null);
	}

	public final Object removeMatchValue(Object keyValue) {
		if (keyValue == null) {
			throw new NullArgumentException("keyValue");
		}
		keyValue = this.structField.getType().convert(keyValue);
		Entry[] table = this.valueMapTable;
		final int hash = keyValue.hashCode();
		final int index = hash & (table.length - 1);
		for (Entry e = table[index], last = null; e != null; last = e, e = e.next) {
			if (e.hash == hash) {
				Object oldValue = e.value;
				if (oldValue == keyValue || oldValue.equals(keyValue)) {
					if (last == null) {
						table[index] = e.next;
					} else {
						last.next = e.next;
					}
					e.next = null;
					Object matchTo = e.matchTo;
					if (matchTo != null) {
						this.matchKeyValueCount--;
					}
					return matchTo;
				}
			}
		}
		return null;
	}

	public final int getMatchValueCount() {
		return this.valueMapSize;
	}

	public final void clearMatchValues() {
		if (this.valueMapSize > 0) {
			Entry[] table = this.valueMapTable;
			for (int i = 0, c = table.length; i < c; i++) {
				table[i] = null;
			}
			this.valueMapSize = 0;
		}
	}

	public final int addMatchValue(Object keyValue, Object matchKeyValue) {
		if (keyValue == null) {
			throw new NullArgumentException("keyValue");
		}
		RPTRecordSetKeyRestrictionImpl matchKr = this.matchKeyRestriction;
		if (matchKr != null) {
			if (matchKeyValue != null) {
				matchKeyValue = matchKr.structField.getType().convert(matchKeyValue);
				this.matchKeyValueCount++;
			}
		} else if (matchKeyValue != null) {
			throw new IllegalArgumentException("matchKeyValue 不可有值");
		}
		keyValue = this.structField.getType().convert(keyValue);
		Entry[] table = this.valueMapTable;
		int tableL = table.length;
		final int hash = keyValue.hashCode();
		int index = hash & (tableL - 1);
		for (Entry e = table[index]; e != null; e = e.next) {
			if (e.hash == hash) {
				Object oldValue = e.value;
				if (oldValue == keyValue || oldValue.equals(keyValue)) {
					e.matchTo = matchKeyValue;
					return this.valueMapSize;
				}
			}
		}
		int newSize = this.valueMapSize + 1;
		if ((this.valueMapSize = newSize) > tableL * 0.75) {
			int newLen = tableL * (tableL < 16 ? 4 : 2);
			int newHigh = newLen - 1;
			Entry[] newTable = new Entry[newLen];
			for (int j = 0; j < tableL; j++) {
				for (Entry e = table[j], next; e != null; e = next) {
					int i = e.hash & newHigh;
					next = e.next;
					e.next = newTable[i];
					newTable[i] = e;
				}
			}
			this.valueMapTable = table = newTable;
			tableL = newLen;
			index = hash & newHigh;
		}
		Entry newEntry = new Entry();
		newEntry.next = table[index];
		newEntry.value = keyValue;
		newEntry.hash = hash;
		newEntry.matchTo = matchKeyValue;
		table[index] = newEntry;
		return newSize;
	}

	public final RPTRecordSetKeyRestrictionImpl setMatchKeyRestriction(
			RPTRecordSetKeyRestriction matchKeyRestriction) {
		RPTRecordSetKeyRestrictionImpl oldKr = this.matchKeyRestriction;
		RPTRecordSetKeyRestrictionImpl newKr = (RPTRecordSetKeyRestrictionImpl) matchKeyRestriction;
		if (newKr != null && newKr.recordSet != this.recordSet && newKr.generation != this.generation) {
			throw new IllegalArgumentException("matchKeyRestriction");
		}
		if (oldKr != newKr) {
			DataType oldMatchType = oldKr != null ? oldKr.structField.getType().getRootType() : null;
			DataType newMatchType = newKr != null ? newKr.structField.getType().getRootType() : null;
			if (newMatchType != oldMatchType) {
				for (Entry e : this.valueMapTable) {
					while (e != null) {
						e.matchTo = null;
						e = e.next;
					}
				}
				this.matchKeyValueCount = 0;
			}
			this.matchKeyRestriction = newKr;
		}
		return oldKr;
	}

	static class Entry {
		int hash;
		Object value;
		Object matchTo;
		Entry next;

		@Override
		public String toString() {
			return this.value == null ? null : this.value.toString();
		}
	}

	private Entry[] valueMapTable = new Entry[1];
	private int valueMapSize;
	private int matchKeyValueCount;
}
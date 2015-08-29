package com.jiuqi.dna.core.internal.da.report;

import java.sql.SQLException;
import java.util.ArrayList;

import com.jiuqi.dna.core.da.ext.RPTRecordSetField;
import com.jiuqi.dna.core.da.ext.RPTRecordSetKey;
import com.jiuqi.dna.core.da.ext.RPTRecordSetRestriction;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.StructFieldDefineImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.report.RPTRecordSetRecordDefine.RPTRecord;

/**
 * 记录集约束
 * 
 * @author gaojingxin
 * 
 */
final class RPTRecordSetRestrictionImpl implements RPTRecordSetRestriction {

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0, c = this.tables.size(); i < c; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(this.tables.get(i).table.name);
		}
		sb.append(']');
		return sb.toString();
	}

	/**
	 * 当前约束使用的键约束，会覆盖记录集定义的键约束。
	 * 
	 * <p>
	 * 默认为空。当有尝试获取约束级别的键约束时，才会构造对象。 <br>
	 * 初始情况下，使用键对应的结构字段，匹配约束为记录集的键约束。
	 * 
	 */
	private RPTRecordSetKeyRestrictionImpl[] keyRestrictions;

	/**
	 * 当前约束内，有效的Key。
	 * 
	 * <p>
	 * 大小同记录集的Key大小，每位对应标识是否有效。
	 * 
	 * <p>
	 * 当约束有输出列时，才不为空。
	 * 
	 */
	private boolean[] validKeys;

	final StructFieldDefineImpl tryGetKeyRestrictionField(int index) {
		if (this.keyRestrictions == null) {
			return null;
		} else if (index < this.keyRestrictions.length) {
			RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
			if (kr != null) {
				return kr.structField;
			}
		}
		return null;
	}

	public final boolean isKeySupported(RPTRecordSetKey key) {
		RPTRecordSetKeyImpl k = (RPTRecordSetKeyImpl) key;
		if (k.recordSet != this.recordSet && k.generation != this.recordSet.generation) {
			throw new IllegalArgumentException("键约束非法。");
		}
		return this.isKeySupported(k.index);
	}

	final void ensurePrepared() {
		if (this.keyRestrictions != null) {
			for (RPTRecordSetKeyRestrictionImpl kr : this.keyRestrictions) {
				if (kr != null) {
					kr.ensurePrepared();
				}
			}
		}
	}

	/**
	 * 更新当前约束的键相关信息
	 * 
	 * <p>
	 * 包括有效键的标识，以及更新键约束的存储空间。
	 */
	final int updateSupportedKeys() {
		int oldKeyCount = this.validKeys != null ? this.validKeys.length : 0;
		int newKeyCount = this.recordSet.keys.size();
		if (oldKeyCount < newKeyCount) {
			RPTRecordSetKeyRestrictionImpl[] newKeyRstr = new RPTRecordSetKeyRestrictionImpl[newKeyCount];
			boolean[] newValidKeys = new boolean[newKeyCount];
			if (oldKeyCount > 0) {
				System.arraycopy(this.keyRestrictions, 0, newKeyRstr, 0, oldKeyCount);
				System.arraycopy(this.validKeys, 0, newValidKeys, 0, oldKeyCount);
			}
			this.keyRestrictions = newKeyRstr;
			this.validKeys = newValidKeys;
		}
		return newKeyCount;
	}

	public final boolean isKeySupported(int index) {
		int size = this.validKeys != null ? this.validKeys.length : 0;
		if (index >= size) {
			size = this.updateSupportedKeys();
		}
		if (index < 0 || size <= index) {
			throw new IndexOutOfBoundsException("key count:" + size + ",index:" + index);
		}
		return this.validKeys[index];
	}

	final RPTRecordSetImpl recordSet;

	final int generation;

	final ArrayList<RPTRecordSetTableInfo> tables = new ArrayList<RPTRecordSetTableInfo>(1);

	final RPTRecordSetFieldImpl internalNewField(
			TableFieldDefineImpl tableField, boolean usingBigDecimal) {
		if (this.generation != this.recordSet.generation) {
			throw new IllegalStateException("对象已经失效。");
		}
		RPTRecordSetTableInfo tableInfo;
		ensureTableInfo: {
			TableDefineImpl table = tableField.owner;
			for (int i = 0, c = this.tables.size(); i < c; i++) {
				RPTRecordSetTableInfo ti = this.tables.get(i);
				if (ti.table == table) {
					tableInfo = ti;
					break ensureTableInfo;
				}
			}
			this.tables.add(tableInfo = new RPTRecordSetTableInfo(this, table));
			this.updateSupportedKeys();
			for (RPTRecordSetKeyImpl key : tableInfo.keys) {
				this.validKeys[key.index] = true;
			}
		}
		return tableInfo.newField(tableField, usingBigDecimal);
	}

	public final void clearMatchValues() {
		if (this.keyRestrictions != null) {
			for (RPTRecordSetKeyRestrictionImpl kr : this.keyRestrictions) {
				if (kr != null) {
					kr.clearMatchValues();
				}
			}
		}
	}

	final int index;

	RPTRecordSetRestrictionImpl(RPTRecordSetImpl recordSet) {
		this.recordSet = recordSet;
		this.generation = recordSet.generation;
		this.index = recordSet.records.size();
	}

	/**
	 * 获取使用的键约束
	 * 
	 * @param index
	 * @param reader
	 * @return
	 */
	final RPTRecordSetKeyRestrictionImpl useKeyRestriction(int index,
			RPTRecordSetRecordReader reader) {
		if (!this.isKeySupported(index)) {
			return null;
		}
		RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
		if (kr == null) {
			kr = this.recordSet.keys.get(index).defaultKeyRestriction;
			reader.addKeyFieldToCache(null, kr.structField);
			return kr;
		} else {
			return kr.useKeyRestriction(reader);
		}
	}

	final RPTRecordSetKeyRestrictionImpl useKeyRestriction(int index) {
		if (!this.isKeySupported(index)) {
			return null;
		}
		RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
		if (kr == null) {
			return this.recordSet.keys.get(index).defaultKeyRestriction;
		} else {
			return kr;
		}
	}

	final RPTRecordSetKeyRestrictionImpl getKeyRestrictionNoCheck(int index) {
		if (!this.isKeySupported(index)) {
			return null;
		}
		RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
		if (kr == null) {
			return this.recordSet.keys.get(index).defaultKeyRestriction;
		} else {
			return kr;
		}
	}

	/**
	 * 如果记录的key值为空,尝试设值
	 * 
	 * @param record
	 *            结果对象
	 * @param index
	 *            键的序号
	 * @return
	 */
	final boolean tryUpdateKeyFieldValueIfNull(RPTRecord record, int index) {
		if (!this.isKeySupported(index)) {
			return false;
		}
		RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
		// 自身的键约束为空
		if (kr == null) {
			// 则使用RecordSet的默认约束
			kr = this.recordSet.keys.get(index).defaultKeyRestriction;
		}
		return kr.tryUpdateKeyFieldValueIfNull(record);
	}

	public final RPTRecordSetKeyRestrictionImpl getKeyRestriction(int index) {
		if (!this.isKeySupported(index)) {
			throw new IllegalArgumentException("本约束不支持键[" + this.recordSet.getKey(index).getName() + "]");
		}
		RPTRecordSetKeyRestrictionImpl[] krs = this.keyRestrictions;
		RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
		if (kr == null) {
			krs[index] = kr = new RPTRecordSetKeyRestrictionImpl(this.recordSet.keys.get(index));
		}
		return kr;
	}

	public final RPTRecordSetKeyRestrictionImpl getKeyRestriction(
			RPTRecordSetKey key) {
		if (key == null) {
			throw new NullArgumentException("key");
		}
		RPTRecordSetKeyImpl k = (RPTRecordSetKeyImpl) key;
		RPTRecordSetKeyRestrictionImpl r = this.getKeyRestriction(k.index);
		if (r.key != k) {
			throw new IllegalArgumentException("key");
		}
		return r;
	}

	public final RPTRecordSetKeyRestrictionImpl getKeyRestriction(String keyName) {
		return this.getKeyRestriction(this.recordSet.getKey(keyName).index);
	}

	final void load(DBAdapterImpl dbAdapter, RPTRecordSetRecordReader reader)
			throws SQLException {
		for (int i = 0, c = this.tables.size(); i < c; i++) {
			RPTRecordSetTableInfo tableInfo = this.tables.get(i);
			tableInfo.load(dbAdapter, reader);
		}
	}

	public final RPTRecordSetField newField(TableFieldDefine tableField) {
		return this.internalNewField((TableFieldDefineImpl) tableField, false);
	}

	public final RPTRecordSetField newField(TableFieldDefine tableField,
			boolean usingBigDecimal) {
		return this.internalNewField((TableFieldDefineImpl) tableField, usingBigDecimal);
	}
}

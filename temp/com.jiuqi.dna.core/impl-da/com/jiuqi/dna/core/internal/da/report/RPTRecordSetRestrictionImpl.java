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
 * ��¼��Լ��
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
	 * ��ǰԼ��ʹ�õļ�Լ�����Ḳ�Ǽ�¼������ļ�Լ����
	 * 
	 * <p>
	 * Ĭ��Ϊ�ա����г��Ի�ȡԼ������ļ�Լ��ʱ���Żṹ����� <br>
	 * ��ʼ����£�ʹ�ü���Ӧ�Ľṹ�ֶΣ�ƥ��Լ��Ϊ��¼���ļ�Լ����
	 * 
	 */
	private RPTRecordSetKeyRestrictionImpl[] keyRestrictions;

	/**
	 * ��ǰԼ���ڣ���Ч��Key��
	 * 
	 * <p>
	 * ��Сͬ��¼����Key��С��ÿλ��Ӧ��ʶ�Ƿ���Ч��
	 * 
	 * <p>
	 * ��Լ���������ʱ���Ų�Ϊ�ա�
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
			throw new IllegalArgumentException("��Լ���Ƿ���");
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
	 * ���µ�ǰԼ���ļ������Ϣ
	 * 
	 * <p>
	 * ������Ч���ı�ʶ���Լ����¼�Լ���Ĵ洢�ռ䡣
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
			throw new IllegalStateException("�����Ѿ�ʧЧ��");
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
	 * ��ȡʹ�õļ�Լ��
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
	 * �����¼��keyֵΪ��,������ֵ
	 * 
	 * @param record
	 *            �������
	 * @param index
	 *            �������
	 * @return
	 */
	final boolean tryUpdateKeyFieldValueIfNull(RPTRecord record, int index) {
		if (!this.isKeySupported(index)) {
			return false;
		}
		RPTRecordSetKeyRestrictionImpl kr = this.keyRestrictions[index];
		// ����ļ�Լ��Ϊ��
		if (kr == null) {
			// ��ʹ��RecordSet��Ĭ��Լ��
			kr = this.recordSet.keys.get(index).defaultKeyRestriction;
		}
		return kr.tryUpdateKeyFieldValueIfNull(record);
	}

	public final RPTRecordSetKeyRestrictionImpl getKeyRestriction(int index) {
		if (!this.isKeySupported(index)) {
			throw new IllegalArgumentException("��Լ����֧�ּ�[" + this.recordSet.getKey(index).getName() + "]");
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

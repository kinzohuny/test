package com.jiuqi.dna.core.da.ext;

import com.jiuqi.dna.core.da.DBAdapter;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.internal.da.report.RPTRecordSetImpl.FactoryImpl;
import com.jiuqi.dna.core.misc.MissingObjectException;

/**
 * ������ѯ�����ݼ�
 * 
 * @author houchunlei
 * 
 * @deprecated
 */
public interface RPTRecordSet {

	/**
	 * @deprecated
	 *
	 */
	public interface Factory {

		public RPTRecordSet newRPTRecordSet();
	}

	public final static Factory factory = new FactoryImpl();

	/**
	 * ��ղ�ѯ���弰��ѯ���
	 */
	public void reset();

	/**
	 * ��ü�¼��Ĭ�ϵ�Լ��
	 * 
	 * <p>
	 * Ĭ��Լ����ÿ����Լ��һ��ͨ��RPTRecordSetKey.getDefaultKeyRestriction()��ø�����<br>
	 */
	public RPTRecordSetRestriction getFirstRestriction();

	/**
	 * ���ݼ�Լ���������Լ�������½��ֶ�ʱʹ��
	 * 
	 * <p>
	 * ����ֶ�ָ���˶�����Լ������ö���Լ����ֵΪ�յļ�Լ��ʹ��RPTRecordSet��Ĭ��Լ��
	 */
	public RPTRecordSetRestriction newRestriction();

	/**
	 * �����ֶθ���
	 */
	public int getFieldCount();

	/**
	 * �½���¼�ֶΣ�ʹ��Ĭ�ϵ�Լ��
	 */
	public RPTRecordSetField newField(TableFieldDefine tableField);

	public RPTRecordSetField newField(TableFieldDefine tableField,
			boolean usingBigDecimal);

	/**
	 * ���ĳλ�õ��ֶ�
	 */
	public RPTRecordSetField getField(int index);

	/**
	 * ���OrderBy�ĸ���
	 */
	public int getOrderByCount();

	/**
	 * ����������
	 * 
	 * @param desc
	 *            �Ƿ���
	 */
	public RPTRecordSetOrderBy newOrderBy(RPTRecordSetColumn column,
			boolean isDesc);

	public RPTRecordSetOrderBy newOrderBy(RPTRecordSetColumn column,
			boolean isDesc, boolean isNullAsMIN);

	/**
	 * ��������������
	 */
	public RPTRecordSetOrderBy newOrderBy(RPTRecordSetColumn column);

	/**
	 * ���OrderBy
	 */
	public RPTRecordSetOrderBy getOrderBy(int index);

	/**
	 * ��ȡ������
	 */
	public int getKeyCount();

	/**
	 * ��ȡ��
	 */
	public RPTRecordSetKey getKey(int index);

	/**
	 * ���ݼ����Ʋ��Ҽ����Ҳ����򷵻�null
	 */
	public RPTRecordSetKey findKey(String keyName);

	/**
	 * ���ݼ����Ʋ��Ҽ�,�Ҳ������׳��쳣
	 */
	public RPTRecordSetKey getKey(String keyName) throws MissingObjectException;

	/**
	 * װ�����ݼ�
	 * 
	 * @return ���ؼ�¼����
	 */
	public int load(DBAdapter dbAdapter);

	/**
	 * װ�����ݼ�
	 * 
	 * @param dbAdapter
	 *            ���ݿ�������
	 * @param offset
	 *            Ҫ�󷵻صļ�¼��ƫ����
	 * @param rowCount
	 *            Ҫ�󷵻صļ�¼�ĸ���
	 * @return ���ؼ�¼����
	 */
	public int load(DBAdapter dbAdapter, int offset, int rowCount);

	/**
	 * ��ȡ���ݿ��з��������ļ�¼����
	 * 
	 * @param dbAdapter
	 *            ������
	 * @return �������ݿ��з��������ļ�¼����
	 */
	public int getRecordCountInDB(DBAdapter dbAdapter);

	/**
	 * ��ü�¼����
	 */
	public int getRecordCount();

	/**
	 * ��õ�ǰ��¼λ��
	 */
	public int getCurrentRecordIndex();

	/**
	 * ���õ�ǰ��¼λ��
	 */
	public void setCurrentRecordIndex(int recordIndex);

	/**
	 * �½���Ŀ��������Ϊ��ǰλ��
	 * 
	 * @return �����¼�¼��λ��
	 */
	public int newRecord();

	/**
	 * ɾ����¼
	 */
	public void remove(int recordIndex);

	/**
	 * ɾ����ǰ��¼
	 */
	public void removeCurrentRecord();

	/**
	 * �������ݼ�
	 * 
	 * @return ���ظ��¸���
	 */
	public int update(DBAdapter dbAdapter);
}
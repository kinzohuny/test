package com.jiuqi.dna.core.da.ext;

/**
 * ��Լ��
 * 
 * @deprecated
 * 
 */
public interface RPTRecordSetKeyRestriction extends RPTRecordSetColumn {

	/**
	 * ����ƥ���ü�Լ����Ĭ�������ƥ�����Ĭ��Լ��
	 */
	public RPTRecordSetKeyRestriction setMatchKeyRestriction(
			RPTRecordSetKeyRestriction matchKeyRestriction);

	/**
	 * ���Լ����
	 */
	public RPTRecordSetKey getKey();

	/**
	 * ��Ӽ�Լ��
	 */
	public int addMatchValue(Object keyValue);

	/**
	 * ��ӱ�Լ���ļ�Լ����ͬʱ����ƥ���Լ����ƥ��ֵ
	 */
	public int addMatchValue(Object keyValue, Object matchKeyValue);

	/**
	 * ���Լ��ֵ
	 */
	public Object removeMatchValue(Object keyValue);

	/**
	 * ��ȡԼ������
	 */
	public int getMatchValueCount();

	/**
	 * ���Լ��
	 */
	public void clearMatchValues();
}

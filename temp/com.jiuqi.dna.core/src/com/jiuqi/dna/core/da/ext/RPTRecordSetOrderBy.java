package com.jiuqi.dna.core.da.ext;

/**
 * ������
 * 
 * @deprecated
 */
public interface RPTRecordSetOrderBy {
	/**
	 * ��ö�Ӧ��
	 */
	public RPTRecordSetColumn getColumn();

	/**
	 * ����Ƿ���
	 */
	public boolean isDesc();

	/**
	 * ��ȡ��ֵ�Ƿ���Ϊ��Сֵ����
	 */
	public boolean isNullAsMIN();
}

package com.jiuqi.dna.core.type;

/**
 * �ַ�������
 * 
 * @author gaojingxin
 * 
 */
public interface SequenceDataType extends ObjectDataType {
	/**
	 * ��ȡ��󳤶�С�ڵ���0��ʾû������
	 */
	public int getMaxLength();

	/**
	 * �Ƿ��Ƕ�������
	 */
	public boolean isFixedLength();
}

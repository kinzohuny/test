package com.jiuqi.dna.core.type;


/**
 * ֵ�������������������ú�ʹ��ڵ�һ��λ��
 * 
 * @author gaojingxin
 * 
 */
public interface ValueIterator extends ReadableValue {
	/**
	 * �ƶ�����һλ�ã��������Ƿ���Ч��
	 * 
	 * @return �����Ƿ���Ч
	 */
	public boolean next();
	/**
	 * ���ص�ǰλ���Ƿ���Ч
	 * 
	 * @return �����Ƿ���Ч
	 */
	public boolean valid();
}

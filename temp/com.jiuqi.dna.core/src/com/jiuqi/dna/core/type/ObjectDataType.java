package com.jiuqi.dna.core.type;

/**
 * ��������
 * 
 * @author gaojingxin
 * 
 */
public interface ObjectDataType extends DataType {
	/**
	 * �Ƿ��Ǹ�ö�����͵�ʵ��
	 */
	public boolean isInstance(Object obj);
}

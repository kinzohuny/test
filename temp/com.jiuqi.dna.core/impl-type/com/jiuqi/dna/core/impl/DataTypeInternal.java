package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.DataType;

/**
 * �ڲ����ͽӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface DataTypeInternal extends DataType {

	public void setArrayOf(ArrayDataTypeBase type);

	/**
	 * ���ص�ǰ���͵���������
	 */
	public ArrayDataTypeBase arrayOf();

	public DataTypeInternal getRootType();

	/**
	 * �������ע�����͵�Java���ͣ�����null��ʾ����ע��Java����Ӱ��
	 */
	public Class<?> getRegClass();
}

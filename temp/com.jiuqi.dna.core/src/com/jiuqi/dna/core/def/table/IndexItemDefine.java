package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.DefineBase;

/**
 * �����ֶζ���
 * 
 * @author gaojingxin
 * 
 */
public interface IndexItemDefine extends DefineBase {

	/**
	 * �����ֶ�
	 * 
	 * @return ���������ֶ�
	 */
	public TableFieldDefine getField();

	/**
	 * �Ƿ�������
	 * 
	 * @return ����
	 */
	public boolean isDesc();
}
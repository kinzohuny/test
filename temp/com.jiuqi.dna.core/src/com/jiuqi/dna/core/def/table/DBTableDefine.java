package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.NamedDefine;

/**
 * �������
 * 
 * @author gaojingxin
 * 
 */
public interface DBTableDefine extends NamedDefine {

	/**
	 * ��������
	 */
	public TableDefine getOwner();

	/**
	 * ������ڸ��������ֶθ���
	 */
	public int getFieldCount();

	/**
	 * ��ȡ������������ݿ��е�����
	 * 
	 * @return
	 */
	public String getNameInDB();
	
	/**
	 * ��ȡ���������
	 * 
	 * @return TableType
	 * 			NORMAL����ͨ����GLOBAL_TEMPORARY��ȫ����ʱ��
	 */
	public TableType getTableType();
}
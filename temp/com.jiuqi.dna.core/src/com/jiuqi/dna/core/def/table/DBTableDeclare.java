package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.NamedDeclare;
import com.jiuqi.dna.core.type.DataType;

/**
 * �������
 * 
 * @author gaojingxin
 * 
 */
public interface DBTableDeclare extends DBTableDefine, NamedDeclare {

	/**
	 * ��������
	 */
	public TableDeclare getOwner();

	/**
	 * �����洢�ڸ�������е��ֶ�
	 */
	public TableFieldDeclare newField(String name, DataType type);
	
	/**
	 * ������������ͣ�NORMAL����ͨ����GLOBAL_TEMPORARY��ȫ����ʱ��
	 * 
	 * @param type void
	 */
	public void setTableType(TableType type);
	
	/**
	 * ��ȡ���������
	 * 
	 * @return TableType
	 * 			NORMAL����ͨ����GLOBAL_TEMPORARY��ȫ����ʱ��
	 */
	public TableType getTableType();
}
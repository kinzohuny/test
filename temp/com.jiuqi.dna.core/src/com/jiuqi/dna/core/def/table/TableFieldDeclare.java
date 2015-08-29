package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.FieldDeclare;
import com.jiuqi.dna.core.def.query.RelationColumnDeclare;
import com.jiuqi.dna.core.type.DataType;

/**
 * �߼��ֶζ���
 * 
 * @author gaojingxin
 * 
 */
public interface TableFieldDeclare extends TableFieldDefine, FieldDeclare,
		RelationColumnDeclare {

	/**
	 * ����
	 */
	public TableDeclare getOwner();

	/**
	 * ���������������
	 */
	public DBTableDeclare getDBTable();

	/**
	 * �����Ƿ����߼�����
	 */
	public void setPrimaryKey(boolean value);

	/**
	 * ����Ĭ��ֵ
	 * 
	 * @param value
	 *            �������������ֶε������������
	 */
	public void setDefault(Object value);

	/**
	 * ���Ըı��ֶεĳ��Ȼ򾫶ȣ�ֻ��Է�LOB���ַ����Ͷ����ƴ���������С��������Ч��
	 * 
	 * @param newType
	 * @return �����Ƿ������ǰ�ֶε��������ͳ�Ϊָ������������
	 */
	public boolean adjustType(DataType newType);

	/**
	 * �������ݿ���������
	 * 
	 * <p>
	 * �Ѿ������ķ��������ø÷���û���κ�Ч����
	 */
	@Deprecated
	public void setNameInDB(String nameInDB);

	/**
	 * �����Ƿ���Ϊģ���ֶ�
	 * 
	 * @param templated
	 */
	public void setTemplated(boolean templated);
}
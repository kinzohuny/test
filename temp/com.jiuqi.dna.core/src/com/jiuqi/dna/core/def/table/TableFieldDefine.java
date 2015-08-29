package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.FieldDefine;
import com.jiuqi.dna.core.def.exp.ConstExpression;
import com.jiuqi.dna.core.def.query.RelationColumnDefine;

/**
 * �߼��ֶζ���
 * 
 * @author gaojingxin
 * 
 */
public interface TableFieldDefine extends FieldDefine, RelationColumnDefine {

	/**
	 * ��ȡ�������߼�����
	 */
	public TableDefine getOwner();

	/**
	 * ��ȡ�ֶ�ʵ�ʴ洢�����ݿ����
	 * 
	 * @return
	 */
	public DBTableDefine getDBTable();

	/**
	 * ��ȡ�Ƿ��������ֶΣ��߼�������
	 * 
	 * @return
	 */
	public boolean isPrimaryKey();

	/**
	 * ��ȡ�Ƿ��Ǽ�¼�б�ʶ�ֶ�(RECID)
	 * 
	 * @return
	 */
	public boolean isRECID();

	/**
	 * ��ȡ�Ƿ����а汾�ֶ�(RECVER)
	 * 
	 * @return
	 */
	public boolean isRECVER();

	public ConstExpression getDefault();

	/**
	 * ��ȡ�����ݿ��У��洢���ֶε�������
	 * 
	 * <p>
	 * Ĭ��Ϊ��д�����ֶ����ơ��������ݿ��DNA-SQL�Ĺؼ��ֳ�ͻʱ�������ֶ����Ƶĳ��ȳ������ݿ�����ʱ���ᱻ���������ԡ�_1�����ƵĹ���
	 * 
	 * @return ����ʵ����
	 */
	public String getNameInDB();

	/**
	 * �Ƿ�Ϊģ���ֶ�
	 * 
	 * @return
	 */
	public boolean isTemplated();
}
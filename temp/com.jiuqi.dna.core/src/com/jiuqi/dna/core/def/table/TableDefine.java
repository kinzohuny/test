package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.MetaElement;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.def.query.RelationDefine;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.type.TupleType;

/**
 * �߼�����
 * 
 * @author gaojingxin
 */
@SuppressWarnings("deprecation")
public interface TableDefine extends TablePartitionDefine, MetaElement,
		TupleType, RelationDefine {

	/**
	 * DUMMY��,������Oracle�е�dual��.
	 */
	public static final TableDefine DUMMY = TableDefineImpl.DUMMY;

	/**
	 * �Ƿ���ԭ����
	 * 
	 * <p>
	 * ԭ�����ʾͨ��TableDeclarator����̻��ľ�̬�߼���
	 * 
	 * @return
	 */
	public boolean isOriginal();

	/**
	 * ���ر�����б�ʶ�е��ֶζ���
	 * 
	 * @return
	 */
	public TableFieldDefine f_RECID();

	/**
	 * ���ر�����а汾�е��ֶζ���
	 * 
	 * @return
	 */
	public TableFieldDefine f_RECVER();

	/**
	 * ��ȡ��������б�
	 * 
	 * @return
	 */
	public NamedElementContainer<? extends DBTableDefine> getDBTables();

	/**
	 * ��ȡ���������
	 * 
	 * @return
	 */
	public DBTableDefine getPrimaryDBTable();

	public TableFieldDefine findColumn(String columnName);

	public TableFieldDefine getColumn(String columnName);

	/**
	 * ��ȡ�ֶζ����б�
	 * 
	 * @return
	 */
	public NamedElementContainer<? extends TableFieldDefine> getFields();

	/**
	 * ������������б�
	 * 
	 * @return �������������б�
	 */
	public NamedElementContainer<? extends IndexDefine> getIndexes();

	/**
	 * ��ñ��ϵ�����б�
	 * 
	 * @return ���ر��ϵ�����б�
	 */
	public NamedElementContainer<? extends TableRelationDefine> getRelations();

	/**
	 * ��ü��ζ����б�
	 * 
	 * @return ���ؼ��ζ����б�
	 */
	@Deprecated
	public NamedElementContainer<? extends HierarchyDefine> getHierarchies();

	/**
	 * ��ñ�ķ���
	 */
	public String getCategory();
	
	/**
	 * ��ȡ�߼�������
	 * 
	 * @return TableType
	 * 			NORMAL����ͨ����GLOBAL_TEMPORARY��ȫ����ʱ��
	 */
	public TableType getTableType();
}
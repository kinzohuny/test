package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.ModifiableContainer;
import com.jiuqi.dna.core.def.NamedDeclare;

/**
 * �����õ��������������
 * 
 * @author gaojingxin
 * 
 */
public interface IndexDeclare extends IndexDefine, NamedDeclare {

	/**
	 * ����
	 */
	public TableDeclare getOwner();

	/**
	 * �����Ƿ���Ψһ����
	 */
	public void setUnique(boolean value);

	/**
	 * ������������ֶε�ö����
	 * 
	 * @return �����еĵ�����
	 */
	public ModifiableContainer<? extends IndexItemDeclare> getItems();

	/**
	 * ���������ֶ�
	 * 
	 * @param field
	 */
	public IndexItemDeclare addItem(TableFieldDefine field);

	/**
	 * ���������ֶ�
	 * 
	 * @param field
	 * @param desc
	 * @return
	 */
	public IndexItemDeclare addItem(TableFieldDefine field, boolean desc);
}
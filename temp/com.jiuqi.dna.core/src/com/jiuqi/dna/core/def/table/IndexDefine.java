package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.ModifiableContainer;
import com.jiuqi.dna.core.def.NamedDefine;

/**
 * �������������
 * 
 * @author gaojingxin
 * 
 */
public interface IndexDefine extends NamedDefine {

	/**
	 * ����
	 * 
	 * @return
	 */
	public TableDefine getOwner();

	/**
	 * �Ƿ���Ψһ����
	 * 
	 * @return
	 */
	public boolean isUnique();

	/**
	 * ��������
	 * 
	 * @return
	 */
	public IndexType getType();

	/**
	 * ������������ֶε�ö����
	 * 
	 * @return �����еĵ�����
	 */
	public ModifiableContainer<? extends IndexItemDefine> getItems();
}
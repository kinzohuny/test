package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.exception.NoPartitionDefineException;

/**
 * ���������
 * 
 * @author houchunlei
 * 
 */
@Deprecated
public interface TablePartitionDeclare extends TablePartitionDefine {
	@Deprecated
	public ModifiableNamedElementContainer<? extends TableFieldDefine> getPartitionFields();

	/**
	 * ���÷����ֶ�
	 * 
	 * @param field
	 * @param others
	 */
	@Deprecated
	public void setPartitionFields(TableFieldDefine field,
			TableFieldDefine... others);

	/**
	 * ���ӷ����ֶ�
	 * 
	 * @param field
	 * @param others
	 */
	@Deprecated
	public void addPartitionField(TableFieldDefine field,
			TableFieldDefine... others);

	/**
	 * ���÷�����������
	 * 
	 * @param suggestion
	 * @throws NoPartitionDefineException
	 */
	@Deprecated
	public void setParitionSuggestion(int suggestion)
			throws NoPartitionDefineException;

	/**
	 * ���ñ��������������
	 * 
	 * <p>
	 * Ĭ��0,��Ϊ��ǰ���ݿ���֧�ֵ����������
	 * 
	 * @param maxPartitionCount
	 * @throws NoPartitionDefineException
	 */
	@Deprecated
	public void setMaxPartitionCount(int maxPartitionCount)
			throws NoPartitionDefineException;
}
package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.DefineBase;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.exception.NoPartitionDefineException;

/**
 * ���������
 * 
 * @author houchunlei
 */
@Deprecated
public interface TablePartitionDefine extends DefineBase {

	/**
	 * �Ƿ����
	 */
	@Deprecated
	public boolean isPartitioned();

	/**
	 * ����������
	 * 
	 * @return
	 * @throws NoPartitionDefineException
	 */
	@Deprecated
	public int getPartitionSuggestion() throws NoPartitionDefineException;

	/**
	 * ��������ķ�������
	 * 
	 * @return
	 * @throws NoPartitionDefineException
	 */
	@Deprecated
	public int getMaxPartitionCount() throws NoPartitionDefineException;

	/**
	 * �������ֶ�
	 */
	@Deprecated
	public NamedElementContainer<? extends TableFieldDefine> getPartitionFields();
}
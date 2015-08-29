package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.DefineBase;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.exception.NoPartitionDefineException;

/**
 * 表分区定义
 * 
 * @author houchunlei
 */
@Deprecated
public interface TablePartitionDefine extends DefineBase {

	/**
	 * 是否分区
	 */
	@Deprecated
	public boolean isPartitioned();

	/**
	 * 分区的行数
	 * 
	 * @return
	 * @throws NoPartitionDefineException
	 */
	@Deprecated
	public int getPartitionSuggestion() throws NoPartitionDefineException;

	/**
	 * 表定义的最大的分区个数
	 * 
	 * @return
	 * @throws NoPartitionDefineException
	 */
	@Deprecated
	public int getMaxPartitionCount() throws NoPartitionDefineException;

	/**
	 * 分区的字段
	 */
	@Deprecated
	public NamedElementContainer<? extends TableFieldDefine> getPartitionFields();
}
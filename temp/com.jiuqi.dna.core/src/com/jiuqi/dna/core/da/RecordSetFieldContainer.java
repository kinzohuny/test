package com.jiuqi.dna.core.da;

import com.jiuqi.dna.core.def.Container;
import com.jiuqi.dna.core.def.MissingDefineException;
import com.jiuqi.dna.core.def.query.QueryColumnDefine;

/**
 * 纪录集字段容器
 * 
 * @author gaojingxin
 * 
 */
public interface RecordSetFieldContainer<TField extends RecordSetField> extends
	Container<TField> {
	
	/**
	 * 根据查询列定义查找纪录集字段
	 * 
	 * @param column 查询列定义
	 * @return 返回列定义或者null
	 * @throws IllegalArgumentException 当列定义无效时抛出异常
	 */
	public TField find(QueryColumnDefine column)
			throws IllegalArgumentException;

	/**
	 * 根据查询列定义查找纪录集字段
	 * 
	 * @param column 查询列定义
	 * @return 返回列定义
	 * @throws MissingDefineException 当找不到字段时抛出异常
	 * @throws IllegalArgumentException 当定义列不无效时抛出异常
	 */
	public TField get(QueryColumnDefine column)
			throws MissingDefineException, IllegalArgumentException;
}

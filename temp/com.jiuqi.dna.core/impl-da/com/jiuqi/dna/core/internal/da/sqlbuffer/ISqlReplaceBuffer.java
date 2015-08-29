package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * mysql的replace语句buffer
 * 
 * @author houchunlei
 * 
 */
public interface ISqlReplaceBuffer extends ISqlBuffer, ISqlCommandBuffer {

	/**
	 * 增加replace字段
	 * 
	 * @param name
	 *            unquoted
	 */
	void newField(String name);

	/**
	 * replace字段的值
	 * 
	 * @return
	 */
	ISqlExprBuffer newValue();
}

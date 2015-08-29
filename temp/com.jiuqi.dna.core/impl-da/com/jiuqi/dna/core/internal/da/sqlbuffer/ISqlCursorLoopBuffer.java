package com.jiuqi.dna.core.internal.da.sqlbuffer;

import com.jiuqi.dna.core.type.DataType;

/**
 * 游标循环语句块
 * 
 * @author niuhaifeng
 * 
 */
public interface ISqlCursorLoopBuffer extends ISqlSegmentBuffer {

	/**
	 * 返回游标的查询定义
	 * 
	 * @return
	 */
	public ISqlQueryBuffer query();

	/**
	 * 声明打开读取游标每行时,装入的变量.
	 */
	public void declare(String name, DataType type);
}

package com.jiuqi.dna.core.internal.da.sqlbuffer;

public interface ISqlDeleteBuffer extends ISqlBuffer, ISqlCommandBuffer {

	/**
	 * delete目标表引用
	 * 
	 * @return
	 */
	public ISqlTableRefBuffer target();

	/**
	 * delete条件
	 * 
	 * @return
	 */
	public ISqlExprBuffer where();

	/**
	 * delete条件为指定游标行
	 * 
	 * @param cursor
	 */
	public void whereCurrentOf(String cursor);
}

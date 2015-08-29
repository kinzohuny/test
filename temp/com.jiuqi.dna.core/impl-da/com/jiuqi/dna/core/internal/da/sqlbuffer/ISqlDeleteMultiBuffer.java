package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * mysql特性的多表删除语句buffer
 * 
 * @author houchunlei
 * 
 */
public interface ISqlDeleteMultiBuffer extends ISqlCommandBuffer {

	/**
	 * delete语句的目标表引用
	 * 
	 * @return
	 */
	public ISqlTableRefBuffer target();

	/**
	 * delete语句过滤条件
	 * 
	 * @return
	 */
	public ISqlExprBuffer where();

	public void from(String alias);
}

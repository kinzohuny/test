package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * Oracle的merge语句
 * 
 * @author niuhaifeng
 * 
 */
public interface ISqlMergeBuffer extends ISqlBuffer, ISqlCommandBuffer {

	/**
	 * 指示using子句使用dual表
	 */
	public void usingDummy();

	/**
	 * 指示using子句使用指定表
	 * 
	 * @param table
	 *            unquoted
	 * @param alias
	 *            unquoted
	 */
	public void usingTable(String table, String alias);

	/**
	 * 指示using子句使用查询结构
	 * 
	 * @param alias
	 *            unquoted
	 * @return
	 */
	public ISqlSelectBuffer usingSubquery(String alias);

	/**
	 * 指示merge子句的on条件
	 * 
	 * @return
	 */
	public ISqlExprBuffer onCondition();

	/**
	 * 指示when not matched子句的插入列.
	 * 
	 * @param field
	 * @return
	 */
	public ISqlExprBuffer insert(String field);

	/**
	 * 指示when matched子句的更新列.
	 * 
	 * @param field
	 * @return
	 */
	public ISqlExprBuffer update(String field);
}

package com.jiuqi.dna.core.internal.da.sqlbuffer;

import com.jiuqi.dna.core.type.DataType;

public interface ISqlSegmentBuffer extends ISqlBuffer, ISqlCommandBuffer,
		IFeaturable {

	/**
	 * 声明变量
	 * 
	 * @param name
	 *            unquoted
	 * @param type
	 */
	public void declare(String name, DataType type);

	/**
	 * 增加insert语句.
	 * 
	 * @param table
	 * @return
	 */
	public ISqlInsertBuffer insert(String table);

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable);

	public ISqlDeleteBuffer delete(String table, String alias);

	/**
	 * 赋值语句
	 * 
	 * @param var
	 * @return
	 */
	public ISqlExprBuffer assign(String var);

	/**
	 * select into的赋值语句
	 * 
	 * @return
	 */
	public ISqlSelectIntoBuffer selectInto();

	public ISqlConditionBuffer ifThenElse();

	/**
	 * 无条件的循环语句
	 * 
	 * @return
	 */
	public ISqlLoopBuffer loop();

	/**
	 * 游标循环语句
	 * 
	 * @param cursor
	 * @param forUpdate
	 * @return
	 */
	public ISqlCursorLoopBuffer cursorLoop(String cursor, boolean forUpdate);

	public void breakLoop();

	public ISqlExprBuffer print();

	public void exit();

	public ISqlExprBuffer returnValue();

}

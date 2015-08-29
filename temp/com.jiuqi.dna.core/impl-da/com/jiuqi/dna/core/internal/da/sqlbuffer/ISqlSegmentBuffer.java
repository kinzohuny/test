package com.jiuqi.dna.core.internal.da.sqlbuffer;

import com.jiuqi.dna.core.type.DataType;

public interface ISqlSegmentBuffer extends ISqlBuffer, ISqlCommandBuffer,
		IFeaturable {

	/**
	 * ��������
	 * 
	 * @param name
	 *            unquoted
	 * @param type
	 */
	public void declare(String name, DataType type);

	/**
	 * ����insert���.
	 * 
	 * @param table
	 * @return
	 */
	public ISqlInsertBuffer insert(String table);

	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable);

	public ISqlDeleteBuffer delete(String table, String alias);

	/**
	 * ��ֵ���
	 * 
	 * @param var
	 * @return
	 */
	public ISqlExprBuffer assign(String var);

	/**
	 * select into�ĸ�ֵ���
	 * 
	 * @return
	 */
	public ISqlSelectIntoBuffer selectInto();

	public ISqlConditionBuffer ifThenElse();

	/**
	 * ��������ѭ�����
	 * 
	 * @return
	 */
	public ISqlLoopBuffer loop();

	/**
	 * �α�ѭ�����
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

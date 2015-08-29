package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * Oracle��merge���
 * 
 * @author niuhaifeng
 * 
 */
public interface ISqlMergeBuffer extends ISqlBuffer, ISqlCommandBuffer {

	/**
	 * ָʾusing�Ӿ�ʹ��dual��
	 */
	public void usingDummy();

	/**
	 * ָʾusing�Ӿ�ʹ��ָ����
	 * 
	 * @param table
	 *            unquoted
	 * @param alias
	 *            unquoted
	 */
	public void usingTable(String table, String alias);

	/**
	 * ָʾusing�Ӿ�ʹ�ò�ѯ�ṹ
	 * 
	 * @param alias
	 *            unquoted
	 * @return
	 */
	public ISqlSelectBuffer usingSubquery(String alias);

	/**
	 * ָʾmerge�Ӿ��on����
	 * 
	 * @return
	 */
	public ISqlExprBuffer onCondition();

	/**
	 * ָʾwhen not matched�Ӿ�Ĳ�����.
	 * 
	 * @param field
	 * @return
	 */
	public ISqlExprBuffer insert(String field);

	/**
	 * ָʾwhen matched�Ӿ�ĸ�����.
	 * 
	 * @param field
	 * @return
	 */
	public ISqlExprBuffer update(String field);
}

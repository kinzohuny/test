package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * SqlBuffer������
 * 
 * @author niuhaifeng
 * 
 */
public interface ISqlCommandFactory extends IFeaturable {

	/**
	 * ����query���buffer
	 * 
	 * @return
	 */
	public ISqlQueryBuffer query();

	/**
	 * ����insert���buffer
	 * 
	 * @param table
	 *            unquoted����
	 * @return
	 */
	public ISqlInsertBuffer insert(String table);

	/**
	 * ����update���buffer
	 * 
	 * @param table
	 * @param alias
	 * @param assignFromSlaveTable
	 *            ��ֵ�Ƿ�ʹ������������.����update��֧��join�����������,��Ҫ����Ϣ�Ծ�������sql���﷨�ṹ.
	 * @return
	 */
	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable);

	/**
	 * ����delete���buffer
	 * 
	 * @param table
	 * @param alias
	 * @return
	 */
	public ISqlDeleteBuffer delete(String table, String alias);

	/**
	 * ���츴�����
	 * 
	 * @return
	 */
	public ISqlSegmentBuffer segment();

}

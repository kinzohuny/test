package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * SqlBuffer工厂类
 * 
 * @author niuhaifeng
 * 
 */
public interface ISqlCommandFactory extends IFeaturable {

	/**
	 * 构造query语句buffer
	 * 
	 * @return
	 */
	public ISqlQueryBuffer query();

	/**
	 * 构造insert语句buffer
	 * 
	 * @param table
	 *            unquoted表名
	 * @return
	 */
	public ISqlInsertBuffer insert(String table);

	/**
	 * 构造update语句buffer
	 * 
	 * @param table
	 * @param alias
	 * @param assignFromSlaveTable
	 *            赋值是否使用了连接引用.对于update不支持join的物理适配层,需要该信息以决定本地sql的语法结构.
	 * @return
	 */
	public ISqlUpdateBuffer update(String table, String alias,
			boolean assignFromSlaveTable);

	/**
	 * 构造delete语句buffer
	 * 
	 * @param table
	 * @param alias
	 * @return
	 */
	public ISqlDeleteBuffer delete(String table, String alias);

	/**
	 * 构造复合语句
	 * 
	 * @return
	 */
	public ISqlSegmentBuffer segment();

}

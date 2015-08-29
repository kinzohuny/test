package com.jiuqi.dna.core.internal.da.sqlbuffer;

import com.jiuqi.dna.core.def.table.TableJoinType;

/**
 * 关系引用buffer
 * 
 * @author niuhaifeng
 * 
 */
public interface ISqlRelationRefBuffer extends ISqlBuffer {

	/**
	 * 连接表
	 * 
	 * @param table
	 *            unquoted表名
	 * @param alias
	 *            unquoted别名
	 * @param type
	 *            连接类型
	 * @return
	 */
	public ISqlJoinedTableRefBuffer joinTable(String table, String alias,
			TableJoinType type);

	/**
	 * 连接查询
	 * 
	 * @param alias
	 *            unquoted别名
	 * @param type
	 *            连接类型
	 * @return
	 */
	public ISqlJoinedQueryRefBuffer joinQuery(String alias, TableJoinType type);

	/**
	 * 连接with
	 * 
	 * @param target
	 *            unquoted的with名称
	 * @param alias
	 *            unquoted别名
	 * @param type
	 *            连接类型
	 * @return
	 */
	public ISqlJoinedWithRefBuffer joinWith(String target, String alias,
			TableJoinType type);
}

package com.jiuqi.dna.core.internal.da.sqlbuffer;

public interface ISqlMergeCommandFactory {

	/**
	 * 构造merge语句buffer
	 * 
	 * @param table
	 *            merge目标表的unquoted名称
	 * @param alias
	 *            merge目标表的unquoted别名
	 * @return
	 */
	public ISqlMergeBuffer merge(String table, String alias);
}

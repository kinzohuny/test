package com.jiuqi.dna.core.internal.da.sqlbuffer;

public interface ISqlMergeCommandFactory {

	/**
	 * ����merge���buffer
	 * 
	 * @param table
	 *            mergeĿ����unquoted����
	 * @param alias
	 *            mergeĿ����unquoted����
	 * @return
	 */
	public ISqlMergeBuffer merge(String table, String alias);
}

package com.jiuqi.dna.core.internal.da.sqlbuffer;

public interface ISqlReplaceCommandFactory {

	/**
	 * ����replace���buffer
	 * 
	 * @param table
	 *            unquoted
	 * @return
	 */
	ISqlReplaceBuffer replace(String table);

}

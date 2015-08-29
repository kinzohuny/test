package com.jiuqi.dna.core.internal.da.sqlbuffer;

public interface ISqlReplaceCommandFactory {

	/**
	 * ππ‘Ïreplace”Ôæ‰buffer
	 * 
	 * @param table
	 *            unquoted
	 * @return
	 */
	ISqlReplaceBuffer replace(String table);

}

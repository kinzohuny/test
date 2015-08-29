package com.jiuqi.dna.core.def.query;

/**
 * 存储过程定义
 * 
 * @author gaojingxin
 */
public interface StoredProcedureDefine extends StatementDefine {

	/**
	 * 获取存储过程返回的结果集个数.
	 * 
	 * @return
	 */
	public int getResultSets();
}

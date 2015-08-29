package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.arg.ArgumentDeclare;
import com.jiuqi.dna.core.type.DataType;

public interface StoredProcedureDeclare extends StoredProcedureDefine,
		StatementDeclare {

	/**
	 * 设置存储过程返回的结果集个数.
	 * 
	 * @param count
	 */
	public void setResultSets(int count);

	/**
	 * 增加参数定义，并指定输出类型
	 * 
	 * @param name
	 *            参数名称
	 * @param type
	 *            参数数据类型
	 * @param output
	 *            参数输出类型
	 * @return
	 */
	public ArgumentDeclare newArgument(String name, DataType type,
			ArgumentOutput output);
}
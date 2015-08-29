package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDeclare;
import com.jiuqi.dna.core.type.DataType;

/**
 * 用户定义函数
 * 
 * @see com.jiuqi.dna.core.def.query.UserFunctionDefine
 * 
 * @author houchunlei
 * 
 */
public interface UserFunctionDeclare extends UserFunctionDefine, NamedDeclare {

	/**
	 * 增加参数声明
	 * 
	 * <p>
	 * 默认使用arg0，arg1，arg2作为参数名称。
	 * 
	 * @param type
	 * @return
	 */
	public FunctionArgumentDeclare newArgument(DataType type);

	/**
	 * 增加参数声明，并指定参数名称。
	 * 
	 * <p>
	 * 辅助信息提供DNA-SQL编辑器使用，强烈建议提供扩展信息。
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	public FunctionArgumentDeclare newArgument(String name, DataType type);
}

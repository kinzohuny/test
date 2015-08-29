package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.def.exp.OperateExpression;
import com.jiuqi.dna.core.type.DataType;

/**
 * 用户定义函数
 * 
 * <ul>
 * <li>仅支持标量函数。
 * <li>目前不支持可选参数。
 * <li>用户定义函数之间不能相互依赖。
 * </ul>
 * 
 * @author houchunlei
 */
public interface UserFunctionDefine extends NamedDefine {

	/**
	 * 获取函数的返回类型
	 * 
	 * @return
	 */
	public DataType getReturnType();

	/**
	 * 获取参数列表
	 * 
	 * @return
	 */
	public NamedElementContainer<? extends FunctionArgumentDefine> getArguments();

	/**
	 * 构造函数运算表达式
	 * 
	 * @param values
	 * @return
	 */
	public OperateExpression expOf(Object... values);
}

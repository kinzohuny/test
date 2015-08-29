package com.jiuqi.dna.core.def.arg;

import com.jiuqi.dna.core.def.FieldDefine;
import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.type.DataTypable;
import com.jiuqi.dna.core.type.DataType;

/**
 * 带参数的，参数化的
 * 
 * @see com.jiuqi.dna.core.def.arg.ArgumentableDefine
 * 
 * @author gaojingxin
 * 
 */
public interface ArgumentableDeclare extends ArgumentableDefine {

	/**
	 * 获得参数集合
	 * 
	 * @return 返回参数集合
	 */
	public ModifiableNamedElementContainer<? extends ArgumentDeclare> getArguments();

	/**
	 * 新增一个参数
	 */
	public ArgumentDeclare newArgument(String name, DataType type);

	/**
	 * 新增一个参数
	 */
	public ArgumentDeclare newArgument(String name, DataTypable typable);

	/**
	 * 新增一个参数
	 * 
	 * @param sample
	 *            根据该值的名称和类型创建参数定义
	 */
	public ArgumentDeclare newArgument(FieldDefine sample);
}
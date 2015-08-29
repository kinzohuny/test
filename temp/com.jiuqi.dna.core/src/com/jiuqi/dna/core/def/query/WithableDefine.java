package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;

/**
 * 支持with定义的
 * 
 * @author houchunlei
 * 
 */
public interface WithableDefine {

	/**
	 * 获取临时结果集的列表
	 * 
	 * @return 未定义则返回null
	 */
	public ModifiableNamedElementContainer<? extends DerivedQueryDefine> getWiths();
}

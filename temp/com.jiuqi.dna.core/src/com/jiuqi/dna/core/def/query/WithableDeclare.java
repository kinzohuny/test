package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;

/**
 * 支持with定义的
 * 
 * @author houchunlei
 * 
 */
public interface WithableDeclare extends WithableDefine {

	public ModifiableNamedElementContainer<? extends DerivedQueryDeclare> getWiths();

	/**
	 * 使用with子句 ，增加临时结果集
	 * 
	 * @param name
	 *            临时结果集的名称，不能与其他临时结果集名称重复。
	 * @return
	 */
	public DerivedQueryDeclare newWith(String name);
}

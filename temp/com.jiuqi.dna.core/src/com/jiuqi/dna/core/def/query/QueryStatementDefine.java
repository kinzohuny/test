package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.Container;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.type.Type;

/**
 * 查询语句定义
 * 
 * @author gaojingxin
 * 
 */
public interface QueryStatementDefine extends SelectDefine, StatementDefine,
		WithableDefine, Type {

	public NamedElementContainer<? extends QueryColumnDefine> getColumns();

	/**
	 * 获取排序项规则定义
	 * 
	 * @return 未定义则返回null
	 */
	public Container<? extends OrderByItemDefine> getOrderBys();

}

package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.DefineBase;
import com.jiuqi.dna.core.def.exp.ValueExpression;

/**
 * 查询分组规则定义
 * 
 * @author houchunlei
 * 
 */
public interface GroupByItemDefine extends DefineBase {

	/**
	 * 获取分组
	 */
	public ValueExpression getExpression();
}

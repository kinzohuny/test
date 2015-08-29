package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.type.DataTypable;

/**
 * 查询语句选择列定义
 * 
 * <p>
 * 表示一个抽象查询定义的输出列
 * 
 * @author houchunlei
 * 
 */
public interface SelectColumnDefine extends RelationColumnDefine, DataTypable {

	/**
	 * 获取所属的查询定义
	 * 
	 * @return 查询定义
	 */
	public SelectDefine getOwner();

	/**
	 * 返回列定义的表达式
	 * 
	 * @return 返回列定义的表达式
	 */
	public ValueExpression getExpression();
}

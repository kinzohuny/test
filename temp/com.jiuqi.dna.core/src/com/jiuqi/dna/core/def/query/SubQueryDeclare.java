package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.PredicateExpression;

/**
 * 子查询定义
 * 
 * @see com.jiuqi.dna.core.def.query.SubQueryDefine
 * 
 * @author houchunlei
 * 
 */
public interface SubQueryDeclare extends SubQueryDefine, SelectDeclare {

	/**
	 * 构造Exits谓词表达式
	 * 
	 * @return
	 */
	public PredicateExpression exists();

	/**
	 * 构造Not Exists谓词表达式
	 * 
	 * @return
	 */
	public PredicateExpression notExists();

}

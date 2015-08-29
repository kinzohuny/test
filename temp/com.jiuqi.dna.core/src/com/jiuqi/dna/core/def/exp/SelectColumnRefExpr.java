package com.jiuqi.dna.core.def.exp;

import com.jiuqi.dna.core.def.query.QueryReferenceDefine;
import com.jiuqi.dna.core.def.query.SelectColumnDefine;

/**
 * 查询列引用表达式
 * 
 * @author houchunlei
 * 
 */
public interface SelectColumnRefExpr extends RelationColumnRefExpr {

	/**
	 * 获取查询列定义
	 */
	public SelectColumnDefine getColumn();

	/**
	 * 获取所在的查询引用定义
	 */
	public QueryReferenceDefine getReference();
}
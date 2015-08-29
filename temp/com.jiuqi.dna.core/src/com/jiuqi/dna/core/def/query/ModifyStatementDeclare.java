package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.TableFieldRefExpr;

/**
 * 更新语句定义
 * 
 * @see com.jiuqi.dna.core.def.query.ModifyStatementDefine
 * 
 * @author houchunlei
 */
public interface ModifyStatementDeclare extends ModifyStatementDefine,
		StatementDeclare, RelationRefDomainDeclare {

	/**
	 * 构造子查询定义
	 * 
	 * @return
	 */
	public SubQueryDeclare newSubQuery();

	/**
	 * 构造导出查询定义,用于from子句使用
	 * 
	 * @return
	 */
	public DerivedQueryDeclare newDerivedQuery();

	/**
	 * 创建字段引用表达式
	 * 
	 * @param field
	 * @return
	 */
	public TableFieldRefExpr expOf(RelationColumnDefine column);

	/**
	 * 创建字段引用表达式
	 * 
	 * @param columnName
	 * @return
	 */
	public TableFieldRefExpr expOf(String columnName);
}

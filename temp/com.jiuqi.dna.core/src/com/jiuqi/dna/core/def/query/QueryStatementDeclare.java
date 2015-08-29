package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.ModifiableContainer;
import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.def.exp.ValueExpression;

/**
 * 查询语句定义
 * 
 * <p>
 * 定义一个可执行的查询语句结构
 * 
 * @see com.jiuqi.dna.core.def.query.QueryStatementDefine
 * 
 * @author gaojingxin
 * 
 */
public interface QueryStatementDeclare extends QueryStatementDefine,
		SelectDeclare, StatementDeclare, WithableDeclare {

	/**
	 * 使用样本查询结构，从当前查询语句的构造“导出查询定义”。
	 * 
	 * <p>
	 * 如果样本查询结构是查询语句定义，该操作会克隆样本中定义的With块，但不会克隆参数定义。
	 * 
	 * @param sample
	 * @return 导出查询定义，用于from子句或with块。
	 */
	public DerivedQueryDeclare newDerivedQuery(SelectDefine sample);

	public ModifiableNamedElementContainer<? extends QueryColumnDeclare> getColumns();

	public QueryColumnDeclare newColumn(RelationColumnDefine field);

	public QueryColumnDeclare newColumn(RelationColumnDefine field, String alias);

	public QueryColumnDeclare newColumn(ValueExpression expr, String alias);

	public QueryColumnDeclare newColumn(ValueExpression expression);

	/**
	 * 获取查询语句的排序规则
	 * 
	 * @return 返回排序项列表
	 */
	public ModifiableContainer<? extends OrderByItemDeclare> getOrderBys();

	/**
	 * 增加排序规则
	 * 
	 * <p>
	 * 排序规则在union之后计算
	 * 
	 * @param field
	 *            排序依据的关系列
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column);

	/**
	 * 增加排序规则
	 * 
	 * <p>
	 * 排序规则在union之后计算
	 * 
	 * @param field
	 *            排序依据的关系列
	 * @param isDesc
	 *            是否降序
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column,
			boolean isDesc);

	/**
	 * 增加排序规则
	 * 
	 * <p>
	 * 排序规则在union之后计算
	 * 
	 * @param value
	 *            排序依据的表达式
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(ValueExpression value);

	/**
	 * 增加排序规则
	 * 
	 * <p>
	 * 排序规则在union之后计算
	 * 
	 * @param value
	 *            排序依据的表达式
	 * @param isDesc
	 *            是否降序
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(ValueExpression value, boolean isDesc);

}

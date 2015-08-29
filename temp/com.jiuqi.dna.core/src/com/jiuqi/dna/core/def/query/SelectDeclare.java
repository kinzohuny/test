package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.ModifiableContainer;
import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.def.exp.ConditionalExpression;
import com.jiuqi.dna.core.def.exp.RelationColumnRefExpr;
import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;

/**
 * 抽象查询结构定义.
 * 
 * @see com.jiuqi.dna.core.def.query.SelectDefine
 * 
 * @author houchunlei
 * 
 */
public interface SelectDeclare extends SelectDefine, RelationDeclare,
		RelationRefBuildable, RelationRefDomainDeclare {

	@Deprecated
	public QuRelationRefDeclare findReference(String name);

	@Deprecated
	public QuRelationRefDeclare getReference(String name);

	public QuRelationRefDeclare findRelationRef(String name);

	public QuRelationRefDeclare getRelationRef(String name);

	public QuRelationRefDeclare getRootReference();

	public Iterable<? extends QuRelationRefDeclare> getReferences();

	/**
	 * 查询结构添加表关系引用.
	 * 
	 * @param table
	 *            表定义.
	 * @return
	 */
	public QuTableRefDeclare newReference(TableDefine table);

	/**
	 * 查询结构添加表关系引用.
	 * 
	 * @param table
	 *            表定义.
	 * @param name
	 *            引用别名.在当前关系引用域内不能重复.
	 * @return
	 */
	public QuTableRefDeclare newReference(TableDefine table, String name);

	/**
	 * 查询结构添加表关系引用.
	 * 
	 * @param table
	 *            表声明器.
	 * @return
	 */
	public QuTableRefDeclare newReference(TableDeclarator table);

	/**
	 * 查询结构添加表关系引用.
	 * 
	 * @param table
	 *            表声明器.
	 * @param name
	 *            引用别名.在当前关系引用域内不能重复.
	 * @return
	 */
	public QuTableRefDeclare newReference(TableDeclarator table, String name);

	/**
	 * 查询结构添加关系引用.
	 * 
	 * @param query
	 *            导出查询定义.
	 * @return
	 */
	public QuQueryRefDeclare newReference(DerivedQueryDefine query);

	/**
	 * 查询结构添加关系引用.
	 * 
	 * @param query
	 *            导出查询定义.
	 * @param name
	 *            引用别名.在当前关系引用域内不能重复.
	 * @return
	 */
	public QuQueryRefDeclare newReference(DerivedQueryDefine query, String name);

	/**
	 * 构造字段引用表达式
	 * 
	 * <p>
	 * 使用第一个指向关系元定义的引用,并构造关系列引用表达式
	 * <p>
	 * 此方法不够严谨,更建议使用<code>QuRelationRefDeclare</code>的<code>expOf</code>
	 * 方法来构造字段引用表达式
	 */
	public RelationColumnRefExpr expOf(RelationColumnDefine column);

	/**
	 * 设置查询行过滤条件
	 */
	public void setCondition(ConditionalExpression where);

	/**
	 * 设置分组规则
	 * 
	 * @see com.jiuqi.dna.core.def.query.GroupByType
	 * 
	 * @param type
	 *            分组规则
	 */
	public void setGroupByType(GroupByType type);

	public ModifiableContainer<? extends GroupByItemDeclare> getGroupBys();

	/**
	 * 增加分组项
	 * 
	 * @param value
	 *            分组依据的表达式
	 * @return
	 */
	public GroupByItemDeclare newGroupBy(ValueExpression value);

	/**
	 * 增加分组项
	 * 
	 * @param field
	 *            分组依据的字段
	 * @return
	 */
	public GroupByItemDeclare newGroupBy(RelationColumnDefine column);

	/**
	 * 设置分组过滤条件.
	 */
	public void setHaving(ConditionalExpression having);

	/**
	 * 设置是否排除重复行.
	 */
	public void setDistinct(boolean distinct);

	public SelectColumnDeclare findColumn(String columnName);

	public SelectColumnDeclare getColumn(String columnName);

	/**
	 * 获得输出列列表.
	 * 
	 * @return 返回字段列表.
	 */
	public ModifiableNamedElementContainer<? extends SelectColumnDeclare> getColumns();

	/**
	 * 增加查询输出列.
	 * 
	 * @param field
	 *            查询列为指定逻辑表列.
	 * @return
	 */
	public SelectColumnDeclare newColumn(RelationColumnDefine field);

	/**
	 * 增加查询输出列.
	 * 
	 * @param field
	 *            输出字段.
	 * @param alias
	 *            输出列别名.
	 * @return
	 */
	public SelectColumnDeclare newColumn(RelationColumnDefine field,
			String alias);

	/**
	 * 增加查询输出列.
	 * 
	 * @param expression
	 *            输出列表达式.
	 * @return
	 */
	public SelectColumnDeclare newColumn(ValueExpression expression);

	/**
	 * 增加查询输出列.
	 * 
	 * @param expression
	 *            输出列表达式.
	 * @param alias
	 *            输出列别名.
	 * @return
	 */
	public SelectColumnDeclare newColumn(ValueExpression expression,
			String alias);

	public ModifiableContainer<? extends SetOperateDeclare> getSetOperates();

	/**
	 * 集合与,不包括重复行
	 * 
	 * <p>
	 * 参数必须由当前查询结构的<code>newDerivedQuery()</code>方法构造.
	 * 
	 * @param query
	 */
	public void union(DerivedQueryDefine query);

	/**
	 * 集合与,包括重复行
	 * 
	 * <p>
	 * 参数必须由当前查询结构的<code>newDerivedQuery()</code>方法构造.
	 * 
	 * @param query
	 */
	public void unionAll(DerivedQueryDefine query);

	/**
	 * 构造子查询定义
	 * 
	 * @return
	 */
	public SubQueryDeclare newSubQuery();

	/**
	 * 构造导出查询定义,用于构造from子查询以及union
	 * 
	 * @return
	 */
	public DerivedQueryDeclare newDerivedQuery();

	/**
	 * 已完全废弃方法,不会执行任何操作,返回空
	 * 
	 * @deprecated
	 */
	@Deprecated
	public ModifiableContainer<? extends OrderByItemDeclare> getOrderBys();

	/**
	 * 已完全废弃方法,不会执行任何操作,返回空
	 * 
	 * @deprecated
	 */
	@Deprecated
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column);

	/**
	 * 已完全废弃方法,不会执行任何操作,返回空
	 * 
	 * @deprecated
	 */
	@Deprecated
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column,
			boolean isDesc);

	/**
	 * 已完全废弃方法,不会执行任何操作,返回空
	 * 
	 * @deprecated
	 */
	@Deprecated
	public OrderByItemDeclare newOrderBy(ValueExpression value);

	/**
	 * 已完全废弃方法,不会执行任何操作,返回空
	 * 
	 * @deprecated
	 */
	@Deprecated
	public OrderByItemDeclare newOrderBy(ValueExpression value, boolean isDesc);
}

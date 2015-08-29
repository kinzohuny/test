package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.Container;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.def.exp.ConditionalExpression;

/**
 * 查询结构定义.描述完整的select结构,包括:select,from,where,group by,having等子句.但不包括order by子句.
 * 
 * @author houchunlei
 * 
 */
public interface SelectDefine extends RelationDefine, RelationRefDomainDefine {

	/**
	 * 根据名称查找当前查询结构定义的关系引用.
	 * 
	 * @param name
	 *            关系引用名称.
	 * @return 不存在则返回null.
	 * @deprecated 使用findRelationRef替代.
	 */
	@Deprecated
	public QuRelationRefDefine findReference(String name);

	/**
	 * 根据名称获取当前查询结构定义的关系引用.
	 * 
	 * @param name
	 *            关系引用名称.
	 * @return 不存在则抛出异常.
	 * @deprecated 使用getRelationRef替代.
	 */
	@Deprecated
	public QuRelationRefDefine getReference(String name);

	public QuRelationRefDefine findRelationRef(String name);

	public QuRelationRefDefine getRelationRef(String name);

	/**
	 * 返回当前查询结构的第一个关系引用定义.
	 * 
	 * @return 未定义则返回null.
	 */
	public QuRelationRefDefine getRootReference();

	/**
	 * 返回当前查询结构定义的所有关系引用的<strong>先序遍历</strong>的可迭代接口.
	 * 
	 * @return 未定义任何关系引用则返回空迭代.
	 */
	public Iterable<? extends QuRelationRefDefine> getReferences();

	/**
	 * 获得行过滤条件,即where子句定义条件.
	 * 
	 * @return 未定义则返回null.
	 */
	public ConditionalExpression getCondition();

	/**
	 * 获取分组规则定义.
	 * 
	 * @return 未定义则返回null.
	 */
	public Container<? extends GroupByItemDefine> getGroupBys();

	/**
	 * 获取分组类型.
	 * 
	 * @see com.jiuqi.dna.core.def.query.GroupByType
	 * 
	 * @return 默认为GroupByType.DEFAULT.
	 */
	public GroupByType getGroupByType();

	/**
	 * 获取分组过滤条件.
	 * 
	 * @return 未定义则返回null.
	 */
	public ConditionalExpression getHaving();

	public SelectColumnDefine findColumn(String columnName);

	public SelectColumnDefine getColumn(String columnName);

	/**
	 * 获取select子句是否排除重复行,默认为false,即不排除重复行.
	 * 
	 * @return
	 */
	public boolean getDistinct();

	/**
	 * 获得输出字段列表.
	 * 
	 * @return 不会返回null.
	 */
	public NamedElementContainer<? extends SelectColumnDefine> getColumns();

	/**
	 * 返回集合运算定义.
	 * 
	 * @return 未定义则返回null.
	 */
	public Container<? extends SetOperateDefine> getSetOperates();

	/**
	 * 已完全废弃方法,不会执行任何操作,返回空.
	 * 
	 * @deprecated
	 */
	@Deprecated
	public Container<? extends OrderByItemDefine> getOrderBys();

}

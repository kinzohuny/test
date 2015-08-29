package com.jiuqi.dna.core.def.query;

/**
 * 关系引用，即指向一个关系元定义的存根，方法<code>getTarget</code>返回当前引用的目标。
 * 关系引用可以认为是个结构基于关系元定义二维表的。
 * 
 * @author houchunlei
 * 
 */
@SuppressWarnings({ "deprecation" })
public interface RelationRefDefine extends MoRelationRefDefine {

	/**
	 * 获取目标元关系定义
	 * 
	 * @return 关系的元定义
	 */
	public RelationDefine getTarget();

	/**
	 * 是否是表引用
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isTableReference();

	/**
	 * 是否是查询引用
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isQueryReference();

}

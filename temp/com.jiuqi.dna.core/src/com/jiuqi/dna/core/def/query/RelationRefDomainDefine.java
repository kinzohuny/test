package com.jiuqi.dna.core.def.query;

/**
 * 关系引用域定义.包含了若干个关系引用定义,并可能属于其他关系引用域.
 * 
 * @author houchunlei
 * 
 */
public interface RelationRefDomainDefine {

	/**
	 * 返回当前域的最近可引用域.最近可引用域不一定就是结构上的直接上级域.
	 * 
	 * @return
	 */
	RelationRefDomainDefine getDomain();

	/**
	 * 在当前域内查找指定名称的关系引用定义.
	 * 
	 * @param name
	 * @return 不存在则返回null
	 */
	RelationRefDefine findRelationRef(String name);

	/**
	 * 获取当前域内指定名称的关系引用定义.
	 * 
	 * @param name
	 * @return 不存在则抛出异常
	 */
	RelationRefDefine getRelationRef(String name);

	/**
	 * 在当前域及有效可引用域内递归查找指定名称的关系引用定义.
	 * 
	 * @param name
	 * @return 不存在则返回null
	 */
	RelationRefDefine findRelationRefRecursively(String name);

	/**
	 * 以递归查找的方式地获取在当前域及有效可引用域内指定名称的关系引用
	 * 
	 * @param name
	 * @return 不存在则抛出异常
	 */
	RelationRefDefine getRelationRefRecursively(String name);
}

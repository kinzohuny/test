package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDefine;

/**
 * 关系列定义
 * 
 * @author houchunlei
 * 
 */
public interface RelationColumnDefine extends NamedDefine {

	/**
	 * 获取所属的关系定义
	 * 
	 * @return 关系定义
	 */
	public RelationDefine getOwner();
}

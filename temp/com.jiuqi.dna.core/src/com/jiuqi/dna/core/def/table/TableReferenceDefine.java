package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.query.RelationRefDefine;

/**
 * 表引用接口
 * 
 * <p>
 * 继承至关系引用定义，表示目标类型为表定义的关系引用。
 * 
 * @see com.jiuqi.dna.core.def.query.RelationRefDefine
 * 
 * @author gaojingxin
 * 
 */
public interface TableReferenceDefine extends RelationRefDefine {

	/**
	 * 获取目标逻辑表
	 */
	public TableDefine getTarget();
}
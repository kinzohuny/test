package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.RelationColumnDeclare;

/**
 * 关系列定义的内部接口
 * 
 * <p>
 * 只是个标记接口
 * 
 * @author houchunlei
 */
interface RelationColumn extends RelationColumnDeclare {

	Relation getOwner();
}

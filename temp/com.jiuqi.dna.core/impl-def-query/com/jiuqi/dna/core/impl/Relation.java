package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.RelationDeclare;
import com.jiuqi.dna.core.misc.SXRenderable;

/**
 * 关系元定义的内部接口
 * 
 * @author houchunlei
 */
interface Relation extends RelationDeclare, SXRenderable {

	RelationColumn getColumn(String columnName);

	RelationColumn findColumn(String columnName);
}

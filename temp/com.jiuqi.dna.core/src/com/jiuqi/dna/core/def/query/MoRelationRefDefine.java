package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDefine;

/**
 * 更新语句使用的关系引用定义
 * 
 * @deprecated 使用RelationRefDefine
 * 
 */
@Deprecated
public interface MoRelationRefDefine extends NamedDefine {

	public RelationDefine getTarget();

	@Deprecated
	public boolean isTableReference();

	@Deprecated
	public boolean isQueryReference();
}

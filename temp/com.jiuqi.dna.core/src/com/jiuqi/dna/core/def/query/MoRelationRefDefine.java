package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDefine;

/**
 * �������ʹ�õĹ�ϵ���ö���
 * 
 * @deprecated ʹ��RelationRefDefine
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

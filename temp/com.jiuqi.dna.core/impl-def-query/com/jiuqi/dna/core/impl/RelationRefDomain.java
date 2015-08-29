package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.RelationRefDomainDeclare;

public interface RelationRefDomain extends RelationRefDomainDeclare {

	RelationRefDomain getDomain();

	RelationRef findRelationRef(String name);

	RelationRef getRelationRef(String name);

	RelationRef findRelationRefRecursively(String name);

	RelationRef getRelationRefRecursively(String name);

	/**
	 * 获取指定名称with定义
	 */
	DerivedQueryImpl getWith(String name);

}

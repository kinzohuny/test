package com.jiuqi.dna.core.def.query;

/**
 * 关系引用域
 * 
 * @author houchunlei
 * 
 */
public interface RelationRefDomainDeclare extends RelationRefDomainDefine {

	RelationRefDomainDeclare getDomain();

	RelationRefDeclare findRelationRef(String name);

	RelationRefDeclare getRelationRef(String name);

	RelationRefDeclare findRelationRefRecursively(String name);

	RelationRefDeclare getRelationRefRecursively(String name);
}

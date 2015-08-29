package com.jiuqi.dna.core.impl;

/**
 * 关系引用定义的所有者
 * 
 * @author houchunlei
 */
interface RelationRefOwner {

	RelationRef getRelationRef(String name);

	RelationRef findRelationRef(String name);

}

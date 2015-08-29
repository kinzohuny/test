package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.QuQueryRefDeclare;

/**
 * 查询中使用的查询引用的内部接口
 * 
 * @author houchunlei
 * 
 */
public interface QuQueryRef extends QuRelationRef, QueryRef, QuQueryRefDeclare {

	DerivedQueryImpl getTarget();

	static final String xml_element_query = "derived-query";
}
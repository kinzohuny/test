package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.QuQueryRefDeclare;

/**
 * ��ѯ��ʹ�õĲ�ѯ���õ��ڲ��ӿ�
 * 
 * @author houchunlei
 * 
 */
public interface QuQueryRef extends QuRelationRef, QueryRef, QuQueryRefDeclare {

	DerivedQueryImpl getTarget();

	static final String xml_element_query = "derived-query";
}
package com.jiuqi.dna.core.def.query;

/**
 * ��ѯ������ʹ�õĲ�ѯ���ö���
 * 
 * @author houchunlei
 * 
 */
public interface QuQueryRefDefine extends QuRelationRefDefine,
		QueryReferenceDefine {

	public DerivedQueryDefine getTarget();
}

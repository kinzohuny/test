package com.jiuqi.dna.core.def.query;

/**
 * ��ѯ���ö���
 * 
 * <p>
 * �̳�����ϵ���ö���,��ʾĿ������Ϊ��ѯ����Ĺ�ϵ����.
 * 
 * @see com.jiuqi.dna.core.def.query.RelationRefDefine
 * 
 * @author houchunlei
 */
public interface QueryReferenceDefine extends RelationRefDefine {

	public SelectDefine getTarget();
}

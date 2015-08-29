package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDefine;

/**
 * ��ϵ��Ԫ����.������ά����ʽ�����ݽṹ��Ԫ���ݶ���.
 * 
 * @author houchunlei
 * 
 */
public interface RelationDefine extends NamedDefine {

	/**
	 * ����ָ�����ƵĹ�ϵ�ж���
	 * 
	 * @param columnName
	 * @return ���ع�ϵ�ж����null
	 */
	public RelationColumnDefine findColumn(String columnName);

	/**
	 * ��ȡָ�����ƵĹ�ϵ�ж���
	 * 
	 * @param columnName
	 * @return ���ع�ϵ�ж�����׳��쳣
	 */
	public RelationColumnDefine getColumn(String columnName);
}

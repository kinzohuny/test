package com.jiuqi.dna.core.def.query;

/**
 * ��ϵ��������.���������ɸ���ϵ���ö���,����������������ϵ������.
 * 
 * @author houchunlei
 * 
 */
public interface RelationRefDomainDefine {

	/**
	 * ���ص�ǰ��������������.�����������һ�����ǽṹ�ϵ�ֱ���ϼ���.
	 * 
	 * @return
	 */
	RelationRefDomainDefine getDomain();

	/**
	 * �ڵ�ǰ���ڲ���ָ�����ƵĹ�ϵ���ö���.
	 * 
	 * @param name
	 * @return �������򷵻�null
	 */
	RelationRefDefine findRelationRef(String name);

	/**
	 * ��ȡ��ǰ����ָ�����ƵĹ�ϵ���ö���.
	 * 
	 * @param name
	 * @return ���������׳��쳣
	 */
	RelationRefDefine getRelationRef(String name);

	/**
	 * �ڵ�ǰ����Ч���������ڵݹ����ָ�����ƵĹ�ϵ���ö���.
	 * 
	 * @param name
	 * @return �������򷵻�null
	 */
	RelationRefDefine findRelationRefRecursively(String name);

	/**
	 * �Եݹ���ҵķ�ʽ�ػ�ȡ�ڵ�ǰ����Ч����������ָ�����ƵĹ�ϵ����
	 * 
	 * @param name
	 * @return ���������׳��쳣
	 */
	RelationRefDefine getRelationRefRecursively(String name);
}

package com.jiuqi.dna.core.def.query;

/**
 * ��ϵ���ã���ָ��һ����ϵԪ����Ĵ��������<code>getTarget</code>���ص�ǰ���õ�Ŀ�ꡣ
 * ��ϵ���ÿ�����Ϊ�Ǹ��ṹ���ڹ�ϵԪ�����ά��ġ�
 * 
 * @author houchunlei
 * 
 */
@SuppressWarnings({ "deprecation" })
public interface RelationRefDefine extends MoRelationRefDefine {

	/**
	 * ��ȡĿ��Ԫ��ϵ����
	 * 
	 * @return ��ϵ��Ԫ����
	 */
	public RelationDefine getTarget();

	/**
	 * �Ƿ��Ǳ�����
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isTableReference();

	/**
	 * �Ƿ��ǲ�ѯ����
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isQueryReference();

}

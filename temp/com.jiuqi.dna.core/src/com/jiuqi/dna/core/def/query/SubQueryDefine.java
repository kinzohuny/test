package com.jiuqi.dna.core.def.query;

/**
 * �Ӳ�ѯ����
 * 
 * <p>
 * ��ָ�ڷ�from�Ӿ���ʹ�õ��Ӳ�ѯ�ṹ.��DerivedQuery��֮ͬ������:����ʹ����ṹ������Ĺ�ϵ����.
 * 
 * <p>
 * �Ӳ�ѯ�������ת��Ϊֵ���ʽ
 * 
 * @see com.jiuqi.dna.core.def.query.DerivedQueryDefine
 * 
 * @author gaojingxin
 * 
 */
public interface SubQueryDefine extends SelectDefine {

	/**
	 * ��ȡ�Ӳ�ѯ���ʽ
	 * 
	 * @return ֵ���ʽ
	 */
	public SubQueryExpression newExpression();

}

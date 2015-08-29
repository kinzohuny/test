package com.jiuqi.dna.core.def.exp;

/**
 * ν�ʱ��ʽ
 * 
 * <p>
 * ��ʾ������Ϊ�߼�ֵ������
 * 
 * @author gaojingxin
 * 
 */
public interface PredicateExpression extends ConditionalExpression,
		Iterable<ValueExpression> {

	/**
	 * ���ν��
	 * 
	 * @return
	 */
	public Predicate getPredicate();

	/**
	 * ���ֵ���ʽ�ĸ���
	 * 
	 * @return
	 */
	public int getCount();

	/**
	 * ���ֵ���ʽ�ĸ���
	 * 
	 * @return
	 */
	public int size();

	/**
	 * ��ȡָ����ֵ���ʽ
	 * 
	 * @param index
	 * 
	 * @return
	 */
	public ValueExpression get(int index);
}
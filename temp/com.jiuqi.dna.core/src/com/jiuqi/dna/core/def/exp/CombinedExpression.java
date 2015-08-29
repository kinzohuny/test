package com.jiuqi.dna.core.def.exp;

/**
 * ���ϱ��ʽ
 * 
 * @author gaojingxin
 * 
 */
public interface CombinedExpression extends ConditionalExpression,
		Iterable<ConditionalExpression> {

	/**
	 * �����Ƿ���������
	 * 
	 * @return �����Ƿ���������
	 */
	public boolean isAnd();

	/**
	 * ����������ʽ�ĸ���
	 * 
	 * @return
	 */
	public int getCount();

	/**
	 * ����������ʽ�ĸ���
	 * 
	 * @return
	 */
	public int size();

	/**
	 * ��ȡָ�����������ʽ
	 * 
	 * @param index
	 * @return
	 */
	public ConditionalExpression get(int index);
}
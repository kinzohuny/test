package com.jiuqi.dna.core.def.exp;

/**
 * ������ʽ
 * 
 * <p>
 * ��ʾ������Ϊֵ���ʽ������
 * 
 * @author gaojingxin
 * 
 */
public interface OperateExpression extends ValueExpression,
		Iterable<ValueExpression> {

	/**
	 * ��ò�����
	 * 
	 * @return
	 */
	public Operator getOperator();

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
	 * @return
	 */
	public ValueExpression get(int index);
}
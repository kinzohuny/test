package com.jiuqi.dna.core.def.exp;

/**
 * �������ʽ���ӿ�
 * 
 * <p>
 * �������ʽָ������Ϊ�������͵ı��ʽ������Ϊ�Ƚ�Ԥ�㣬�߼�����ȡ�
 * 
 * @author gaojingxin
 * 
 */
public interface ConditionalExpression {

	/**
	 * �Ƿ�ȡ��
	 * 
	 * @return
	 */
	public boolean isNot();

	/**
	 * ��ȡȡ��������
	 */
	public ConditionalExpression not();

	/**
	 * ��ȡ������
	 * 
	 * @param conditions
	 * @return
	 */
	public ConditionalExpression and(ConditionalExpression one,
			ConditionalExpression... others);

	/**
	 * ��ȡ������
	 * 
	 * @param conditions
	 * @return
	 */
	public ConditionalExpression or(ConditionalExpression one,
			ConditionalExpression... others);

	/**
	 * ����case
	 * 
	 * <pre>
	 * CASE WHEN current_condition THEN returnValue [...n] [ELSE defaultValue] END
	 * </pre>
	 * 
	 * @param returnValue
	 *            ֵ���ʽ
	 * @param others
	 *            ������ʽ��ֵ���ʽ��,�������Դ�Ĭ��ֵ
	 * @return
	 */
	public SearchedCaseExpr searchedCase(Object returnValue, Object... others);
}
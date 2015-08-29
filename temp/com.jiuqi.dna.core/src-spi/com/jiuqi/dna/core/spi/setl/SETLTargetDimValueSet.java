package com.jiuqi.dna.core.spi.setl;

/**
 * ά��ֵ���ϡ�<br>
 * �ýӿ���ָ����ȡ����ʵ�֡�
 */
public interface SETLTargetDimValueSet {
	/**
	 * ��ȡֵ����Ŀ
	 */
	public int size();

	/**
	 * ����һ��ά��ֵ
	 * 
	 * @param value
	 */
	public void addValue(Object value);

	/**
	 * �򼯺�������һ������ҿ�������[from, to)�������������from��������to<br>
	 * �Ⱥ����ӵ�ͬһ�������е����䲻�ܴ����ص�������
	 * 
	 * @param from
	 * @param to
	 */
	public void addRange(Object from, Object to);
}

package com.jiuqi.dna.core.invoke;

/**
 * �첽����ľ��
 * 
 * @author gaojingxin
 * 
 * @param <TTask>
 *            ��������
 */
public interface AsyncTask<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
        extends AsyncHandle {
	/**
	 * ���ִ����������
	 * 
	 * @return ����ִ����������
	 * @throws IllegalStateException
	 *             �������δִ���꣬���׳����쳣
	 */
	public TTask getTask() throws IllegalStateException;

	/**
	 * ��ø������ִ�з���
	 * 
	 * @return ����ִ�з���
	 */
	public TMethod getMethod();
}

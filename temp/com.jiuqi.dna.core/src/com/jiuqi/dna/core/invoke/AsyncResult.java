package com.jiuqi.dna.core.invoke;

import com.jiuqi.dna.core.misc.MissingObjectException;

/**
 * �첽��ѯ�ľ��
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 *            �������
 */
public interface AsyncResult<TResult> extends AsyncHandle {
	/**
	 * ���ִ�����Ľ��
	 * 
	 * @return ���ؽ��
	 * @throws IllegalStateException
	 *             ��������δ���أ����׳����쳣
	 * @throws MissingObjectException
	 *             ���û�з��ؽ����null�����׳����쳣
	 */
	public TResult getResult() throws IllegalStateException,
	        MissingObjectException;

	/**
	 * ���ؽ���Ƿ�Ϊ��
	 * 
	 * @return ���ؽ���Ƿ�Ϊ��
	 * @throws IllegalStateException
	 *             ��������δ���أ����׳����쳣
	 */
	public boolean isNull() throws IllegalStateException;

	/**
	 * �������������
	 * 
	 * @return ��������������
	 */
	public Class<TResult> getResultClass();
}

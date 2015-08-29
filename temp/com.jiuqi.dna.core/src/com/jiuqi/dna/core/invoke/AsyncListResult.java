package com.jiuqi.dna.core.invoke;

import java.util.List;

/**
 * �첽��ѯ�б�ľ��
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */

@SuppressWarnings("deprecation")
public interface AsyncListResult<TResult> extends AsyncHandle,
		AsyncResultList<TResult> {
	/**
	 * ���ִ�����Ľ���б�
	 * 
	 * @return ���ؽ���б�
	 * @throws IllegalStateException
	 *             ��������δ���أ����׳����쳣
	 */
	public List<TResult> getResultList() throws IllegalStateException;

	/**
	 * �������������
	 * 
	 * @return ��������������
	 */
	public Class<TResult> getResultClass();
}

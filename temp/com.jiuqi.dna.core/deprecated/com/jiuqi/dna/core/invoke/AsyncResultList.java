package com.jiuqi.dna.core.invoke;

import java.util.List;

/**
 * �첽��ѯ�б�ľ�����Ѿ���������ʹ��AsyncListResult
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */
@Deprecated
public interface AsyncResultList<TResult> extends AsyncHandle {
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

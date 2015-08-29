package com.jiuqi.dna.core.invoke;

/**
 * �����첽��ѯ�б�ľ��
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */

@SuppressWarnings("deprecation")
public interface OneKeyAsyncListResult<TResult, TKey> extends
		AsyncListResult<TResult>, OneKeyOverlappedResultList<TResult, TKey> {
	/**
	 * �õ���һ����
	 * 
	 * @return ���ص�һ����
	 */
	public TKey getKey1();
}

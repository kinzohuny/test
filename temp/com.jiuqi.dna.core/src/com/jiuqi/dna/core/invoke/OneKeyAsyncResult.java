package com.jiuqi.dna.core.invoke;

/**
 * �����첽��ѯ�ľ��
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 * @param <TKey>
 */
@SuppressWarnings("deprecation")
public interface OneKeyAsyncResult<TResult, TKey> extends AsyncResult<TResult>,
		OneKeyOverlappedResult<TResult, TKey> {
	/**
	 * ��øò�ѯ�ĵ�һ����
	 * 
	 * @return ���ص�һ����
	 */
	public TKey getKey1();
}

package com.jiuqi.dna.core.invoke;

/**
 * �����첽��ѯ�ľ�����Ѿ���������ʹ��OneKeyAsyncResult
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 * @param <TKey>
 */
@Deprecated
public interface OneKeyOverlappedResult<TResult, TKey> extends
		AsyncResult<TResult> {
	/**
	 * ��øò�ѯ�ĵ�һ����
	 * 
	 * @return ���ص�һ����
	 */
	public TKey getKey1();
}

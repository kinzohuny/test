package com.jiuqi.dna.core.invoke;

/**
 * ˫���첽��ѯ�ľ��
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 * @param <TKey>
 */
@SuppressWarnings("deprecation")
public interface TwoKeyAsyncResult<TResult, TKey1, TKey2> extends
		OneKeyAsyncResult<TResult, TKey1>,
		TwoKeyOverlappedResult<TResult, TKey1, TKey2> {
	/**
	 * ��øò�ѯ�ĵڶ�����
	 * 
	 * @return ���صڶ�����
	 */
	public TKey2 getKey2();
}

package com.jiuqi.dna.core.invoke;

/**
 * ˫���첽��ѯ�ľ�����Ѿ���������ʹ��TwoKeyAsyncResult
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 * @param <TKey>
 */
@Deprecated
public interface TwoKeyOverlappedResult<TResult, TKey1, TKey2> extends
		OneKeyOverlappedResult<TResult, TKey1> {
	/**
	 * ��øò�ѯ�ĵڶ�����
	 * 
	 * @return ���صڶ�����
	 */
	public TKey2 getKey2();
}

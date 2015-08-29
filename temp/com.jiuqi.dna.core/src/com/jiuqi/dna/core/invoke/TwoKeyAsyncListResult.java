package com.jiuqi.dna.core.invoke;

/**
 * ˫���첽��ѯ�б�ľ��
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */
@SuppressWarnings("deprecation")
public interface TwoKeyAsyncListResult<TResult, TKey1, TKey2> extends
		OneKeyAsyncListResult<TResult, TKey1>,
		TwoKeyOverlappedResultList<TResult, TKey1, TKey2> {
	/**
	 * �õ��ڶ�����
	 * 
	 * @return ���صڶ�����
	 */
	public TKey2 getKey2();
}

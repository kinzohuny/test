package com.jiuqi.dna.core.invoke;

/**
 * ˫���첽��ѯ�б�ľ�����Ѿ���������ʹ��TwoKeyAsyncListResult
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */
@Deprecated
public interface TwoKeyOverlappedResultList<TResult, TKey1, TKey2> extends
		OneKeyOverlappedResultList<TResult, TKey1> {
	/**
	 * �õ��ڶ�����
	 * 
	 * @return ���صڶ�����
	 */
	public TKey2 getKey2();
}

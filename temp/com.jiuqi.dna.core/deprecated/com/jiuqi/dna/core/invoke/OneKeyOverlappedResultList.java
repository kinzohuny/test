package com.jiuqi.dna.core.invoke;

/**
 * �����첽��ѯ�б�ľ�����Ѿ���������ʹ��OneKeyAsyncListResult
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */
@Deprecated
public interface OneKeyOverlappedResultList<TResult, TKey> extends
		AsyncResultList<TResult> {
	/**
	 * �õ���һ����
	 * 
	 * @return ���ص�һ����
	 */
	public TKey getKey1();
}

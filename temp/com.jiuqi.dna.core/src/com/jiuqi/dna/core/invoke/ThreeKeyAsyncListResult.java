package com.jiuqi.dna.core.invoke;

/**
 * �����첽��ѯ�б�ľ��
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */
@SuppressWarnings("deprecation")
public interface ThreeKeyAsyncListResult<TResult, TKey1, TKey2, TKey3> extends
		TwoKeyAsyncListResult<TResult, TKey1, TKey2>,
		ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> {
	/**
	 * �õ���������
	 * 
	 * @return ���ص�������
	 */
	public TKey3 getKey3();
}

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
public interface ThreeKeyAsyncResult<TResult, TKey1, TKey2, TKey3> extends
		TwoKeyAsyncResult<TResult, TKey1, TKey2>,
		ThreeKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> {
	/**
	 * ��øò�ѯ�ĵ�������
	 * 
	 * @return ���ص�������
	 */
	public TKey3 getKey3();
}

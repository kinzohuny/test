package com.jiuqi.dna.core.invoke;

/**
 * ˫���첽��ѯ�ľ�����Ѿ���������ʹ��ThreeKeyAsyncResult
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 * @param <TKey>
 */
@Deprecated
public interface ThreeKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> extends
		TwoKeyOverlappedResult<TResult, TKey1, TKey2> {
	/**
	 * ��øò�ѯ�ĵ�������
	 * 
	 * @return ���ص�������
	 */
	public TKey3 getKey3();
}

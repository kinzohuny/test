package com.jiuqi.dna.core.invoke;

/**
 * �����첽��ѯ�б�ľ�����Ѿ���������ʹ��ThreeKeyAsyncListResult
 * 
 * @author gaojingxin
 * 
 * @param <TResult>
 */
@Deprecated
public interface ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3>
		extends TwoKeyOverlappedResultList<TResult, TKey1, TKey2> {
	/**
	 * �õ���������
	 * 
	 * @return ���ص�������
	 */
	public TKey3 getKey3();
}

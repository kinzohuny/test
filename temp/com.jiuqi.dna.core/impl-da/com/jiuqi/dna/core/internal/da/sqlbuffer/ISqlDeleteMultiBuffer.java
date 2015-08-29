package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * mysql���ԵĶ��ɾ�����buffer
 * 
 * @author houchunlei
 * 
 */
public interface ISqlDeleteMultiBuffer extends ISqlCommandBuffer {

	/**
	 * delete����Ŀ�������
	 * 
	 * @return
	 */
	public ISqlTableRefBuffer target();

	/**
	 * delete����������
	 * 
	 * @return
	 */
	public ISqlExprBuffer where();

	public void from(String alias);
}

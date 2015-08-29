package com.jiuqi.dna.core.internal.da.sqlbuffer;

public interface ISqlDeleteBuffer extends ISqlBuffer, ISqlCommandBuffer {

	/**
	 * deleteĿ�������
	 * 
	 * @return
	 */
	public ISqlTableRefBuffer target();

	/**
	 * delete����
	 * 
	 * @return
	 */
	public ISqlExprBuffer where();

	/**
	 * delete����Ϊָ���α���
	 * 
	 * @param cursor
	 */
	public void whereCurrentOf(String cursor);
}

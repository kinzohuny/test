package com.jiuqi.dna.core.def.model;

/**
 * �б�����
 * 
 * @author gaojingxin
 * 
 */
public interface ListPropertyValue extends Iterable<Object> {
	/**
	 * �б��С
	 */
	public int size();

	/**
	 * ��ȡĳԪ��
	 * 
	 * @param index
	 *            λ��
	 */
	public Object get(int index);
}

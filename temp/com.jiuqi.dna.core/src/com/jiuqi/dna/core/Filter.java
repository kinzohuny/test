package com.jiuqi.dna.core;

/**
 * ������
 * 
 * @author gaojingxin
 * 
 * @param <TItem>
 */
public interface Filter<TItem> {

	/**
	 * �жϹ������Ƿ����ĳ��
	 * 
	 * @param item
	 * @return ���ع������Ƿ����ĳ��
	 */
	public boolean accept(TItem item);
}

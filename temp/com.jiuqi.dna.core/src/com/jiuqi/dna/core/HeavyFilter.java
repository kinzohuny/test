package com.jiuqi.dna.core;

/**
 * �ⲿ���������ṩ�������Ļ����Ĺ���
 * 
 * @author gaojingxin
 * 
 * @param <TItem>
 */
public interface HeavyFilter<TItem> extends Filter<TItem> {
	/**
	 * �жϹ������Ƿ����ĳ��
	 * 
	 * @return ���ع������Ƿ����ĳ��
	 */
	public boolean accept(Context context, TItem item);
}

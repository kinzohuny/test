package com.jiuqi.dna.core.internal.da.sqlbuffer;

public interface IFeaturable {

	/**
	 * ��ȡ����
	 * 
	 * @param <T>
	 * @param clazz
	 * @return ��ǰ����֧��ָ������,�򷵻����Ե�ʵ��,���򷵻�null.
	 */
	public <T> T getFeature(Class<T> clazz);
}

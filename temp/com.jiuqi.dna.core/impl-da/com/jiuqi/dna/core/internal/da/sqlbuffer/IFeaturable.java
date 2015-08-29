package com.jiuqi.dna.core.internal.da.sqlbuffer;

public interface IFeaturable {

	/**
	 * 获取特性
	 * 
	 * @param <T>
	 * @param clazz
	 * @return 当前对象支持指定特性,则返回特性的实例,否则返回null.
	 */
	public <T> T getFeature(Class<T> clazz);
}

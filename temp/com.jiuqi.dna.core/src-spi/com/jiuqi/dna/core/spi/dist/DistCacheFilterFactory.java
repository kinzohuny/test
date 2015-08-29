package com.jiuqi.dna.core.spi.dist;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.Filter;

/**
 * 分布式DNA系统的缓存过滤器
 * 
 * <p>
 * 
 * @author houchunlei
 * 
 * @param <T>
 */
public interface DistCacheFilterFactory<T> {

	/**
	 * 创建新的缓存过滤器
	 * 
	 * @param context
	 * @param args
	 * @return
	 */
	public Filter<T> newInstance(Context context, String args);
}
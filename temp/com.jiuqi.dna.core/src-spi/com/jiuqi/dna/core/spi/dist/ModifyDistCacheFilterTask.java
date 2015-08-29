package com.jiuqi.dna.core.spi.dist;

import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * 修改分布式DNA的缓存过滤器任务
 * 
 * @author houchunlei
 * 
 */
public final class ModifyDistCacheFilterTask<TFacade> extends SimpleTask {

	public final Class<?> facadeClass;
	public final String template;
	public final String space;
	public final Filter<TFacade> filter;

	public ModifyDistCacheFilterTask(Class<?> facadeClass, String template,
			String space, Filter<TFacade> filter) {
		this.facadeClass = facadeClass;
		this.template = template;
		this.space = space;
		this.filter = filter;
	}
}
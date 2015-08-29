package com.jiuqi.dna.core.spi.dist;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 分布式DNA环境下的缓存过滤器
 * 
 * <p>
 * 可用于定义：节点加载的缓存范围；节点禁止修改的缓存范围。
 * 
 * @author houchunlei
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DistCacheFilter {

	/**
	 * 过滤器的规则模板
	 * 
	 * @return
	 */
	public String template();
}
package com.jiuqi.dna.core.resource;

/**
 * 可设置类别的资源请求器
 * 
 * @author gaojingxin
 * 
 */
public interface CategorialResourceQuerier extends ResourceQuerier {
	/**
	 * 设置资源的类别
	 */
	public void setCategory(Object category);
}

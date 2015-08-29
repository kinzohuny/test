package com.jiuqi.dna.core.resource;

/**
 * 资源子项链节
 * 
 * @author gaojingxin
 * 
 */
public interface ResourceTokenLink<TFacade> {

	/**
	 * 节点上的资源标识
	 */
	public ResourceToken<TFacade> getToken();

	/**
	 * 下一个链节，或者null。
	 */
	public ResourceTokenLink<TFacade> next();

}

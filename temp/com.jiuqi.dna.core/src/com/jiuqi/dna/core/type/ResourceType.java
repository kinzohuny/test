package com.jiuqi.dna.core.type;

/**
 * 资源引用类型
 * 
 * @author gaojingxin
 * 
 */
@Deprecated
public interface ResourceType<TFacade> extends Type {
	/**
	 * 资源的外观类型
	 */
	public Class<TFacade> getFacadeClass();

	/**
	 * 资源的类别
	 */
	public Object getCategory();
}

package com.jiuqi.dna.core.resource;

import com.jiuqi.dna.core.exception.DisposedException;

/**
 * 资源存根
 * 
 * @author gaojingxin
 * 
 * @param <TFacade>
 *            资源外观
 */
public interface ResourceStub<TFacade> {

	/**
	 * 获得资源模式
	 */
	public ResourceKind getKind();

	/**
	 * 同一外观资源的另一类别
	 */
	public Object getCategory();

	/**
	 * 获得外观类
	 */
	public Class<TFacade> getFacadeClass();

	/**
	 * 获得该资源的外观对象
	 * 
	 * @return 返回资源所指的对象，即实际要使用的对象
	 */
	public TFacade getFacade() throws DisposedException;

	/**
	 * 尝试获得该资源的外观对象
	 * 无效时返回null
	 */
	public TFacade tryGetFacade();
}

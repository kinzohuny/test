package com.jiuqi.dna.core.resource;

/**
 * 资源标识
 * 
 * 
 * @param <TFacade>
 */
public interface ResourceToken<TFacade> extends ResourceStub<TFacade> {

	/**
	 * 空标识（无法定位资源值）
	 */
	@SuppressWarnings("unchecked")
	public static final ResourceToken MISSING = ResourceService.MISSTOKEN;

	/**
	 * 获得本类资源的父节点(资源树上级)
	 * 
	 * @return 返回父标识或者null
	 */
	public ResourceToken<TFacade> getParent();

	/**
	 * 返回本类资源的直接下级链表(资源树下级)
	 * 
	 * @return 返回第一个子链节点，或者null表示没有孩子
	 */
	public ResourceTokenLink<TFacade> getChildren();

	/**
	 * 获得引用当前资源的资源
	 * 
	 * @param <TSuperFacade>
	 *            引用资源外观类型
	 * @param superTokenFacadeClass
	 *            引用资源外观类
	 * @return 返回引用当前资源的资源，null表示当前资源没有被引用
	 * @throws IllegalArgumentException
	 *             不存在这样的资源引用定义则抛出异常
	 */
	public <TSuperFacade> ResourceToken<TSuperFacade> getSuperToken(
			Class<TSuperFacade> superTokenFacadeClass)
			throws IllegalArgumentException;

	/**
	 * 获得当前资源引用的某类资源的链表
	 * 
	 * @param <TSubFacade>
	 *            被引用资源的外观
	 * @param subTokenFacadeClass
	 *            被引用资源的外观类
	 * @return 返回第一个子链节点，或者null表示没有当前资源没有引用任何资源
	 * @throws IllegalArgumentException
	 *             不存在这样的资源引用定义则抛出异常
	 */
	public <TSubFacade> ResourceTokenLink<TSubFacade> getSubTokens(
			Class<TSubFacade> subTokenFacadeClass)
			throws IllegalArgumentException;

}

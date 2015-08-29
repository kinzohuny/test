package com.jiuqi.dna.core.resource;

/**
 * 资源句柄<br>
 * 用以包装资源对象以及其信息
 * 
 * @author gaojingxin
 * 
 * @param <TFacade>
 *            资源的读取方面（接口或对象）该类型的所有方法应该只包含对资源的只读访问方法
 */
public interface ResourceHandle<TFacade> extends ResourceStub<TFacade> {

	/**
	 * 获得子资源查询器
	 */
	public ResourceQuerier getOwnedResourceQuerier();

	/**
	 * 获得资源标识
	 */
	public ResourceToken<TFacade> getToken();

	/**
	 * 关闭句柄，释放锁
	 */
	public void closeHandle();

}

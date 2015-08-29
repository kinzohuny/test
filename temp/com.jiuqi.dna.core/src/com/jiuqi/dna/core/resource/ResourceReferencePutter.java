package com.jiuqi.dna.core.resource;

import com.jiuqi.dna.core.auth.Operation;

/**
 * 资源引用设置器接口。
 * 
 * @author gaojingxin
 * 
 * @param <TFacade>
 *            资源外观，即资源实现提供的只读接口
 * @param <TImpl>
 *            资源实现类型，既可以用来修改资源的接口或者类型，大部分时候使用资源的实现类型
 * @param <TKeysHolder>
 *            资源键源，既可以从中得到资源的键的值的接口或者类型，大部分时候使用资源的实现类型
 */
public interface ResourceReferencePutter<TFacade, TImpl extends TFacade, TKeysHolder> {
	/**
	 * 获得资源类别
	 */
	public Object getCategory();

	/**
	 * 设置资源服务中的资源对象之间的引用关系。
	 * <p>
	 * 这个操作会把资源对象<code>reference</code>放到指定资源对象<code>holder</code>中。
	 * <p>
	 * 这个操作不会影响<code>reference</code>的其它设置，也就是说，如果<code>reference</code>
	 * 已经存在于其它holder中，这里也不会将<code>reference</code>从那些holder中移除。
	 * 
	 * @param <THolderFacade>
	 *            保持<code>reference</code>引用的资源的外观类型
	 * @param holder
	 *            引用的保持者
	 * @param reference
	 *            引用对象
	 */
	<THolderFacade> void putResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference);

	<THolderFacade> void putResourceReferenceBy(ResourceToken<TFacade> holder,
			ResourceToken<THolderFacade> reference);

	/**
	 * 移除资源服务中的资源对象之间的引用关系。
	 * <p>
	 * 这个操作只解除资源对象之间的引用关系，并不从资源服务中删除资源对象。
	 * 
	 * @param <THolderFacade>
	 *            保持<code>reference</code>引用的资源的外观类型
	 * @param holder
	 *            引用的保持者
	 * @param reference
	 *            引用对象
	 */
	<THolderFacade> void removeResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference);

	<TReferenceFacade> void removeResourceReferenceBy(
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference);

	/**
	 * 移除资源服务中的资源对象之间的引用关系。
	 * <p>
	 * 这个操作只解除资源对象之间的引用关系，并不从资源服务中删除资源对象。
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param <THolderFacade>
	 *            保持<code>reference</code>引用的资源的外观类型
	 * @param holder
	 *            引用的保持者
	 * @param reference
	 *            引用对象
	 */
	<THolderFacade> void removeResourceReference(
			Operation<? super TFacade> operation,
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference);

	<TReferenceFacade> void removeResourceReferenceBy(
			Operation<? super TReferenceFacade> operation,
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference);
}

package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.service.Publish.Mode;

/**
 * 资源管理器基类代理
 * 
 * @author gaojingxin
 * 
 */
@SuppressWarnings({ "rawtypes" })
final class ResourceServiceBroker extends
		ServiceInvokeeBase<ResourceServiceBase, Context, None, None, None> {

	ResourceServiceBroker(ResourceServiceBase resourceService, Mode publishMode) {
		this.resourceService = resourceService;
		this.publishMode = publishMode;
	}

	@Override
	final boolean match(Class<?> key1Class, Class<?> key2Class,
			Class<?> key3Class, int mask) {
		return mask == MASK_RESOURCE;
	}

	@Override
	final Class<?> getTargetClass() {
		return this.resourceService.facadeClass;
	}

	final ResourceServiceBase resourceService;

	@Override
	final ServiceBase<?> getService() {
		return this.resourceService;
	}

	@Override
	final ResourceServiceBase provide(Context context) throws Throwable {
		return this.resourceService;
	}

	@Override
	final ResourceServiceBase getResourceService() {
		return this.resourceService;
	}

}

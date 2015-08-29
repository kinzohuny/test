package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.publish.BundleToken;
import com.jiuqi.dna.core.type.Convert;

final class PublishedService extends PublishedElement {

	final Class<? extends ServiceBase<?>> clazz;
	final Class<? extends ResourceServiceBase<?, ?, ?>> ownerResourceClass;
	ServiceBase<?> ref;
	float priority;
	final boolean xmlPriorityValid;

	@Override
	final float getPriority(StartupStep<?> step) {
		return step == service_init ? this.priority : 0.0f;
	}

	final static String xml_attr_class = "class";
	final static String xml_attr_owner = "owner";
	final static String xml_attr_priority = "priority";

	@SuppressWarnings("unchecked")
	PublishedService(BundleToken bundle, SXElement element) throws Throwable {
		this.clazz = (Class) bundle.loadClass(element.getAttribute(xml_attr_class), ServiceBase.class);
		Class ownerResourceClass = null;
		if (ResourceServiceBase.class.isAssignableFrom(this.clazz)) {
			String ownerName = element.getAttribute(xml_attr_owner);
			if (ownerName != null && ownerName.length() > 0) {
				ownerResourceClass = bundle.loadClass(ownerName, ResourceServiceBase.class);
			}
		}
		this.ownerResourceClass = ownerResourceClass;
		String p = element.getAttribute(xml_attr_priority, null);
		boolean xmlPriorityValid;
		if (p == null || p.length() == 0) {
			xmlPriorityValid = false;
		} else {
			try {
				this.priority = Convert.toFloat(p);
				xmlPriorityValid = true;
			} catch (Throwable e) {
				xmlPriorityValid = false;
			}
		}
		this.xmlPriorityValid = xmlPriorityValid;

	}

	/**
	 * 服务实例化
	 */
	final static StartupStepBase<PublishedService> create = new StartupStepBase<PublishedService>(StartupStep.SERVICE_HIGHEST_PRI, "实例化服务对象") {

		@Override
		public StartupStep<PublishedService> doStep(ResolveHelper helper,
				PublishedService target) throws Throwable {
			target.ref = helper.newObject(target.clazz, target.space);
			target.ref.bundle = target.bundle;
			target.ref.setSpace(target.space);
			target.space.regService(target.ref, target.publishMode, helper.catcher);
			if (!target.xmlPriorityValid) {
				target.priority = target.ref.getPriority();
			}
			return reg_invokee;
		}

	};

	/**
	 * 注册服务调用器
	 */
	final static StartupStepBase<PublishedService> reg_invokee = new StartupStepBase<PublishedService>(create, 0x100, "注册服务调用器") {

		@Override
		public StartupStep<PublishedService> doStep(ResolveHelper helper,
				PublishedService target) throws Throwable {
			target.ref.regInvokees(target.publishMode, helper.catcher);
			if (target.ref instanceof ResourceServiceBase<?, ?, ?>) {
				if (target.ownerResourceClass != null) {
					return resource_service_owner;
				} else {
					return resource_service_keyinfo;
				}
			} else {
				// helper.incServiceToInit();
				return service_init;
			}
		}

	};
	/**
	 * 确定资源服务的父资源
	 */
	final static StartupStepBase<PublishedService> resource_service_owner = new StartupStepBase<PublishedService>(reg_invokee, 0x100, "确定资源服务的父资源服务") {

		@Override
		public StartupStep<PublishedService> doStep(ResolveHelper helper,
				PublishedService target) throws Throwable {
			ResourceServiceBase<?, ?, ?> owner = target.space.findElement(target.ownerResourceClass);
			if (owner == null) {
				throw new IllegalArgumentException("无法定位父资源服务:" + target.ownerResourceClass);
			}
			target.ref.trySetOwnerResourceService(owner);
			return resource_service_keyinfo;
		}

	};
	/**
	 * 确定资源定位信息
	 */
	final static StartupStepBase<PublishedService> resource_service_keyinfo = new StartupStepBase<PublishedService>(resource_service_owner, 0x100, "确定资源定位信息") {

		@Override
		public StartupStep<PublishedService> doStep(ResolveHelper helper,
				PublishedService target) throws Throwable {
			helper.tryBuildResourceKeyPathInfos(target.ref);
			return resource_ref_info;
		}

	};
	/**
	 * 确定资源引用信息
	 */
	final static StartupStepBase<PublishedService> resource_ref_info = new StartupStepBase<PublishedService>(resource_service_keyinfo, 0x100, "确定资源引用信息") {

		@Override
		public StartupStep<PublishedService> doStep(ResolveHelper helper,
				PublishedService target) throws Throwable {
			helper.tryBuildResourceRefInfos(target.ref);
			// helper.incServiceToInit();
			return service_init;
		}
	};
	/**
	 * 服务初始化
	 */
	final static StartupStepBase<PublishedService> service_init = new StartupStepBase<PublishedService>(resource_ref_info, 0x100, "初始化服务") {

		@Override
		public StartupStep<PublishedService> doStep(ResolveHelper helper,
				PublishedService target) throws Throwable {
			long start = System.currentTimeMillis();
			helper.tryInitService(target.ref);
			if (ContextVariableIntl.PRINT_INIT_SERVICE) {
				ResolveHelper.logStartInfo("初始化服务," + target.clazz.getName() + "," + (System.currentTimeMillis() - start));
			}
			return null;
		}

	};
}

package com.jiuqi.dna.core.impl;

/**
 * 站点启动过程
 * 
 * @author niuhaifeng
 * 
 */
class SiteStartupEntry extends StartupEntry {
	final Site site;

	public SiteStartupEntry(Site site) {
		this.site = site;
	}

	/**
	 * 启动站点步骤在实例化表定义之前
	 */
	final static StartupStepBase<SiteStartupEntry> STARTUP = new StartupStepBase<SiteStartupEntry>(PublishedDeclarator.information_prepare, 0x50, "准备启动站点") {
		@Override
		public StartupStep<SiteStartupEntry> doStep(ResolveHelper helper,
				SiteStartupEntry target) throws Throwable {
			target.site.starter.start();
			return LOAD_RESOURCE;
		}
	};

	final static StartupStepBase<SiteStartupEntry> LOAD_RESOURCE = new StartupStepBase<SiteStartupEntry>(PublishedService.resource_ref_info, 0x50, "初始化资源") {
		@Override
		public StartupStep<SiteStartupEntry> doStep(ResolveHelper helper,
				SiteStartupEntry target) throws Throwable {
			// 初始化资源定义
			target.site.cache.initializeDefines();
			// 装载资源
			target.site.starter.loadResources();
			// 结束初始化
			target.site.cache.finishInitialize();
			return STARTUP_FINISH;
		}
	};

	final static StartupStepBase<SiteStartupEntry> STARTUP_FINISH = new StartupStepBase<SiteStartupEntry>(PublishedService.service_init, 0x50, "站点启动完成") {
		@Override
		public StartupStep<SiteStartupEntry> doStep(ResolveHelper helper,
				SiteStartupEntry target) throws Throwable {
			target.site.starter.finish();
			return null;
		}
	};
}

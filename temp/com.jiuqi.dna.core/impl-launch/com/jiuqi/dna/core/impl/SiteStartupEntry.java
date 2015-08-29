package com.jiuqi.dna.core.impl;

/**
 * վ����������
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
	 * ����վ�㲽����ʵ��������֮ǰ
	 */
	final static StartupStepBase<SiteStartupEntry> STARTUP = new StartupStepBase<SiteStartupEntry>(PublishedDeclarator.information_prepare, 0x50, "׼������վ��") {
		@Override
		public StartupStep<SiteStartupEntry> doStep(ResolveHelper helper,
				SiteStartupEntry target) throws Throwable {
			target.site.starter.start();
			return LOAD_RESOURCE;
		}
	};

	final static StartupStepBase<SiteStartupEntry> LOAD_RESOURCE = new StartupStepBase<SiteStartupEntry>(PublishedService.resource_ref_info, 0x50, "��ʼ����Դ") {
		@Override
		public StartupStep<SiteStartupEntry> doStep(ResolveHelper helper,
				SiteStartupEntry target) throws Throwable {
			// ��ʼ����Դ����
			target.site.cache.initializeDefines();
			// װ����Դ
			target.site.starter.loadResources();
			// ������ʼ��
			target.site.cache.finishInitialize();
			return STARTUP_FINISH;
		}
	};

	final static StartupStepBase<SiteStartupEntry> STARTUP_FINISH = new StartupStepBase<SiteStartupEntry>(PublishedService.service_init, 0x50, "վ���������") {
		@Override
		public StartupStep<SiteStartupEntry> doStep(ResolveHelper helper,
				SiteStartupEntry target) throws Throwable {
			target.site.starter.finish();
			return null;
		}
	};
}

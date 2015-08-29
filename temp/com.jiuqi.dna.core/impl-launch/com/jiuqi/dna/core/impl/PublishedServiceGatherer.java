package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.publish.BundleToken;

/**
 * 服务发布收集器
 * 
 * @author gaojingxin
 * 
 */
final class PublishedServiceGatherer extends
		PublishedElementGatherer<PublishedService> {
	@Override
	final void afterGatherElement(PublishedService pe, ResolveHelper helper) {
		helper.regStartupEntry(PublishedService.create, pe);
	}

	@Override
	protected PublishedService parseElement(SXElement element,
			BundleToken bundle) throws Throwable {
		return new PublishedService(bundle, element);
	}
}

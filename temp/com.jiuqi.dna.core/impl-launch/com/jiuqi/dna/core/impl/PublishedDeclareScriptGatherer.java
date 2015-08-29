package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.publish.BundleToken;

/**
 * ���񷢲��ռ���
 * 
 * @author gaojingxin
 * 
 */
final class PublishedDeclareScriptGatherer extends
		PublishedElementGatherer<PublishedDeclareScript> {

	@Override
	protected final PublishedDeclareScript parseElement(SXElement element,
			BundleToken bundle) throws Throwable {
		return new PublishedDeclareScript(element, bundle);
	}

	@Override
	final void afterGatherElement(PublishedDeclareScript pe,
			ResolveHelper helper) {
		if (pe.space.regDeclareScript(pe.declareName, pe.type, pe.url, pe.publishMode, helper.catcher)) {
			// TODO ע�ᵽ��صĽ׶�ʵ��������
			// helper.regStartupEntry(this, pe);
		}
	}
}

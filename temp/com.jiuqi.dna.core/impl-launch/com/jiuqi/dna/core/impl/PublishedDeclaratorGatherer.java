package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.PublishedDeclarator.CreateStep;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.publish.BundleToken;

final class PublishedDeclaratorGatherer extends
		PublishedElementGatherer<PublishedDeclarator> {

	@Override
	protected final PublishedDeclarator parseElement(SXElement element,
			BundleToken bundle) throws Throwable {
		return new PublishedDeclarator(bundle.loadClass(element.getAttribute(PublishedElementGatherer.xml_attr_class), this.beginStep.baseClass));
	}

	@Override
	final void afterGatherElement(PublishedDeclarator pe, ResolveHelper helper) {
		helper.regStartupEntry(this.beginStep, pe);
		this.beginStep.tryLoadScript(pe, helper);
	}

	final private CreateStep beginStep;

	PublishedDeclaratorGatherer(SXElement element) {
		String elementTag = element.getAttribute(PublishedElementGatherer.xml_attr_element);
		this.beginStep = PublishedDeclarator.beginSteps.get(elementTag);
		if (this.beginStep == null) {
			throw new UnsupportedOperationException("不支持的标记:" + elementTag);
		}
	}
}

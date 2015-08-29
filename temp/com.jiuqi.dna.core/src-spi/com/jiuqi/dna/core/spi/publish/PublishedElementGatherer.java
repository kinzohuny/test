package com.jiuqi.dna.core.spi.publish;

import com.jiuqi.dna.core.misc.SXElement;

public abstract class PublishedElementGatherer<TPublishedElement extends PublishedElement>
        extends
        com.jiuqi.dna.core.impl.PublishedElementGatherer<TPublishedElement> {
	@Override
	protected abstract TPublishedElement parseElement(SXElement element,
	        BundleToken bundle) throws Throwable;
}

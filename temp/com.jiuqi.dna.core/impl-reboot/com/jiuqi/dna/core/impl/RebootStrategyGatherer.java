package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.impl.RebootStrategyGatherer.RebootStrategyElement;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.publish.BundleToken;

public class RebootStrategyGatherer extends
		PublishedElementGatherer<RebootStrategyElement> {
	static class RebootStrategyElement extends PublishedElement {
		private final Class<?> clazz;
		private final String name;

		RebootStrategyElement(String name, Class<?> clazz) {
			this.name = name;
			this.clazz = clazz;
		}
	}

	@Override
	protected RebootStrategyElement parseElement(SXElement element,
			BundleToken bundle) throws Throwable {
		String className = element.getAttribute(xml_attr_class);
		if (className == null) {
			throw new IllegalArgumentException("Bundle[" + bundle.getName() + "]�е�dna.xmlԪ��[" + element + "]ȱ��[class]����");
		}
		return new RebootStrategyElement(element.getAttribute("name"), bundle.loadClass(className, RebootStrategy.class));
	}

	@Override
	void afterGatherElement(RebootStrategyElement pe, ResolveHelper helper) {
		pe.space.regNamedDefineToSpace(RebootStrategyDefine.class, new RebootStrategyDefineImpl(pe.name, pe.clazz), null);
	}
}

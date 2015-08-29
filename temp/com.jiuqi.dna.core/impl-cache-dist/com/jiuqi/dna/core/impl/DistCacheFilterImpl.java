package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.def.obja.StructClass;

@StructClass
final class DistCacheFilterImpl {

	// 针对自身的为"";其它应用节点为url
	final String target;

	final Class<?> facadeClass;
	final String template;
	final Filter<?> inner;

	DistCacheFilterImpl(String target, Class<?> facadeClass, String template,
			Filter<?> inner) {
		this.target = target;
		this.template = template;
		this.facadeClass = facadeClass;
		this.inner = inner;
	}
}
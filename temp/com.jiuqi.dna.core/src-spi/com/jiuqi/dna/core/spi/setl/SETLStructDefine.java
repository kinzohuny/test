package com.jiuqi.dna.core.spi.setl;

import com.jiuqi.dna.core.def.obja.DynamicObject;
import com.jiuqi.dna.core.impl.StructDefineImpl;

public class SETLStructDefine extends StructDefineImpl {
	protected SETLStructDefine(String name) {
		super(name, DynamicObject.class);
	}

	@Override
	protected String structTypeNamePrefix() {
		throw new UnsupportedOperationException();
	}
}

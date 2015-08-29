package com.jiuqi.dna.core.impl;

final class ArgumentsDefine extends StructDefineImpl {

	@Override
	protected final String structTypeNamePrefix() {
		return "arguments:";
	}

	ArgumentsDefine(Class<?> soClass) {
		super("args", soClass);
		this.tryLoadJavaFields(true);
	}

}

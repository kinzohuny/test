package com.jiuqi.dna.core.impl;

class RebootStrategyDefineImpl extends NamedDefineImpl implements
		RebootStrategyDefine {
	private final Class<?> clazz;

	RebootStrategyDefineImpl(String name, Class<?> clazz) {
		super(name);
		this.clazz = clazz;
	}

	public Class<?> getStrategyClass() {
		return this.clazz;
	}

	@Override
	public String getXMLTagName() {
		return this.name;
	}
}

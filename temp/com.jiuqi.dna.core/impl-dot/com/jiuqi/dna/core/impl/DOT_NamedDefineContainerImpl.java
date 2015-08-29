package com.jiuqi.dna.core.impl;

@SuppressWarnings("unchecked")
final class DOT_NamedDefineContainerImpl extends
		DOT_ListBase<NamedDefineContainerImpl> {

	@Override
	protected final NamedDefineContainerImpl newList(int cap) {
		return new NamedDefineContainerImpl();
	}
}

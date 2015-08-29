package com.jiuqi.dna.core.impl;

public interface CacheInitializeCollector {

	public boolean haveData();

	public void addCreateGroupData(final CacheGroup<?, ?, ?> group);

	public <TFacade, TImpl extends TFacade, TKeysHolder> void addCreateHolderData(
			final CacheHolder<TFacade, TImpl, TKeysHolder> item,
			final TImpl value, final TKeysHolder keysHolder);

	public void addCreateTreeNodeData(CacheHolder<?, ?, ?> parent,
			CacheHolder<?, ?, ?> child);

	public void addCreateReferenceData(CacheHolder<?, ?, ?> holder,
			CacheHolder<?, ?, ?> reference);
}
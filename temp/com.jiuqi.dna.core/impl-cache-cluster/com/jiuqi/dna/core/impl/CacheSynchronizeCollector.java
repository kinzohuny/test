package com.jiuqi.dna.core.impl;

public interface CacheSynchronizeCollector extends CacheInitializeCollector {

	public void addModifyGroupData(final CacheGroup<?, ?, ?> group);

	public void addRemoveGroupData(final CacheGroup<?, ?, ?> group);

	public <TFacade, TImpl extends TFacade, TKeysHolder> void addModifyHolderData(
			final CacheHolder<TFacade, TImpl, TKeysHolder> item,
			final TImpl newValue, final TKeysHolder newKeysHolder);

	public void addRemoveHolderData(final CacheHolder<?, ?, ?> item);

	public void addRemoveTreeNodeData(final CacheHolder<?, ?, ?> item);

	public void addRemoveReferenceData(CacheHolder<?, ?, ?> holder,
			CacheHolder<?, ?, ?> reference);

	public void addReloadAuhtorityData(final CacheHolder<?, ?, ?> item);
}
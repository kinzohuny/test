package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.def.obja.StructClass;

@StructClass
class CacheClusterSynchronizeTask extends CacheClusterInitializeTask implements
		CacheSynchronizeCollector {

	CacheClusterSynchronizeTask(final boolean isInitializeSynchronizeTask) {
		this.isInitializeSynchronizeTask = isInitializeSynchronizeTask;
		this.haveData = false;
	}

	public void addModifyGroupData(final CacheGroup<?, ?, ?> group) {
		final ModifyGroupData modifyGroupData = new ModifyGroupData(group);
		if (this.modifyGroupDatas == null) {
			this.modifyGroupDatas = new ModifyGroupData[] { modifyGroupData };
		} else {
			final int oldCount = this.modifyGroupDatas.length;
			final ModifyGroupData[] newModifyGroupDatas = new ModifyGroupData[oldCount + 1];
			System.arraycopy(this.modifyGroupDatas, 0, newModifyGroupDatas, 0, oldCount);
			newModifyGroupDatas[oldCount] = modifyGroupData;
			this.modifyGroupDatas = newModifyGroupDatas;
		}
		super.haveData = true;
	}

	public void addRemoveGroupData(final CacheGroup<?, ?, ?> group) {
		final RemoveGroupData removeGroupData = new RemoveGroupData(group);
		if (this.removeGroupDatas == null) {
			this.removeGroupDatas = new RemoveGroupData[] { removeGroupData };
		} else {
			final int oldCount = this.removeGroupDatas.length;
			final RemoveGroupData[] newRemoveGroupDatas = new RemoveGroupData[oldCount + 1];
			System.arraycopy(this.removeGroupDatas, 0, newRemoveGroupDatas, 0, oldCount);
			newRemoveGroupDatas[oldCount] = removeGroupData;
			this.removeGroupDatas = newRemoveGroupDatas;
		}
		super.haveData = true;
	}

	public <TFacade, TImpl extends TFacade, TKeysHolder> void addModifyHolderData(
			final CacheHolder<TFacade, TImpl, TKeysHolder> item,
			final TImpl newValue, final TKeysHolder newKeysHolder) {
		if (this.modifyHolderDataList == null) {
			this.modifyHolderDataList = new ArrayList<ModifyHolderData<?, ?, ?>>();
		}
		this.modifyHolderDataList.add(new ModifyHolderData<TFacade, TImpl, TKeysHolder>(item, newValue, newKeysHolder));
		super.haveData = true;
	}

	public void addRemoveHolderData(final CacheHolder<?, ?, ?> item) {
		if (this.removeHolderDataList == null) {
			this.removeHolderDataList = new ArrayList<RemoveHolderData>();
		}
		this.removeHolderDataList.add(new RemoveHolderData(item));
		super.haveData = true;
	}

	public void addRemoveTreeNodeData(final CacheHolder<?, ?, ?> item) {
		if (this.removeTreeNodeDataList == null) {
			this.removeTreeNodeDataList = new ArrayList<RemoveTreeNodeData>();
		}
		this.removeTreeNodeDataList.add(new RemoveTreeNodeData(item.longIdentifier));
		super.haveData = true;
	}

	public void addRemoveReferenceData(CacheHolder<?, ?, ?> holderItem,
			CacheHolder<?, ?, ?> referenceItem) {
		if (this.removeReferenceDataList == null) {
			this.removeReferenceDataList = new ArrayList<RemoveReferenceData>();
		}
		this.removeReferenceDataList.add(new RemoveReferenceData(holderItem.longIdentifier, referenceItem.longIdentifier));
		super.haveData = true;
	}

	public void addReloadAuhtorityData(final CacheHolder<?, ?, ?> item) {
		if (this.reloadAuthorityDataList == null) {
			this.reloadAuthorityDataList = new ArrayList<ReloadAuthorityData>();
		}
		this.reloadAuthorityDataList.add(new ReloadAuthorityData(item.longIdentifier));
		super.haveData = true;
	}

	final ModifyGroupData[] getModifyGroupDatas() {
		return this.modifyGroupDatas;
	}

	final RemoveGroupData[] getRemoveGroupDatas() {
		return this.removeGroupDatas;
	}

	final ArrayList<ModifyHolderData<?, ?, ?>> getModifyHolderDataList() {
		return this.modifyHolderDataList;
	}

	final ArrayList<RemoveHolderData> getRemoveHolderDataList() {
		return this.removeHolderDataList;
	}

	final ArrayList<RemoveTreeNodeData> getRemoveTreeNodeDataList() {
		return this.removeTreeNodeDataList;
	}

	final ArrayList<RemoveReferenceData> getRemoveReferenceDataList() {
		return this.removeReferenceDataList;
	}

	final ArrayList<ReloadAuthorityData> getReloadAuthorityDataList() {
		return this.reloadAuthorityDataList;
	}

	final boolean isInitializeSynchronizeTask;

	private ModifyGroupData[] modifyGroupDatas;

	private RemoveGroupData[] removeGroupDatas;

	private ArrayList<ModifyHolderData<?, ?, ?>> modifyHolderDataList;

	private ArrayList<RemoveHolderData> removeHolderDataList;

	private ArrayList<RemoveTreeNodeData> removeTreeNodeDataList;

	private ArrayList<RemoveReferenceData> removeReferenceDataList;

	private ArrayList<ReloadAuthorityData> reloadAuthorityDataList;

	@StructClass
	static final class ModifyGroupData {

		private ModifyGroupData(final CacheGroup<?, ?, ?> group) {
			this.longIdentifier = group.longIdentifier;
			this.initializeState = group.forceGetInitialzieState();
			this.catchedInitializeException = group.forceGetInitializeException() != null;
		}

		final long longIdentifier;

		final byte initializeState;

		final boolean catchedInitializeException;

	}

	@StructClass
	static final class RemoveGroupData {
		private RemoveGroupData(final CacheGroup<?, ?, ?> group) {
			this.longIdentifier = group.longIdentifier;
		}

		final long longIdentifier;
	}

	@StructClass
	static final class ModifyHolderData<TFacade, TImpl extends TFacade, TKeysHolder> {
		private ModifyHolderData(
				final CacheHolder<TFacade, TImpl, TKeysHolder> item,
				final TImpl newValue, final TKeysHolder newKeysHolder) {
			this.longIdentifier = item.longIdentifier;
			this.newValue = newValue;
			this.newKeysHolder = newKeysHolder;
			this.userData = item.ownGroup.define.resourceService.internalExtractSerialUserData(newValue, newKeysHolder);
		}

		final long longIdentifier;

		final TImpl newValue;

		final TKeysHolder newKeysHolder;

		final Object userData;

		transient ResourceServiceBase<TFacade, TImpl, TKeysHolder> resourceService;
	}

	@StructClass
	static final class RemoveHolderData {
		private RemoveHolderData(final CacheHolder<?, ?, ?> item) {
			this.longIdentifier = item.longIdentifier;
		}

		final long longIdentifier;
	}

	@StructClass
	static final class RemoveTreeNodeData {
		private RemoveTreeNodeData(final long childNodeIdentifier) {
			this.nodeIdentifier = childNodeIdentifier;
		}

		final long nodeIdentifier;
	}

	@StructClass
	static final class RemoveReferenceData {
		private RemoveReferenceData(final long holderIdentifier,
				final long referenceIdentifier) {
			this.holderIdentifier = holderIdentifier;
			this.referenceIdentifier = referenceIdentifier;
		}

		final long holderIdentifier;

		final long referenceIdentifier;
	}

	@StructClass
	static final class ReloadAuthorityData {

		private ReloadAuthorityData(final long actorIdentifier) {
			this.actorIdentifier = actorIdentifier;
		}

		final long actorIdentifier;
	}
}

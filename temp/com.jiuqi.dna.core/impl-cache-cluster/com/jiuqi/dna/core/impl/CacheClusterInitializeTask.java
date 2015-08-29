package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

@StructClass
public class CacheClusterInitializeTask extends SimpleTask implements
		CacheInitializeCollector {

	CacheClusterInitializeTask() {
	}

	public final boolean haveData() {
		return this.haveData;
	}

	public void addCreateGroupData(final CacheGroup<?, ?, ?> group) {
		final CreateGroupData createGroupData = new CreateGroupData(group);
		if (this.createGroupDatas == null) {
			this.createGroupDatas = new CreateGroupData[] { createGroupData };
		} else {
			final int oldCount = this.createGroupDatas.length;
			final CreateGroupData[] newCreateGroupDatas = new CreateGroupData[oldCount + 1];
			System.arraycopy(this.createGroupDatas, 0, newCreateGroupDatas, 0, oldCount);
			newCreateGroupDatas[oldCount] = createGroupData;
			this.createGroupDatas = newCreateGroupDatas;
		}
		this.haveData = true;
	}

	public <TFacade, TImpl extends TFacade, TKeysHolder> void addCreateHolderData(
			final CacheHolder<TFacade, TImpl, TKeysHolder> item,
			final TImpl value, final TKeysHolder keysHolder) {
		if (this.createHolderDataList == null) {
			this.createHolderDataList = new ArrayList<CreateHolderData<?, ?, ?>>();
		}
		this.createHolderDataList.add(new CreateHolderData<TFacade, TImpl, TKeysHolder>(item, value, keysHolder));
		this.haveData = true;
	}

	public void addCreateTreeNodeData(CacheHolder<?, ?, ?> parentItem,
			CacheHolder<?, ?, ?> childItem) {
		if (this.createTreeNodeDataList == null) {
			this.createTreeNodeDataList = new ArrayList<CreateTreeNodeData>();
		}
		this.createTreeNodeDataList.add(new CreateTreeNodeData(parentItem == null ? null : parentItem.longIdentifier, childItem.longIdentifier));
		this.haveData = true;
	}

	public void addCreateReferenceData(CacheHolder<?, ?, ?> holderItem,
			CacheHolder<?, ?, ?> referenceItem) {
		if (this.createReferenceDataList == null) {
			this.createReferenceDataList = new ArrayList<CreateReferenceData>();
		}
		this.createReferenceDataList.add(new CreateReferenceData(holderItem.longIdentifier, referenceItem.longIdentifier));
		this.haveData = true;
	}

	final CreateGroupData[] getCreateGroupDatas() {
		return this.createGroupDatas;
	}

	final ArrayList<CreateHolderData<?, ?, ?>> getCreateHolderDataList() {
		return this.createHolderDataList;
	}

	final ArrayList<CreateTreeNodeData> getCreateTreeNodeDataList() {
		return this.createTreeNodeDataList;
	}

	final ArrayList<CreateReferenceData> getCreateReferenceDataList() {
		return this.createReferenceDataList;
	}

	boolean haveData;

	int cacheClock;

	private CreateGroupData[] createGroupDatas;

	private ArrayList<CreateHolderData<?, ?, ?>> createHolderDataList;

	private ArrayList<CreateTreeNodeData> createTreeNodeDataList;

	private ArrayList<CreateReferenceData> createReferenceDataList;

	@StructClass
	static final class CreateGroupData {
		private CreateGroupData(final CacheGroup<?, ?, ?> group) {
			this.longIdentifier = group.longIdentifier;
			this.spaceIdentifer = group.ownSpace == null ? null : group.ownSpace.identifier;
			// if (group.define.isAccessControlDefine()) {
			// this.ACGUIDIdentifier =
			// group.accessControlInformation.ACGUIDIdentifier;
			// this.ACLongIdentifier =
			// group.accessControlInformation.ACLongIdentifier;
			// } else {
			// this.ACGUIDIdentifier = null;
			// this.ACLongIdentifier = 0L;
			// }
			this.defineIdentifier = group.define.GUIDIdentifier;
			this.title = group.title;
			this.initializeState = group.forceGetInitialzieState();
			this.catchedInitializeException = group.forceGetInitializeException() != null;
			this.clock = group.clock;
		}

		final long longIdentifier;

		final Object spaceIdentifer;

		// final GUID ACGUIDIdentifier;
		//
		// final long ACLongIdentifier;

		final GUID defineIdentifier;

		final String title;

		final byte initializeState;

		final boolean catchedInitializeException;

		final int clock;
	}

	@StructClass
	static final class CreateHolderData<TFacade, TImpl extends TFacade, TKeysHolder> {
		private CreateHolderData(
				final CacheHolder<TFacade, TImpl, TKeysHolder> item,
				final TImpl value, final TKeysHolder keysHolder) {
			this.longIdentifier = item.longIdentifier;
			this.groupIdentifier = item.ownGroup.longIdentifier;
			this.value = value;
			this.keysHolder = keysHolder;
			// final AccessControlCacheHolder<?, ?, ?> ACItem = item
			// .asAccessControlHolder();
			// if (ACItem == null) {
			// this.ACGUIDIdentifier = null;
			// this.ACLongIdentifier = 0L;
			// } else {
			// this.ACGUIDIdentifier = ACItem.ACGUIDIdentifier;
			// this.ACLongIdentifier = ACItem.ACLongIdentifier;
			// }
			this.userData = item.ownGroup.define.resourceService.internalExtractSerialUserData(value, keysHolder);
			this.clock = item.clock;
		}

		final long longIdentifier;

		final long groupIdentifier;

		final TImpl value;

		final TKeysHolder keysHolder;

		final Object userData;

		// final GUID ACGUIDIdentifier;
		//
		// final long ACLongIdentifier;

		final int clock;

		transient CacheHolder<TFacade, TImpl, TKeysHolder> cacheHolder;

	}

	@StructClass
	static final class CreateTreeNodeData {
		private CreateTreeNodeData(final Long parentNodeIdentifier,
				final long childNodeIdentifier) {
			this.parentNodeIdentifier = parentNodeIdentifier;
			this.childNodeIdentifier = childNodeIdentifier;
		}

		final Long parentNodeIdentifier;

		final long childNodeIdentifier;
	}

	@StructClass
	static final class CreateReferenceData {
		private CreateReferenceData(final long holderIdentifier,
				final long referenceIdentifier) {
			this.holderIdentifier = holderIdentifier;
			this.referenceIdentifier = referenceIdentifier;
		}

		final long holderIdentifier;

		final long referenceIdentifier;
	}

}

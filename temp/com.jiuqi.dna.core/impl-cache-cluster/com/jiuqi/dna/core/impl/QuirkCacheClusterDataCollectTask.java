package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.type.GUID;

@StructClass
class QuirkCacheClusterDataCollectTask extends CacheClusterSynchronizeTask {

	private ArrayList<CreateGroupData2> createGroupDataList2;

	private ArrayList<RemoveGroupData2> removeGroupDataList2;

	private ArrayList<CreateHolderData2<?, ?, ?>> createHolderDataList2;

	private ArrayList<ModifyHolderData2<?, ?, ?>> modifyHolderDataList2;

	private ArrayList<RemoveHolderData2<?, ?>> removeHolderDataList2;

	private ArrayList<ReferenceData2> createReferenceDataList2;

	private ArrayList<ReferenceData2> removeReferenceDataList2;

	private ArrayList<ReferenceData2> createTreeNodeDataList2;

	private ArrayList<ReloadAuthorityData2<?, ?>> reloadAuthorityDataList2;

	QuirkCacheClusterDataCollectTask() {
		super(false);
	}

	public final void addCreateGroupData(final CacheGroup<?, ?, ?> group) {
		super.addCreateGroupData(group);
		if (this.createGroupDataList2 == null) {
			this.createGroupDataList2 = new ArrayList<CreateGroupData2>();
		}
		this.createGroupDataList2.add(new CreateGroupData2(group));
	}

	public final void addModifyGroupData(final CacheGroup<?, ?, ?> group) {
		super.addModifyGroupData(group);
	}

	public final void addRemoveGroupData(final CacheGroup<?, ?, ?> group) {
		super.addRemoveGroupData(group);
		if (this.removeGroupDataList2 == null) {
			this.removeGroupDataList2 = new ArrayList<RemoveGroupData2>();
		}
		this.removeGroupDataList2.add(new RemoveGroupData2(group));
	}

	public final <TFacade, TImpl extends TFacade, TKeysHolder> void addCreateHolderData(
			final CacheHolder<TFacade, TImpl, TKeysHolder> item,
			final TImpl value, final TKeysHolder keysHolder) {
		super.addCreateHolderData(item, value, keysHolder);
		if (this.createHolderDataList2 == null) {
			this.createHolderDataList2 = new ArrayList<CreateHolderData2<?, ?, ?>>();
		}
		this.createHolderDataList2.add(new CreateHolderData2<TFacade, TImpl, TKeysHolder>(item, value, keysHolder));
	}

	public final <TFacade, TImpl extends TFacade, TKeysHolder> void addModifyHolderData(
			final CacheHolder<TFacade, TImpl, TKeysHolder> item,
			final TImpl newValue, final TKeysHolder newKeysHolder) {
		super.addModifyHolderData(item, newValue, newKeysHolder);
		if (this.modifyHolderDataList2 == null) {
			this.modifyHolderDataList2 = new ArrayList<ModifyHolderData2<?, ?, ?>>();
		}
		this.modifyHolderDataList2.add(new ModifyHolderData2<TFacade, TImpl, TKeysHolder>(item, newValue, newKeysHolder));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void addRemoveHolderData(final CacheHolder<?, ?, ?> item) {
		super.addRemoveHolderData(item);
		if (this.removeHolderDataList2 == null) {
			this.removeHolderDataList2 = new ArrayList<RemoveHolderData2<?, ?>>();
		}
		this.removeHolderDataList2.add(new RemoveHolderData2(item));
	}

	public final void addCreateTreeNodeData(CacheHolder<?, ?, ?> parentItem,
			CacheHolder<?, ?, ?> childItem) {
		super.addCreateTreeNodeData(parentItem, childItem);
		if (this.createTreeNodeDataList2 == null) {
			this.createTreeNodeDataList2 = new ArrayList<ReferenceData2>();
		}
		this.createTreeNodeDataList2.add(new ReferenceData2(parentItem, childItem));
	}

	public final void addRemoveTreeNodeData(final CacheHolder<?, ?, ?> item) {
		super.addRemoveTreeNodeData(item);
	}

	public final void addCreateReferenceData(CacheHolder<?, ?, ?> holderItem,
			CacheHolder<?, ?, ?> referenceItem) {
		super.addCreateReferenceData(holderItem, referenceItem);
		if (this.createReferenceDataList2 == null) {
			this.createReferenceDataList2 = new ArrayList<ReferenceData2>();
		}
		this.createReferenceDataList2.add(new ReferenceData2(holderItem, referenceItem));
	}

	public final void addRemoveReferenceData(CacheHolder<?, ?, ?> holderItem,
			CacheHolder<?, ?, ?> referenceItem) {
		super.addRemoveReferenceData(holderItem, referenceItem);
		if (this.removeReferenceDataList2 == null) {
			this.removeReferenceDataList2 = new ArrayList<ReferenceData2>();
		}
		this.removeReferenceDataList2.add(new ReferenceData2(holderItem, referenceItem));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void addReloadAuhtorityData(// final boolean forUser,
			final CacheHolder<?, ?, ?> item) {
		super.addReloadAuhtorityData(item);
		if (this.reloadAuthorityDataList2 == null) {
			this.reloadAuthorityDataList2 = new ArrayList<ReloadAuthorityData2<?, ?>>();
		}
		this.reloadAuthorityDataList2.add(new ReloadAuthorityData2(item));
	}

	final ArrayList<CreateGroupData2> getCreateGroupData2List2() {
		return this.createGroupDataList2;
	}

	final ArrayList<RemoveGroupData2> getRemoveGroupData2List2() {
		return this.removeGroupDataList2;
	}

	final ArrayList<CreateHolderData2<?, ?, ?>> getCreateHolderDataList2() {
		return this.createHolderDataList2;
	}

	final ArrayList<ModifyHolderData2<?, ?, ?>> getModifyHolderData2List2() {
		return this.modifyHolderDataList2;
	}

	final ArrayList<RemoveHolderData2<?, ?>> getRemoveHolderDataList2() {
		return this.removeHolderDataList2;
	}

	final ArrayList<ReferenceData2> getCreateReferenceDataList2() {
		return this.createReferenceDataList2;
	}

	final ArrayList<ReferenceData2> getRemoveReferenceDataList2() {
		return this.removeReferenceDataList2;
	}

	final ArrayList<ReferenceData2> getCreateTreeNodeDataList2() {
		return this.createTreeNodeDataList2;
	}

	final ArrayList<ReloadAuthorityData2<?, ?>> getReloadAuthorityDataList2() {
		return this.reloadAuthorityDataList2;
	}

	@StructClass
	static final class CreateGroupData2 {
		private CreateGroupData2(final CacheGroup<?, ?, ?> group) {
			this.spaceIdentifer = group.ownSpace == null ? null : group.ownSpace.identifier;
			this.defineIdentifier = group.define.GUIDIdentifier;
			this.title = group.title;
			this.initializeState = group.forceGetInitialzieState();
			this.catchedInitializeException = group.forceGetInitializeException() != null;
			this.facadeClass = group.define.facadeClass;
		}

		final Class<?> facadeClass;

		final Object spaceIdentifer;

		final GUID defineIdentifier;

		final String title;

		final byte initializeState;

		final boolean catchedInitializeException;
	}

	@StructClass
	static final class RemoveGroupData2 {
		private RemoveGroupData2(final CacheGroup<?, ?, ?> group) {
			this.spaceIdentifer = group.ownSpace == null ? null : group.ownSpace.identifier;
			this.defineIdentifier = group.define.GUIDIdentifier;
			this.facadeClass = group.define.facadeClass;
		}

		final Class<?> facadeClass;

		final GUID defineIdentifier;

		final Object spaceIdentifer;

	}

	@StructClass
	static final class CreateHolderData2<TFacade, TImpl extends TFacade, TKeysHolder> {
		private CreateHolderData2(
				final CacheHolder<TFacade, TImpl, TKeysHolder> item,
				final TImpl value, final TKeysHolder keysHolder) {
			this.value = value;
			this.keysHolder = keysHolder;
			this.userData = item.ownGroup.define.resourceService.internalExtractSerialUserData(value, keysHolder);
			this.facadeClass = item.getFacadeClass();
			this.groupIdentifier = item.ownGroup.ownSpace.identifier;
		}

		final Class<TFacade> facadeClass;

		final Object groupIdentifier;

		final TImpl value;

		final TKeysHolder keysHolder;

		final Object userData;
	}

	@StructClass
	static final class ModifyHolderData2<TFacade, TImpl extends TFacade, TKeysHolder> {
		private ModifyHolderData2(
				final CacheHolder<TFacade, TImpl, TKeysHolder> item,
				final TImpl value, final TKeysHolder keysHolder) {
			this.value = value;
			this.keysHolder = keysHolder;
			this.userData = item.ownGroup.define.resourceService.internalExtractSerialUserData(value, keysHolder);
			this.facadeClass = item.getFacadeClass();
			this.groupIdentifier = item.ownGroup.ownSpace.identifier;
		}

		final Class<TFacade> facadeClass;

		final Object groupIdentifier;

		final TImpl value;

		final TKeysHolder keysHolder;

		final Object userData;
	}

	@StructClass
	static final class RemoveHolderData2<TFacade, TKeysHolder> {

		private RemoveHolderData2(
				final CacheHolder<TFacade, ?, TKeysHolder> item) {
			this.keysHolder = item.forceGetKeysHolder();
			this.facadeClass = item.getFacadeClass();
			this.groupIdentifier = item.ownGroup.ownSpace.identifier;
		}

		final TKeysHolder keysHolder;

		final Class<TFacade> facadeClass;

		final Object groupIdentifier;
	}

	@StructClass
	static final class ReferenceData2 {
		private ReferenceData2(final CacheHolder<?, ?, ?> item1,
				final CacheHolder<?, ?, ?> item2) {
			this.keysHolder1 = item1 == null ? null : item1.forceGetKeysHolder();
			this.facadeClass1 = item1 == null ? null : item1.getFacadeClass();
			this.groupIdentifier1 = item1 == null ? null : item1.ownGroup.ownSpace.identifier;
			this.keysHolder2 = item2 == null ? null : item2.forceGetKeysHolder();
			this.facadeClass2 = item2 == null ? null : item2.getFacadeClass();
			this.groupIdentifier2 = item2 == null ? null : item2.ownGroup.ownSpace.identifier;
		}

		final Object keysHolder1;
		final Class<?> facadeClass1;
		final Object groupIdentifier1;

		final Object keysHolder2;
		final Class<?> facadeClass2;
		final Object groupIdentifier2;

	}

	@StructClass
	static final class ReloadAuthorityData2<TFacade, TKeysHolder> {

		private ReloadAuthorityData2(
				final CacheHolder<TFacade, ?, TKeysHolder> item) {
			this.keysHolder = item.forceGetKeysHolder();
			this.facadeClass = item.getFacadeClass();
			this.groupIdentifier = item.ownGroup.ownSpace.identifier;
		}

		final TKeysHolder keysHolder;

		final Class<TFacade> facadeClass;

		final Object groupIdentifier;

	}

}

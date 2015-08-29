package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.def.obja.StructField;
import com.jiuqi.dna.core.impl.CacheDefine.KeyDefine;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

@StructClass
public final class CacheModifyingEvent extends SimpleTask implements
		CacheSynchronizeCollector {

	boolean haveData;
	CreateGroupData[] createGroupDatas;
	RemoveGroupData[] removeGroupDatas;
	ArrayList<CreateHolderData> createHolderDataList;
	ArrayList<ModifyHolderData> modifyHolderDataList;
	ArrayList<RemoveHolderData> removeHolderDataList;
	ArrayList<CreateTreeNodeData> createTreeNodeDataList;
	ArrayList<CreateReferenceData> createReferenceDataList;
	ArrayList<RemoveReferenceData> removeReferenceDataList;
	ArrayList<ReloadAuthorityData> reloadAuthorityDataList;

	public boolean haveData() {
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

	public void addCreateGroupData(CacheModifyingEvent src) {
		this.createGroupDatas = src.createGroupDatas;
		if (this.createGroupDatas != null && this.createGroupDatas.length > 0) {
			this.haveData = true;
		}
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
		this.haveData = true;
	}

	public void addRemoveGroupData(CacheModifyingEvent src) {
		this.removeGroupDatas = src.removeGroupDatas;
		if (this.removeGroupDatas != null && this.removeGroupDatas.length > 0) {
			this.haveData = true;
		}
	}

	public <TFacade, TImpl extends TFacade, TKeysHolder> void addCreateHolderData(
			final CacheHolder<TFacade, TImpl, TKeysHolder> item,
			final TImpl value, final TKeysHolder keysHolder) {
		if (this.createHolderDataList == null) {
			this.createHolderDataList = new ArrayList<CreateHolderData>();
		}
		this.createHolderDataList.add(new CreateHolderData(item, value, keysHolder));
		this.haveData = true;
	}

	public <TFacade, TImpl extends TFacade, TKeysHolder> void addModifyHolderData(
			final CacheHolder<TFacade, TImpl, TKeysHolder> item,
			final TImpl newValue, final TKeysHolder newKeysHolder) {
		if (this.modifyHolderDataList == null) {
			this.modifyHolderDataList = new ArrayList<ModifyHolderData>();
		}
		this.modifyHolderDataList.add(new ModifyHolderData(item, newValue, newKeysHolder));
		this.haveData = true;
	}

	public void addRemoveHolderData(CacheHolder<?, ?, ?> item) {
		if (this.removeHolderDataList == null) {
			this.removeHolderDataList = new ArrayList<RemoveHolderData>();
		}
		this.removeHolderDataList.add(new RemoveHolderData(item));
		this.haveData = true;
	}

	public void addCreateReferenceData(CacheHolder<?, ?, ?> holderItem,
			CacheHolder<?, ?, ?> referenceItem) {
		if (this.createReferenceDataList == null) {
			this.createReferenceDataList = new ArrayList<CreateReferenceData>();
		}
		this.createReferenceDataList.add(new CreateReferenceData(holderItem, referenceItem));
		this.haveData = true;
	}

	public void addRemoveReferenceData(CacheHolder<?, ?, ?> holder,
			CacheHolder<?, ?, ?> reference) {
		if (this.removeReferenceDataList == null) {
			this.removeReferenceDataList = new ArrayList<RemoveReferenceData>();
		}
		this.removeReferenceDataList.add(new RemoveReferenceData(holder, reference));
		this.haveData = true;
	}

	public void addCreateTreeNodeData(CacheHolder<?, ?, ?> parent,
			CacheHolder<?, ?, ?> child) {
		if (this.createTreeNodeDataList == null) {
			this.createTreeNodeDataList = new ArrayList<CreateTreeNodeData>();
		}
		this.createTreeNodeDataList.add(new CreateTreeNodeData(parent, child));
		this.haveData = true;
	}

	@StructClass
	static final class CreateGroupData {

		private CreateGroupData(final CacheGroup<?, ?, ?> group) {
			this.spaceIdentifer = group.ownSpace == null ? null : group.ownSpace.identifier;
			this.defineIdentifier = group.define.GUIDIdentifier;
			this.title = group.title;
		}

		final Object spaceIdentifer;
		final GUID defineIdentifier;
		final String title;
	}

	@StructClass
	static final class RemoveGroupData {

		private RemoveGroupData(final CacheGroup<?, ?, ?> group) {
			this.spaceIdentifer = group.ownSpace == null ? null : group.ownSpace.identifier;
			this.defineIdentifier = group.define.GUIDIdentifier;
		}

		final Object spaceIdentifer;
		final GUID defineIdentifier;
	}

	@StructClass
	static final class CreateHolderData {

		private CreateHolderData(final CacheHolder item, final Object value,
				final Object keysHolder) {
			this.spaceIdentifier = item.ownGroup.ownSpace.identifier;
			this.define = item.ownGroup.define;
			this.defineIdentifier = item.ownGroup.define.GUIDIdentifier;
			this.value = OBJAContext.clone(item.tryGetValue(), false, item.ownGroup.define.implementStruct);
			this.keysHolder = (value == keysHolder ? this.value : OBJAContext.clone(keysHolder));
			this.userData = item.ownGroup.define.resourceService.internalExtractSerialUserData(value, keysHolder);
		}

		final Object spaceIdentifier;
		@StructField(stateField = true)
		final CacheDefine<?, ?, ?> define;
		final GUID defineIdentifier;
		final Object value;
		final Object keysHolder;
		final Object userData;
		transient CacheHolder<?, ?, ?> cacheHolder;
	}

	@StructClass
	static final class ModifyHolderData {

		private ModifyHolderData(final CacheHolder item, final Object newValue,
				final Object newKeysHolder) {
			this.spaceIdentifier = item.ownGroup.ownSpace.identifier;
			this.define = item.ownGroup.define;
			this.defineIdentifier = item.ownGroup.define.GUIDIdentifier;
			this.value = OBJAContext.clone(item.tryGetValue(), false, item.ownGroup.define.implementStruct);
			KeyDefine keyDefine = item.ownGroup.define.keyDefines[0];
			this.key1Value = keyDefine.getKeyValue1(item.forceGetKeysHolder());
			this.key2Value = keyDefine.getKeyValue2(item.forceGetKeysHolder());
			this.key3Value = keyDefine.getKeyValue3(item.forceGetKeysHolder());
			this.newValue = OBJAContext.clone(newValue, false, item.ownGroup.define.implementStruct);
			this.newKeysHolder = newValue == newKeysHolder ? this.newValue : OBJAContext.clone(newKeysHolder);
			this.userData = item.ownGroup.define.resourceService.internalExtractSerialUserData(newValue, newKeysHolder);
		}

		final Object spaceIdentifier;
		final CacheDefine<?, ?, ?> define;
		final GUID defineIdentifier;
		@StructField(stateField = true)
		final Object value;
		final Object key1Value;
		final Object key2Value;
		final Object key3Value;
		final Object newValue;
		final Object newKeysHolder;
		final Object userData;
		transient CacheHolder<?, ?, ?> cacheHolder;
	}

	@StructClass
	static final class RemoveHolderData {

		private <TFacade, TImpl extends TFacade, TKeysHolder> RemoveHolderData(
				final CacheHolder<TFacade, TImpl, TKeysHolder> item) {
			this.spaceIdentifier = item.ownGroup.ownSpace.identifier;
			this.define = item.ownGroup.define;
			this.defineIdentifier = item.ownGroup.define.GUIDIdentifier;
			this.value = OBJAContext.clone(item.unsafeGetValue(), false, item.ownGroup.define.implementStruct);
			KeyDefine<TFacade, TImpl, TKeysHolder> keyDefine = item.ownGroup.define.keyDefines[0];
			this.key1Value = keyDefine.getKeyValue1(item.forceGetKeysHolder());
			this.key2Value = keyDefine.getKeyValue2(item.forceGetKeysHolder());
			this.key3Value = keyDefine.getKeyValue3(item.forceGetKeysHolder());
		}

		final Object spaceIdentifier;
		@StructField(stateField = true)
		final CacheDefine<?, ?, ?> define;
		final GUID defineIdentifier;
		@StructField(stateField = true)
		final Object value;
		final Object key1Value;
		final Object key2Value;
		final Object key3Value;
	}

	@StructClass
	static final class CreateTreeNodeData {

		private CreateTreeNodeData(final CacheHolder parent,
				final CacheHolder child) {
			this.define = child.ownGroup.define;
			this.defineIdentifier = this.define.GUIDIdentifier;
			this.spaceIdentifier = child.ownGroup.ownSpace.identifier;
			KeyDefine holderKeyDefine = child.ownGroup.define.keyDefines[0];
			if (parent == null) {
				this.parentValue = null;
				this.parentNull = true;
				this.parentKey1Value = null;
				this.parentKey2Value = null;
				this.parentKey3Value = null;
			} else {
				this.parentValue = OBJAContext.clone(parent.tryGetValue(), false, parent.ownGroup.define.implementStruct);
				this.parentNull = false;
				this.parentKey1Value = holderKeyDefine.getKeyValue1(parent.forceGetKeysHolder());
				this.parentKey2Value = holderKeyDefine.getKeyValue2(parent.forceGetKeysHolder());
				this.parentKey3Value = holderKeyDefine.getKeyValue3(parent.forceGetKeysHolder());
			}
			this.childValue = OBJAContext.clone(child.tryGetValue(), false, child.ownGroup.define.implementStruct);
			this.childKey1Value = holderKeyDefine.getKeyValue1(child.forceGetKeysHolder());
			this.childKey2Value = holderKeyDefine.getKeyValue2(child.forceGetKeysHolder());
			this.childKey3Value = holderKeyDefine.getKeyValue3(child.forceGetKeysHolder());
		}

		@StructField(stateField = true)
		final CacheDefine<?, ?, ?> define;
		final Object spaceIdentifier;
		final GUID defineIdentifier;
		@StructField(stateField = true)
		final Object parentValue;
		final boolean parentNull;
		final Object parentKey1Value;
		final Object parentKey2Value;
		final Object parentKey3Value;
		@StructField(stateField = true)
		final Object childValue;
		final Object childKey1Value;
		final Object childKey2Value;
		final Object childKey3Value;
	}

	@StructClass
	static final class CreateReferenceData {

		private CreateReferenceData(final CacheHolder holder,
				final CacheHolder reference) {
			this.holderSpaceIdentifier = holder.ownGroup.ownSpace.identifier;
			this.holderDefine = holder.ownGroup.define;
			this.holderDefineIdentifier = this.holderDefine.GUIDIdentifier;
			this.holderValue = OBJAContext.clone(holder.tryGetValue(), false, this.holderDefine.implementStruct);
			KeyDefine holderKeyDefine = holder.ownGroup.define.keyDefines[0];
			this.holderKey1Value = holderKeyDefine.getKeyValue1(holder.forceGetKeysHolder());
			this.holderKey2Value = holderKeyDefine.getKeyValue2(holder.forceGetKeysHolder());
			this.holderKey3Value = holderKeyDefine.getKeyValue3(holder.forceGetKeysHolder());
			this.referenceSpaceIdentifier = reference.ownGroup.ownSpace.identifier;
			this.referenceDefine = reference.ownGroup.define;
			this.referenceDefineIdentifier = this.referenceDefine.GUIDIdentifier;
			this.referenceValue = OBJAContext.clone(reference.tryGetValue(), false, this.referenceDefine.implementStruct);
			KeyDefine referentKeyDefine = reference.ownGroup.define.keyDefines[0];
			this.referenceKey1Value = referentKeyDefine.getKeyValue1(reference.forceGetKeysHolder());
			this.referenceKey2Value = referentKeyDefine.getKeyValue2(reference.forceGetKeysHolder());
			this.referenceKey3Value = referentKeyDefine.getKeyValue3(reference.forceGetKeysHolder());
		}

		final Object holderSpaceIdentifier;
		@StructField(stateField = true)
		final CacheDefine<?, ?, ?> holderDefine;
		final GUID holderDefineIdentifier;
		@StructField(stateField = true)
		final Object holderValue;
		final Object holderKey1Value;
		final Object holderKey2Value;
		final Object holderKey3Value;

		final Object referenceSpaceIdentifier;
		@StructField(stateField = true)
		final CacheDefine<?, ?, ?> referenceDefine;
		final GUID referenceDefineIdentifier;
		@StructField(stateField = true)
		final Object referenceValue;
		final Object referenceKey1Value;
		final Object referenceKey2Value;
		final Object referenceKey3Value;
	}

	@StructClass
	static final class RemoveReferenceData {

		private RemoveReferenceData(final CacheHolder holder,
				final CacheHolder reference) {
			this.holderSpaceIdentifier = holder.ownGroup.ownSpace.identifier;
			this.holderDefine = holder.ownGroup.define;
			this.holderDefineIdentifier = this.holderDefine.GUIDIdentifier;
			this.holderValue = OBJAContext.clone(holder.tryGetValue(), false, this.holderDefine.implementStruct);
			KeyDefine holderKeyDefine = holder.ownGroup.define.keyDefines[0];
			this.holderKey1Value = holderKeyDefine.getKeyValue1(holder.forceGetKeysHolder());
			this.holderKey2Value = holderKeyDefine.getKeyValue2(holder.forceGetKeysHolder());
			this.holderKey3Value = holderKeyDefine.getKeyValue3(holder.forceGetKeysHolder());
			this.referenceSpaceIdentifier = reference.ownGroup.ownSpace.identifier;
			this.referenceDefine = reference.ownGroup.define;
			this.referenceDefineIdentifier = this.referenceDefine.GUIDIdentifier;
			this.referenceValue = OBJAContext.clone(reference.tryGetValue(), false, this.referenceDefine.implementStruct);
			KeyDefine referentKeyDefine = reference.ownGroup.define.keyDefines[0];
			this.referenceKey1Value = referentKeyDefine.getKeyValue1(reference.forceGetKeysHolder());
			this.referenceKey2Value = referentKeyDefine.getKeyValue2(reference.forceGetKeysHolder());
			this.referenceKey3Value = referentKeyDefine.getKeyValue3(reference.forceGetKeysHolder());
		}

		final Object holderSpaceIdentifier;
		@StructField(stateField = true)
		final CacheDefine<?, ?, ?> holderDefine;
		final GUID holderDefineIdentifier;
		@StructField(stateField = true)
		final Object holderValue;
		final Object holderKey1Value;
		final Object holderKey2Value;
		final Object holderKey3Value;

		final Object referenceSpaceIdentifier;
		@StructField(stateField = true)
		final CacheDefine<?, ?, ?> referenceDefine;
		final GUID referenceDefineIdentifier;
		@StructField(stateField = true)
		final Object referenceValue;
		final Object referenceKey1Value;
		final Object referenceKey2Value;
		final Object referenceKey3Value;
	}

	@StructClass
	static final class ReloadAuthorityData {

		// 0,user; 1,role; 2,identify
		final int type;
		final Object spaceIdentifier;
		final GUID identifier;
		final GUID ACVersion;

		ReloadAuthorityData(CacheHolder<?, ?, ?> holder) {
			this.spaceIdentifier = holder.ownGroup.ownSpace.identifier;
			if (holder instanceof UserCacheHolder) {
				this.type = 0;
				UserCacheHolder u = (UserCacheHolder) holder;
				this.identifier = u.identifier;
				this.ACVersion = null;
			} else if (holder instanceof RoleCacheHolder) {
				this.type = 1;
				RoleCacheHolder r = (RoleCacheHolder) holder;
				this.identifier = r.identifier;
				this.ACVersion = null;
			} else {
				this.type = 2;
				IdentifyCacheHolder i = (IdentifyCacheHolder) holder;
				this.identifier = i.identifier;
				this.ACVersion = i.ACVersion;
			}
		}
		
		ReloadAuthorityData(ReloadAuthorityData s) {
			this.spaceIdentifier = s.spaceIdentifier;
			this.type = s.type;
			this.identifier = s.identifier;
			this.ACVersion = s.ACVersion;
		}
	}

	public void addModifyGroupData(CacheGroup<?, ?, ?> group) {
		// HOU Auto-generated method stub

	}

	public void addRemoveTreeNodeData(CacheHolder<?, ?, ?> item) {
		// HOU Auto-generated method stub

	}

	public void addReloadAuhtorityData(CacheHolder<?, ?, ?> item) {
		if (this.reloadAuthorityDataList == null) {
			this.reloadAuthorityDataList = new ArrayList<ReloadAuthorityData>();
		}
		this.reloadAuthorityDataList.add(new ReloadAuthorityData(item));
		this.haveData = true;
	}
}
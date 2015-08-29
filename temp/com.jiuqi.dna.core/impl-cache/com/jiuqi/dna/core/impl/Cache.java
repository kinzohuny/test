package com.jiuqi.dna.core.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.jiuqi.dna.core.impl.CacheClusterInitializeTask.CreateGroupData;
import com.jiuqi.dna.core.impl.CacheClusterInitializeTask.CreateHolderData;
import com.jiuqi.dna.core.impl.CacheClusterInitializeTask.CreateReferenceData;
import com.jiuqi.dna.core.impl.CacheClusterInitializeTask.CreateTreeNodeData;
import com.jiuqi.dna.core.impl.CacheClusterSynchronizeTask.ModifyGroupData;
import com.jiuqi.dna.core.impl.CacheClusterSynchronizeTask.ModifyHolderData;
import com.jiuqi.dna.core.impl.CacheClusterSynchronizeTask.ReloadAuthorityData;
import com.jiuqi.dna.core.impl.CacheClusterSynchronizeTask.RemoveGroupData;
import com.jiuqi.dna.core.impl.CacheClusterSynchronizeTask.RemoveHolderData;
import com.jiuqi.dna.core.impl.CacheClusterSynchronizeTask.RemoveReferenceData;
import com.jiuqi.dna.core.misc.HashUtil;
import com.jiuqi.dna.core.type.GUID;

@SuppressWarnings({ "unchecked", "rawtypes" })
final class Cache extends Acquirable {

	static final boolean IN_DEBUG_MODE = Boolean.getBoolean("com.jiuqi.dna.debug.cache");

	static final boolean INITIALIZEGROUP_INSAMETRANS = Boolean.getBoolean("com.jiuqi.dna.cache.initgroup.insametrans");

	private static final int ID_LEN = 64;

	private static final int TYPE_LEN = 3;

	private static final long TYPE_CACHE_GROUP = 0x01L << (ID_LEN - NetClusterImpl.NODE_INDEX_LEN - TYPE_LEN);

	private static final long TYPE_CACHE_HOLDER = 0x02L << (ID_LEN - NetClusterImpl.NODE_INDEX_LEN - TYPE_LEN);

	private static final int DEFAULT_DEFINE_MAP_CAPACITY = 4;

	private static final int DEFAULT_GROUP_MAP_CAPACITY = 4;

	static final void printInformation(final String information) {
		System.out.println("缓存信息： " + information);
	}

	static final void printWarningMessage(final String warningMessage) {
		System.err.println("缓存警告： " + warningMessage);
	}

	static final void printErrorMessage(final String errorMessage) {
		System.err.println("缓存错误： " + errorMessage);
	}

	/**
	 * 缓存定义容器
	 * 
	 * <p>
	 * 由于定义在站点启动完成之前就能全部确定，不存在多线程修改定义，所以不需要进行并发控制。
	 */
	static final class DefineContainer {

		private DefineContainer() {
		}

		final CacheDefine<?, ?, ?> findDefine(final GUID GUIDIdentifier) {
			CacheDefine<?, ?, ?> existDefine = this.defineMapByGUIDIdentifier[HashUtil.hash(GUIDIdentifier) & this.hashMask];
			while (existDefine != null) {
				if (existDefine.GUIDIdentifier.equals(GUIDIdentifier)) {
					return existDefine;
				} else {
					existDefine = existDefine.nextInMapByGUIDIdentifier;
				}
			}
			return null;
		}

		private final <TFacade> CacheDefine<TFacade, ?, ?> findDefine(
				final Class<TFacade> facadeClass) {
			CacheDefine<?, ?, ?> existDefine = this.defineMapByFacadeClass[HashUtil.hash(facadeClass) & this.hashMask];
			while (existDefine != null) {
				if (existDefine.facadeClass == facadeClass) {
					return (CacheDefine<TFacade, ?, ?>) existDefine;
				} else {
					existDefine = existDefine.nextInMapByFacadeClass;
				}
			}
			return null;
		}

		final void registDefine(final CacheDefine<?, ?, ?> define) {
			if (this.defineCount == this.hashMask) {
				final int oldCapacity = this.defineMapByFacadeClass.length;
				final int newCapacity = oldCapacity * 2;
				final int newHashMask = newCapacity - 1;
				final CacheDefine<?, ?, ?>[] newDefineMapByGUIDIdentifier = new CacheDefine<?, ?, ?>[newCapacity];
				for (CacheDefine<?, ?, ?> existDefine : this.defineMapByGUIDIdentifier) {
					while (existDefine != null) {
						final int newHashIndex = HashUtil.hash(existDefine.GUIDIdentifier) & newHashMask;
						final CacheDefine<?, ?, ?> tempDefine = existDefine;
						existDefine = existDefine.nextInMapByGUIDIdentifier;
						tempDefine.nextInMapByGUIDIdentifier = newDefineMapByGUIDIdentifier[newHashIndex];
						newDefineMapByGUIDIdentifier[newHashIndex] = tempDefine;
					}
				}
				final CacheDefine<?, ?, ?>[] newDefineMapByFacadeClass = new CacheDefine<?, ?, ?>[newCapacity];
				for (CacheDefine<?, ?, ?> existDefine : this.defineMapByFacadeClass) {
					while (existDefine != null) {
						final int newHashIndex = HashUtil.hash(existDefine.facadeClass) & newHashMask;
						final CacheDefine<?, ?, ?> tempDefine = existDefine;
						existDefine = existDefine.nextInMapByFacadeClass;
						tempDefine.nextInMapByFacadeClass = newDefineMapByFacadeClass[newHashIndex];
						newDefineMapByFacadeClass[newHashIndex] = tempDefine;
					}
				}
				this.defineMapByGUIDIdentifier = newDefineMapByGUIDIdentifier;
				this.defineMapByFacadeClass = newDefineMapByFacadeClass;
				this.hashMask = newHashMask;
			}
			int hashIndex = HashUtil.hash(define.GUIDIdentifier) & this.hashMask;
			define.nextInMapByGUIDIdentifier = this.defineMapByGUIDIdentifier[hashIndex];
			this.defineMapByGUIDIdentifier[hashIndex] = define;
			hashIndex = HashUtil.hash(define.facadeClass) & this.hashMask;
			define.nextInMapByFacadeClass = this.defineMapByFacadeClass[hashIndex];
			this.defineMapByFacadeClass[hashIndex] = define;
			this.defineCount++;
		}

		private final void reset() {
			this.defineMapByGUIDIdentifier = new CacheDefine<?, ?, ?>[DEFAULT_DEFINE_MAP_CAPACITY];
			this.defineMapByFacadeClass = new CacheDefine<?, ?, ?>[DEFAULT_DEFINE_MAP_CAPACITY];
			this.defineCount = 0;
			this.hashMask = DEFAULT_DEFINE_MAP_CAPACITY - 1;
		}

		private final ArrayList<CacheDefine<?, ?, ?>> getDefineList() {
			final ArrayList<CacheDefine<?, ?, ?>> defineList = new ArrayList<CacheDefine<?, ?, ?>>();
			for (CacheDefine<?, ?, ?> existDefine : this.defineMapByFacadeClass) {
				while (existDefine != null) {
					defineList.add(existDefine);
					existDefine = existDefine.nextInMapByFacadeClass;
				}
			}
			return defineList;
		}

		private CacheDefine<?, ?, ?>[] defineMapByGUIDIdentifier;

		private CacheDefine<?, ?, ?>[] defineMapByFacadeClass;

		private int defineCount;

		private int hashMask;

	}

	static final class ClusterGroupContainer {

		private ClusterGroupContainer(final IDGenerator identifierGenerator) {
			final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
			this.readLock = lock.readLock();
			this.writeLock = lock.writeLock();
			this.identifierGenerator = identifierGenerator;
			this.groupCount = 0;
			this.groupMap = new CacheGroup<?, ?, ?>[128];
			this.hashMask = 128 - 1;
		}

		final CacheGroup<?, ?, ?> findGroup(final long longIdentifier) {
			this.readLock.lock();
			try {
				if (this.groupCount == 0) {
					return null;
				} else {
					final int hashIndex = (int) longIdentifier & this.hashMask;
					CacheGroup<?, ?, ?> existItem = this.groupMap[hashIndex];
					while (existItem != null) {
						if (existItem.longIdentifier == longIdentifier) {
							return existItem;
						} else {
							existItem = existItem.nextInContainer;
						}
					}
				}
				return null;
			} finally {
				this.readLock.unlock();
			}
		}

		final long onCreatedGroup(final CacheGroup<?, ?, ?> group,
				final Long fixLongIdentifier) {
			if (!group.inCluster) {
				return 0L;
			}
			this.writeLock.lock();
			try {
				if (this.groupCount == this.hashMask) {
					final int oldCapacity = this.groupMap.length;
					final int newCapacity = oldCapacity * 2;
					final int newHashMask = newCapacity - 1;
					final CacheGroup<?, ?, ?>[] newGroupMap = new CacheGroup<?, ?, ?>[newCapacity];
					for (CacheGroup<?, ?, ?> existGroup : this.groupMap) {
						while (existGroup != null) {
							final int newHashIndex = (int) existGroup.longIdentifier & newHashMask;
							final CacheGroup<?, ?, ?> tempGroup = existGroup;
							existGroup = existGroup.nextInContainer;
							tempGroup.nextInContainer = newGroupMap[newHashIndex];
							newGroupMap[newHashIndex] = tempGroup;
						}
					}
					this.hashMask = newHashMask;
					this.groupMap = newGroupMap;
				}
				long identifier;
				if (fixLongIdentifier == null) {
					loop: for (identifier = this.identifierGenerator.next();; identifier = this.identifierGenerator.next()) {
						int hashIndex = (int) identifier & this.hashMask;
						CacheGroup<?, ?, ?> existGroup = this.groupMap[hashIndex];
						while (existGroup != null) {
							if (existGroup.longIdentifier == identifier) {
								continue loop;
							} else {
								existGroup = existGroup.nextInContainer;
							}
						}
						break;
					}
				} else {
					identifier = fixLongIdentifier;
				}
				final int hashIndex = (int) (identifier) & this.hashMask;
				group.nextInContainer = this.groupMap[hashIndex];
				this.groupMap[hashIndex] = group;
				this.groupCount++;
				return identifier;
			} finally {
				this.writeLock.unlock();
			}
		}

		final void onDisposedGroup(final CacheGroup<?, ?, ?> group) {
			if (!group.inCluster) {
				return;
			}
			this.writeLock.lock();
			try {
				if (this.groupCount == 0) {
					return;
				} else {
					final int hashIndex = (int) (group.longIdentifier) & this.hashMask;
					CacheGroup<?, ?, ?> existGroup = this.groupMap[hashIndex];
					CacheGroup<?, ?, ?> lastGroup = null;
					while (existGroup != null) {
						if (existGroup.longIdentifier == group.longIdentifier) {
							if (lastGroup == null) {
								this.groupMap[hashIndex] = existGroup.nextInContainer;
							} else {
								lastGroup.nextInContainer = existGroup.nextInContainer;
							}
							existGroup.nextInContainer = null;
							this.groupCount--;
							break;
						} else {
							lastGroup = existGroup;
							existGroup = existGroup.nextInContainer;
						}
					}
				}
			} finally {
				this.writeLock.unlock();
			}
		}

		private final IDGenerator identifierGenerator;

		private final ReentrantReadWriteLock.ReadLock readLock;

		private final ReentrantReadWriteLock.WriteLock writeLock;

		private volatile int groupCount;

		private volatile CacheGroup<?, ?, ?>[] groupMap;

		private volatile int hashMask;

	}

	static final class ClusterHolderContainer {

		private ClusterHolderContainer(final IDGenerator identifierGenerator) {
			final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
			this.readLock = lock.readLock();
			this.writeLock = lock.writeLock();
			this.identifierGenerator = identifierGenerator;
			this.holderCount = 0;
			this.holderMap = new CacheHolder<?, ?, ?>[128];
			this.hashMask = 128 - 1;
		}

		final CacheHolder<?, ?, ?> findHolder(final long longIdentifier) {
			this.readLock.lock();
			try {
				if (this.holderCount == 0) {
					return null;
				} else {
					final int hashIndex = (int) longIdentifier & this.hashMask;
					CacheHolder<?, ?, ?> existHolder = this.holderMap[hashIndex];
					while (existHolder != null) {
						if (existHolder.longIdentifier == longIdentifier) {
							return existHolder;
						} else {
							existHolder = existHolder.nextInGlobalContainer;
						}
					}
				}
				return null;
			} finally {
				this.readLock.unlock();
			}
		}

		final long onCreatedHolder(final CacheHolder<?, ?, ?> holder,
				final Long fixLongIdentifier) {
			if (holder.ownGroup.inCluster) {
				this.writeLock.lock();
				try {
					if (this.holderCount == this.hashMask) {
						final int oldCapacity = this.holderMap.length;
						final int newCapacity = oldCapacity * 2;
						final int newHashMask = newCapacity - 1;
						final CacheHolder<?, ?, ?>[] newHolderMap = new CacheHolder<?, ?, ?>[newCapacity];
						for (CacheHolder<?, ?, ?> existHolder : this.holderMap) {
							while (existHolder != null) {
								final int newHashIndex = (int) existHolder.longIdentifier & newHashMask;
								final CacheHolder<?, ?, ?> tempHolder = existHolder;
								existHolder = existHolder.nextInGlobalContainer;
								tempHolder.nextInGlobalContainer = newHolderMap[newHashIndex];
								newHolderMap[newHashIndex] = tempHolder;
							}
						}
						this.hashMask = newHashMask;
						this.holderMap = newHolderMap;
					}
					long identifier;
					if (fixLongIdentifier != null) {
						identifier = fixLongIdentifier;
					} else {
						loop: for (identifier = this.identifierGenerator.next();; identifier = this.identifierGenerator.next()) {
							int hashIndex = (int) identifier & this.hashMask;
							CacheHolder<?, ?, ?> existHolder = this.holderMap[hashIndex];
							while (existHolder != null) {
								if (existHolder.longIdentifier == identifier) {
									continue loop;
								} else {
									existHolder = existHolder.nextInGlobalContainer;
								}
							}
							break;
						}
					}
					final int hashIndex = (int) (identifier) & this.hashMask;
					holder.nextInGlobalContainer = this.holderMap[hashIndex];
					this.holderMap[hashIndex] = holder;
					this.holderCount++;
					return identifier;
				} finally {
					this.writeLock.unlock();
				}
			} else {
				return 0L;
			}
		}

		final void onDisposedHolder(final CacheHolder<?, ?, ?> holder) {
			if (!holder.ownGroup.inCluster) {
				return;
			}
			this.writeLock.lock();
			try {
				if (this.holderCount == 0) {
					return;
				} else {
					final int hashIndex = (int) (holder.longIdentifier) & this.hashMask;
					CacheHolder<?, ?, ?> existHolder = this.holderMap[hashIndex];
					CacheHolder<?, ?, ?> lastHolder = null;
					while (existHolder != null) {
						if (existHolder.longIdentifier == holder.longIdentifier) {
							if (lastHolder == null) {
								this.holderMap[hashIndex] = existHolder.nextInGlobalContainer;
							} else {
								lastHolder.nextInGlobalContainer = existHolder.nextInGlobalContainer;
							}
							existHolder.nextInGlobalContainer = null;
							this.holderCount--;
							return;
						} else {
							lastHolder = existHolder;
							existHolder = existHolder.nextInGlobalContainer;
						}
					}
				}
			} finally {
				this.writeLock.unlock();
			}
		}

		private final IDGenerator identifierGenerator;

		private final ReentrantReadWriteLock.ReadLock readLock;

		private final ReentrantReadWriteLock.WriteLock writeLock;

		private volatile int holderCount;

		private volatile CacheHolder<?, ?, ?>[] holderMap;

		private volatile int hashMask;

	}

	static final class ACGroupContainer extends
			HashMap<GUID, CacheGroup<?, ?, ?>> {

		private static final ArrayList<AccessControlCacheHolderOfGroup> EMPTY_ACCACHEHOLDEROFGROUP_LIST;

		static {
			EMPTY_ACCACHEHOLDEROFGROUP_LIST = new ArrayList<AccessControlCacheHolderOfGroup>(0);
		}

		private static final long serialVersionUID = 3209205285036684358L;

		private ACGroupContainer() {
			this.accessControlCacheHolderOfGroupList = null;
		}

		final void onCreatedGroup(
				final AccessControlCacheHolderOfGroup groupHolder,
				final GUID ACGUIDIdentifier) {
			final CacheGroup<?, ?, ?> existGroup;
			if ((existGroup = this.put(ACGUIDIdentifier, groupHolder.cacheGroup)) != null) {
				this.put(ACGUIDIdentifier, existGroup);
				throw new RuntimeException("冲突的访问控制缓存组定义。冲突标识为[" + ACGUIDIdentifier + "]。缓存组1[" + existGroup.ownSpace.identifier + "," + existGroup.define.facadeClass + "]，缓存组2[" + groupHolder.cacheGroup.ownSpace.identifier + "," + groupHolder.cacheGroup.define.facadeClass + "]");
			}
			ArrayList<AccessControlCacheHolderOfGroup> accessControlCacheHolderOfGroupList = new ArrayList<AccessControlCacheHolderOfGroup>();
			if (this.accessControlCacheHolderOfGroupList != null) {
				accessControlCacheHolderOfGroupList.addAll(this.accessControlCacheHolderOfGroupList);
			}
			accessControlCacheHolderOfGroupList.add(groupHolder);
			this.accessControlCacheHolderOfGroupList = accessControlCacheHolderOfGroupList;
		}

		final CacheGroup<?, ?, ?> onDisposedGroup(final GUID groupIdentifier) {
			final CacheGroup<?, ?, ?> group = this.remove(groupIdentifier);
			if (group != null) {
				final ArrayList<AccessControlCacheHolderOfGroup> accessControlCacheHandlerOfGroups = new ArrayList<AccessControlCacheHolderOfGroup>(this.accessControlCacheHolderOfGroupList.size());
				accessControlCacheHandlerOfGroups.addAll(this.accessControlCacheHolderOfGroupList);
				accessControlCacheHandlerOfGroups.remove(group.accessControlInformation.accessControlCacheItem);
				this.accessControlCacheHolderOfGroupList = accessControlCacheHandlerOfGroups;
			}
			return group;
		}

		final ArrayList<AccessControlCacheHolderOfGroup> getAccessControlCacheItemOfGroup() {
			final ArrayList<AccessControlCacheHolderOfGroup> accessControlCacheHandlerOfGroups = this.accessControlCacheHolderOfGroupList;
			if (accessControlCacheHandlerOfGroups == null) {
				return EMPTY_ACCACHEHOLDEROFGROUP_LIST;
			}
			return accessControlCacheHandlerOfGroups;
		}

		private volatile ArrayList<AccessControlCacheHolderOfGroup> accessControlCacheHolderOfGroupList;

	}

	static final class ACLongIdentifierGenerator {

		private static final long TYPE_MASK = 0x0fL << 56;
		private static final long TYPE_LOCAL = 0x01L << 56;
		private static final long TYPE_CLUSTER = 0x02L << 56;

		private static final boolean isClusterIdentifier(final long identifier) {
			return (identifier & TYPE_MASK) == TYPE_CLUSTER;
		}

		private ACLongIdentifierGenerator(final long nodeIndex) {
			this.localACLongIdentifierGenerator = new IDGenerator(TYPE_LOCAL, 4);
			this.clusterACLongIdentifierGenerator = new IDGenerator(nodeIndex | TYPE_CLUSTER, 8);
		}

		final long next(final boolean clusterIdentifier) {
			if (clusterIdentifier) {
				return this.clusterACLongIdentifierGenerator.next();
			} else {
				return this.localACLongIdentifierGenerator.next();
			}
		}

		private final IDGenerator localACLongIdentifierGenerator;

		private final IDGenerator clusterACLongIdentifierGenerator;

	}

	Cache(final Site site) {
		this.site = site;
		final long nodeIndex = ((long) site.getNetCluster().thisClusterNodeIndex) << (ID_LEN - NetClusterImpl.NODE_INDEX_LEN);
		this.resourceServiceContainer = new HashMap<Class<?>, ResourceServiceBase<?, ?, ?>>();
		this.defineContainer = new DefineContainer();
		this.ACGroupContainer = new ACGroupContainer();
		this.ACIdentifierGenerator = new ACLongIdentifierGenerator(nodeIndex);
		this.clusterGroupContainer = new ClusterGroupContainer(new IDGenerator(nodeIndex | TYPE_CACHE_GROUP, 8));
		this.clusterHolderContainer = new ClusterHolderContainer(new IDGenerator(nodeIndex | TYPE_CACHE_HOLDER, 8));
		this.defaultGroupSpace = new DefaultGroupSpace();
		final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		this.readLock = lock.readLock();
		this.writeLock = lock.writeLock();
		this.spaceCount = 0;
		this.spaceMap = new CustomGroupSpace[4];
		this.hashMask = 4 - 1;
		this.initialized = false;
	}

	@Override
	final boolean needSynchronizeInCluster() {
		return true;
	}

	final <TFacade> ResourceServiceBase<TFacade, ?, ?> getResourceService(
			final Class<TFacade> facadeClass) {
		final ResourceServiceBase<TFacade, ?, ?> existResourceService = this.findResourceService(facadeClass);
		if (existResourceService == null) {
			throw new NoSuchDefineException(facadeClass);
		} else {
			return existResourceService;
		}
	}

	final <TFacade> ResourceServiceBase<TFacade, ?, ?> findResourceService(
			final Class<TFacade> facadeClass) {
		return (ResourceServiceBase<TFacade, ?, ?>) (this.resourceServiceContainer.get(facadeClass));
	}

	final void putResourceService(
			final ResourceServiceBase<?, ?, ?> resourceService) {
		final ResourceServiceBase<?, ?, ?> existResourceService = this.resourceServiceContainer.put(resourceService.facadeClass, resourceService);
		if (existResourceService != null) {
			this.resourceServiceContainer.put(existResourceService.facadeClass, existResourceService);
			throw new ConflictingDefineException(existResourceService.facadeClass);
		}
	}

	final void initializeDefines() {
		if (this.initialized) {
			throw new CacheStateError();
		}
		this.defineContainer.reset();
		this.defaultGroupSpace.reset();
		for (ResourceServiceBase<?, ?, ?> resourceService : this.resourceServiceContainer.values()) {
			try {
				resourceService.ensureCacheDefine(this);
			} catch (Throwable e) {
				this.site.application.catcher.catchException(e, null);
			}
		}
	}

	final void initializeCache(final CacheClusterInitializeTask task,
			Transaction transaction) {
		if (this.initialized) {
			throw new CacheStateError();
		}
		this.defaultGroupSpace.reset();
		final ArrayList<CacheDefine<?, ?, ?>> defineList = this.defineContainer.getDefineList();
		final ArrayList<CacheDefine<?, ?, ?>> needCreateDefaultGroupDefineList;
		if (defineList.size() > 0) {
			if (task == null) {
				needCreateDefaultGroupDefineList = defineList;
			} else {
				this.clock = task.cacheClock;
				needCreateDefaultGroupDefineList = new ArrayList<CacheDefine<?, ?, ?>>();
				needCreateDefaultGroupDefineList.addAll(defineList);
				final CreateGroupData[] createGroupDatas = task.getCreateGroupDatas();
				if (createGroupDatas != null) {
					for (CreateGroupData data : createGroupDatas) {
						final CacheDefine<?, ?, ?> define = this.findDefine(data.defineIdentifier);
						if (define != null) {
							CacheGroup group;
							if (CacheGroupSpace.isPreservedSpaceIdentifier(data.spaceIdentifer)) {
								group = this.defaultGroupSpace.remoteCreateGroupAndCommit(define, data.title, data.longIdentifier, data.initializeState, data.catchedInitializeException ? new GroupInitialiazeException(define.facadeClass, data.spaceIdentifer) : null);
								needCreateDefaultGroupDefineList.remove(define);
							} else {
								group = this.getSpace(data.spaceIdentifer).remoteCreateGroupAndCommit(define, data.title, data.longIdentifier, data.initializeState, data.catchedInitializeException ? new GroupInitialiazeException(define.facadeClass, data.spaceIdentifer) : null);
							}
							group.clock = data.clock;
						}
					}
				}
				final ArrayList<CreateHolderData<?, ?, ?>> createHolderDataList = task.getCreateHolderDataList();
				if (createHolderDataList != null) {
					for (CreateHolderData data : createHolderDataList) {
						final CacheGroup group = this.clusterGroupContainer.findGroup(data.groupIdentifier);
						if (group == null) {
							throw new IllegalStateException(String.format("集群：找不到缓存组[%x]", data.groupIdentifier));
						}
						CacheHolder holder = group.remoteCreateHolderAndCommit(data.value, data.keysHolder, data.longIdentifier);
						holder.clock = data.clock;
						data.cacheHolder = holder;
					}
				}
				final ArrayList<CreateTreeNodeData> createTreeNodeDataList = task.getCreateTreeNodeDataList();
				if (createTreeNodeDataList != null) {
					for (CreateTreeNodeData data : createTreeNodeDataList) {
						final CacheHolder childHolder = this.clusterHolderContainer.findHolder(data.childNodeIdentifier);
						if (childHolder != null) {
							final CacheHolder parentHolder;
							if (data.parentNodeIdentifier == null) {
								parentHolder = null;
							} else {
								parentHolder = this.clusterHolderContainer.findHolder(data.parentNodeIdentifier);
								if (parentHolder == null) {
									continue;
								}
							}
							childHolder.ownGroup.tryGetBindTree().remoteCreateNodeAndCommit(parentHolder, childHolder);
						}
					}
				}
				final ArrayList<CreateReferenceData> createReferenceDataList = task.getCreateReferenceDataList();
				if (createReferenceDataList != null) {
					for (CreateReferenceData data : createReferenceDataList) {
						final CacheHolder<?, ?, ?> holder = this.clusterHolderContainer.findHolder(data.holderIdentifier);
						if (holder != null) {
							final CacheHolder<?, ?, ?> reference = this.clusterHolderContainer.findHolder(data.referenceIdentifier);
							if (reference != null) {
								holder.localCreateReferenceAndCommit(reference);
							}
						}
					}
				}
				// 还原自定义数据
				ContextImpl<?, ?, ?> context = transaction.getCurrentContext();
				if (createHolderDataList != null) {
					for (CreateHolderData data : createHolderDataList) {
						if (data.userData != null) {
							data.cacheHolder.restoreSerialUserData(data.userData, context);
						}
					}
				}
			}
			for (CacheDefine<?, ?, ?> define : needCreateDefaultGroupDefineList) {
				if (define.kind.isGlobal) {
					this.defaultGroupSpace.localCreateGroupAndCommit(define, define.title);
				}
			}
		}
	}

	final void finishInitialize() {
		this.initialized = true;
	}

	final CacheDefine<?, ?, ?> getDefine(final GUID defineIdentifier) {
		final CacheDefine<?, ?, ?> define = this.defineContainer.findDefine(defineIdentifier);
		if (define == null) {
			throw new NoSuchDefineException(defineIdentifier);
		} else {
			return define;
		}
	}

	final <TFacade> CacheDefine<TFacade, ?, ?> getDefine(
			final Class<TFacade> facadeClass) {
		final CacheDefine<TFacade, ?, ?> define = this.defineContainer.findDefine(facadeClass);
		if (define == null) {
			throw new NoSuchDefineException(facadeClass);
		} else {
			return define;
		}
	}

	final CacheDefine<?, ?, ?> findDefine(final GUID defineIdentifier) {
		return this.defineContainer.findDefine(defineIdentifier);
	}

	final <TFacade> CacheDefine<TFacade, ?, ?> findDefine(
			final Class<TFacade> facadeClass) {
		if (this.initialized) {
			return this.defineContainer.findDefine(facadeClass);
		} else {
			return null;
		}
	}

	final CustomGroupSpace findSpace(final Object spaceIdentifier) {
		if (CacheGroupSpace.isPreservedSpaceIdentifier(spaceIdentifier)) {
			throw new IllegalArgumentException("无效的用户自定义缓存组空间标识。[" + spaceIdentifier + "]");
		}
		this.readLock.lock();
		try {
			if (this.spaceCount == 0) {
				return null;
			} else {
				final int hashIndex = HashUtil.hash(spaceIdentifier) & this.hashMask;
				CustomGroupSpace existSpace = this.spaceMap[hashIndex];
				while (existSpace != null) {
					if (existSpace.identifier.equals(spaceIdentifier)) {
						return existSpace;
					} else {
						existSpace = existSpace.next;
					}
				}
			}
			return null;
		} finally {
			this.readLock.unlock();
		}
	}

	final <TFacade, TImplement extends TFacade, TKeysHolder> CacheGroup<TFacade, TImplement, TKeysHolder> localCreateGroup(
			final Object spaceIdentifier,
			final CacheDefine<TFacade, TImplement, TKeysHolder> define,
			final String title, final Transaction transaction) {
		if (CacheGroupSpace.isPreservedSpaceIdentifier(spaceIdentifier)) {
			throw new UnsupportedOperationException("不支持在默认缓存组空间中创建新缓存组。");
		}
		if (this.initialized) {
			transaction.handleAcquirable(this, AcquireFor.MODIFY_ITEMS);
			CustomGroupSpace space = this.findSpace(spaceIdentifier);
			if (space == null) {
				space = this.getSpace(spaceIdentifier);
			}
			return space.localCreateGroup(define, title, transaction);
		} else {
			throw new CacheStateError();
		}
	}

	final <TFacade, TImplement extends TFacade, TKeysHolder> CacheGroup<TFacade, TImplement, TKeysHolder> localRemoveGroup(
			final CacheDefine<TFacade, TImplement, TKeysHolder> define,
			final Object spaceIdentifier, final Transaction transaction) {
		if (CacheGroupSpace.isPreservedSpaceIdentifier(spaceIdentifier)) {
			throw new UnsupportedOperationException("不支持删除默认缓存组空间中的缓存组。");
		}
		if (this.initialized) {
			transaction.handleAcquirable(this, AcquireFor.MODIFY_ITEMS);
			CustomGroupSpace space = this.findSpace(spaceIdentifier);
			if (space != null) {
				final CacheGroup<?, ?, ?> existGroup = space.findGroup(define.facadeClass, transaction);
				if (existGroup != null) {
					space.localRemoveGroup(existGroup, transaction);
				}
				return (CacheGroup<TFacade, TImplement, TKeysHolder>) existGroup;
			} else {
				return null;
			}
		} else {
			throw new CacheStateError();
		}
	}

	final <TFacade, TImplement extends TFacade, TKeysHolder> CacheGroup<TFacade, TImplement, TKeysHolder> remoteCreateGroup(
			final Object spaceIdentifier,
			final CacheDefine<TFacade, TImplement, TKeysHolder> define,
			final String title, final Long fixLongIdentifier,
			final Byte fixInitializeState, final Throwable initializeException,
			final Transaction transaction) {
		if (CacheGroupSpace.isPreservedSpaceIdentifier(spaceIdentifier)) {
			throw new UnsupportedOperationException("不支持在默认缓存组空间中创建新缓存组。");
		}
		if (this.initialized) {
			CustomGroupSpace space = this.findSpace(spaceIdentifier);
			if (space == null) {
				space = this.getSpace(spaceIdentifier);
			}
			return space.remoteCreateGroup(define, title, fixLongIdentifier, fixInitializeState, initializeException, transaction);
		} else {
			throw new CacheStateError();
		}
	}

	final <TFacade, TImplement extends TFacade, TKeysHolder> CacheGroup<TFacade, TImplement, TKeysHolder> remoteRemoveGroup(
			final CacheDefine<TFacade, TImplement, TKeysHolder> define,
			final Object spaceIdentifier, final Transaction transaction) {
		if (CacheGroupSpace.isPreservedSpaceIdentifier(spaceIdentifier)) {
			throw new UnsupportedOperationException("不支持删除默认缓存组空间中的缓存组。");
		}
		if (this.initialized) {
			CustomGroupSpace space = this.findSpace(spaceIdentifier);
			if (space != null) {
				final CacheGroup<?, ?, ?> existGroup = space.findGroup(define.facadeClass, transaction);
				if (existGroup != null) {
					space.remoteRemoveGroup(existGroup, transaction);
				}
				return (CacheGroup<TFacade, TImplement, TKeysHolder>) existGroup;
			} else {
				return null;
			}
		} else {
			throw new CacheStateError();
		}
	}

	final void collectData(final CacheClusterInitializeTask task,
			final Transaction transaction) {
		this.readLock.lock();
		try {
			task.cacheClock = this.clock;
			this.defaultGroupSpace.collectData(task, transaction);
			for (CustomGroupSpace space : this.spaceMap) {
				while (space != null) {
					space.collectData(task, transaction);
					space = space.next;
				}
			}
			if (Cache.IN_DEBUG_MODE && transaction.kind == TransactionKind.REMOTE) {
				List<ResourceInClusterSyncInfo> infoes = new ArrayList<Cache.ResourceInClusterSyncInfo>();
				infoes.addAll(syncInfoes.values());
				Collections.sort(infoes);
				PrintWriter pw = null;
				try {
					File parent = new File(transaction.getCurrentSession().getApplication().getDNARoot(), "logs/core/cluster/quirk");
					if (!parent.exists()) {
						parent.mkdirs();
					}
					pw = new PrintWriter(new File(parent, "sync_times.log"));
					StringBuffer str = new StringBuffer();
					for (ResourceInClusterSyncInfo info : infoes) {
						str.append(info.getFacade() + " : " + info.getTimes() / (1000 * 1000) + "ms\r\n");
					}
					pw.write(str.toString());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					if (pw != null) {
						pw.close();
					}
				}
			}
		} finally {
			this.readLock.unlock();
		}
	}

	final void synchronizeModifiedData(final CacheClusterSynchronizeTask task,
			final Transaction transaction) {
		if (Cache.INITIALIZEGROUP_INSAMETRANS) {
			this.internalSynchronizeModifiedDataII(task, transaction);
			return;
		}
		if (task.isInitializeSynchronizeTask) {
			this.internalSynchronizeModifiedDataWhenInitialize(task, transaction);
		} else {
			this.internalSynchronizeModifiedData(task, transaction);
		}
	}

	final CustomGroupSpace getSpace(final Object spaceIdentifier) {
		this.writeLock.lock();
		try {
			final CustomGroupSpace space = this.findSpace(spaceIdentifier);
			if (space != null) {
				return space;
			}
			if (this.spaceCount == this.hashMask) {
				final int oldCapacity = this.spaceMap.length;
				final int newCapacity = oldCapacity * 2;
				final int newHashMask = newCapacity - 1;
				final CustomGroupSpace newSpaceMap[] = new CustomGroupSpace[newCapacity];
				for (CustomGroupSpace existSpace : this.spaceMap) {
					while (existSpace != null) {
						final int newHashIndex = HashUtil.hash(existSpace.identifier) & newHashMask;
						final CustomGroupSpace tempSpace = existSpace;
						existSpace = existSpace.next;
						tempSpace.next = newSpaceMap[newHashIndex];
						newSpaceMap[newHashIndex] = tempSpace;
					}
				}
				this.hashMask = newHashMask;
				this.spaceMap = newSpaceMap;
			}
			final CustomGroupSpace newSpace = new CustomGroupSpace(spaceIdentifier);
			final int hashIndex = HashUtil.hash(spaceIdentifier) & this.hashMask;
			newSpace.next = this.spaceMap[hashIndex];
			this.spaceMap[hashIndex] = newSpace;
			this.spaceCount++;
			return newSpace;
		} finally {
			this.writeLock.unlock();
		}
	}

	final long[] scavengeClusterAuthorityData(final long[] ACL,
			final ArrayList<Long> authorityDataList) {
		AccessControlHelper.scavengeAuthorityDataFrom(ACL, authorityDataList);
		int point = 0;
		for (int index = 0, endIndex = authorityDataList.size(); index < endIndex;) {
			final long identifier = authorityDataList.get(index);
			if (ACLongIdentifierGenerator.isClusterIdentifier(identifier)) {
				authorityDataList.set(point++, authorityDataList.get(index++));
				authorityDataList.set(point++, authorityDataList.get(index++));
			} else {
				index += 2;
			}
		}
		for (int index = authorityDataList.size() - 1, endIndex = point; index >= endIndex; index--) {
			authorityDataList.remove(index);
		}
		final int count = authorityDataList.size();
		if (count == 0) {
			return AccessControlHelper.EMPTY_ACL;
		} else {
			final long[] result = new long[count];
			for (int index = 0, endIndex = count; index < endIndex; index++) {
				result[index] = authorityDataList.get(index);
			}
			return result;
		}
	}

	private final void internalSynchronizeModifiedDataWhenInitialize(
			final CacheClusterSynchronizeTask task,
			final Transaction transaction) {
		final RemoveGroupData[] removeGroupDatas = task.getRemoveGroupDatas();
		if (removeGroupDatas != null) {
			for (RemoveGroupData data : removeGroupDatas) {
				final CacheGroup<?, ?, ?> group = this.clusterGroupContainer.findGroup(data.longIdentifier);
				if (group == null) {
					throw new IllegalStateException(String.format("集群：找不到资源组[%x]", data.longIdentifier));
				}
				this.remoteRemoveGroup(group.define, group.ownSpace.identifier, transaction);
			}
		}
		final CreateGroupData[] createGroupDatas = task.getCreateGroupDatas();
		if (createGroupDatas != null) {
			for (CreateGroupData data : createGroupDatas) {
				final CacheDefine<?, ?, ?> define = this.findDefine(data.defineIdentifier);
				if (define == null) {
					throw new IllegalStateException(String.format("集群：找不到资源定义[%s]", data.defineIdentifier));
				}
				this.remoteCreateGroup(data.spaceIdentifer, define, data.title, data.longIdentifier, data.initializeState, data.catchedInitializeException ? new GroupInitialiazeException(define.facadeClass, data.spaceIdentifer, "缓存组初始化的过程中出现异常。") : null, transaction);
			}
		}
		final ArrayList<CreateHolderData<?, ?, ?>> createHolderDataList = task.getCreateHolderDataList();
		if (createHolderDataList != null) {
			final ArrayList<CacheGroup<?, ?, ?>> tempGroupList = new ArrayList<CacheGroup<?, ?, ?>>();
			for (CreateHolderData data : createHolderDataList) {
				final CacheGroup group = this.clusterGroupContainer.findGroup(data.groupIdentifier);
				if (group == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存组[%x]", data.groupIdentifier));
				}
				if (!tempGroupList.contains(group)) {
					group.forceSetInitializing();
					tempGroupList.add(group);
				}
				data.cacheHolder = group.remoteCreateHolder(data.value, data.keysHolder, data.longIdentifier, transaction);
			}
			for (CacheGroup<?, ?, ?> group : tempGroupList) {
				group.forceSetInitialized();
			}
		}
		final ArrayList<CreateTreeNodeData> createTreeNodeDataList = task.getCreateTreeNodeDataList();
		if (createTreeNodeDataList != null) {
			for (CreateTreeNodeData data : createTreeNodeDataList) {
				final CacheHolder childHolder = this.clusterHolderContainer.findHolder(data.childNodeIdentifier);
				if (childHolder != null) {
					final CacheHolder parentHolder;
					if (data.parentNodeIdentifier == null) {
						parentHolder = null;
					} else {
						parentHolder = this.clusterHolderContainer.findHolder(data.parentNodeIdentifier);
						if (parentHolder == null) {
							continue;
						}
					}
					childHolder.ownGroup.tryGetBindTree().remoteCreateNode(parentHolder, childHolder, transaction);
				}
			}
		}
		final ArrayList<CreateReferenceData> createReferenceDataList = task.getCreateReferenceDataList();
		if (createReferenceDataList != null) {
			for (CreateReferenceData data : createReferenceDataList) {
				final CacheHolder<?, ?, ?> holder = this.clusterHolderContainer.findHolder(data.holderIdentifier);
				if (holder != null) {
					final CacheHolder<?, ?, ?> reference = this.clusterHolderContainer.findHolder(data.referenceIdentifier);
					if (reference != null) {
						holder.remoteCreateReference(reference, transaction);
					}
				}
			}
		}
		// 还原自定义数据
		ContextImpl<?, ?, ?> context = transaction.getCurrentContext();
		if (createHolderDataList != null) {
			for (CreateHolderData data : createHolderDataList) {
				if (data.userData != null) {
					data.cacheHolder.restoreSerialUserData(data.userData, context);
				}
			}
		}
	}

	private final void internalSynchronizeModifiedData(
			final CacheClusterSynchronizeTask task,
			final Transaction transaction) {
		final RemoveGroupData[] removeGroupDatas = task.getRemoveGroupDatas();
		if (removeGroupDatas != null) {
			for (RemoveGroupData data : removeGroupDatas) {
				final CacheGroup<?, ?, ?> group = this.clusterGroupContainer.findGroup(data.longIdentifier);
				if (group == null) {
					throw new IllegalStateException(String.format("集群：找不到资源组[%x]", data.longIdentifier));
				}
				this.remoteRemoveGroup(group.define, group.ownSpace.identifier, transaction);
			}
		}
		final CreateGroupData[] createGroupDatas = task.getCreateGroupDatas();
		if (createGroupDatas != null) {
			for (CreateGroupData data : createGroupDatas) {
				final CacheDefine<?, ?, ?> define = this.findDefine(data.defineIdentifier);
				if (define == null) {
					throw new IllegalStateException(String.format("集群：找不到资源定义[%s]", data.defineIdentifier));
				}
				this.remoteCreateGroup(data.spaceIdentifer, define, data.title, data.longIdentifier, data.initializeState, data.catchedInitializeException ? new GroupInitialiazeException(define.facadeClass, data.spaceIdentifer, "缓存组初始化的过程中出现异常。") : null, transaction);
			}
		}
		final ArrayList<RemoveHolderData> removeHolderDataList = task.getRemoveHolderDataList();
		if (removeHolderDataList != null) {
			for (RemoveHolderData data : removeHolderDataList) {
				final CacheHolder holder = this.clusterHolderContainer.findHolder(data.longIdentifier);
				if (holder == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.longIdentifier));
				}
				holder.remoteRemove(transaction);
			}
		}
		final ArrayList<ModifyHolderData<?, ?, ?>> modifyHolderDataList = task.getModifyHolderDataList();
		if (modifyHolderDataList != null) {
			for (ModifyHolderData data : modifyHolderDataList) {
				final CacheHolder<?, ?, ?> holder = this.clusterHolderContainer.findHolder(data.longIdentifier);
				if (holder == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.longIdentifier));
				}
				holder.tryGetModifiableValue();
				holder.tryPostModifiedValueWithoutCheck(data.newValue, data.newKeysHolder, transaction);
				data.resourceService = holder.ownGroup.define.resourceService;
			}
		}
		final ArrayList<CreateHolderData<?, ?, ?>> createHolderDataList = task.getCreateHolderDataList();
		if (createHolderDataList != null) {
			for (CreateHolderData data : createHolderDataList) {
				final CacheGroup group = this.clusterGroupContainer.findGroup(data.groupIdentifier);
				if (group == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存组[%x]", data.groupIdentifier));
				}
				data.cacheHolder = group.remoteCreateHolder(data.value, data.keysHolder, data.longIdentifier, transaction);
			}
		}
		final ArrayList<CreateTreeNodeData> createTreeNodeDataList = task.getCreateTreeNodeDataList();
		if (createTreeNodeDataList != null) {
			for (CreateTreeNodeData data : createTreeNodeDataList) {
				final CacheHolder childHolder = this.clusterHolderContainer.findHolder(data.childNodeIdentifier);
				if (childHolder == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.childNodeIdentifier));
				}
				CacheHolder parentHolder = null;
				if (data.parentNodeIdentifier != null) {
					parentHolder = this.clusterHolderContainer.findHolder(data.parentNodeIdentifier);
					if (parentHolder == null) {
						throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.parentNodeIdentifier));
					}
				}
				childHolder.ownGroup.tryGetBindTree().remoteCreateNode(parentHolder, childHolder, transaction);
			}
		}
		final ArrayList<RemoveReferenceData> removeReferenceDataList = task.getRemoveReferenceDataList();
		if (removeReferenceDataList != null) {
			for (RemoveReferenceData data : removeReferenceDataList) {
				final CacheHolder<?, ?, ?> holder = this.clusterHolderContainer.findHolder(data.holderIdentifier);
				if (holder == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.holderIdentifier));
				}
				final CacheHolder<?, ?, ?> reference = this.clusterHolderContainer.findHolder(data.referenceIdentifier);
				if (reference == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.referenceIdentifier));
				}
				holder.remoteRemoveReference(reference, transaction);
			}
		}
		final ArrayList<CreateReferenceData> createReferenceDataList = task.getCreateReferenceDataList();
		if (createReferenceDataList != null) {
			for (CreateReferenceData data : createReferenceDataList) {
				final CacheHolder<?, ?, ?> holder = this.clusterHolderContainer.findHolder(data.holderIdentifier);
				if (holder == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.holderIdentifier));
				}
				final CacheHolder<?, ?, ?> reference = this.clusterHolderContainer.findHolder(data.referenceIdentifier);
				if (reference == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.referenceIdentifier));
				}
				holder.remoteCreateReference(reference, transaction);
			}
		}
		final ArrayList<ReloadAuthorityData> reloadAuthorityDataList = task.getReloadAuthorityDataList();
		if (reloadAuthorityDataList != null) {
			for (ReloadAuthorityData data : reloadAuthorityDataList) {
				final CacheHolder holder = this.clusterHolderContainer.findHolder(data.actorIdentifier);
				if (holder instanceof ActorCacheHolder) {
					((ActorCacheHolder<?, ?, ?>) holder).resetACL();
				} else if (holder instanceof IdentifyCacheHolder) {
					((IdentifyCacheHolder) holder).resetACL();
				} else {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.actorIdentifier));
				}
			}
		}
		// 还原自定义数据
		ContextImpl<?, ?, ?> context = transaction.getCurrentContext();
		if (createHolderDataList != null) {
			for (CreateHolderData data : createHolderDataList) {
				if (data.userData != null) {
					data.cacheHolder.restoreSerialUserData(data.userData, context);
				}
			}
		}
		if (modifyHolderDataList != null) {
			for (ModifyHolderData data : modifyHolderDataList) {
				if (data.userData != null) {
					context.restoreCacheHolderUserData(data.resourceService, data.userData, data.newValue, data.newKeysHolder);
				}
			}
		}
	}

	private final void internalSynchronizeModifiedDataII(
			final CacheClusterSynchronizeTask task,
			final Transaction transaction) {
		final RemoveGroupData[] removeGroupDatas = task.getRemoveGroupDatas();
		if (removeGroupDatas != null) {
			for (RemoveGroupData data : removeGroupDatas) {
				final CacheGroup<?, ?, ?> group = this.clusterGroupContainer.findGroup(data.longIdentifier);
				if (group == null) {
					throw new IllegalStateException(String.format("集群：找不到资源组[%x]", data.longIdentifier));
				}
				this.remoteRemoveGroup(group.define, group.ownSpace.identifier, transaction);
			}
		}
		final ModifyGroupData[] modifyGroupDatas = task.getModifyGroupDatas();
		if (modifyGroupDatas != null) {
			for (ModifyGroupData data : modifyGroupDatas) {
				final CacheGroup<?, ?, ?> group = this.clusterGroupContainer.findGroup(data.longIdentifier);
				if (group == null) {
					throw new IllegalStateException(String.format("集群：找不到资源组[%x]", data.longIdentifier));
				}
				group.forceSetInitializeState(data.initializeState, data.catchedInitializeException);
			}
		}
		final CreateGroupData[] createGroupDatas = task.getCreateGroupDatas();
		if (createGroupDatas != null) {
			for (CreateGroupData data : createGroupDatas) {
				final CacheDefine<?, ?, ?> define = this.findDefine(data.defineIdentifier);
				if (define == null) {
					throw new IllegalStateException(String.format("集群：找不到资源定义[%s]", data.defineIdentifier));
				}
				this.remoteCreateGroup(data.spaceIdentifer, define, data.title, data.longIdentifier, data.initializeState, data.catchedInitializeException ? new GroupInitialiazeException(define.facadeClass, data.spaceIdentifer, "缓存组初始化的过程中出现异常。") : null, transaction);
			}
		}
		final ArrayList<RemoveHolderData> removeHolderDataList = task.getRemoveHolderDataList();
		if (removeHolderDataList != null) {
			for (RemoveHolderData data : removeHolderDataList) {
				final CacheHolder holder = this.clusterHolderContainer.findHolder(data.longIdentifier);
				if (holder == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.longIdentifier));
				}
				holder.remoteRemove(transaction);
			}
		}
		final ArrayList<ModifyHolderData<?, ?, ?>> modifyHolderDataList = task.getModifyHolderDataList();
		if (modifyHolderDataList != null) {
			for (ModifyHolderData data : modifyHolderDataList) {
				final CacheHolder<?, ?, ?> holder = this.clusterHolderContainer.findHolder(data.longIdentifier);
				if (holder == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.longIdentifier));
				}
				holder.tryGetModifiableValue();
				holder.tryPostModifiedValueWithoutCheck(data.newValue, data.newKeysHolder, transaction);
				data.resourceService = holder.ownGroup.define.resourceService;
			}
		}
		final ArrayList<CreateHolderData<?, ?, ?>> createHolderDataList = task.getCreateHolderDataList();
		if (createHolderDataList != null) {
			for (CreateHolderData data : createHolderDataList) {
				final CacheGroup group = this.clusterGroupContainer.findGroup(data.groupIdentifier);
				if (group == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存组[%x]", data.groupIdentifier));
				}
				data.cacheHolder = group.remoteCreateHolder(data.value, data.keysHolder, data.longIdentifier, transaction);
			}
		}
		final ArrayList<CreateTreeNodeData> createTreeNodeDataList = task.getCreateTreeNodeDataList();
		if (createTreeNodeDataList != null) {
			for (CreateTreeNodeData data : createTreeNodeDataList) {
				final CacheHolder childHolder = this.clusterHolderContainer.findHolder(data.childNodeIdentifier);
				if (childHolder == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.childNodeIdentifier));
				}
				CacheHolder parentHolder = null;
				if (data.parentNodeIdentifier != null) {
					parentHolder = this.clusterHolderContainer.findHolder(data.parentNodeIdentifier);
					if (parentHolder == null) {
						throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.parentNodeIdentifier));
					}
				}
				childHolder.ownGroup.tryGetBindTree().remoteCreateNode(parentHolder, childHolder, transaction);
			}
		}
		final ArrayList<RemoveReferenceData> removeReferenceDataList = task.getRemoveReferenceDataList();
		if (removeReferenceDataList != null) {
			for (RemoveReferenceData data : removeReferenceDataList) {
				final CacheHolder<?, ?, ?> holder = this.clusterHolderContainer.findHolder(data.holderIdentifier);
				if (holder == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.holderIdentifier));
				}
				final CacheHolder<?, ?, ?> reference = this.clusterHolderContainer.findHolder(data.referenceIdentifier);
				if (reference == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.referenceIdentifier));
				}
				holder.remoteRemoveReference(reference, transaction);
			}
		}
		final ArrayList<CreateReferenceData> createReferenceDataList = task.getCreateReferenceDataList();
		if (createReferenceDataList != null) {
			for (CreateReferenceData data : createReferenceDataList) {
				final CacheHolder<?, ?, ?> holder = this.clusterHolderContainer.findHolder(data.holderIdentifier);
				if (holder == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.holderIdentifier));
				}
				final CacheHolder<?, ?, ?> reference = this.clusterHolderContainer.findHolder(data.referenceIdentifier);
				if (reference == null) {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.referenceIdentifier));
				}
				holder.remoteCreateReference(reference, transaction);
			}
		}
		final ArrayList<ReloadAuthorityData> reloadAuthorityDataList = task.getReloadAuthorityDataList();
		if (reloadAuthorityDataList != null) {
			for (ReloadAuthorityData data : reloadAuthorityDataList) {
				final CacheHolder holder = this.clusterHolderContainer.findHolder(data.actorIdentifier);
				if (holder instanceof ActorCacheHolder) {
					((ActorCacheHolder<?, ?, ?>) holder).resetACL();
				} else if (holder instanceof IdentifyCacheHolder) {
					((IdentifyCacheHolder) holder).resetACL();
				} else {
					throw new IllegalStateException(String.format("集群：找不到缓存项[%x]", data.actorIdentifier));
				}
			}
		}
		// 还原自定义数据
		ContextImpl<?, ?, ?> context = transaction.getCurrentContext();
		if (createHolderDataList != null) {
			for (CreateHolderData data : createHolderDataList) {
				if (data.userData != null) {
					data.cacheHolder.restoreSerialUserData(data.userData, context);
				}
			}
		}
		if (modifyHolderDataList != null) {
			for (ModifyHolderData data : modifyHolderDataList) {
				if (data.userData != null) {
					context.restoreCacheHolderUserData(data.resourceService, data.userData, data.newValue, data.newKeysHolder);
				}
			}
		}
	}

	final Site site;

	final DefineContainer defineContainer;

	private final HashMap<Class<?>, ResourceServiceBase<?, ?, ?>> resourceServiceContainer;

	final ClusterHolderContainer clusterHolderContainer;

	final ClusterGroupContainer clusterGroupContainer;

	final ACGroupContainer ACGroupContainer;

	final ACLongIdentifierGenerator ACIdentifierGenerator;

	final DefaultGroupSpace defaultGroupSpace;

	private final ReentrantReadWriteLock.ReadLock readLock;

	private final ReentrantReadWriteLock.WriteLock writeLock;

	private volatile int spaceCount;

	private volatile CustomGroupSpace[] spaceMap;

	private volatile int hashMask;

	private boolean initialized;

	final static class DefaultGroupSpace extends CacheGroupSpace {

		private DefaultGroupSpace() {
			super(null);
		}

		@Override
		final CustomGroupSpace asCustomGroupSpace() {
			return null;
		}

		@Override
		final <TFacade> CacheGroup<TFacade, ?, ?> getGroup(
				final Class<TFacade> facadeClass, final Transaction transaction) {
			final CacheGroup<TFacade, ?, ?> existGroup = this.findGroup(facadeClass, transaction);
			if (existGroup == null) {
				throw new NotFoundGroupException(facadeClass, null);
			} else {
				return existGroup;
			}
		}

		@Override
		final <TFacade> CacheGroup<TFacade, ?, ?> findGroup(
				final Class<TFacade> facadeClass, final Transaction transaction) {
			final int hashIndex = HashUtil.hash(facadeClass) & (this.groupMap.length - 1);
			CacheGroup<?, ?, ?> existGroup = this.groupMap[hashIndex];
			while (existGroup != null) {
				if (existGroup.define.facadeClass == facadeClass) {
					return (CacheGroup<TFacade, ?, ?>) existGroup;
				} else {
					existGroup = existGroup.nextInSpace;
				}
			}
			return null;
		}

		private final <TFacade, TImplement extends TFacade, TKeysHolder> CacheGroup<TFacade, TImplement, TKeysHolder> localCreateGroupAndCommit(
				final CacheDefine<TFacade, TImplement, TKeysHolder> define,
				final String title) {
			final CacheGroup<TFacade, TImplement, TKeysHolder> newGroup = define.newGroup(this, title, null, null, null);
			return this.internalCreateGroupAndCommit(newGroup);
		}

		private final <TFacade, TImplement extends TFacade, TKeysHolder> CacheGroup<TFacade, TImplement, TKeysHolder> remoteCreateGroupAndCommit(
				final CacheDefine<TFacade, TImplement, TKeysHolder> define,
				final String title, final Long fixLongIdentifier,
				final Byte fixInitializeState,
				final Throwable initializeException) {
			final CacheGroup<TFacade, TImplement, TKeysHolder> newGroup = define.newGroup(this, title, fixLongIdentifier, fixInitializeState, initializeException);
			return this.internalCreateGroupAndCommit(newGroup);
		}

		private final <TFacade, TImplement extends TFacade, TKeysHolder> CacheGroup<TFacade, TImplement, TKeysHolder> internalCreateGroupAndCommit(
				final CacheGroup<TFacade, TImplement, TKeysHolder> newGroup) {
			if (this.groupMap.length == this.groupCount) {
				final int oldCapacity = this.groupMap.length;
				final int newCapacity = oldCapacity * 2;
				final int newHashMask = newCapacity - 1;
				final CacheGroup<?, ?, ?>[] newGroupMap = new CacheGroup<?, ?, ?>[newCapacity];
				for (int index = 0; index < oldCapacity; index++) {
					CacheGroup<?, ?, ?> existGroup = this.groupMap[index];
					while (existGroup != null) {
						final int newHashIndex = HashUtil.hash(existGroup.define.facadeClass) & newHashMask;
						final CacheGroup<?, ?, ?> tempGroup = existGroup;
						existGroup = existGroup.nextInSpace;
						tempGroup.nextInSpace = newGroupMap[newHashIndex];
						newGroupMap[newHashIndex] = tempGroup;
					}
				}
				this.groupMap = newGroupMap;
			}
			newGroup.forceSetResolved();
			final int hashIndex = HashUtil.hash(newGroup.define.facadeClass) & (this.groupMap.length - 1);
			newGroup.nextInSpace = this.groupMap[hashIndex];
			this.groupMap[hashIndex] = newGroup;
			this.groupCount++;
			return newGroup;
		}

		private final void collectData(final CacheClusterInitializeTask task,
				final Transaction transaction) {
			if (this.groupCount > 0) {
				for (CacheGroup<?, ?, ?> existGroup : this.groupMap) {
					while (existGroup != null) {
						if (existGroup.inCluster) {
							if (Cache.IN_DEBUG_MODE && transaction.kind == TransactionKind.REMOTE) {
								long startTime = System.nanoTime();
								existGroup.collectGroupData(task, transaction);
								long estimatedTime = System.nanoTime() - startTime;
								String facade = existGroup.define.facadeClass.getName();
								synchronized (this) {
									ResourceInClusterSyncInfo info = syncInfoes.get(facade);
									if (info == null) {
										syncInfoes.put(facade, new ResourceInClusterSyncInfo(facade, estimatedTime));
									} else {
										info.setTimes(info.getTimes() + estimatedTime);
									}
								}
							} else {
								existGroup.collectGroupData(task, transaction);
							}

						}
						existGroup = existGroup.nextInSpace;
					}
				}
			}
		}

		private final void reset() {
			this.groupMap = new CacheGroup<?, ?, ?>[Cache.DEFAULT_GROUP_MAP_CAPACITY];
			this.groupCount = 0;
		}

		private final void initializeAllCacheGroup(final Transaction transaction) {
			if (this.groupCount > 0) {
				for (CacheGroup<?, ?, ?> group : this.groupMap) {
					while (group != null) {
						if (group.define.kind.inCluster) {
							group.ensureInitialized(transaction);
						}
						group = group.nextInSpace;
					}
				}
			}
		}

		private CacheGroup<?, ?, ?>[] groupMap;

		private int groupCount;

		@Override
		public String toString() {
			return "(NONE)";
		}
	}

	final static Map<String, ResourceInClusterSyncInfo> syncInfoes = new HashMap<String, ResourceInClusterSyncInfo>();

	final static class ResourceInClusterSyncInfo implements
			Comparable<ResourceInClusterSyncInfo> {
		private String facade;
		private long times;

		ResourceInClusterSyncInfo(String facade, long times) {
			this.facade = facade;
			this.times = times;
		}

		final String getFacade() {
			return this.facade;
		}

		final void setFacade(String facade) {
			this.facade = facade;
		}

		final long getTimes() {
			return this.times;
		}

		final void setTimes(long times) {
			this.times = times;
		}

		public int compareTo(ResourceInClusterSyncInfo o) {
			return new Long(o.getTimes()).compareTo(this.times);
		}
	}

	final class CustomGroupSpace extends CacheGroupSpace {

		private CustomGroupSpace(final Object objectIdentifier) {
			super(objectIdentifier);
			if (CacheGroupSpace.isValidatedSpaceIdentifier(objectIdentifier)) {
				final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
				this.readLock = lock.readLock();
				this.writeLock = lock.writeLock();
				this.version = Byte.MIN_VALUE;
			} else {
				throw new IllegalArgumentException("非法的缓存组空间标识。[" + objectIdentifier + "]");
			}
		}

		@Override
		final CustomGroupSpace asCustomGroupSpace() {
			return this;
		}

		final boolean notBelong(final Cache cache) {
			return cache != Cache.this;
		}

		@Override
		final <TFacade> CacheGroup<TFacade, ?, ?> getGroup(
				final Class<TFacade> facadeClass, final Transaction transaction) {
			final CacheGroup<TFacade, ?, ?> group = this.findGroup(facadeClass, transaction);
			if (group == null) {
				throw new NotFoundGroupException(facadeClass, this.identifier);
			} else {
				return group;
			}
		}

		@Override
		final <TFacade> CacheGroup<TFacade, ?, ?> findGroup(
				final Class<TFacade> facadeClass, final Transaction transaction) {
			this.readLock.lock();
			try {
				if (this.groupMap == null) {
					return null;
				} else {
					final int hashIndex = HashUtil.hash(facadeClass) & (this.hashMask);
					CacheGroup<?, ?, ?> existGroup = this.groupMap[hashIndex];
					if (Cache.this.isModifiableOnTransaction(transaction)) {
						while (existGroup != null) {
							if (existGroup.define.facadeClass == facadeClass) {
								switch (existGroup.getState()) {
								case CacheGroup.STATE_RESOLVED:
								case CacheGroup.STATE_CREATED:
								case CacheGroup.STATE_RESETTING:
									return (CacheGroup<TFacade, ?, ?>) existGroup;
								}
							}
							existGroup = existGroup.nextInSpace;
						}
					} else {
						while (existGroup != null) {
							if (existGroup.define.facadeClass == facadeClass) {
								switch (existGroup.getState()) {
								case CacheGroup.STATE_RESOLVED:
								case CacheGroup.STATE_REMOVED:
								case CacheGroup.STATE_RESETTING:
									return (CacheGroup<TFacade, ?, ?>) existGroup;
								}
							}
							existGroup = existGroup.nextInSpace;
						}
					}
				}
			} finally {
				this.readLock.unlock();
			}
			return null;
		}

		private final <TFacade, TImplement extends TFacade, TKeysHolder> CacheGroup<TFacade, TImplement, TKeysHolder> localCreateGroup(
				final CacheDefine<TFacade, TImplement, TKeysHolder> define,
				final String title, final Transaction transaction) {
			// transaction.handleAcquirable(Cache.this,
			// AcquireFor.MODIFY_ITEMS);
			final CacheGroup<TFacade, TImplement, TKeysHolder> group = this.internalCreateGroup(define, title, null, null, null, transaction, true);
			if (group.isNew()) {
				transaction.handleAcquirable(group, AcquireFor.ADD);
			}
			return group;
		}

		private final void localRemoveGroup(final CacheGroup<?, ?, ?> group,
				final Transaction transaction) {
			if (group.localRemove(transaction)) {
				this.disposeGroup(group, transaction);
			}
		}

		private final void remoteRemoveGroup(final CacheGroup<?, ?, ?> group,
				final Transaction transaction) {
			group.remoteRemove(transaction);
		}

		private final <TFacade, TImplement extends TFacade, TKeysHolder> CacheGroup<TFacade, TImplement, TKeysHolder> remoteCreateGroupAndCommit(
				final CacheDefine<TFacade, TImplement, TKeysHolder> define,
				final String title, final Long fixLongIdentifier,
				final Byte fixInitializeState,
				final Throwable initializeException) {
			final CacheGroup<TFacade, TImplement, TKeysHolder> newGroup = define.newGroup(this, title, fixLongIdentifier, fixInitializeState, initializeException);
			if (this.groupMap == null) {
				this.groupMap = new CacheGroup<?, ?, ?>[Cache.DEFAULT_GROUP_MAP_CAPACITY];
				this.hashMask = Cache.DEFAULT_GROUP_MAP_CAPACITY - 1;
			} else {
				if (this.groupMap.length == this.groupCount) {
					final int oldCapacity = this.groupMap.length;
					final int newCapacity = oldCapacity * 2;
					final int newHashMask = newCapacity - 1;
					final CacheGroup<?, ?, ?>[] newGroupMap = new CacheGroup<?, ?, ?>[newCapacity];
					for (int index = 0; index < oldCapacity; index++) {
						CacheGroup<?, ?, ?> existGroup = this.groupMap[index];
						while (existGroup != null) {
							final int newHashIndex = HashUtil.hash(existGroup.define.facadeClass) & newHashMask;
							final CacheGroup<?, ?, ?> tempGroup = existGroup;
							existGroup = existGroup.nextInSpace;
							tempGroup.nextInSpace = newGroupMap[newHashIndex];
							newGroupMap[newHashIndex] = tempGroup;
						}
					}
					this.groupMap = newGroupMap;
					this.hashMask = newHashMask;
				}
			}
			newGroup.forceSetResolved();
			final int hashIndex = HashUtil.hash(newGroup.define.facadeClass) & this.hashMask;
			newGroup.nextInSpace = this.groupMap[hashIndex];
			this.groupMap[hashIndex] = newGroup;
			this.groupCount++;
			return newGroup;
		}

		private final <TFacade, TImplement extends TFacade, TKeysHolder> CacheGroup<TFacade, TImplement, TKeysHolder> remoteCreateGroup(
				final CacheDefine<TFacade, TImplement, TKeysHolder> define,
				final String title, final Long fixLongIdentifier,
				final Byte fixInitializeState,
				final Throwable initializeException,
				final Transaction transaction) {
			final CacheGroup<TFacade, TImplement, TKeysHolder> group = this.internalCreateGroup(define, title, fixLongIdentifier, fixInitializeState, initializeException, transaction, false);
			if (group.isNew()) {
				transaction.remoteHandleNewAcquirable(group);
				return group;
			} else {
				throw new CacheStateError();
			}
		}

		private final void collectData(final CacheClusterInitializeTask task,
				final Transaction transaction) {
			if (this.groupCount > 0) {
				for (CacheGroup<?, ?, ?> existGroup : this.groupMap) {
					while (existGroup != null) {
						if (existGroup.inCluster) {
							if (Cache.IN_DEBUG_MODE && transaction.kind == TransactionKind.REMOTE) {
								long startTime = System.nanoTime();
								existGroup.collectGroupData(task, transaction);
								long estimatedTime = System.nanoTime() - startTime;
								String facade = existGroup.define.facadeClass.getName();
								synchronized (this) {
									ResourceInClusterSyncInfo info = syncInfoes.get(facade);
									if (info == null) {
										syncInfoes.put(facade, new ResourceInClusterSyncInfo(facade, estimatedTime));
									} else {
										info.setTimes(info.getTimes() + estimatedTime);
									}
								}
							} else {
								existGroup.collectGroupData(task, transaction);
							}
						}
						existGroup = existGroup.nextInSpace;
					}
				}
			}
		}

		final void resolveGroup(final CacheGroup<?, ?, ?> group,
				final Transaction transaction) {
			group.resolve(transaction);
		}

		final void disposeGroup(final CacheGroup<?, ?, ?> group,
				final Transaction transaction) {
			this.writeLock.lock();
			try {
				try {
					final int hashIndex = HashUtil.hash(group.define.facadeClass) & this.hashMask;
					boolean found = false;
					for (CacheGroup<?, ?, ?> existGroup = this.groupMap[hashIndex], lastExistGroup = null; existGroup != null; lastExistGroup = existGroup, existGroup = existGroup.nextInSpace) {
						if (existGroup == group) {
							if (lastExistGroup == null) {
								this.groupMap[hashIndex] = group.nextInSpace;
							} else {
								lastExistGroup.nextInSpace = group.nextInSpace;
							}
							group.nextInSpace = null;
							this.groupCount--;
							found = true;
							break;
						}
					}
					if (!found) {
						throw new IllegalStateException("资源服务：销毁资源组时发生错误，资源组[" + group + "]所属空间[" + this.identifier + "]中并不包含该资源组对象");
					}
				} finally {
					this.updateVersion();
				}
			} finally {
				this.writeLock.unlock();
			}
			group.dispose(transaction);
		}

		private final void updateVersion() {
			if (this.version == Byte.MAX_VALUE) {
				this.version = Byte.MIN_VALUE;
			} else {
				this.version++;
			}
		}

		private final <TFacade, TImplement extends TFacade, TKeysHolder> CacheGroup<TFacade, TImplement, TKeysHolder> internalCreateGroup(
				final CacheDefine<TFacade, TImplement, TKeysHolder> define,
				final String title, final Long fixLongIdentifier,
				final Byte fixInitializeState,
				final Throwable initializeException,
				final Transaction transaction, final boolean needLockCache) {
			final Class<?> facadeClass = define.facadeClass;
			final int facadeClassHashCode = HashUtil.hash(facadeClass);
			CacheGroup<?, ?, ?> existGroup;
			int hashIndex;
			this.readLock.lock();
			try {
				if (this.groupMap != null) {
					hashIndex = facadeClassHashCode & (this.hashMask);
					existGroup = this.groupMap[hashIndex];
					while (existGroup != null) {
						if (existGroup.define.facadeClass.equals(facadeClass)) {
							return (CacheGroup<TFacade, TImplement, TKeysHolder>) existGroup;
						} else {
							existGroup = existGroup.nextInSpace;
						}
					}
				}
			} finally {
				this.readLock.unlock();
			}
			if (needLockCache) {
				transaction.handleAcquirable(Cache.this, AcquireFor.MODIFY_ITEMS);
			}
			this.writeLock.lock();
			try {
				try {
					if (this.groupMap != null) {
						hashIndex = facadeClassHashCode & (this.hashMask);
						existGroup = this.groupMap[hashIndex];
						while (existGroup != null) {
							if (existGroup.define.facadeClass.equals(facadeClass)) {
								return (CacheGroup<TFacade, TImplement, TKeysHolder>) existGroup;
							} else {
								existGroup = existGroup.nextInSpace;
							}
						}
					} else {
						this.groupMap = new CacheGroup<?, ?, ?>[Cache.DEFAULT_GROUP_MAP_CAPACITY];
						this.hashMask = Cache.DEFAULT_GROUP_MAP_CAPACITY - 1;
					}
					if (this.groupMap.length == this.groupCount) {
						final int oldCapacity = this.groupMap.length;
						final int newCapacity = oldCapacity * 2;
						final int newHashMask = newCapacity - 1;
						final CacheGroup<?, ?, ?>[] newGroupMap = new CacheGroup<?, ?, ?>[newCapacity];
						for (int index = 0; index < oldCapacity; index++) {
							existGroup = this.groupMap[index];
							while (existGroup != null) {
								final int newHashIndex = HashUtil.hash(existGroup.define.facadeClass) & newHashMask;
								final CacheGroup<?, ?, ?> tempGroup = existGroup;
								existGroup = existGroup.nextInSpace;
								tempGroup.nextInSpace = newGroupMap[newHashIndex];
								newGroupMap[newHashIndex] = tempGroup;
							}
						}
						this.groupMap = newGroupMap;
						this.hashMask = newHashMask;
					}
					final CacheGroup<TFacade, TImplement, TKeysHolder> newGroup = define.newGroup(this, title, fixLongIdentifier, fixInitializeState, initializeException);
					hashIndex = facadeClassHashCode & this.hashMask;
					newGroup.nextInSpace = this.groupMap[hashIndex];
					this.groupMap[hashIndex] = newGroup;
					this.groupCount++;
					return newGroup;
				} finally {
					this.updateVersion();
				}
			} finally {
				this.writeLock.unlock();
			}
		}

		private final void initializeAllCacheGroup(final Transaction transaction) {
			if (this.groupCount > 0) {
				for (CacheGroup<?, ?, ?> group : this.groupMap) {
					while (group != null) {
						if (group.define.kind.inCluster) {
							group.ensureInitialized(transaction);
						}
						group = group.nextInSpace;
					}
				}
			}
		}

		private final ReentrantReadWriteLock.ReadLock readLock;

		private final ReentrantReadWriteLock.WriteLock writeLock;

		private volatile int groupCount;

		private volatile CacheGroup<?, ?, ?>[] groupMap;

		private volatile int hashMask;

		private volatile byte version;

		private volatile CustomGroupSpace next;

		@Override
		public final String toString() {
			return this.identifier.toString();
		}
	}

	final void initializeAllCacheGroup(final Transaction transaction) {
		this.defaultGroupSpace.initializeAllCacheGroup(transaction);
		if (this.spaceCount > 0) {
			for (CustomGroupSpace space : this.spaceMap) {
				while (space != null) {
					space.initializeAllCacheGroup(transaction);
					space = space.next;
				}
			}
		}
	}

	final HashMap<Class<?>, ResourceServiceBase<?, ?, ?>> getResourceServiceContainer() {
		return this.resourceServiceContainer;
	}
	
	final CustomGroupSpace[] getCustomGroupSpace() {
		return this.spaceMap;
	}
}
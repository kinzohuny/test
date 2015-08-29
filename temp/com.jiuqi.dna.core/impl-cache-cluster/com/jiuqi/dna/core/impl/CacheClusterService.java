package com.jiuqi.dna.core.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.SiteState;
import com.jiuqi.dna.core.da.ORMAccessor;
import com.jiuqi.dna.core.def.query.DeleteStatementDeclare;
import com.jiuqi.dna.core.impl.CacheDefine.KeyDefine;
import com.jiuqi.dna.core.impl.CacheDefine.PutPolicy;
import com.jiuqi.dna.core.impl.NUnserializer.ObjectTypeQuerier;
import com.jiuqi.dna.core.impl.QuirkCacheClusterDataCollectTask.CreateGroupData2;
import com.jiuqi.dna.core.impl.QuirkCacheClusterDataCollectTask.CreateHolderData2;
import com.jiuqi.dna.core.impl.QuirkCacheClusterDataCollectTask.ModifyHolderData2;
import com.jiuqi.dna.core.impl.QuirkCacheClusterDataCollectTask.ReferenceData2;
import com.jiuqi.dna.core.impl.QuirkCacheClusterDataCollectTask.ReloadAuthorityData2;
import com.jiuqi.dna.core.impl.QuirkCacheClusterDataCollectTask.RemoveGroupData2;
import com.jiuqi.dna.core.impl.QuirkCacheClusterDataCollectTask.RemoveHolderData2;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.log.Logger;
import com.jiuqi.dna.core.resource.CategorialResourceModifier;
import com.jiuqi.dna.core.service.AsyncInfo;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.service.Publish.Mode;
import com.jiuqi.dna.core.spi.application.Application;
import com.jiuqi.dna.core.type.GUID;

final class CacheClusterService extends ServiceBase<ContextImpl<?, ?, ?>> {

	private static final String TITLE;

	private final ORM_CoreClusterData ormCoreClusterData;
	private final TD_CoreClusterData tdCoreClusterData;
	private ORM_CoreResourceLog ormCoreResourceLog;

	private int synchronzieTransactionId; // 需要保证只有一个同步事务

	static {
		TITLE = "缓存集群服务";
	}

	protected CacheClusterService(ORM_CoreClusterData ormCoreClusterData,
			TD_CoreClusterData tdCoreClusterData,
			ORM_CoreResourceLog ormCoreResourceLog) {
		super(TITLE);
		this.ormCoreClusterData = ormCoreClusterData;
		this.tdCoreClusterData = tdCoreClusterData;
		this.ormCoreResourceLog = ormCoreResourceLog;
	}

	@Override
	protected void init(Context context) throws Throwable {
		super.init(context);
		if (this.site.application.netNodeManager.thisCluster.multiNodes) {
			this.startSynchronizMonitor((ContextImpl<?, ?, ?>) context);
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class CacheClusterInitializeTaskHandler extends
			TaskMethodHandler<CacheClusterInitializeTask, None> {

		protected CacheClusterInitializeTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final CacheClusterInitializeTask task) throws Throwable {
			CacheClusterService.this.site.cache.initializeCache(task, context.transaction);
		}

	}

	@Publish(Mode.SITE_PUBLIC)
	final class CacheClusterSynchronizeTaskHandler extends
			TaskMethodHandler<CacheClusterSynchronizeTask, None> {

		protected CacheClusterSynchronizeTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final CacheClusterSynchronizeTask task) throws Throwable {
			context.transaction.remotePrepare(task);
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class WeakCacheClusterNotifyTaskHandler extends
			TaskMethodHandler<QuirkCacheClusterNotifyTask, None> {

		protected WeakCacheClusterNotifyTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final QuirkCacheClusterNotifyTask task) throws Throwable {
			if (CacheClusterService.this.synchronzieTransactionId == context.transaction.id) {
				return; // 避免循环
			}
			// serialize
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			SafeDataFragmentImpl outFragment = new SafeDataFragmentImpl(1024);
			NSerializer serializer = NSerializer.getRemoteCompatibleFactory(NSerializer.getHighestSerializeVersion()).newNSerializer();
			boolean end = serializer.serializeStart(task.synchronizeData, outFragment, true);
			baos.write(outFragment.getBytes(), 0, outFragment.getPosition());
			while (!end) {
				outFragment.setPosition(0);
				end = serializer.serializeRest(outFragment);
				baos.write(outFragment.getBytes(), 0, outFragment.getPosition());
			}
			//
			ORMAccessor<QuirkCacheClusterData> ormAccessor = context.newORMAccessor(CacheClusterService.this.ormCoreClusterData);
			for (NetNodeImpl node : task.nodeList) {
				QuirkCacheClusterData cacheData = new QuirkCacheClusterData();
				cacheData.setId(GUID.randomID());
				cacheData.setIndex(node.channel.getRemoteNodeIndex());
				cacheData.setFrom(context.transaction.getNodeIndex());
				cacheData.setTime(System.nanoTime());
				cacheData.setData(baos.toByteArray());
				ormAccessor.insert(cacheData);
			}
		}
	}

	@Publish
	final class QuirkCacheResourceLogTaskHander extends
			TaskMethodHandler<QuirkCacheResourceLogTask, None> {

		protected QuirkCacheResourceLogTaskHander() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				QuirkCacheResourceLogTask task) throws Throwable {

			if (CacheClusterService.this.synchronzieTransactionId == context.transaction.id) {
				return; // 避免重复记录
			}
			if (!task.haveData) {
				return;
			}
			ORMAccessorProxy<QuirkCacheResourceLog> orm = context.newORMAccessor(CacheClusterService.this.ormCoreResourceLog);
			try {
				// 新增资源
				if (task.getCreateHolderDataList2() != null) {
					for (CreateHolderData2<?, ?, ?> dataHolder : task.getCreateHolderDataList2()) {
						String facade = dataHolder.facadeClass.getName();
						CacheClusterService.this.doPersistent(orm, facade);
					}
				}
				// 修改资源
				if (task.getModifyHolderData2List2() != null) {
					for (ModifyHolderData2<?, ?, ?> dataHolder : task.getModifyHolderData2List2()) {
						String facade = dataHolder.facadeClass.getName();
						CacheClusterService.this.doPersistent(orm, facade);
					}
				}

				// 删除资源
				if (task.getRemoveHolderDataList2() != null) {
					for (RemoveHolderData2<?, ?> dataHolder : task.getRemoveHolderDataList2()) {
						String facade = dataHolder.facadeClass.getName();
						CacheClusterService.this.doPersistent(orm, facade);
					}
				}

				// 修改资源引用
				if (task.getCreateReferenceDataList2() != null) {
					for (ReferenceData2 ref : task.getCreateReferenceDataList2()) {
						String facade1 = ref.facadeClass1.getName();
						CacheClusterService.this.doPersistent(orm, facade1);
						String facade2 = ref.facadeClass2.getName();
						CacheClusterService.this.doPersistent(orm, facade2);
					}
				}
				if (task.getRemoveReferenceDataList2() != null) {
					for (ReferenceData2 ref : task.getRemoveReferenceDataList2()) {
						String facade1 = ref.facadeClass1.getName();
						CacheClusterService.this.doPersistent(orm, facade1);
						String facade2 = ref.facadeClass2.getName();
						CacheClusterService.this.doPersistent(orm, facade2);
					}
				}

				// 修改资源树形结构
				if (task.getCreateTreeNodeDataList2() != null) {
					for (ReferenceData2 ref : task.getCreateTreeNodeDataList2()) {
						String facade1 = ref.facadeClass1.getName();
						CacheClusterService.this.doPersistent(orm, facade1);
						String facade2 = ref.facadeClass2.getName();
						CacheClusterService.this.doPersistent(orm, facade2);
					}
				}

			} finally {
				orm.unuse();
			}
		}

	}

	private final void doPersistent(
			ORMAccessorProxy<QuirkCacheResourceLog> orm, String facade) {
		if (orm == null || facade == null || facade.length() == 0) {
			return;
		}
		QuirkCacheResourceLog log = orm.findByPKey(facade);
		if (log != null) {
			log.setModifyTimes(log.getModifyTimes() + 1);
			orm.update(log);
		} else {
			log = new QuirkCacheResourceLog();
			log.setId(GUID.randomID());
			log.setFacade(facade);
			log.setModifyTimes(1);
			log.setQuirk(false);
			orm.insert(log);
		}
	}

	@Publish
	final class WeakCacheClusterQueryTaskHandler extends
			TaskMethodHandler<QuirkCacheClusterQueryTask, None> {

		protected WeakCacheClusterQueryTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final QuirkCacheClusterQueryTask task) throws Throwable {
			// 站点可用时，查询是否有同步数据需要处理
			if (context.getSiteState() == SiteState.ACTIVE) {
				ORMAccessor<QuirkCacheClusterData> ormAccessor = context.newORMAccessor(CacheClusterService.this.ormCoreClusterData);
				List<QuirkCacheClusterData> cacheDataList = ormAccessor.fetch(context.transaction.getNodeIndex());
				if (cacheDataList != null && cacheDataList.size() > 0) {
					context.waitFor(context.asyncHandle(new QuirkCacheClusterSynchronizeTask(cacheDataList)));
					ormAccessor.delete(cacheDataList);
				}
			}
		}
	}

	@Publish
	final class WeakCacheClusterProcessTaskHandler extends
			TaskMethodHandler<QuirkCacheClusterSynchronizeTask, None> {

		protected WeakCacheClusterProcessTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final QuirkCacheClusterSynchronizeTask task) throws Throwable {
			CacheClusterService.this.synchronzieTransactionId = context.transaction.id;
			for (QuirkCacheClusterData cacheData : task.cacheDataList) {
				try {
					this.processData(context, cacheData);
				} catch (Throwable t) {
					String msg = "同步Quirk缓存变化失败";
					Logger logger = DNALogManager.getLogger("core/cluster/quirk");
					logger.logFatal(null, msg, t, false);
					if (Application.IN_DEBUG_MODE) {
						System.err.println(msg);
						t.printStackTrace();
					}
				}
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private final void processData(final ContextImpl<?, ?, ?> context,
				QuirkCacheClusterData cacheData) throws Throwable {
			//
			byte[] data = cacheData.getData();
			SafeDataFragmentImpl inFragment = new SafeDataFragmentImpl(data.length);
			System.arraycopy(data, 0, inFragment.getBytes(), 0, data.length);
			NUnserializer unserializer = NUnserializer.newUnserializer(NUnserializer.getHighestSerializeVersion(), ObjectTypeQuerier.staticObjectTypeQuerier);
			try {
				unserializer.unserializeStart(inFragment, null);
			} catch (Throwable t) {
				String msg = "反序列化Quirk缓存变化数据失败";
				Logger logger = DNALogManager.getLogger("core/cluster/quirk");
				logger.logFatal(null, msg, t, false);
				if (Application.IN_DEBUG_MODE) {
					System.err.println(msg);
					t.printStackTrace();
				}
				throw t;
			}
			QuirkCacheClusterDataCollectTask synchronizeData = (QuirkCacheClusterDataCollectTask) unserializer.getUnserialzedObject();

			//
			List<RemoveGroupData2> removeGroupData2List2 = synchronizeData.getRemoveGroupData2List2();
			if (removeGroupData2List2 != null) {
				for (RemoveGroupData2 item : removeGroupData2List2) {
					ResourceServiceBase resourceService = context.transaction.site.findResourceService(item.facadeClass, InvokeeQueryMode.IN_SITE);
					SpaceNode old = resourceService.updateContextSpace(context);
					try {
						CacheGroup group = context.findCacheGroup(item.facadeClass);
						resourceService.site.cache.localRemoveGroup(group.define, item.spaceIdentifer, context.transaction);
					} finally {
						if (old != null) {
							old.updateContextSpace(context);
						}
					}
				}
			}

			//
			List<CreateGroupData2> createGroupData2List2 = synchronizeData.getCreateGroupData2List2();
			if (createGroupData2List2 != null) {
				for (CreateGroupData2 item : createGroupData2List2) {
					ResourceServiceBase resourceService = context.transaction.site.findResourceService(item.facadeClass, InvokeeQueryMode.IN_SITE);
					SpaceNode old = resourceService.updateContextSpace(context);
					try {
						Cache cache = resourceService.site.cache;
						final CacheDefine<?, ?, ?> define = cache.findDefine(item.defineIdentifier);
						cache.localCreateGroup(item.spaceIdentifer, define, item.title, context.transaction);
					} finally {
						if (old != null) {
							old.updateContextSpace(context);
						}
					}

				}
			}

			//
			List<RemoveHolderData2<?, ?>> removeHolderDataList2 = synchronizeData.getRemoveHolderDataList2();
			if (removeHolderDataList2 != null) {
				for (RemoveHolderData2<?, ?> item : removeHolderDataList2) {
					ResourceServiceBase resourceService = context.transaction.site.findResourceService(item.facadeClass, InvokeeQueryMode.IN_SITE);
					SpaceNode old = resourceService.updateContextSpace(context);
					try {
						CacheGroup group = this.findCacheGroup(context, item.facadeClass, item.groupIdentifier);
						KeyDefine[] keyDefines = group.define.keyDefines;
						Object key1 = null;
						Object key2 = null;
						Object key3 = null;
						if (keyDefines != null && keyDefines.length > 0) {
							key1 = keyDefines[0].getKeyValue1(item.keysHolder);
							key2 = keyDefines[0].getKeyValue2(item.keysHolder);
							key3 = keyDefines[0].getKeyValue3(item.keysHolder);
						}
						group.localTryRemoveHolder(key1 != null ? key1.getClass() : null, key2 != null ? key2.getClass() : null, key3 != null ? key3.getClass() : null, key1, key2, key3, context.transaction);
					} finally {
						if (old != null) {
							old.updateContextSpace(context);
						}
					}
				}

			}
			//
			List<ModifyHolderData2<?, ?, ?>> modifyHolderDataList2 = synchronizeData.getModifyHolderData2List2();
			if (modifyHolderDataList2 != null) {
				for (ModifyHolderData2<?, ?, ?> item : modifyHolderDataList2) {
					ResourceServiceBase resourceService = context.transaction.site.findResourceService(item.facadeClass, InvokeeQueryMode.IN_SITE);
					SpaceNode old = resourceService.updateContextSpace(context);
					try {
						CacheHolder<?, ?, ?> holder = this.modifyCacheHolder(context, item.facadeClass, item.groupIdentifier, item.keysHolder);
						if (holder != null) {
							holder.tryGetModifiableValue();
							holder.tryPostModifiedValueWithoutCheck(item.value, item.keysHolder, context.transaction);
							context.restoreCacheHolderUserData(resourceService, item.userData, item.value, item.keysHolder);
						} else {
							String msg = "找不到指定的CacheHolder[" + item.facadeClass.getName() + "," + item.groupIdentifier + "]";
							Throwable t = new IllegalStateException("");
							Logger logger = DNALogManager.getLogger("core/cluster/quirk");
							logger.logFatal(null, msg, t, false);
						}
					} finally {
						if (old != null) {
							old.updateContextSpace(context);
						}
					}
				}
			}
			//
			List<CreateHolderData2<?, ?, ?>> createHolderDataList2 = synchronizeData.getCreateHolderDataList2();
			if (createHolderDataList2 != null) {
				for (CreateHolderData2<?, ?, ?> item : createHolderDataList2) {
					ResourceServiceBase resourceService = context.transaction.site.findResourceService(item.facadeClass, InvokeeQueryMode.IN_SITE);
					SpaceNode old = resourceService.updateContextSpace(context);
					try {
						CacheGroup group = this.findCacheGroup(context, item.facadeClass, item.groupIdentifier);
						CacheHolder holder = group.localTryCreateHolder(item.value, item.keysHolder, PutPolicy.REPLACE, context.transaction);
						holder.restoreSerialUserData(item.userData, context);
					} finally {
						if (old != null) {
							old.updateContextSpace(context);
						}
					}
				}
			}
			//
			List<ReferenceData2> createTreeNodeDataList2 = synchronizeData.getCreateTreeNodeDataList2();
			if (createTreeNodeDataList2 != null) {
				for (ReferenceData2 item : createTreeNodeDataList2) {
					ResourceServiceBase resourceService = context.transaction.site.findResourceService(item.facadeClass2, InvokeeQueryMode.IN_SITE);
					SpaceNode old = resourceService.updateContextSpace(context);
					try {
						CacheHolder parent = null;
						if (item.facadeClass1 != null) {
							parent = this.findCacheHolder(context, item.facadeClass1, item.groupIdentifier1, item.keysHolder1);
						}
						CacheHolder child = this.findCacheHolder(context, item.facadeClass2, item.groupIdentifier2, item.keysHolder2);
						child.ownGroup.tryGetBindTree().localTryCreateNode(parent, child, context.transaction);
					} finally {
						if (old != null) {
							old.updateContextSpace(context);
						}
					}
				}
			}
			//
			List<ReferenceData2> removeReferenceDataList2 = synchronizeData.getRemoveReferenceDataList2();
			if (removeReferenceDataList2 != null) {
				for (ReferenceData2 item : removeReferenceDataList2) {
					ResourceServiceBase resourceService = context.transaction.site.findResourceService(item.facadeClass1, InvokeeQueryMode.IN_SITE);
					SpaceNode old = resourceService.updateContextSpace(context);
					try {
						CacheHolder ch1 = this.findCacheHolder(context, item.facadeClass1, item.groupIdentifier1, item.keysHolder1);
						CacheHolder ch2 = this.findCacheHolder(context, item.facadeClass2, item.groupIdentifier2, item.keysHolder2);
						if (ch1 != null && ch2 != null) {
							context.removeResourceReference(ch1, ch2);
						}
					} finally {
						if (old != null) {
							old.updateContextSpace(context);
						}
					}
				}
			}
			//
			List<ReferenceData2> createReferenceDataList2 = synchronizeData.getCreateReferenceDataList2();
			if (createReferenceDataList2 != null) {
				for (ReferenceData2 item : createReferenceDataList2) {
					ResourceServiceBase resourceService = context.transaction.site.findResourceService(item.facadeClass1, InvokeeQueryMode.IN_SITE);
					SpaceNode old = resourceService.updateContextSpace(context);
					try {
						context.putResourceReference(this.findCacheHolder(context, item.facadeClass1, item.groupIdentifier1, item.keysHolder1), this.findCacheHolder(context, item.facadeClass2, item.groupIdentifier2, item.keysHolder2));
					} finally {
						if (old != null) {
							old.updateContextSpace(context);
						}
					}
				}
			}
			//
			List<ReloadAuthorityData2<?, ?>> reloadAuthorityDataList = synchronizeData.getReloadAuthorityDataList2();
			if (reloadAuthorityDataList != null) {
				for (ReloadAuthorityData2<?, ?> item : reloadAuthorityDataList) {
					final CacheHolder holder = this.findCacheHolder(context, item.facadeClass, item.groupIdentifier, item.keysHolder);
					if (holder instanceof ActorCacheHolder) {
						((ActorCacheHolder<?, ?, ?>) holder).resetACL();
					} else if (holder instanceof IdentifyCacheHolder) {
						((IdentifyCacheHolder) holder).resetACL();
					}
				}
			}
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private CacheHolder<?, ?, ?> modifyCacheHolder(
				final ContextImpl context, Class facadeClass,
				Object groupIdentifier, Object keysHolder) {
			CacheGroup group = this.findCacheGroup(context, facadeClass, groupIdentifier);
			KeyDefine[] keyDefines = group.define.keyDefines;
			Object key1 = null;
			Object key2 = null;
			Object key3 = null;
			if (keyDefines != null) {
				for (KeyDefine keyDefine : keyDefines) {
					key1 = keyDefine.getKeyValue1(keysHolder);
					key2 = keyDefine.getKeyValue2(keysHolder);
					key3 = keyDefine.getKeyValue3(keysHolder);
					CacheHolder holder = group.localTryFindHolder(key1 != null ? key1.getClass() : null, key2 != null ? key2.getClass() : null, key3 != null ? key3.getClass() : null, key1, key2, key3, context.transaction);
					if (holder != null) {
						return holder;
					}
				}
			}
			return null;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private CacheHolder findCacheHolder(final ContextImpl context,
				Class facadeClass, Object groupIdentifier, Object keysHolder) {
			CacheGroup group = this.findCacheGroup(context, facadeClass, groupIdentifier);
			CategorialResourceModifier categorialResourceModifier = context.usingResourceCategory(groupIdentifier);
			if (group == null) {
				String msg = "找不到指定的CacheGroup[" + facadeClass.getName() + "," + groupIdentifier + "]";
				Throwable t = new IllegalStateException("");
				Logger logger = DNALogManager.getLogger("core/cluster/quirk");
				logger.logFatal(null, msg, t, false);
				return null;
			}
			KeyDefine[] keyDefines = group.define.keyDefines;
			Object key1 = null;
			Object key2 = null;
			Object key3 = null;
			if (keyDefines != null) {
				for (KeyDefine keyDefine : keyDefines) {
					key1 = keyDefine.getKeyValue1(keysHolder);
					key2 = keyDefine.getKeyValue2(keysHolder);
					key3 = keyDefine.getKeyValue3(keysHolder);
					CacheHolder holder = null;
					if (categorialResourceModifier instanceof CategorialResContextAdapter) {
						holder = ((CategorialResContextAdapter) categorialResourceModifier).internalFindResourceToken(facadeClass, key1 != null ? key1.getClass() : null, key2 != null ? key2.getClass() : null, key3 != null ? key3.getClass() : null, key1, key2, key3);
					} else {
						holder = context.internalFindResourceToken(facadeClass, key1 != null ? key1.getClass() : null, key2 != null ? key2.getClass() : null, key3 != null ? key3.getClass() : null, key1, key2, key3);
					}
					if (holder != null) {
						return holder;
					}
				}
			}
			return null;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private CacheGroup findCacheGroup(ContextImpl<?, ?, ?> context,
				Class facadeClass, Object groupIdentifier) {
			CategorialResourceModifier categorialResourceModifier = context.usingResourceCategory(groupIdentifier);
			if (categorialResourceModifier instanceof CategorialResContextAdapter) {
				return ((CategorialResContextAdapter) categorialResourceModifier).findCacheGroup(facadeClass);
			} else {
				return context.findCacheGroup(facadeClass);
			}
		}
	}

	private final void startSynchronizMonitor(ContextImpl<?, ?, ?> context) {
		//
		DeleteStatementDeclare deleteStatementDeclare = context.newDeleteStatement(this.tdCoreClusterData);
		deleteStatementDeclare.setCondition(deleteStatementDeclare.expOf(this.tdCoreClusterData.f_index).xEq(deleteStatementDeclare.newArgument(this.tdCoreClusterData.f_index)));
		context.executeUpdate(deleteStatementDeclare, new Integer(context.transaction.getNodeIndex()));
		//
		AsyncInfo asyncInfo = new AsyncInfo();
		asyncInfo.setPeiod(3 * 1000);
		context.asyncHandle(new QuirkCacheClusterQueryTask(), asyncInfo);
	}

}

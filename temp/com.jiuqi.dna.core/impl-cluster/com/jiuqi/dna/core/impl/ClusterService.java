package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.log.Logger;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.service.Publish.Mode;
import com.jiuqi.dna.core.type.GUID;

final class ClusterService extends ServiceBase<ContextImpl<?, ?, ?>> {

	protected ClusterService() {
		super("集群服务");
	}

	@Publish(Mode.SITE_PUBLIC)
	final class SiteRequestSynchronizeTaskHandler extends
			TaskMethodHandler<SiteRequestSynchronizeTask, None> {
		SiteRequestSynchronizeTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final SiteRequestSynchronizeTask task) throws Throwable {
			final Site site = ClusterService.this.site;
			NetNodeImpl target = context.remoteCaller;
			NetClusterImpl c = site.getNetCluster();
			if (target.cluster != c || !site.shared) {
				return;
			}
			int nodeIndex = target.channel.getRemoteNodeIndex();
			GUID nodeID = target.channel.getRemoteNodeID();
			String msg = String.format("集群：远程节点请求加入集群，索引号[%d]ID[%s]", nodeIndex, nodeID);
			Logger logger = DNALogManager.getLogger("core/cluster");
			logger.logInfo(null, msg, false);
			System.out.println(msg);
			// 锁站点
			site.acquireLock();
			try {
				SiteStateSyncTask stateSyncTask = new SiteStateSyncTask(site.starter.getLockVer());
				site.cache.collectData(stateSyncTask.cacheInitTask, context.transaction);
				// 同步资源，不新建事务，使用当前事务
				context.remoteCaller.newSession(site).newRemoteTransactionRequest(stateSyncTask, None.NONE, context.transaction, new RSIPropertiesSetter() {
					public void setProperties(RSIPropertySet props) {
						props.setProp(RemoteInvokeData.PROP_NORETURN, Boolean.TRUE);
					}
				}).internalWaitStop(0);// 可能抛出异常
				// 通知节点加入集群
				synchronized (c) {
					ArrayList<NetRequestImpl> arr = new ArrayList<NetRequestImpl>();
					for (NetNodeImpl n = c.getFirstNetNode(); n != null; n = n.getNextNodeInCluster()) {
						if (n.getState() == NetNodeImpl.STATE_READY) {
							arr.add(n.newSession(site).newRemoteTransactionRequest(new SiteStartupTask(nodeID), None.NONE, context.transaction));
						}
					}
					for (NetRequestImpl w : arr) {
						w.internalWaitStop(0);
					}
					target.setState(NetNodeImpl.STATE_READY);
				}
				msg = String.format("集群：远程节点加入集群成功，索引号[%d]ID[%s]", nodeIndex, nodeID);
				logger.logInfo(null, msg, false);
				System.out.println(msg);
			} catch (Exception e) {
				msg = String.format("集群：远程节点加入集群时发生异常，索引号[%d]ID[%s]", nodeIndex, nodeID);
				logger.logFatal(null, msg, e, false);
				System.out.println(msg);
				e.printStackTrace();
			} finally {
				// 解锁
				site.releaseLock();
			}
		}
	}

	@StructClass
	final static class SiteStateSyncTask extends SimpleTask {
		final int lockVer;
		final CacheClusterInitializeTask cacheInitTask = new CacheClusterInitializeTask();

		public SiteStateSyncTask(int lockVer) {
			this.lockVer = lockVer;
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class SiteStateSyncTaskHandler extends
			TaskMethodHandler<SiteStateSyncTask, None> {
		public SiteStateSyncTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				SiteStateSyncTask task) throws Throwable {
			context.handle(task.cacheInitTask);
			ClusterService.this.site.starter.putLock(context.remoteCaller, task.lockVer);
		}
	}

	@StructClass
	final static class SiteStartupTask extends SimpleTask {
		final GUID nodeID;

		public SiteStartupTask(GUID nodeID) {
			this.nodeID = nodeID;
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class SiteStartupTaskHandler extends
			TaskMethodHandler<SiteStartupTask, None> {
		public SiteStartupTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context, SiteStartupTask task)
				throws Throwable {
			final Site site = ClusterService.this.site;
			if (site.shared) {
				NetSelfClusterImpl c = site.getNetCluster();
				synchronized (c) {
					for (NetNodeImpl n = c.getFirstNetNode(); n != null; n = n.getNextNodeInCluster()) {
						if (n.channel.getRemoteNodeID().equals(task.nodeID)) {
							n.setState(NetNodeImpl.STATE_READY);
							String msg = String.format("集群：远程节点加入集群，索引号[%d]ID[%s]", n.channel.getRemoteNodeIndex(), task.nodeID);
							DNALogManager.getLogger("core/cluster").logInfo(null, msg, false);
							System.out.println(msg);
							break;
						}
					}
				}
			}
		}
	}
}

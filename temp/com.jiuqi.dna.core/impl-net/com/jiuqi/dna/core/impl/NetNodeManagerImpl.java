package com.jiuqi.dna.core.impl;

import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.type.GUID;

/**
 * 网络节点管理器
 * 
 * @author gaojingxin
 * 
 */
final class NetNodeManagerImpl {

	final NetSelfClusterImpl thisCluster;
	final ApplicationImpl application;
	final NetChannelManagerImpl netChannelManager;
	private HashMap<NetChannelImpl, NetNodeImpl> nodes = new HashMap<NetChannelImpl, NetNodeImpl>();
	private HashMap<GUID, NetClusterImpl> clusters = new HashMap<GUID, NetClusterImpl>();

	final NetNodeToken queryRemoteNodeID(URL host, Proxy proxy) {
		NetNodeToken cached = this.netChannelManager.findCachedToken(host);
		if (cached != null) {
			return cached;
		}
		return this.netChannelManager.queryRemoteNodeInfo(host, proxy)[0];
	}

	private final NetClusterImpl ensureCluster(GUID clusterID) {
		if (clusterID == null) {
			throw new NullArgumentException("clusterID");
		}
		if (clusterID.equals(this.thisCluster.clusterID)) {
			return this.thisCluster;
		}
		synchronized (this.clusters) {
			NetClusterImpl cluster = this.clusters.get(clusterID);
			if (cluster == null) {
				cluster = new NetClusterImpl(clusterID);
				this.clusters.put(clusterID, cluster);
			}
			return cluster;
		}
	}

	final NetNodeImpl ensureNetNode(NetNodeToken remoteNodeInfo) {
		return this.ensureNetNode(this.netChannelManager.ensureChannel(remoteNodeInfo));
	}

	final NetNodeImpl ensureNetNode(NetChannelImpl channel) {
		if (channel == null) {
			throw new NullArgumentException("channel");
		}
		NetNodeImpl node;
		NetChannelImpl oldChannel = null;
		final GUID clusterID = channel.getRemoteAppID();
		final int nodeIndex = channel.getRemoteNodeIndex();
		synchronized (this.nodes) {
			node = this.nodes.get(channel);
			if (node != null) {
				return node;
			}
			NetClusterImpl cluster = this.ensureCluster(clusterID);
			synchronized (cluster) {
				for (NetNodeImpl n = cluster.getFirstNetNode(); n != null; n = n.getNextNodeInCluster()) {
					if (n.channel.getRemoteNodeIndex() == nodeIndex) {
						n.setState(NetNodeImpl.STATE_DISABLED);
						oldChannel = n.channel;
						break;
					}
				}
				node = cluster.createNode(this, channel);
			}
			this.nodes.put(channel, node);
		}
		if (oldChannel != null) {
			oldChannel.unuse();
		}
		return node;
	}

	public final NetNodeToken[] queryNetNodeInfo(URL host, Proxy proxy) {
		return this.netChannelManager.queryRemoteNodeInfo(host, proxy);
	}

	private final AtomicInteger requestIDSeed = new AtomicInteger();

	final int newRequestID() {
		return this.requestIDSeed.incrementAndGet();
	}

	private final void onChannelDisabled(NetChannelImpl channel) {
		NetNodeImpl node;
		synchronized (this.nodes) {
			// 从manager上移除node
			node = this.nodes.remove(channel);
			if (node == null) {
				return;
			}
			synchronized (this.clusters) {
				// 从cluster上移除node
				NetClusterImpl cluster = node.cluster;
				synchronized (cluster) {
					node.setState(NetNodeImpl.STATE_DISABLED);
					cluster.removeNode(node);
					// 从manager上移除cluster
					if (!cluster.haveRemoteNode() && cluster != this.thisCluster) {
						this.clusters.remove(cluster.clusterID);
					}
				}
			}
		}
		// 销毁node
		node.dispose();
		// 通知站点
		this.application.getDefaultSite().onNetNodeDisposed(node);
	}

	public NetNodeManagerImpl(NetChannelManagerImpl netChannelManager,
			final NetSelfClusterImpl thisCluster, SXElement config) {
		if (netChannelManager == null) {
			throw new NullArgumentException("netChannelManager");
		}
		if (thisCluster == null) {
			throw new NullArgumentException("thisCluster");
		}
		this.application = netChannelManager.application;
		this.thisCluster = thisCluster;
		this.netChannelManager = netChannelManager;
		this.clusters.put(this.thisCluster.clusterID, this.thisCluster);
	}

	private boolean isActive;

	final boolean isActive() {
		return this.isActive;
	}

	final void active() {
		if (this.isActive) {
			throw new IllegalStateException();
		}
		this.netChannelManager.setNetIOHandler(new DataPackageReceiver() {
			public void channelDisabled(NetChannelImpl channel) {
				NetNodeManagerImpl.this.onChannelDisabled(channel);
			}

			public void packageArriving(NetChannelImpl channel,
					DataInputFragment fragment,
					NetPackageReceivingStarter starter) throws Throwable {
				switch (fragment.readByte()) {
				case INetPackageSign.REQUEST_PACKAGE:
					ensureNetNode(channel).onRequestPackageArriving(fragment, starter);
					break;
				case INetPackageSign.TYPE_PACKAGE:
					ensureNetNode(channel).onTypePackageArriving(starter);
					break;
				case INetPackageSign.SITE_PACKAGE:
					SiteStarter.startReceivingPackage(starter, ensureNetNode(channel), fragment);
					break;
				case INetPackageSign.TRANSACTION_PACKAGE:
					Transaction.startReceivingPackage(starter, ensureNetNode(channel), fragment);
					break;
				}
			}
		});
		this.isActive = true;
		this.thisCluster.initClusterNodes();
	}

	// ===========================以下集群相关=============================================

	final static String xml_element_net = "net";
}

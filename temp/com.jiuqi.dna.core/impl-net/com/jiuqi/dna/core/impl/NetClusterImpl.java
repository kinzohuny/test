package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.GUID;

/**
 * 网络集群节点
 * 
 * @author gaojingxin
 * 
 */
class NetClusterImpl {

	/**
	 * 节点掩码的长度，该长度决定了集群节点的最大数目(2^NODE_INDEX_LEN - 1)
	 */
	public static final int NODE_INDEX_LEN = 5;
	public static final int DEFAULT_NODE_INDEX = 1;
	public static final int MIN_NODE_INDEX = 1;
	public static final int MAX_NODE_INDEX = (1 << NODE_INDEX_LEN) - 1;
	public static final int MAX_NODE_COUNT = MAX_NODE_INDEX - MIN_NODE_INDEX + 1;

	final GUID clusterID;
	private NetNodeImpl first;

	NetClusterImpl(GUID clusterID) {
		if (clusterID == null) {
			throw new NullArgumentException("clusterID");
		}
		this.clusterID = clusterID;
	}

	/**
	 * 获取节点列表中的首个元素，非线程安全方法
	 */
	final NetNodeImpl getFirstNetNode() {
		return this.first;
	}

	/**
	 * 新建一个节点并放入列表中，非线程安全方法
	 */
	NetNodeImpl createNode(NetNodeManagerImpl owner, NetChannelImpl channel) {
		return this.first = new NetNodeImpl(owner, this, channel, this.first);
	}

	/**
	 * 从列表中删除一个节点，非线程安全方法
	 */
	void removeNode(NetNodeImpl node) {
		if (node == null) {
			throw new NullArgumentException("node");
		}
		if (node.cluster != this) {
			throw new IllegalArgumentException();
		}
		if (node == this.first) {
			this.first = node.getNextNodeInCluster();
		} else {
			for (NetNodeImpl n = this.first; n != null; n = n.getNextNodeInCluster()) {
				if (n.getNextNodeInCluster() == node) {
					n.setNextNodeInCluster(node.getNextNodeInCluster());
					break;
				}
			}
		}
	}

	/**
	 * 检查是否有节点，非线程安全方法
	 */
	final boolean haveRemoteNode() {
		return this.first != null;
	}

	static final void checkNetNodeIndex(final int index) {
		if (index < NetClusterImpl.MIN_NODE_INDEX || index > NetClusterImpl.MAX_NODE_INDEX) {
			throw new UnsupportedOperationException("集群节点索引号[" + index + "]有误。[" + NetClusterImpl.MIN_NODE_INDEX + ".." + NetClusterImpl.MAX_NODE_INDEX + "]");
		}
	}

}

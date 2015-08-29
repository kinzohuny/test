package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.GUID;

/**
 * ���缯Ⱥ�ڵ�
 * 
 * @author gaojingxin
 * 
 */
class NetClusterImpl {

	/**
	 * �ڵ�����ĳ��ȣ��ó��Ⱦ����˼�Ⱥ�ڵ�������Ŀ(2^NODE_INDEX_LEN - 1)
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
	 * ��ȡ�ڵ��б��е��׸�Ԫ�أ����̰߳�ȫ����
	 */
	final NetNodeImpl getFirstNetNode() {
		return this.first;
	}

	/**
	 * �½�һ���ڵ㲢�����б��У����̰߳�ȫ����
	 */
	NetNodeImpl createNode(NetNodeManagerImpl owner, NetChannelImpl channel) {
		return this.first = new NetNodeImpl(owner, this, channel, this.first);
	}

	/**
	 * ���б���ɾ��һ���ڵ㣬���̰߳�ȫ����
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
	 * ����Ƿ��нڵ㣬���̰߳�ȫ����
	 */
	final boolean haveRemoteNode() {
		return this.first != null;
	}

	static final void checkNetNodeIndex(final int index) {
		if (index < NetClusterImpl.MIN_NODE_INDEX || index > NetClusterImpl.MAX_NODE_INDEX) {
			throw new UnsupportedOperationException("��Ⱥ�ڵ�������[" + index + "]����[" + NetClusterImpl.MIN_NODE_INDEX + ".." + NetClusterImpl.MAX_NODE_INDEX + "]");
		}
	}

}

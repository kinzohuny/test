package com.jiuqi.dna.core.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.type.GUID;

final class NetSelfClusterImpl extends NetClusterImpl {

	final static String xml_attr_id = "id";
	final static String xml_element_cluster = "cluster";
	final static String xml_element_node = "node";
	final static String xml_attr_index = "index";
	final static String xml_attr_url = "url";
	final static long NET_TIMEOUT = 60000;

	final int thisClusterNodeIndex;
	final ApplicationImpl application;
	final boolean multiNodes;

	NetSelfClusterImpl(ApplicationImpl application, SXElement config) {
		super(getClusterID(config));
		this.application = application;
		this.config = config;
		if (config == null) {
			this.multiNodes = false;
			this.thisClusterNodeIndex = DEFAULT_NODE_INDEX;
		} else {
			this.multiNodes = true;
			this.thisClusterNodeIndex = config.getInt(xml_attr_index, DEFAULT_NODE_INDEX);
			checkNetNodeIndex(this.thisClusterNodeIndex);
		}
	}

	private static GUID getClusterID(SXElement config) {
		if (config == null) {
			return GUID.randomID();
		}
		final GUID clusterID = config.getGUID(xml_attr_id, GUID.randomID());
		ResolveHelper.logStartInfo("��ȺID��" + clusterID);
		return clusterID;
	}

	@Override
	final NetNodeImpl createNode(NetNodeManagerImpl owner,
			NetChannelImpl channel) {
		final NetNodeImpl netNode = super.createNode(owner, channel);
		channel.incKeepAlive(true);
		channel.setTimeout(NET_TIMEOUT);
		return netNode;
	}

	@Override
	final void removeNode(NetNodeImpl node) {
		super.removeNode(node);
		node.channel.incKeepAlive(false);
	}

	final SXElement config;

	final void initClusterNodes() {
		if (this.config != null) {
			synchronized (System.out) {
				ApplicationImpl.printDateTime(System.out);
				System.out.println(": DNA ����...��ʼ��ʼ����Ⱥ�ڵ�����");
			}
			for (SXElement config : this.config.getChildren(xml_element_node)) {
				final String host = config.getAttribute(xml_attr_url);
				if (host != null && host.length() > 0) {
					synchronized (System.out) {
						ApplicationImpl.printDateTime(System.out);
						System.out.print(": DNA ����...���ӵ���Ⱥ�ڵ�[" + host + "]...");
					}
					NetNodeManagerImpl netNodeMgr = this.application.netNodeManager;
					NetNodeToken[] nodes;
					try {
						nodes = netNodeMgr.queryNetNodeInfo(new URL(host), null);
					} catch (Throwable e) {
						System.out.println("ʧ��");
						continue;
					}
					try {
						if (nodes.length > 1) {
							throw new IllegalArgumentException(String.format("��Ⱥ���ڵ�[%d]��������Ϣ����ȷ����ַ[%s]����ָ��һ��dna���񣬶�ʵ��ָ��һ����Ⱥ", this.thisClusterNodeIndex, host));
						}
						NetNodeToken info = nodes[0];
						if (!this.application.localNodeID.equals(info.appID)) {
							NetNodeImpl node = netNodeMgr.ensureNetNode(info);
							if (!this.clusterID.equals(node.channel.getRemoteAppID())) {
								throw new IllegalArgumentException(String.format("��Ⱥ���ڵ�[%d]��������Ϣ����ȷ����ַ[%s]ָ��ڵ㲻�ڼ�Ⱥ��", this.thisClusterNodeIndex, host));
							}
						}
						System.out.println("�ɹ�");
					} catch (Throwable e) {
						e.printStackTrace();
						throw Utils.tryThrowException(e);
					}
				}
			}
			synchronized (System.out) {
				ApplicationImpl.printDateTime(System.out);
				System.out.println(": DNA ����...������ʼ����Ⱥ�ڵ�����");
			}
		}
	}

	// TODO ��ʱ�Ķ��ĵĴ���
	final Iterable<URL> otherNodes() {
		if (this.other == null) {
			synchronized (this) {
				if (this.other == null) {
					ArrayList<URL> other = new ArrayList<URL>();
					if (this.config != null) {
						for (SXElement one : this.config.getChildren(NetSelfClusterImpl.xml_element_node)) {
							final String host = one.getAttribute(NetSelfClusterImpl.xml_attr_url);
							if (host != null && host.length() > 0) {
								final URL url;
								try {
									url = new URL(host);
								} catch (MalformedURLException e) {
									continue;
								}
								other.add(url);
							}
						}
					}
					this.other = other;
				}
			}
		}
		return this.other;
	}

	private volatile ArrayList<URL> other;
}
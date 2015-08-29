package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.RemoteLoginInfo;
import com.jiuqi.dna.core.RemoteLoginLife;
import com.jiuqi.dna.core.exception.NullArgumentException;

@Deprecated
final class RemoteLoginInfoImpl implements RemoteLoginInfo {
	private volatile NetNodeInfo nodeInfo;
	final String user;
	final String password;
	final RemoteLoginLife life;

	RemoteLoginInfoImpl(NetNodeInfo nodeInfo, String user, String password,
			RemoteLoginLife life) {
		if (nodeInfo == null) {
			throw new NullArgumentException("nodeInfo");
		}
		if (life == null) {
			throw new NullArgumentException("life");
		}
		this.nodeInfo = nodeInfo;
		this.user = user;
		this.password = password;
		this.life = life;
	}

	synchronized final NetConnection getConnection() {
		NetNodeInfo cni = this.nodeInfo;
		NetConnection nc = cni.getConnection();
		if (nc == null || !nc.isConnected()) {
			cni = cni.reconnect();
			if (cni != this.nodeInfo) {
				this.nodeInfo = cni;
			}
			nc = cni.getConnection();
			// check again
			if (nc == null || !nc.isConnected()) {
				cni = cni.reconnect();
				if (cni != this.nodeInfo) {
					this.nodeInfo = cni;
				}
				nc = cni.getConnection();
				if (nc == null || !nc.isConnected()) {
					throw new UnsupportedOperationException("无法建立连接");
				}
			}
		}
		return nc;
	}

	final boolean isToSelf() {
		return this.nodeInfo.isToSelf();
	}

	public String getHost() {
		return this.nodeInfo.getHost();
	}

	public boolean isSecure() {
		return this.nodeInfo.isSecure();
	}

	public RemoteLoginLife getLife() {
		return this.life;
	}

	public int getPort() {
		return this.nodeInfo.getPort();
	}

	public String getUser() {
		return this.user;
	}
}

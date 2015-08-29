package com.jiuqi.dna.core.impl;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import com.jiuqi.dna.core.type.GUID;

final class NetNodeToken {

	final NetProtocolVersion ver;

	/**
	 * ��Ⱥ�ڵ���ţ���dna-server.xml������
	 */
	final int index;

	/**
	 * ��Application��localNodeID
	 */
	final GUID appID;

	/**
	 * NetConnectionListener��URL
	 */
	final URL ncl;

	final Proxy proxy;

	private int hashCode = 0;

	NetNodeToken(NetProtocolVersion ver, int index, GUID appID, URL ncl,
			Proxy proxy) {
		this.ver = ver;
		this.index = index;
		this.appID = appID;
		this.ncl = ncl;
		this.proxy = proxy == null ? Proxy.NO_PROXY : proxy;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof NetNodeToken) {
			NetNodeToken n = (NetNodeToken) obj;
			if (n.index == this.index && n.appID.equals(this.appID)) {
				if (n.ncl != null && this.ncl != null) {
					// �����������ӣ��Ƚϵ�ַ
					return n.ncl.equals(this.ncl);
				} else if (n.ncl == null && this.ncl == null) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			if (this.ncl != null) {
				this.hashCode = ((this.ncl.hashCode() ^ this.appID.hashCode()) << 5) ^ this.index;
			} else {
				this.hashCode = (this.appID.hashCode() << 5) ^ this.index;
			}
		}
		return this.hashCode;
	}

	@Override
	public String toString() {
		return String.format("NetNodeToken[address = %s, index = %d, id = %s]", this.ncl, this.index, this.appID);
	}
}

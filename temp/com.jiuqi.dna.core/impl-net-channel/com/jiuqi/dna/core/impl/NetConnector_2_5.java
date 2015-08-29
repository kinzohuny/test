package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.net.www.MessageHeader;

import com.jiuqi.dna.core.type.GUID;

final class NetConnector_2_5 extends NetConnector {

	@Override
	public boolean accept(NetProtocolVersion ver) {
		return ver == NetProtocolVersion.VER_2_5;
	}

	@Override
	public void acceptNodeInfoRequest(NetProtocolVersion ver,
			HttpServletRequest req, HttpServletResponse resp,
			ApplicationImpl app) throws IOException {
		// 返回NodeID
		resp.setHeader(HTTP_HEADER_NODE_ID, app.localNodeID.toString());
	}

	@Override
	public NetNodeToken[] parseNodeInfo(NetProtocolVersion ver,
			MessageHeader mh, InputStream in, URL ncl, Proxy proxy)
			throws IOException {
		try {
			String remoteNodeIDStr = mh.findValue(HTTP_HEADER_NODE_ID);
			return new NetNodeToken[] { new NetNodeToken(ver, NetClusterImpl.DEFAULT_NODE_INDEX, GUID.valueOf(remoteNodeIDStr), ncl, proxy) };
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new IOException("网络通信：NodeInfo的格式错误");
		}
	}

	@Override
	protected NetChannelImpl parseRequest(NetProtocolVersion ver,
			HttpServletRequest req, ApplicationImpl app) throws IOException {
		final String remoteNodeIDStr = req.getHeader(HTTP_HEADER_NODE_ID);
		if (remoteNodeIDStr == null) {
			throw new IOException("缺少NodeID参数");
		}
		return app.netChannelManager.ensureChannel(new NetNodeToken(ver, NetClusterImpl.DEFAULT_NODE_INDEX, GUID.valueOf(remoteNodeIDStr), null, null));
	}
}

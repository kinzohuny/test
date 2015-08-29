package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.net.www.MessageHeader;

import com.jiuqi.dna.core.type.GUID;

final class NetConnector_3_0_1 extends NetConnector {
	@Override
	public boolean accept(NetProtocolVersion ver) {
		switch (ver) {
		case VER_3_0:
		case VER_3_0_1:
			return true;
		}
		return false;
	}

	@Override
	public void acceptNodeInfoRequest(NetProtocolVersion ver,
			HttpServletRequest req, HttpServletResponse resp,
			ApplicationImpl app) throws IOException {
		// 返回NodeInfo列表
		PrintWriter w = resp.getWriter();
		w.write(String.format("%s: %s\r\n", app.netNodeManager.thisCluster.thisClusterNodeIndex, app.localNodeID));
		NetClusterImpl c = app.netNodeManager.thisCluster;
		synchronized (c) {
			for (NetNodeImpl n = c.getFirstNetNode(); n != null; n = n.getNextNodeInCluster()) {
				if (n.getState() == NetNodeImpl.STATE_READY) {
					w.write(String.format("%s: %s\r\n", n.channel.getRemoteNodeIndex(), n.channel.getRemoteNodeID()));
				}
			}
		}
		w.flush();
	}

	@Override
	public NetNodeToken[] parseNodeInfo(NetProtocolVersion ver,
			MessageHeader mh, InputStream in, URL ncl, Proxy proxy)
			throws IOException {
		// nodeIndex + ": " + GUID + "\r\n"
		char[] buf = new char[38 * NetClusterImpl.MAX_NODE_COUNT];
		int size = 0;
		InputStreamReader r = new InputStreamReader(in);
		try {
			for (int c = 0;;) {
				c = r.read(buf, size, buf.length - size);
				if (c == -1) {
					break;
				}
				size += c;
			}
		} finally {
			r.close();
		}
		ArrayList<NetNodeToken> arr = new ArrayList<NetNodeToken>();
		for (int offset = 0; offset < size;) {
			try {
				int i = offset;
				while (i < size && buf[i] != ':') {
					i++;
				}
				int nodeIndex = Integer.parseInt(String.valueOf(buf, offset, i - offset));
				i = offset = i + 2; // skip ": "
				while (i < size && buf[i] != '\r') {
					i++;
				}
				GUID id = GUID.valueOf(String.valueOf(buf, offset, i - offset));
				arr.add(new NetNodeToken(ver, nodeIndex, id, ncl, proxy));
				offset = i + 2; // skip "\r\n"
			} catch (Throwable e) {
				throw new IOException("远程节点的NodeID格式不正确");
			}
		}
		if (arr.size() == 0) {
			throw new IOException("网络通信：无法获取远程地址[" + ncl + "]的节点信息");
		}
		String header = mh.findValue("Set-Cookie");
		if (header == null || header.indexOf(ROUTE_COOKIE_NAME) < 0) {
			return new NetNodeToken[] { arr.get(0) };
		}
		return arr.toArray(new NetNodeToken[arr.size()]);
	}

	@Override
	protected final NetChannelImpl parseRequest(NetProtocolVersion ver,
			HttpServletRequest req, ApplicationImpl app) throws IOException {
		final String remoteNodeIndexStr = req.getHeader(HTTP_HEADER_NODE_INDEX);
		final String remoteNodeIDStr = req.getHeader(HTTP_HEADER_NODE_ID);
		if (remoteNodeIndexStr == null) {
			throw new IOException("缺少NodeIndex参数");
		}
		if (remoteNodeIDStr == null) {
			throw new IOException("缺少NodeID参数");
		}
		final NetNodeToken token = new NetNodeToken(ver, Integer.parseInt(remoteNodeIndexStr), GUID.valueOf(remoteNodeIDStr), null, null);
		return app.netChannelManager.ensureChannel(token);
	}
}

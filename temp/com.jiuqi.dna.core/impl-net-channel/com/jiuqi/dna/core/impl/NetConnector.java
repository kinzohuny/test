package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.net.www.MessageHeader;

import com.jiuqi.dna.core.exception.NullArgumentException;

abstract class NetConnector {
	/**
	 * 节点的索引号
	 */
	final static String HTTP_HEADER_NODE_INDEX = "DNA-NET-NodeIndex";
	/**
	 * 节点ID
	 */
	final static String HTTP_HEADER_NODE_ID = "DNA-NET-NodeID";
	/**
	 * 当前协议使用的版本，从3.0开始引入
	 */
	final static String HTTP_HEADER_CONNECTOR_TAG = "DNA-NET-ConnectorTag";
	/**
	 * 协议支持的最高版本，从3.0.1开始引入
	 */
	final static String HTTP_HEADER_MAX_VER = "DNA-NET-MaxVer";
	/**
	 * 路由索引号
	 */
	final static String ROUTE_COOKIE_NAME = "DNA_ROUTE_INDEX";

	private final static NetConnector[] connectors;

	static {
		connectors = new NetConnector[] { new NetConnector_3_0_1(), new NetConnector_2_5() };
	}

	public final static NetConnector getConnector(NetProtocolVersion ver) {
		if (ver == null) {
			throw new NullArgumentException("ver");
		}
		for (NetConnector c : connectors) {
			if (c.accept(ver)) {
				return c;
			}
		}
		throw new IllegalStateException("不支持连接器类型[" + ver.tag + "]");
	}

	public abstract boolean accept(NetProtocolVersion ver);

	public abstract void acceptNodeInfoRequest(NetProtocolVersion ver,
			HttpServletRequest req, HttpServletResponse resp,
			ApplicationImpl app) throws IOException;

	public abstract NetNodeToken[] parseNodeInfo(NetProtocolVersion ver,
			MessageHeader mh, InputStream in, URL ncl, Proxy proxy)
			throws IOException;

	protected abstract NetChannelImpl parseRequest(NetProtocolVersion ver,
			HttpServletRequest req, ApplicationImpl app) throws IOException;

	public final void acceptInput(NetProtocolVersion ver,
			HttpServletRequest req, HttpServletResponse resp,
			ApplicationImpl app) throws IOException {
		checkNodeIndex(req, app);
		this.parseRequest(ver, req, app).attachServletInput(req.getInputStream(), getRemoteInfo(req));
	}

	public final void acceptOutput(NetProtocolVersion ver,
			HttpServletRequest req, HttpServletResponse resp,
			ApplicationImpl app) throws IOException {
		checkNodeIndex(req, app);
		this.parseRequest(ver, req, app).attachServletOutput(resp.getOutputStream(), getRemoteInfo(req));
	}

	private final static String getRemoteInfo(HttpServletRequest req) {
		String remoteHost = req.getRemoteHost();
		String remoteAddr = req.getRemoteAddr();
		return ((remoteHost == null || remoteHost.equals(remoteAddr)) ? "" : (remoteHost + "/")) + remoteAddr + ":" + req.getRemotePort();
	}

	protected final static void checkNodeIndex(HttpServletRequest req,
			ApplicationImpl app) throws IOException {
		// 检查NodeIndex是否一致，即apache的路由配置是否正确
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			String nodeIndex = new StringBuilder(4).append('.').append(app.netNodeManager.thisCluster.thisClusterNodeIndex).toString();
			for (Cookie c : cookies) {
				if (ROUTE_COOKIE_NAME.equals(c.getName())) {
					String value = c.getValue();
					if (value.startsWith(".") && !nodeIndex.equals(value)) {
						throw new IOException("网络通信：NodeIndex和当前节点不一致，可能是由于代理服务器配置不正确导致");
					}
					break;
				}
			}
		}
	}
}

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
	 * �ڵ��������
	 */
	final static String HTTP_HEADER_NODE_INDEX = "DNA-NET-NodeIndex";
	/**
	 * �ڵ�ID
	 */
	final static String HTTP_HEADER_NODE_ID = "DNA-NET-NodeID";
	/**
	 * ��ǰЭ��ʹ�õİ汾����3.0��ʼ����
	 */
	final static String HTTP_HEADER_CONNECTOR_TAG = "DNA-NET-ConnectorTag";
	/**
	 * Э��֧�ֵ���߰汾����3.0.1��ʼ����
	 */
	final static String HTTP_HEADER_MAX_VER = "DNA-NET-MaxVer";
	/**
	 * ·��������
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
		throw new IllegalStateException("��֧������������[" + ver.tag + "]");
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
		// ���NodeIndex�Ƿ�һ�£���apache��·�������Ƿ���ȷ
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			String nodeIndex = new StringBuilder(4).append('.').append(app.netNodeManager.thisCluster.thisClusterNodeIndex).toString();
			for (Cookie c : cookies) {
				if (ROUTE_COOKIE_NAME.equals(c.getName())) {
					String value = c.getValue();
					if (value.startsWith(".") && !nodeIndex.equals(value)) {
						throw new IOException("����ͨ�ţ�NodeIndex�͵�ǰ�ڵ㲻һ�£����������ڴ�����������ò���ȷ����");
					}
					break;
				}
			}
		}
	}
}

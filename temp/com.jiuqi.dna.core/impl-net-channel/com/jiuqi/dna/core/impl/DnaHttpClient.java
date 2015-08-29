package com.jiuqi.dna.core.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.URL;

import sun.net.www.MessageHeader;
import sun.net.www.http.ChunkedInputStream;
import sun.net.www.http.ChunkedOutputStream;
import sun.net.www.http.HttpClient;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.GUID;

class DnaHttpClient extends HttpClient {
	final static String USER_AGENT = "DNA/2.5";
	private final String localNodeIndex;
	private final String localNodeID;

	public DnaHttpClient(URL address, Proxy proxy, int localNodeIndex,
			GUID localNodeID) throws IOException {
		super(address, proxy, -1);
		this.serverSocket.setSoLinger(true, 5);
		this.serverInput = new BufferedInputStream(this.serverSocket.getInputStream());
		this.localNodeIndex = String.valueOf(localNodeIndex);
		this.localNodeID = localNodeID.toString();
	}

	public final NetNodeToken[] queryNodeInfo() throws IOException {
		MessageHeader mh = new MessageHeader();
		mh.prepend("GET " + this.getURLFile() + " HTTP/1.1", null);
		mh.add("Host", this.url.getHost());
		mh.add("User-Agent", USER_AGENT);
		// 兼容3.0版本
		mh.add(NetConnector.HTTP_HEADER_CONNECTOR_TAG, NetProtocolVersion.VER_3_0.tag);
		mh.set("Connection", "close");
		this.writeRequests(mh, null);
		this.serverOutput.flush();
		mh.parseHeader(this.serverInput);
		String resp = mh.getValue(0);
		if (resp == null || !resp.startsWith("HTTP/1.1 200 OK")) {
			throw new IOException("收到错误的HTTP回复:" + resp);
		}
		InputStream in = this.serverInput;
		if ("chunked".equalsIgnoreCase(mh.findValue("Transfer-Encoding"))) {
			in = new ChunkedInputStream(in, this, mh);
		}
		// 获取连接器版本
		String tag = mh.findValue(NetConnector.HTTP_HEADER_CONNECTOR_TAG);
		NetProtocolVersion connectorVer;
		if (tag == null || tag.length() == 0) {
			// 兼容2.5
			connectorVer = NetProtocolVersion.VER_2_5;
		} else {
			connectorVer = NetProtocolVersion.getFromTag(tag);
			if (connectorVer == null) {
				throw new UnsupportedOperationException("不支持连接器类型[" + tag + "]");
			}
		}
		NetConnector connector = NetConnector.getConnector(connectorVer);
		// 获取协议最高版本
		tag = mh.findValue(NetConnector.HTTP_HEADER_MAX_VER);
		NetProtocolVersion ver;
		if (tag == null || tag.length() == 0) {
			// 兼容3.0以下版本
			ver = connectorVer;
		} else {
			ver = NetProtocolVersion.getFromTag(tag);
			if (ver == null) {
				// 远程主机版本比本地主机版本高，使用本地最高版本
				ver = NetProtocolVersion.maxVer();
			}
		}
		return connector.parseNodeInfo(ver, mh, in, this.url, this.proxy);
	}

	public final OutputStream openOutput(NetNodeToken info) throws IOException {
		if (info == null) {
			throw new NullArgumentException("info");
		}
		MessageHeader mh = new MessageHeader();
		mh.prepend("POST " + this.getURLFile() + " HTTP/1.1", null);
		mh.add("Host", this.url.getHost());
		mh.add("User-Agent", USER_AGENT);
		mh.add("Transfer-Encoding", "chunked");
		mh.add(NetConnector.HTTP_HEADER_NODE_INDEX, this.localNodeIndex);
		mh.add(NetConnector.HTTP_HEADER_NODE_ID, this.localNodeID);
		// 设置协议版本
		mh.add(NetConnector.HTTP_HEADER_CONNECTOR_TAG, info.ver.tag);
		mh.add("Cookie", String.format("%s=.%s", NetConnector.ROUTE_COOKIE_NAME, info.index));
		mh.add("Connection", "close");
		this.writeRequests(mh, null);
		return new ChunkedOutputStream(this.serverOutput);
	}

	public final InputStream openInput(NetNodeToken info) throws IOException {
		if (info == null) {
			throw new NullArgumentException("info");
		}
		MessageHeader mh = new MessageHeader();
		mh.prepend("GET " + this.getURLFile() + " HTTP/1.1", null);
		mh.add("Host", this.url.getHost());
		mh.add("User-Agent", USER_AGENT);
		mh.add(NetConnector.HTTP_HEADER_NODE_INDEX, this.localNodeIndex);
		mh.add(NetConnector.HTTP_HEADER_NODE_ID, this.localNodeID);
		// 设置协议版本
		mh.add(NetConnector.HTTP_HEADER_CONNECTOR_TAG, info.ver.tag);
		mh.add("Cookie", String.format("%s=.%s", NetConnector.ROUTE_COOKIE_NAME, info.index));
		mh.add("Connection", "close");
		this.writeRequests(mh, null);
		this.getOutputStream().flush();
		mh.parseHeader(this.serverInput);
		String resp = mh.getValue(0);
		if (!resp.startsWith("HTTP/1.1 200 OK")) {
			throw new IOException("收到错误的HTTP回复:" + resp);
		}
		if ("chunked".equalsIgnoreCase(mh.findValue("Transfer-Encoding"))) {
			return new ChunkedInputStream(this.serverInput, this, mh);
		}
		return this.serverInput;
	}
}

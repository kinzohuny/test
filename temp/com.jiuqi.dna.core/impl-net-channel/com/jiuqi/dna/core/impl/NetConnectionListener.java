package com.jiuqi.dna.core.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.core.http.DNAHttpServlet;

public class NetConnectionListener extends DNAHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * GET用于向对方发送数据
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.accept(req, resp, false);
	}

	/**
	 * POST用于接收对方发送的数据
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.accept(req, resp, true);
	}

	private final void accept(HttpServletRequest req, HttpServletResponse resp,
			boolean inOrOut) throws IOException {
		try {
			resp.setHeader("Connection", "close");
			// 检查客户端类型
			String userAgent = req.getHeader("User-Agent");
			if (userAgent == null || !userAgent.equals(DnaHttpClient.USER_AGENT)) {
				throw new IOException("UserAgent类型不正确");
			}
			ApplicationImpl app = (ApplicationImpl) this.getApplication();
			// 检查网络初始化是否完成
			if (!app.netNodeManager.isActive()) {
				throw new IOException("网络通信组件未完成初始化");
			}
			// 获取connector
			NetProtocolVersion ver;
			String tag = req.getHeader(NetConnector.HTTP_HEADER_CONNECTOR_TAG);
			if (tag == null || tag.length() == 0) {
				// 兼容DNA2.5，DNA2.5不支持HTTP_HEADER_CONNECTOR_TAG
				ver = NetProtocolVersion.VER_2_5;
			} else {
				ver = NetProtocolVersion.getFromTag(tag);
			}
			NetConnector connector = NetConnector.getConnector(ver);
			// 如果包含HTTP_HEADER_NODE_ID字段，则当作Input请求处理，否则当作查询NodeInfo的请求
			final String remoteNodeIDStr = req.getHeader(NetConnector.HTTP_HEADER_NODE_ID);
			if (remoteNodeIDStr != null) {
				// 接受连接
				if (inOrOut) {
					connector.acceptInput(ver, req, resp, app);
				} else {
					connector.acceptOutput(ver, req, resp, app);
				}
			} else {
				// 返回当选协议版本
				resp.setHeader(NetConnector.HTTP_HEADER_CONNECTOR_TAG, tag);
				// 返回协议最高版本
				resp.setHeader(NetConnector.HTTP_HEADER_MAX_VER, NetProtocolVersion.maxVer().tag);
				// 返回NodeInfo
				connector.acceptNodeInfoRequest(ver, req, resp, app);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw Utils.tryThrowException(e);
		}
	}
}

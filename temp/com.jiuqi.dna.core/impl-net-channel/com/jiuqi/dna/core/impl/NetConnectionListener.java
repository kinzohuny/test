package com.jiuqi.dna.core.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.core.http.DNAHttpServlet;

public class NetConnectionListener extends DNAHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * GET������Է���������
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.accept(req, resp, false);
	}

	/**
	 * POST���ڽ��նԷ����͵�����
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
			// ���ͻ�������
			String userAgent = req.getHeader("User-Agent");
			if (userAgent == null || !userAgent.equals(DnaHttpClient.USER_AGENT)) {
				throw new IOException("UserAgent���Ͳ���ȷ");
			}
			ApplicationImpl app = (ApplicationImpl) this.getApplication();
			// ��������ʼ���Ƿ����
			if (!app.netNodeManager.isActive()) {
				throw new IOException("����ͨ�����δ��ɳ�ʼ��");
			}
			// ��ȡconnector
			NetProtocolVersion ver;
			String tag = req.getHeader(NetConnector.HTTP_HEADER_CONNECTOR_TAG);
			if (tag == null || tag.length() == 0) {
				// ����DNA2.5��DNA2.5��֧��HTTP_HEADER_CONNECTOR_TAG
				ver = NetProtocolVersion.VER_2_5;
			} else {
				ver = NetProtocolVersion.getFromTag(tag);
			}
			NetConnector connector = NetConnector.getConnector(ver);
			// �������HTTP_HEADER_NODE_ID�ֶΣ�����Input��������������ѯNodeInfo������
			final String remoteNodeIDStr = req.getHeader(NetConnector.HTTP_HEADER_NODE_ID);
			if (remoteNodeIDStr != null) {
				// ��������
				if (inOrOut) {
					connector.acceptInput(ver, req, resp, app);
				} else {
					connector.acceptOutput(ver, req, resp, app);
				}
			} else {
				// ���ص�ѡЭ��汾
				resp.setHeader(NetConnector.HTTP_HEADER_CONNECTOR_TAG, tag);
				// ����Э����߰汾
				resp.setHeader(NetConnector.HTTP_HEADER_MAX_VER, NetProtocolVersion.maxVer().tag);
				// ����NodeInfo
				connector.acceptNodeInfoRequest(ver, req, resp, app);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw Utils.tryThrowException(e);
		}
	}
}

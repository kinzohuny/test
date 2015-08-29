package com.jiuqi.dna.core.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.core.http.DNAHttpServlet;

public final class ReliableRSIServlet extends DNAHttpServlet implements
		ReliableRSI {

	private static final long serialVersionUID = 8105131241637777413L;

	@Override
	protected final void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		this.accept(req, resp);
	}

	@Override
	protected final void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		this.accept(req, resp);
	}

	private final void accept(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		final String userAgent = req.getHeader("User-Agent");
		if (userAgent == null || !userAgent.equals(DnaHttpClient.USER_AGENT)) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else {
			final ApplicationImpl application = (ApplicationImpl) this.getApplication();
			final String action = req.getHeader(HTTP_HEADER_ACTION);
			if (ACTION_GETINFOMATION.equals(action)) {
				this.setResponseHeader(resp, action, application);
			} else if (ACTION_INVOKE.equals(action)) {
				// 如果调用方提供的远程信息与本地信息是一致的，则处理调用，否则，返回本地最新的节点信息
				final String handlerVersionString = req.getHeader(HTTP_HEADER_HANDLER_VERSION);
				if (this.checkRequestHeader(req)) {
					this.setResponseHeader(resp, action, application);
					ReliableRSIHandler.getHandler(Short.valueOf(handlerVersionString)).handleRequest(application, req, resp);
				} else {
					this.setResponseHeader(resp, ACTION_GETINFOMATION, application);
				}
			} else {
				throw new UnsupportedOperationException("不支持的远程调用动作。" + action);
			}
		}
	}

	private final void setResponseHeader(final HttpServletResponse resp,
			final String action, final ApplicationImpl application) {
		resp.setHeader(HTTP_HEADER_ACTION, action);
		resp.setHeader(NetConnector.HTTP_HEADER_NODE_INDEX, String.valueOf(application.netNodeManager.thisCluster.thisClusterNodeIndex));
		resp.setHeader(HTTP_HEADER_HIGTHEST_HANDLER_VERSION, String.valueOf(ReliableRSIHandler.getHigthestHandlerVersion()));
		resp.setHeader(HTTP_HEADER_HIGTHEST_SERIALIZE_VERSION, String.valueOf(NSerializer.getHighestSerializeVersion()));
	}

	private final boolean checkRequestHeader(final HttpServletRequest req) {
		final short higthestHandlerVersion = Short.valueOf(req.getHeader(HTTP_HEADER_HIGTHEST_HANDLER_VERSION));
		final short higthestSerializeVersion = Short.valueOf(req.getHeader(HTTP_HEADER_HIGTHEST_SERIALIZE_VERSION));
		return higthestHandlerVersion == ReliableRSIHandler.getHigthestHandlerVersion() && higthestSerializeVersion == NSerializer.getHighestSerializeVersion();
	}

}

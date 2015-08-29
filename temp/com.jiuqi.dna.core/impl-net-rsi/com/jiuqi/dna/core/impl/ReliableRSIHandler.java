package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

abstract class ReliableRSIHandler implements ReliableRSI {

	static final short getHigthestHandlerVersion() {
		return HANDLERS[0].version;
	}

	static final ReliableRSIHandler getRemoteCompatibleHandler(
			final short version) {
		final ReliableRSIHandler[] handlers = HANDLERS;
		for (int index = 0, endIndex = handlers.length; index < endIndex; index++) {
			final ReliableRSIHandler handler = handlers[index];
			if (handler.isCompatibleTo(version)) {
				return handler;
			}
		}
		throw new IllegalArgumentException(String.format("无法取得与远程节点兼容的调用处理器，远程支持的调用处理器版本过低。远程版本[%x]", version));
	}

	static final ReliableRSIHandler getHandler(final short version) {
		final ReliableRSIHandler[] handlers = HANDLERS;
		for (int index = 0, endIndex = handlers.length; index < endIndex; index++) {
			final ReliableRSIHandler handler = handlers[index];
			if (handler.version == version) {
				return handler;
			}
		}
		throw new UnsupportedOperationException(String.format("不支持版本为[%x]的远程调用过程。", version));
	}

	private static final ReliableRSIHandler[] HANDLERS;

	static {
		HANDLERS = new ReliableRSIHandler[] { new ReliableRSIHandler_1_0() };
	}

	ReliableRSIHandler(final short version) {
		this.version = version;
	}

	final boolean isCompatibleTo(final short version) {
		return this.version <= version;
	}

	abstract void sendRequest(NSerializer serializer, OutputStream os,
			Object infomation) throws IOException;

	abstract Object handleResponse(InputStream is, Object information)
			throws IOException;

	abstract void handleRequest(ApplicationImpl application,
			HttpServletRequest req, HttpServletResponse resp)
			throws IOException;

	final short version;

}

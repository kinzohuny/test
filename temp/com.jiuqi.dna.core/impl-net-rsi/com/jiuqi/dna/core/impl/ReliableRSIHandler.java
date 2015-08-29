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
		throw new IllegalArgumentException(String.format("�޷�ȡ����Զ�̽ڵ���ݵĵ��ô�������Զ��֧�ֵĵ��ô������汾���͡�Զ�̰汾[%x]", version));
	}

	static final ReliableRSIHandler getHandler(final short version) {
		final ReliableRSIHandler[] handlers = HANDLERS;
		for (int index = 0, endIndex = handlers.length; index < endIndex; index++) {
			final ReliableRSIHandler handler = handlers[index];
			if (handler.version == version) {
				return handler;
			}
		}
		throw new UnsupportedOperationException(String.format("��֧�ְ汾Ϊ[%x]��Զ�̵��ù��̡�", version));
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

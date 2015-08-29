package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiuqi.dna.core.ContextKind;
import com.jiuqi.dna.core.SessionKind;
import com.jiuqi.dna.core.impl.NUnserializer.ObjectTypeQuerier;

final class ReliableRSIHandler_1_0 extends ReliableRSIHandler {

	ReliableRSIHandler_1_0() {
		super((short) 0x0100);
	}

	@Override
	final void sendRequest(final NSerializer serializer, final OutputStream os,
			final Object infomation) throws IOException {
		try {
			final short serializerVersion = serializer.getVersion();
			DataFragment dataFragment = this.allocRequestFragment(true, serializerVersion);
			int availableOffset = dataFragment.getAvailableOffset();
			byte controlFlag = NetChannelImpl.CTRL_FLAG_PACKAGE | NetChannelImpl.CTRL_FLAG_PACKAGE_FIRST;
			if (!serializer.serializeStart(infomation, dataFragment, true)) {
				do {
					this.sendRequestFragment(os, dataFragment, availableOffset, controlFlag);
					dataFragment = this.allocRequestFragment(false, serializerVersion);
					availableOffset = dataFragment.getAvailableOffset();
					controlFlag = NetChannelImpl.CTRL_FLAG_PACKAGE;
				} while (!serializer.serializeRest(dataFragment));
			}
			controlFlag |= NetChannelImpl.CTRL_FLAG_PACKAGE_LAST;
			this.sendRequestFragment(os, dataFragment, availableOffset, controlFlag);
		} finally {
			os.close();
		}
	}

	@Override
	final Object handleResponse(final InputStream is, final Object information)
			throws IOException {
		try {
			final DataFragmentReader fragmentReader = new DataFragmentReader(is);
			SafeDataFragmentImpl dataFragment = fragmentReader.getNextDataFragment();
			// dataFragment.readByte();
			// dataFragment.readInt();
			// dataFragment.readByte();
			// 6 = byteLen + intLen + byteLen
			dataFragment.skip(6);
			final byte responseInformationType = dataFragment.readByte();
			// dataFragment.readInt();
			// dataFragment.readLong();
			// dataFragment.readFloat();
			// 16 = intLen + longLen + floatLen
			dataFragment.skip(16);
			final short serializerVersion = dataFragment.readShort();
			final NUnserializer unserializer = NUnserializer.newUnserializer(serializerVersion, ObjectTypeQuerier.staticObjectTypeQuerier);
			final boolean unserializeFinished;
			switch (responseInformationType) {
			case NetRequestImpl.REQUEST_PACKAGE_TYPE_RESPONSE:
				unserializeFinished = unserializer.unserializeStart(dataFragment, information);
				break;
			case NetRequestImpl.REQUEST_PACKAGE_TYPE_EXCEPTION:
				unserializeFinished = unserializer.unserializeStart(dataFragment, null);
				break;
			default:
				throw new UnsupportedOperationException("收到不支持的远程调用响应信息。类型为" + responseInformationType);
			}
			if (!unserializeFinished) {
				for (;;) {
					dataFragment = fragmentReader.getNextDataFragment();
					// dataFragment.readByte();
					// dataFragment.readInt();
					// 5 = byteLen + intLen
					dataFragment.skip(5);
					if (unserializer.unserializeRest(dataFragment)) {
						break;
					}
				}
			}
			return unserializer.getUnserialzedObject();
		} finally {
			is.close();
		}
	}

	private final DataFragment allocRequestFragment(final boolean first,
			final short serializerVersion) {
		final DataFragment dataFragment = new SafeDataFragmentImpl(DATAFRAGMENT_CAPACITY);
		dataFragment.skip(4);
		// write contorl flag
		dataFragment.writeByte((byte) 0);
		// write package ID
		dataFragment.writeInt(0);
		if (first) {
			// write package type
			dataFragment.writeByte((byte) 0);
			// write request type
			dataFragment.writeByte(NetRequestImpl.REQUEST_PACKAGE_TYPE_POST);
			// write request ID
			dataFragment.writeInt(0);
			// write remote session ID
			dataFragment.writeLong(0);
			// write serialize version
			dataFragment.writeShort(serializerVersion);
		}
		return dataFragment;
	}

	private final void sendRequestFragment(final OutputStream os,
			final DataFragment dataFragment, final int availableOffset,
			final byte controlFlag) throws IOException {
		final int fragmentLen = dataFragment.getPosition() - availableOffset;
		dataFragment.setPosition(availableOffset);
		// write fragment size
		dataFragment.writeInt(fragmentLen - availableOffset - 4);
		dataFragment.writeByte(controlFlag);
		os.write(dataFragment.getBytes(), availableOffset, fragmentLen);
	}

	@Override
	final void handleRequest(final ApplicationImpl application,
			final HttpServletRequest req, final HttpServletResponse resp)
			throws IOException {
		final RemoteInvokeData invokeInfo;
		try {
			final Object received = this.receiveRequest(req);
			if (received instanceof RemoteInvokeData) {
				invokeInfo = (RemoteInvokeData) received;
				final SessionImpl session = application.sessionManager.newSession(SessionKind.REMOTE, BuildInUser.anonym, null, null);
				try {
					final ContextImpl<?, ?, ?> context = session.newContext(ContextKind.TRANSIENT);
					try {
						invokeInfo.invoke(context);
					} finally {
						context.dispose();
					}
				} finally {
					session.dispose(0);
				}
			} else {
				throw new UnsupportedOperationException("不支持的远程调用类型。" + (received == null ? "null" : received.getClass()));
			}
		} catch (Throwable e) {
			this.sendResponse(resp, (Short) req.getAttribute(REQUEST_ATTR_SERIALIZE_VERSION), NetRequestImpl.REQUEST_PACKAGE_TYPE_EXCEPTION, new ExceptionInfo(e));
			return;
		}
		this.sendResponse(resp, (Short) req.getAttribute(REQUEST_ATTR_SERIALIZE_VERSION), NetRequestImpl.REQUEST_PACKAGE_TYPE_RESPONSE, invokeInfo);
	}

	private final Object receiveRequest(final HttpServletRequest req)
			throws IOException {
		final InputStream is = req.getInputStream();
		final DataFragmentReader fragmentReader = new DataFragmentReader(is);
		SafeDataFragmentImpl dataFragment = fragmentReader.getNextDataFragment();
		// dataFragment.readByte();
		// dataFragment.readInt();
		// dataFragment.readByte();
		// dataFragment.readByte();
		// dataFragment.readInt();
		// dataFragment.readLong();
		// 19 = byteLen + intLen + byteLen + byteLen + intLen + longLen;
		dataFragment.skip(19);
		final short serializerVersion = dataFragment.readShort();
		req.setAttribute(REQUEST_ATTR_SERIALIZE_VERSION, serializerVersion);
		final NUnserializer unserializer = NUnserializer.newUnserializer(serializerVersion, ObjectTypeQuerier.staticObjectTypeQuerier);
		if (!unserializer.unserializeStart(dataFragment, null)) {
			for (;;) {
				dataFragment = fragmentReader.getNextDataFragment();
				// dataFragment.readByte();
				// dataFragment.readInt();
				// 5 = byteLen + intLen
				dataFragment.skip(5);
				if (unserializer.unserializeRest(dataFragment)) {
					break;
				}
			}
		}
		return unserializer.getUnserialzedObject();
	}

	private final void sendResponse(final HttpServletResponse resp,
			final short serializeVersion, final byte informationType,
			final Object information) throws IOException {
		final OutputStream os = resp.getOutputStream();
		try {
			final NSerializer serializer = NSerializer.getRemoteCompatibleFactory(serializeVersion).newNSerializer();
			final short serializerVersion = serializer.getVersion();
			DataFragment dataFragment = this.allocResponseFragment(true, serializerVersion, informationType);
			int availableOffset = dataFragment.getAvailableOffset();
			byte controlFlag = NetChannelImpl.CTRL_FLAG_PACKAGE | NetChannelImpl.CTRL_FLAG_PACKAGE_FIRST;
			if (!serializer.serializeStart(information, dataFragment, true)) {
				do {
					this.sendResponseFragment(os, dataFragment, availableOffset, controlFlag);
					dataFragment = this.allocResponseFragment(false, serializerVersion, informationType);
					availableOffset = dataFragment.getAvailableOffset();
					controlFlag = NetChannelImpl.CTRL_FLAG_PACKAGE;
				} while (!serializer.serializeRest(dataFragment));
			}
			controlFlag |= NetChannelImpl.CTRL_FLAG_PACKAGE_LAST;
			this.sendResponseFragment(os, dataFragment, availableOffset, controlFlag);
		} finally {
			os.close();
		}
	}

	private final DataFragment allocResponseFragment(final boolean first,
			final short serializerVersion, final byte responseType) {
		final DataFragment dataFragment = new SafeDataFragmentImpl(DATAFRAGMENT_CAPACITY);
		dataFragment.skip(4);
		// write control flag
		dataFragment.writeByte((byte) 0);
		// write package ID
		dataFragment.writeInt(0);
		if (first) {
			// write package type
			dataFragment.writeByte((byte) 0);
			// write response type
			dataFragment.writeByte(responseType);
			// write request ID
			dataFragment.writeInt(0);
			// write remote session ID
			dataFragment.writeLong(0);
			// write process
			dataFragment.writeFloat(1);
			// write serialize version
			dataFragment.writeShort(serializerVersion);
		}
		return dataFragment;
	}

	private final void sendResponseFragment(final OutputStream os,
			final DataFragment dataFragment, final int availableOffset,
			final byte controlFlag) throws IOException {
		final int fragmentLen = dataFragment.getPosition() - availableOffset;
		dataFragment.setPosition(availableOffset);
		dataFragment.writeInt(fragmentLen - availableOffset - 4);
		dataFragment.writeByte(controlFlag);
		os.write(dataFragment.getBytes(), availableOffset, fragmentLen);
	}

}

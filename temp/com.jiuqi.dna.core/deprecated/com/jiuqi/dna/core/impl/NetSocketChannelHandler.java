/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File NetSocketChannelHandler.java
 * Date Dec 10, 2009
 */
package com.jiuqi.dna.core.impl;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;

import com.jiuqi.dna.core.spi.http.DetachedSocketChannelHandler;
import com.jiuqi.dna.core.spi.http.DetachedSocketChannelWithSSLHandler;
import com.jiuqi.dna.core.type.GUID;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
final class NetSocketChannelHandler extends NetManagerBased implements
		DetachedSocketChannelHandler, DetachedSocketChannelWithSSLHandler {
	NetSocketChannelHandler(NetManager netManager) {
		super(netManager);
	}

	public void handleDetackedSocketChannel(SocketChannel socketChannel) {
		NetConnection nc = null;
		try {
			socketChannel.socket().setTcpNoDelay(true);
			socketChannel.write(ByteBuffer.wrap(NetManager.getRsiResponseSignalData()));

			// XXX 标线内的做法尚不成熟
			/* ------------------------------ */
			byte[] msg = this.netManager.getServerInfo();
			ByteBuffer msgBuf = ByteBuffer.wrap(msg);
			do {
				socketChannel.write(msgBuf);
			} while (msgBuf.hasRemaining());
			/* ------------------------------ */
			ByteBuffer ciBuf = ByteBuffer.allocate(21);
			do {
				socketChannel.read(ciBuf);
			} while (ciBuf.hasRemaining());
			ciBuf.flip();
			ciBuf.get(msg, 0, 5);
			Endianness remote = Endianness.parseEndianness(msg[0]);
			int remotePort = remote.getInt(msg, 1);
			ciBuf.get(msg, 0, 16);
			GUID remoteId = GUID.valueOf(msg);
			/* ------------------------------ */
			NetNodeInfo nni = this.netManager.ensureGet(socketChannel.socket().getInetAddress(), remotePort, true);
			nni.setSecure(false);
			/* ------------------------------ */

			nc = new NetConnection(this.netManager, nni, remote, remoteId, socketChannel);
		} catch (Throwable e) {
			try {
				socketChannel.close();
			} catch (Throwable ignore) {
			}
			throw Utils.tryThrowException(e);
		}
		this.netManager.prepare(nc);
	}

	public void handleDetackedSocketChannel(SocketChannel socketChannel,
			SSLEngine sslEngine) {
		NetConnectionWithSSL nc = null;
		try {
			socketChannel.socket().setTcpNoDelay(true);
			ByteBuffer buf = ByteBuffer.allocate(sslEngine.getSession().getPacketBufferSize());
			sslEngine.wrap(ByteBuffer.wrap(NetManager.getRsiResponseSignalData()), buf);
			buf.flip();
			do {
				socketChannel.write(buf);
			} while (buf.hasRemaining());

			// XXX 标线内的做法尚不成熟
			/* ------------------------------ */
			byte[] msg = this.netManager.getServerInfo();
			ByteBuffer msgBuf = ByteBuffer.wrap(msg);
			ByteBuffer sslPacket = ByteBuffer.allocate(sslEngine.getSession().getPacketBufferSize());
			sslEngine.wrap(msgBuf, sslPacket);
			sslPacket.flip();
			do {
				socketChannel.write(sslPacket);
			} while (sslPacket.hasRemaining());
			/* ------------------------------ */
			ByteBuffer ciBuf = ByteBuffer.allocate(64);
			int len = 0;
			SSLEngineResult sslResult;
			do {
				sslPacket.clear();
				if (socketChannel.read(sslPacket) < 0) {
					try {
						socketChannel.close();
					} catch (Throwable ignore) {
					}
					throw new ClosedChannelException();
				}
				sslPacket.flip();
				sslResult = sslEngine.unwrap(sslPacket, ciBuf);
				len += sslResult.bytesProduced();
			} while (len < 21);
			ciBuf.flip();
			ciBuf.get(msg, 0, 5);
			Endianness remote = Endianness.parseEndianness(msg[0]);
			int remotePort = remote.getInt(msg, 1);
			ciBuf.get(msg, 0, 16);
			GUID remoteId = GUID.valueOf(msg);
			/* ------------------------------ */
			NetNodeInfo nni = this.netManager.ensureGet(socketChannel.socket().getInetAddress(), remotePort, true);
			nni.setSecure(true);
			/* ------------------------------ */

			nc = new NetConnectionWithSSL(this.netManager, nni, remote, remoteId, socketChannel, sslEngine);
		} catch (Throwable e) {
			try {
				socketChannel.close();
			} catch (Throwable ignore) {
			}
			try {
				sslEngine.closeOutbound();
			} catch (Throwable ignore) {
			}
			throw Utils.tryThrowException(e);
		}
		this.netManager.prepare(nc);
	}
}

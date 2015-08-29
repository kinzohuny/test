/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteEvent.java
 * Date 2009-4-16
 */
package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.Event;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
@StructClass
final class RemoteEvent implements RemoteRequest<RemoteEventStubImpl> {
	final Event event;
	final boolean wait;

	RemoteEvent(Event event, boolean wait) {
		if (event == null) {
			throw new NullArgumentException("event");
		}
		this.event = event;
		this.wait = wait;
	}

	public RemoteReturn execute(ContextImpl<?, ?, ?> context) throws Throwable {
		if (this.wait) {
			context.dispatch(this.event);
		} else {
			context.occur(this.event);
		}
		return VoidReturn.VOID;
	}

	public final PacketCode getPacketCode() {
		return PacketCode.EVENT_REQUEST;
	}

	public void writeTo(StructuredObjectSerializer serializer)
			throws IOException, StructDefineNotFoundException {
		serializer.writeDataOnly(this);
	}

	public RemoteEventStubImpl newStub(NetConnection netConnection) {
		if (netConnection == null) {
			throw new NullArgumentException("newConnection");
		}
		return new RemoteEventStubImpl(netConnection, this);
	}
}

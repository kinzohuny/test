/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteAsyncEvent.java
 * Date 2009-4-16
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.Event;

/**
 * 
 * @author LRJ
 * @version 1.0
 */
final class RemoteAsyncEvent extends RemoteAsyncHandle implements AsyncEvent {
	final RemoteEvent remoteEvent;

	RemoteAsyncEvent(RemoteEvent remoteEvent,
			RemoteEventStubImpl remoteEventStub) {
		super(remoteEventStub);
		if (remoteEvent == null) {
			throw new NullArgumentException("remoteEvent");
		}
		this.remoteEvent = remoteEvent;
	}

	public final Event getEvent() {
		return this.remoteEvent.event;
	}

	public final boolean needWait() {
		return this.remoteEvent.wait;
	}
}

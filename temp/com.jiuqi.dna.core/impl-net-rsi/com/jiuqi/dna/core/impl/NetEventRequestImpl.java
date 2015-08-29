package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.invoke.Event;

final class NetEventRequestImpl<TEvent extends Event> extends NetRequestImpl {

	final RemoteEventDataEx<TEvent> data;

	NetEventRequestImpl(NetSessionImpl session, RemoteEventDataEx<TEvent> data) {
		super(session);
		this.data = data;
	}

	@Override
	protected Object getDataObject() {
		return null;
	}
}
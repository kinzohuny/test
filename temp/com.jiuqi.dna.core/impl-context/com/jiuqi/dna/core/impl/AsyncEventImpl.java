package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.spi.work.WorkingThread;

final class AsyncEventImpl extends AsyncServiceInvoke {

	AsyncEventImpl(SessionImpl session, SpaceNode occurAt,
			EventListenerChain listeners, Event event) {
		super(session, occurAt);
		this.event = event;
		this.listeners = listeners;
		super.beginAsync();
	}

	private final EventListenerChain listeners;
	private final Event event;

	@Override
	protected final void workDoing(WorkingThread thread) throws Throwable {
		this.context.processEvents(this.listeners, this.event, false);
	}
}
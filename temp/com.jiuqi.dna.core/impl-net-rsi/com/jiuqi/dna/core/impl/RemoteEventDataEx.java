package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.def.obja.StructField;
import com.jiuqi.dna.core.invoke.Event;

@StructClass
class RemoteEventDataEx<TEvent extends Event> extends RemoteInvokeData {

	final TEvent event;

	RemoteEventDataEx(TEvent event) {
		this.event = event;
	}

	@StructField
	private RSIProperties properties;

	@Override
	public final Object getProp(int key) {
		return this.properties != null ? this.properties.get(key) : null;
	}

	@Override
	public final Object setProp(int key, Object value) {
		if (this.properties != null) {
			return this.properties.set(key, value);
		} else if (value != null) {
			this.properties = new RSIProperties();
			return this.properties.set(key, value);
		} else {
			return null;
		}
	}

	@Override
	final int getTransactionID() {
		return 0;
	}

	@Override
	final void invoke(ContextImpl<?, ?, ?> context) throws Throwable {
		super.invoke(context);
		context.dispatch(this.event, false, null);
	}
}
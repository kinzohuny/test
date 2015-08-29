package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.def.obja.StructField;
import com.jiuqi.dna.core.invoke.Task;

@StructClass
class RemoteTaskDataEx<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
		extends NetTaskRequestImpl.RemoteTaskData<TTask, TMethod> implements
		RSIPropertySet {

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

	public RemoteTaskDataEx(TTask task, TMethod method, int transactionID) {
		super(task, method, transactionID);
	}

}

package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.invoke.AsyncTask;
import com.jiuqi.dna.core.invoke.Task;

final class NetTaskRequestImpl<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
		extends NetRequestImpl implements AsyncTask<TTask, TMethod> {

	@StructClass
	static class RemoteTaskData<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
			extends RemoteInvokeData {

		final TTask task;
		final TMethod method;
		final int transactionID;

		@Override
		final int getTransactionID() {
			return this.transactionID;
		}

		RemoteTaskData(TTask task, TMethod method, int transactionID) {
			this.task = task;
			this.method = method;
			this.transactionID = transactionID;
		}

		@Override
		final void invoke(ContextImpl<?, ?, ?> context) throws Throwable {
			super.invoke(context);
			context.handle(this.task, this.method);
		}
	}

	private final RemoteTaskData<TTask, TMethod> data;

	@Override
	public final Object getDataObject() {
		return this.data;
	}

	public final TMethod getMethod() {
		return this.data.method;
	}

	public final TTask getTask() throws IllegalStateException {
		this.checkFinished();
		return this.data.task;
	}

	NetTaskRequestImpl(NetSessionImpl session, TTask task, TMethod method,
			int transactionID, RSIPropertiesSetter setter) {
		super(session);
		if (session.netNode.channel.remoteNodeInfo.ver.ver >= NetProtocolVersion.VER_3_0_1.ver) {
			RemoteTaskDataEx<TTask, TMethod> data = new RemoteTaskDataEx<TTask, TMethod>(task, method, transactionID);
			this.session.fillProperties(data);
			if (setter != null) {
				setter.setProperties(data);
			}
			this.data = data;
		} else {
			this.data = new RemoteTaskData<TTask, TMethod>(task, method, transactionID);
		}
	}

	NetTaskRequestImpl(NetSessionImpl session, TTask task, TMethod method,
			RSIPropertiesSetter setter) {
		this(session, task, method, 0, setter);
	}
}
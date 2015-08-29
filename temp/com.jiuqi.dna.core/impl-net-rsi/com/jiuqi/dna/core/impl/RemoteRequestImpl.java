package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.ContextKind;
import com.jiuqi.dna.core.SessionKind;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.impl.DataPackageReceiver.NetPackageReceivingStarter;
import com.jiuqi.dna.core.impl.NSerializer.NSerializerFactory;
import com.jiuqi.dna.core.impl.NUnserializer.ObjectTypeQuerier;
import com.jiuqi.dna.core.impl.NetNodeImpl.ITypeQueryHandler;
import com.jiuqi.dna.core.spi.work.Work;
import com.jiuqi.dna.core.spi.work.WorkingThread;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;

/**
 * Զ������
 * 
 * @author gaojingxin
 * 
 */
public class RemoteRequestImpl extends Work implements ObjectTypeQuerier,
		NSerializerFactoryProvider, ITypeQueryHandler {
	public final DataType findElseAsync(GUID typeID) {
		synchronized (this) {
			DataType dt = this.netNode.findDataTypeOrQueryRemote(typeID, this.requestID, true);
			if (dt == null) {
				final AsyncIOStub<?> ioStub = this.ioStub;
				if (ioStub == null) {
					throw new IllegalStateException("�첽IO�����Ч");
				}
				ioStub.suspend();
			}
			return dt;
		}
	}

	public final NSerializerFactory getNSerializerFactory() {
		return this.netNode.getNSerializerFactory();
	}

	private volatile AsyncIOStub<?> ioStub;

	public void typeResolved(boolean succeed) {
		synchronized (this) {
			final AsyncIOStub<?> ioStub = this.ioStub;
			if (ioStub != null) {
				if (succeed) {
					ioStub.resume();
				} else {
					this.netNode.unRegisterRemoteRequest(this.requestID);
					ioStub.cancel();
				}
			}
		}
	}

	private SessionImpl session;
	private final NetNodeImpl netNode;
	final int requestID;

	/**
	 * ȷ���Ự
	 * 
	 * @param sessionID
	 */
	private void ensureSession(long sessionID) {
		if (this.session == null) {
			final SessionManager sm = this.netNode.owner.application.sessionManager;
			this.session = sm.getOrFindSession(sessionID, false);
			if (this.session == null) {
				this.session = sm.newSession(SessionKind.REMOTE, User.anonym, null, null);
			}
		}
	}

	final void onNetNodeDisposed(Throwable e) {
		this.cancel();
	}

	RemoteRequestImpl(NetNodeImpl netNode, int requestID) {
		this.netNode = netNode;
		this.requestID = requestID;
	}

	// /////////////////////////
	// // ������ύ���/////////

	private static class ReceivingDataResolver extends
			SerializedDataResolver<RemoteRequestImpl> {

		@Override
		protected final boolean readHead(DataInputFragment fragment,
				RemoteRequestImpl attachment) {
			attachment.ensureSession(fragment.readLong());
			return false;
		};

		ReceivingDataResolver(ObjectTypeQuerier objectTypeQuerier) {
			super(objectTypeQuerier);
		}

		@Override
		public void onFragmentInFailed(RemoteRequestImpl attachment)
				throws Throwable {
			attachment.onReceiveFailed();
		}

		@Override
		protected void finishUnserialze(Object unserialzedObject,
				RemoteRequestImpl attachment) {
			attachment.onRequestReceived(unserialzedObject);
		}
	}

	final void startReceiveData(NetPackageReceivingStarter starter,
			byte requestPackageType) {
		switch (requestPackageType) {
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_POST:
			synchronized (this) {
				this.ioStub = starter.startReceivingPackage(new ReceivingDataResolver(this), this);
			}
			break;
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_CANCEL:
			starter.startReceivingPackage(requestCancelNotifyResolver, this);
			break;
		}
	}

	// // ������ύ���/////////
	// /////////////////////////

	// /////////////////////////
	// // �������///////////
	private static class ResposeDataBuilder extends
			SerializedDataBuilder<RemoteRequestImpl> {

		final byte requestPackageType;
		final float progress;

		public ResposeDataBuilder(Object objectToSerialize, float progress,
				byte requestPackageType) {
			super(objectToSerialize, INetPackageSign.REQUEST_PACKAGE);
			this.requestPackageType = requestPackageType;
			this.progress = progress;
		}

		@Override
		protected void writeHead(DataOutputFragment fragment,
				RemoteRequestImpl attachment) {
			super.writeHead(fragment, attachment);
			fragment.writeByte(this.requestPackageType);
			fragment.writeInt(attachment.requestID);
			final SessionImpl session = attachment.session;
			if (session != null) {
				fragment.writeLong(session.id);
			} else {
				fragment.writeLong(0);
			}
			fragment.writeFloat(this.progress);
		}

	}

	final void startSendResponse() {
		this.netNode.unRegisterRemoteRequest(this.requestID);
		final ResposeDataBuilder builder;
		if (this.exception != null) {
			builder = new ResposeDataBuilder(new ExceptionInfo(this.exception), this.finalProgress, NetRequestImpl.REQUEST_PACKAGE_TYPE_EXCEPTION);
			this.exception = null;// helpGC
		} else {
			Object obj = this.received;
			if ((obj instanceof RSIPropertySet) && Boolean.TRUE.equals(((RSIPropertySet) obj).getProp(RemoteInvokeData.PROP_NORETURN))) {
				obj = null;
			}
			builder = new ResposeDataBuilder(obj, this.finalProgress, NetRequestImpl.REQUEST_PACKAGE_TYPE_RESPONSE);
			this.received = null;// helpGC
		}
		this.netNode.channel.startSendingPackage(builder, this);
	}

	// // �������///////////
	// /////////////////////////

	// //////////////////////
	// // ȡ�����///////////

	/**
	 * 
	 * 
	 * @author gaojingxin
	 * 
	 */
	private static final DataFragmentBuilder<RemoteRequestImpl> requestCancelledNotifyBuilder = new DataFragmentBuilder<RemoteRequestImpl>() {
		public final boolean tryResetPackage(RemoteRequestImpl attachment) {
			return true;
		}

		public final void onFragmentOutFinished(RemoteRequestImpl attachment) {
			// Do nothing
		}

		public final void onFragmentOutError(RemoteRequestImpl attachment) {
			// Do nothing
		}

		public final boolean buildFragment(DataOutputFragment fragment,
				RemoteRequestImpl attachment) throws Throwable {
			fragment.writeByte(INetPackageSign.REQUEST_PACKAGE);
			fragment.writeByte(NetRequestImpl.REQUEST_PACKAGE_TYPE_CANCELED);
			fragment.writeInt(attachment.requestID);
			final SessionImpl session = attachment.session;
			if (session != null) {
				fragment.writeLong(session.id);
			} else {
				fragment.writeLong(0);
			}
			return false;
		}
	};

	private final void startSendCancelledNotify() {
		this.netNode.unRegisterRemoteRequest(this.requestID);
		this.netNode.channel.startSendingPackage(requestCancelledNotifyBuilder, this);
	}

	/**
	 * ��ȡ��֪ͨ����ʱ
	 */
	private final void onCancelReceived() {
		synchronized (this) {
			switch (this.getState()) {
			case POSTING:
			case STARTING:
				this.cancel();
				break;
			case PROCESSING:
				this.cancel();
			case FINISHED:
			case ERROR:
			case CANCELED:
			case CANCELING:
				return;
			default:
				throw this.illegalState();
			}
		}
		this.startSendCancelledNotify();
	}

	private final void onReceiveFailed() {
		// XXX
		this.netNode.unRegisterRemoteRequest(this.requestID);
	}

	private static final DataFragmentResolver<RemoteRequestImpl> requestCancelNotifyResolver = new DataFragmentResolver<RemoteRequestImpl>() {

		public boolean resolveFragment(DataInputFragment fragment,
				RemoteRequestImpl attachment) throws Throwable {
			attachment.onCancelReceived();
			return true;
		}

		public void onFragmentInFailed(RemoteRequestImpl attachment)
				throws Throwable {
		}
	};
	// // ȡ�����///////////
	// //////////////////////

	// //////////////////////
	// // ִ�����///////////
	private Object received;

	/**
	 * �����������������ʱ
	 */
	private void onRequestReceived(Object received) {
		this.received = received;
		this.netNode.owner.application.overlappedManager.startWork(this);
	}

	@Override
	protected void workDoing(WorkingThread thread) throws Throwable {
		if (this.received instanceof RemoteInvokeData) {
			final Site site = this.session.application.getDefaultSite();
			final RemoteInvokeData invokeData = (RemoteInvokeData) this.received;
			int transactionID = invokeData.getTransactionID();
			if (transactionID != Transaction.INVALID_TRANSACTION_ID) {
				this.context = this.session.newContext(site.getTransaction(transactionID, this.netNode), ContextKind.INTERNAL, this.netNode);
			} else {
				this.context = this.session.newContext(site.newTransaction(SessionImpl.getTransactionKind(ContextKind.TRANSIENT), null), ContextKind.TRANSIENT, this.netNode);
			}
			invokeData.invoke(this.context);
		} else {
			throw new UnsupportedOperationException("��֧�ֵ�Զ�̷���������ͣ�" + (this.received != null ? this.received.getClass() : "null class"));
		}
	}

	@Override
	protected final void doWork(WorkingThread thread) throws Throwable {
		try {
			super.doWork(thread);
		} finally {
			switch (this.getState()) {
			case FINISHED:
			case ERROR:
				this.startSendResponse();
				break;
			case CANCELED:
			default:
				this.startSendCancelledNotify();
				break;
			}
		}
	}

	@Override
	protected final void workFinalizing(Throwable e) {
		this.exception = e;
		try {
			if (e == null) {
				this.finalProgress = 1;
			} else {
				this.finalProgress = this.context != null ? -this.context.progress : 0;
			}
			if (this.context != null) {
				this.context.dispose();
				this.context = null;
			}
		} finally {
			if (this.session != null) {
				this.session.dispose(0);
				this.session = null;
			}
		}
	}

	@Override
	protected final void workCanceling() {
		final ContextImpl<?, ?, ?> context = this.context;
		if (context != null) {
			this.context.cancel();
		}
	}

	/**
	 * ִ����δ�ػ���쳣
	 */
	private Throwable exception;
	/**
	 * ���ս���
	 */
	private float finalProgress;
	/**
	 * ������
	 */
	private volatile ContextImpl<?, ?, ?> context;
	// // ִ�����///////////
	// //////////////////////
}

package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.util.List;

import com.jiuqi.dna.core.SiteState;
import com.jiuqi.dna.core.exception.ExceptionFromRemote;
import com.jiuqi.dna.core.impl.DataPackageReceiver.NetPackageReceivingStarter;
import com.jiuqi.dna.core.impl.NSerializer.NSerializerFactory;
import com.jiuqi.dna.core.impl.NUnserializer.ObjectTypeQuerier;
import com.jiuqi.dna.core.impl.NetNodeImpl.ITypeQueryHandler;
import com.jiuqi.dna.core.info.Info;
import com.jiuqi.dna.core.invoke.AsyncHandle;
import com.jiuqi.dna.core.invoke.AsyncState;
import com.jiuqi.dna.core.invoke.Waitable;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;

/**
 * Զ�̵�������<br>
 * 
 * ���ݷ��Ͱ���ʽ:
 * 
 * <pre>
 * 1. ������{
 * 	byte �����=0x01;
 *  byte ���ͱ��=0x01;
 * 	int ����ID;
 * 	long �ỰID;//������ʱ��ʾ�»Ự
 * 	short ���л��㷨�汾;
 * 	... �����������л����� 
 * }
 * </pre>
 * 
 * <pre>
 * 2. �����ȡ��֪ͨ{
 * 	byte �����=0x01;
 *  byte ���ͱ��=0x02;
 * 	int ��ȡ��������ID;
 * }
 * </pre>
 * 
 * <pre>
 * 3. ��Ӧ��ȡ��ȷ�ϴ�{
 * 	byte �����=0x01;
 *  byte ���ͱ��=0x03;
 * 	int ȡ��������ID;
 *  float �Ѿ������˵Ľ���
 * }
 * </pre>
 * 
 * <pre>
 * 4. ��Ӧ�˵�����Ϣ����{
 * 	byte �����=0x01;
 *  byte ���ͱ��=0x04;
 * 	int ��Ӧ������ID;
 * 	short ���л��㷨�汾;
 * 	...��Ϣ��������л�����
 * }
 * </pre>
 * 
 * <pre>
 * 5. ��Ӧ�˷����쳣{
 * 	byte �����=0x01;
 *  byte ���ͱ��=0x05;
 * 	int ��Ӧ������ID;
 * 	short ���л��㷨�汾;
 * 	...�쳣��������л�����
 * }
 * </pre>
 * 
 * <pre>
 * 6. ��Ӧ��������Ӧ{
 * 	byte �����=0x01;
 *  byte ���ͱ��=0x06;
 * 	int ��Ӧ������ID;
 * 	short ���л��㷨�汾;
 * 	...���ض�������л�����
 * }
 * </pre>
 * 
 * @author gaojingxin
 * 
 */
public abstract class NetRequestImpl implements AsyncHandle, Waitable,
		ObjectTypeQuerier, NSerializerFactoryProvider, ITypeQueryHandler {
	public final DataType findElseAsync(GUID typeID) {
		synchronized (this) {
			final DataType dt = this.session.netNode.findDataTypeOrQueryRemote(typeID, this.requestID, false);
			if (dt == null) {
				final AsyncIOStub<?> ioStub = this.resposeAsyncIOStub;
				if (ioStub == null) {
					throw new IllegalStateException("�첽IO�����Ч");
				}
				ioStub.suspend();
			}
			return dt;
		}
	}

	public final NSerializerFactory getNSerializerFactory() {
		return this.session.netNode.getNSerializerFactory();
	}

	/**
	 * ��������ݷ�������
	 */
	public final static byte REQUEST_PACKAGE_TYPE_POST = 0x01;
	/**
	 * �����ȡ������
	 */
	public final static byte REQUEST_PACKAGE_TYPE_CANCEL = 0x02;
	/**
	 * ��Ӧ��ȡ��ȷ�ϴ�����
	 */
	public final static byte REQUEST_PACKAGE_TYPE_CANCELED = 0x03;
	/**
	 * ��Ӧ�˵�����Ϣ��������
	 */
	public final static byte REQUEST_PACKAGE_TYPE_INFO = 0x04;
	/**
	 * ��Ӧ�˵����쳣��������
	 */
	public final static byte REQUEST_PACKAGE_TYPE_EXCEPTION = 0x05;
	/**
	 * ��Ӧ�˵��ö��󷵻�����
	 */
	public final static byte REQUEST_PACKAGE_TYPE_RESPONSE = 0x06;
	/**
	 * ����ID
	 */
	final int requestID;
	/**
	 * �����Ӧ�ĻỰ
	 */
	final NetSessionImpl session;
	/**
	 * �����쳣
	 */
	private Throwable exception;
	/**
	 * ״̬,������״̬<br>
	 * PROCESSING,PROCESSING_WAITED, CANCELING, CANCELING_WAITED,FINISHED,
	 * ERROR,CANCELED
	 */
	private volatile AsyncState state;
	private volatile float progress;

	/**
	 * ��Ϊ���л��Ķ���
	 */
	protected abstract Object getDataObject();

	private volatile AsyncIOStub<?> resposeAsyncIOStub;

	public void typeResolved(boolean succeed) {
		synchronized (this) {
			final AsyncIOStub<?> resposeAsyncIOStub = this.resposeAsyncIOStub;
			if (resposeAsyncIOStub != null) {
				if (succeed) {
					resposeAsyncIOStub.resume();
				} else {
					resposeAsyncIOStub.cancel();
				}
			}
		}
	}

	private void onSendingDataError() {
		try {
			synchronized (this) {
				switch (this.state) {
				case PROCESSING:
				case CANCELING:
					this.progress = -0.001f;
					this.exception = new IOException("Զ�̵��ã�Զ�̵���������ʧ�ܣ������������硢���л��ȷ���Ĵ�����");
					this.state = AsyncState.ERROR;
					this.notifyAll();
					return;
				default:
					throw new IllegalStateException("state: " + this.state);
				}
			}
		} finally {
			this.session.netNode.unRegisterLocalRequest(this.requestID);
		}
	}

	private void onRemoteCancelled(float progress) {
		try {
			synchronized (this) {
				switch (this.state) {
				case CANCELING:
					this.state = AsyncState.CANCELED;
					this.notifyAll();
					break;
				default:
					throw new IllegalStateException("state: " + this.state);
				}
			}
		} finally {
			this.session.netNode.unRegisterLocalRequest(this.requestID);
		}
	}

	private void onResponseDataReceived(Object received, float progress,
			byte requestPackageType) {
		try {
			this.progress = progress;
			switch (requestPackageType) {
			case REQUEST_PACKAGE_TYPE_EXCEPTION:
				if (received instanceof ExceptionInfo) {
					this.exception = ((ExceptionInfo) received).toException();
					synchronized (this) {
						this.resposeAsyncIOStub = null;
						switch (this.state) {
						case CANCELING:
						case PROCESSING:
							this.state = AsyncState.ERROR;
							this.notifyAll();
							break;
						default:
							throw new IllegalStateException("state: " + this.state);
						}
					}
				} else {
					this.onDataReceivingFailed(requestPackageType);
				}
				break;
			case REQUEST_PACKAGE_TYPE_RESPONSE:
				if (received == this.getDataObject()) {
					synchronized (this) {
						this.resposeAsyncIOStub = null;
						switch (this.state) {
						case CANCELING:
						case PROCESSING:
							this.state = AsyncState.FINISHED;
							this.notifyAll();
							break;
						default:
							throw new IllegalStateException("state: " + this.state);
						}
					}
				} else {
					this.onDataReceivingFailed(requestPackageType);
				}
				break;
			}
		} finally {
			this.session.netNode.unRegisterLocalRequest(this.requestID);
		}
	}

	private void onDataReceivingFailed(byte requestPackageType) {
		try {
			synchronized (this) {
				this.resposeAsyncIOStub = null;
				switch (this.state) {
				case FINISHED:
				case ERROR:
				case CANCELED:
					return;
				case PROCESSING:
				case CANCELING:
					this.state = AsyncState.ERROR;
					this.notifyAll();
					if (this.progress > 0) {
						this.progress = -this.progress;
					} else if (this.progress == 0) {
						this.progress = -0.001f;
					}
					this.exception = new IOException("�������ݳ���");
					break;
				default:
					throw new IllegalStateException("state: " + this.state);
				}
			}
		} finally {
			this.session.netNode.unRegisterLocalRequest(this.requestID);
		}
	}

	/**
	 * ����ķ������ݹ�����
	 * 
	 * @author gaojingxin
	 * 
	 */
	private static class RequestSendDataBuilder extends
			SerializedDataBuilder<NetRequestImpl> {
		RequestSendDataBuilder(Object objectToSerialize) {
			super(objectToSerialize, INetPackageSign.REQUEST_PACKAGE);
		}

		@Override
		public void onFragmentOutFinished(NetRequestImpl attachment) {
		}

		@Override
		public void onFragmentOutError(NetRequestImpl attachment) {
			attachment.onSendingDataError();
		}

		@Override
		protected final void writeHead(DataOutputFragment fragment,
				NetRequestImpl attachment) {
			super.writeHead(fragment, attachment);
			// ��ʼ״̬
			fragment.writeByte(REQUEST_PACKAGE_TYPE_POST);
			// ����ID
			fragment.writeInt(attachment.requestID);
			// �ỰID
			fragment.writeLong(attachment.session.getRemoteSessionID());
		}
	}

	/**
	 * ��ʼ��������
	 */
	final void startSendingRequest() {
		this.session.netNode.registerLocalRequest(this);
		try {
			this.state = AsyncState.PROCESSING;
			this.session.netNode.channel.startSendingPackage(new RequestSendDataBuilder(this.getDataObject()), this);
		} catch (Throwable e) {
			this.session.netNode.unRegisterLocalRequest(this.requestID);
			throw Utils.tryThrowException(e);
		}
	}

	/**
	 * ����ķ������ݹ�����
	 * 
	 * @author gaojingxin
	 * 
	 */
	private static class ResponseDataResolver extends
			SerializedDataResolver<NetRequestImpl> {

		private final byte requestPackageType;
		private float progress;

		public ResponseDataResolver(ObjectTypeQuerier objectTypeQuerier,
				Object destHint, byte requestPackageType) {
			super(objectTypeQuerier, destHint);
			this.requestPackageType = requestPackageType;
		}

		@Override
		protected final boolean readHead(DataInputFragment fragment,
				NetRequestImpl attachment) {
			// attachment.state
			if (super.readHead(fragment, attachment)) {
				return true;
			}
			attachment.session.setRemoteSessionID(fragment.readLong());// SessionID
			this.progress = fragment.readFloat();
			return false;
		}

		@Override
		public void onFragmentInFailed(NetRequestImpl attachment)
				throws Throwable {
			attachment.onDataReceivingFailed(this.requestPackageType);
		}

		@Override
		protected void finishUnserialze(Object unserialzedObject,
				NetRequestImpl attachment) {
			attachment.onResponseDataReceived(unserialzedObject, this.progress, this.requestPackageType);
		}

	}

	/**
	 * ��ʼ���ܽ�����������صĵ�����Ϣ��ȡ��ȷ�ϣ��쳣�����
	 */
	final void startReceivingRespose(NetPackageReceivingStarter starter,
			byte requestPackageType) {
		final DataFragmentResolver<NetRequestImpl> resolver;
		switch (requestPackageType) {
		case REQUEST_PACKAGE_TYPE_EXCEPTION:
			resolver = new ResponseDataResolver(this, null, requestPackageType);
			break;
		case REQUEST_PACKAGE_TYPE_RESPONSE:
			Object obj = this.getDataObject();
			if ((obj instanceof RSIPropertySet) && Boolean.TRUE.equals(((RSIPropertySet) obj).getProp(RemoteInvokeData.PROP_NORETURN))) {
				obj = null;
			}
			resolver = new ResponseDataResolver(this, obj, requestPackageType);
			break;
		case REQUEST_PACKAGE_TYPE_CANCELED:
			resolver = requestCancelledNotifyResolver;
			break;
		default:
			throw new IllegalStateException();
		}
		this.resposeAsyncIOStub = starter.startReceivingPackage(resolver, this);
	}

	/**
	 * ����ķ������ݹ�����
	 * 
	 * @author gaojingxin
	 * 
	 */
	private static final DataFragmentBuilder<NetRequestImpl> requestCancelNotifyBuilder = new DataFragmentBuilder<NetRequestImpl>() {
		public final boolean tryResetPackage(NetRequestImpl attachment) {
			return true;
		}

		public final void onFragmentOutFinished(NetRequestImpl attachment) {
			// Do nothing
		}

		public final void onFragmentOutError(NetRequestImpl attachment) {
			// Do nothing
		}

		public final boolean buildFragment(DataOutputFragment fragment,
				NetRequestImpl attachment) throws Throwable {
			fragment.writeByte(INetPackageSign.REQUEST_PACKAGE);
			fragment.writeByte(REQUEST_PACKAGE_TYPE_CANCEL);
			fragment.writeInt(attachment.requestID);
			return false;
		}
	};

	protected final void checkFinished() throws IllegalStateException {
		if (!this.state.stopped) {
			throw new IllegalStateException("Զ�̲�����δ���");
		}
	}

	NetRequestImpl(NetSessionImpl session) {
		this.session = session;
		this.requestID = session.netNode.owner.newRequestID();
	}

	public final NetSessionImpl getNetSession() {
		return this.session;
	}

	public final void cancel() {
		synchronized (this) {
			switch (this.state) {
			case FINISHED:
			case ERROR:
			case CANCELED:
			case CANCELING:
				return;
			case PROCESSING:
				this.state = AsyncState.CANCELING;
				break;
			default:
				throw new IllegalStateException("state: " + this.state);
			}
		}
		// ����ȡ��֪ͨ����Ϊ��֪��Զ��ʵ��״̬
		this.session.netNode.channel.startSendingPackage(requestCancelNotifyBuilder, this);
	}

	final void onNetNodeDisposed(Throwable e) {
		synchronized (this) {
			this.resposeAsyncIOStub = null;
			if (!this.state.stopped) {
				this.exception = e;
				this.state = AsyncState.ERROR;
				this.notifyAll();
			}
		}
	}

	private static final DataFragmentResolver<NetRequestImpl> requestCancelledNotifyResolver = new DataFragmentResolver<NetRequestImpl>() {

		public boolean resolveFragment(DataInputFragment fragment,
				NetRequestImpl attachment) throws Throwable {
			fragment.readLong();// SessionID
			attachment.onRemoteCancelled(fragment.readFloat());
			return true;
		}

		public void onFragmentInFailed(NetRequestImpl attachment)
				throws Throwable {
			attachment.onRemoteCancelled(0);
		}
	};

	public final int fetchInfos(List<Info> to) {
		// TODO Auto-generated method stub
		return 0;
	}

	public final Throwable getException() {
		return this.exception;
	}

	final void waitAndTryThrow() {
		try {
			this.waitStop(0);
		} catch (InterruptedException e) {
			throw Utils.tryThrowException(e);
		}
		switch (this.state) {
		case ERROR:
			if (this.exception != null) {
				throw Utils.tryThrowException(this.exception);
			} else {
				throw new ExceptionFromRemote("δ֪��Զ�̵��ô���", ExceptionFromRemote.class.getName());
			}
		case CANCELED:
			if (this.exception != null) {
				throw Utils.tryThrowException(this.exception);
			} else {
				throw new ExceptionFromRemote("Զ�̵������ȡ��", ExceptionFromRemote.class.getName());
			}
		}
	}

	public final float getProgress() {
		return this.progress;
	}

	public final AsyncState getState() {
		return this.state;
	}

	public void waitStop(long timeout) throws InterruptedException {
		if (this.session.site.state == SiteState.INITING) {
			throw new UnsupportedOperationException("��֧�ֵ�վ�����ʼ���ڼ��������첽���ã���Щ���ý���վ���ʼ����ɺ�Ż�����");
		}
		this.internalWaitStop(timeout);
	}

	final void internalWaitStop(long timeout) throws InterruptedException {
		synchronized (this) {
			long outTime = 0;
			for (;;) {
				if (this.state.stopped) {
					return;// ��������
				}
				if (timeout != 0) {
					if (outTime == 0) {
						outTime = System.nanoTime() / 1000000L + timeout;
					} else {
						timeout = outTime - System.nanoTime() / 1000000L;
						if (timeout <= 0) {
							return;// ��ʱ
						}
					}
				}
				this.wait(timeout);
			}
		}
	}
}

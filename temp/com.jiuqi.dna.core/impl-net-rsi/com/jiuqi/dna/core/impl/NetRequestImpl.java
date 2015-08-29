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
 * 远程调用请求<br>
 * 
 * 数据发送包格式:
 * 
 * <pre>
 * 1. 请求体{
 * 	byte 类别标记=0x01;
 *  byte 类型标记=0x01;
 * 	int 请求ID;
 * 	long 会话ID;//等于零时表示新会话
 * 	short 序列化算法版本;
 * 	... 请求对象的序列化数据 
 * }
 * </pre>
 * 
 * <pre>
 * 2. 发起端取消通知{
 * 	byte 类别标记=0x01;
 *  byte 类型标记=0x02;
 * 	int 待取消的请求ID;
 * }
 * </pre>
 * 
 * <pre>
 * 3. 响应端取消确认答复{
 * 	byte 类别标记=0x01;
 *  byte 类型标记=0x03;
 * 	int 取消的请求ID;
 *  float 已经处理了的进度
 * }
 * </pre>
 * 
 * <pre>
 * 4. 响应端调用消息回馈{
 * 	byte 类别标记=0x01;
 *  byte 类型标记=0x04;
 * 	int 对应的请求ID;
 * 	short 序列化算法版本;
 * 	...消息对象的序列化数据
 * }
 * </pre>
 * 
 * <pre>
 * 5. 响应端返回异常{
 * 	byte 类别标记=0x01;
 *  byte 类型标记=0x05;
 * 	int 对应的请求ID;
 * 	short 序列化算法版本;
 * 	...异常对象的序列化数据
 * }
 * </pre>
 * 
 * <pre>
 * 6. 响应端数据响应{
 * 	byte 类别标记=0x01;
 *  byte 类型标记=0x06;
 * 	int 对应的请求ID;
 * 	short 序列化算法版本;
 * 	...返回对象的序列化数据
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
					throw new IllegalStateException("异步IO存根无效");
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
	 * 请求的数据发送类型
	 */
	public final static byte REQUEST_PACKAGE_TYPE_POST = 0x01;
	/**
	 * 请求的取消类型
	 */
	public final static byte REQUEST_PACKAGE_TYPE_CANCEL = 0x02;
	/**
	 * 响应端取消确认答复类型
	 */
	public final static byte REQUEST_PACKAGE_TYPE_CANCELED = 0x03;
	/**
	 * 响应端调用消息回馈类型
	 */
	public final static byte REQUEST_PACKAGE_TYPE_INFO = 0x04;
	/**
	 * 响应端调用异常回馈类型
	 */
	public final static byte REQUEST_PACKAGE_TYPE_EXCEPTION = 0x05;
	/**
	 * 响应端调用对象返回类型
	 */
	public final static byte REQUEST_PACKAGE_TYPE_RESPONSE = 0x06;
	/**
	 * 请求ID
	 */
	final int requestID;
	/**
	 * 请求对应的会话
	 */
	final NetSessionImpl session;
	/**
	 * 错误异常
	 */
	private Throwable exception;
	/**
	 * 状态,有如下状态<br>
	 * PROCESSING,PROCESSING_WAITED, CANCELING, CANCELING_WAITED,FINISHED,
	 * ERROR,CANCELED
	 */
	private volatile AsyncState state;
	private volatile float progress;

	/**
	 * 作为序列化的对象
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
					this.exception = new IOException("远程调用：远程调用请求发送失败，可能由于网络、序列化等方面的错误导致");
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
					this.exception = new IOException("接收数据出错");
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
	 * 请求的发送数据构造器
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
			// 初始状态
			fragment.writeByte(REQUEST_PACKAGE_TYPE_POST);
			// 请求ID
			fragment.writeInt(attachment.requestID);
			// 会话ID
			fragment.writeLong(attachment.session.getRemoteSessionID());
		}
	}

	/**
	 * 开始发送数据
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
	 * 请求的发送数据构造器
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
	 * 开始接受结果，包括返回的调用信息，取消确认，异常，结果
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
	 * 请求的发送数据构造器
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
			throw new IllegalStateException("远程操作还未完成");
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
		// 发送取消通知，因为不知道远端实际状态
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
				throw new ExceptionFromRemote("未知的远程调用错误", ExceptionFromRemote.class.getName());
			}
		case CANCELED:
			if (this.exception != null) {
				throw Utils.tryThrowException(this.exception);
			} else {
				throw new ExceptionFromRemote("远程调用因故取消", ExceptionFromRemote.class.getName());
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
			throw new UnsupportedOperationException("不支持等站点待初始化期间启动的异步调用，这些调用将在站点初始化完成后才会启动");
		}
		this.internalWaitStop(timeout);
	}

	final void internalWaitStop(long timeout) throws InterruptedException {
		synchronized (this) {
			long outTime = 0;
			for (;;) {
				if (this.state.stopped) {
					return;// 结束返回
				}
				if (timeout != 0) {
					if (outTime == 0) {
						outTime = System.nanoTime() / 1000000L + timeout;
					} else {
						timeout = outTime - System.nanoTime() / 1000000L;
						if (timeout <= 0) {
							return;// 超时
						}
					}
				}
				this.wait(timeout);
			}
		}
	}
}

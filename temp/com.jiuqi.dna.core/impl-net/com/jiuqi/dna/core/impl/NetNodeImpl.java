package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.DataPackageReceiver.NetPackageReceivingStarter;
import com.jiuqi.dna.core.impl.NSerializer.NSerializerFactory;
import com.jiuqi.dna.core.impl.NUnserializer.ObjectTypeQuerier;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ObjectDataType;

/**
 * 网络节点
 * 
 * @author gaojingxin
 * 
 */
final class NetNodeImpl implements NSerializerFactoryProvider {

	/**
	 * 状态：初始状态
	 */
	final static byte STATE_INIT = 0x01;
	/**
	 * 状态：启动完毕
	 */
	final static byte STATE_READY = 0x02;
	/**
	 * 状态：已失效
	 */
	final static byte STATE_DISABLED = 0x03;

	private NetNodeImpl nextInCluster;

	/**
	 * 管理器
	 */
	final NetNodeManagerImpl owner;
	/**
	 * 所属集群
	 */
	final NetClusterImpl cluster;
	/**
	 * 对应的网络通道
	 */
	final NetChannelImpl channel;
	/**
	 * 向远程序列化数据的序列化器工厂
	 */
	private final NSerializerFactory serializerFactory;

	private volatile byte state;

	public NetNodeImpl(NetNodeManagerImpl owner, NetClusterImpl cluster,
			NetChannelImpl netChannel, NetNodeImpl nextInCluster) {
		if (netChannel == null) {
			throw new NullArgumentException("netChannel");
		}
		if (cluster == null) {
			throw new NullArgumentException("cluster");
		}
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		this.serializerFactory = NSerializer.getRemoteCompatibleFactory(netChannel.getRemoteSerializeVersion());
		this.channel = netChannel;
		this.cluster = cluster;
		this.owner = owner;
		this.nextInCluster = nextInCluster;
	}

	final byte getState() {
		return this.state;
	}

	final void setState(byte state) {
		synchronized (this) {
			if (state > this.state) {
				if (state == STATE_DISABLED && this.state == STATE_READY && (this.cluster instanceof NetSelfClusterImpl)) {
					System.out.println(String.format("集群：远程节点退出集群，索引号[%d]ID[%s]", this.channel.getRemoteNodeIndex(), this.channel.getRemoteNodeID()));
				}
				this.state = state;
			}
		}
	}

	final NetNodeImpl getNextNodeInCluster() {
		return this.nextInCluster;
	}

	final void setNextNodeInCluster(NetNodeImpl node) {
		this.nextInCluster = node;
	}

	final void incKeepAlive(boolean isKeepAlive) {
		this.channel.incKeepAlive(isKeepAlive);
	}

	final boolean isKeepAlive() {
		return this.channel.isKeepAlive();
	}

	public final NSerializerFactory getNSerializerFactory() {
		return this.serializerFactory;
	}

	public NetNodeManagerImpl getOwner() {
		return this.owner;
	}

	// //////////////////////////////////////////
	// //// 本地请求有关/////////////////////////

	public final NetSessionImpl newSession(Site site, String username,
			GUID passwordMD5) {
		return new NetSessionImpl(this, site, username, passwordMD5);
	}

	/**
	 * 创建与该节点间的网络会话
	 */
	public final NetSessionImpl newSession(Site site) {
		return this.newSession(site, null, null);
	}

	/**
	 * 发向远程的请求
	 */
	private final IntKeyMap<NetRequestImpl> requestsToRemote = new IntKeyMap<NetRequestImpl>();

	final void registerLocalRequest(NetRequestImpl request) {
		synchronized (this.requestsToRemote) {
			if (this.requestsToRemote.put(request.requestID, request) != null) {
				return;
			}
		}
		this.channel.incKeepAlive(true);
	}

	final NetRequestImpl unRegisterLocalRequest(int requestID) {
		final NetRequestImpl req;
		synchronized (this.requestsToRemote) {
			req = this.requestsToRemote.remove(requestID);
		}
		if (req != null) {
			this.channel.incKeepAlive(false);
		}
		return req;
	}

	// //// 本地请求有关/////////////////////////
	// //////////////////////////////////////////

	// //////////////////////////////////////////
	// //// 远程请求有关/////////////////////////
	/**
	 * 来自远程的请求
	 */
	private final IntKeyMap<RemoteRequestImpl> requestsFromRemote = new IntKeyMap<RemoteRequestImpl>();

	final RemoteRequestImpl unRegisterRemoteRequest(int requestID) {
		final RemoteRequestImpl req;
		synchronized (this.requestsFromRemote) {
			req = this.requestsFromRemote.remove(requestID);
		}
		if (req != null) {
			this.channel.incKeepAlive(false);
		}
		return req;
	}

	final void onRequestPackageArriving(DataInputFragment fragment,
			NetPackageReceivingStarter starter) {
		final byte requestPackageType = fragment.readByte();
		final int requestID = fragment.readInt();
		switch (requestPackageType) {
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_POST:
			final RemoteRequestImpl remoteRequest = new RemoteRequestImpl(this, requestID);
			synchronized (this.requestsFromRemote) {
				RemoteRequestImpl oldRemoteRequest = this.requestsFromRemote.put(requestID, remoteRequest);
				if (oldRemoteRequest != null) {
					// 出现了重复ID的请求，忽略旧请求
					this.requestsFromRemote.put(requestID, oldRemoteRequest);
					return;
				}
			}
			this.channel.incKeepAlive(true);
			remoteRequest.startReceiveData(starter, requestPackageType);
			break;
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_CANCEL:
			final RemoteRequestImpl existsRemoteRequest;
			synchronized (this.requestsFromRemote) {
				existsRemoteRequest = this.requestsFromRemote.get(requestID);
			}
			if (existsRemoteRequest != null) {
				existsRemoteRequest.startReceiveData(starter, requestPackageType);
			}
			break;
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_INFO:
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_EXCEPTION:
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_RESPONSE:
		case NetRequestImpl.REQUEST_PACKAGE_TYPE_CANCELED:
			final NetRequestImpl request;
			synchronized (this.requestsToRemote) {
				request = this.requestsToRemote.get(requestID);
			}
			if (request != null && request.session.netNode.channel == this.channel) {
				request.startReceivingRespose(starter, requestPackageType);
			}
			break;
		}
	}

	// //// 远程请求有关/////////////////////////
	// //////////////////////////////////////////
	// //////////////////////////////////////////
	// //// 类型请求有关/////////////////////////

	static interface ITypeQueryHandler {
		public void typeResolved(boolean succeed);
	}

	@StructClass
	static class RemoteTypeQuery {
		final GUID typeID;
		final int requestID;
		final boolean remoteRequest;
		final boolean simpleQuery;

		RemoteTypeQuery(GUID typeID, boolean simpleQuery, int requestID,
				boolean remoteRequest) {
			this.typeID = DataTypeBase.calcComponentTypeID(typeID);
			this.simpleQuery = simpleQuery;
			this.requestID = requestID;
			this.remoteRequest = remoteRequest;
		}

		RemoteTypeQuery(RemoteTypeQueryResult sample, boolean simpleQuery) {
			this.simpleQuery = simpleQuery;
			this.typeID = sample.typeID;
			this.requestID = sample.requestID;
			this.remoteRequest = sample.remoteRequest;
		}
	}

	/**
	 * 简单类型结果
	 * 
	 * @author gaojingxin
	 * 
	 */
	@StructClass
	private static class RemoteTypeQueryResult {
		final GUID typeID;
		/**
		 * 对应的Java类类型，如果找不到则返回null
		 */
		private final String className;
		// final Object typeInfo;//
		// SerializationStructInfo，SerializationEnumInfo
		final int requestID;
		final boolean remoteRequest;

		public final String getClassName() {
			if (this.className != null) {
				final int i = this.className.indexOf('@');
				if (i >= 0) {
					return this.className.substring(0, i);
				}
			}
			return this.className;
		}

		RemoteTypeQueryResult(RemoteTypeQuery query, ObjectDataType dataType) {
			if (dataType != null && query.simpleQuery) {// 暂时只支持简单类型请求
				this.className = dataType.getJavaClass().getName();
				// this.typeInfo = null;
			} else {
				this.className = null;
				// this.typeInfo = null;
			}
			this.typeID = query.typeID;
			this.requestID = query.requestID;
			this.remoteRequest = query.remoteRequest;
		}
	}

	/**
	 * 类型请求和结果包还原器
	 * 
	 * @author gaojingxin
	 * 
	 */
	private static final class TypePackageResolver extends
			SerializedDataResolver<NetNodeImpl> {
		TypePackageResolver() {
			super(ObjectTypeQuerier.staticObjectTypeQuerier);
		}

		@Override
		protected void finishUnserialze(Object unserialzedObject,
				NetNodeImpl attachment) {
			final Class<?> oc = unserialzedObject.getClass();
			if (oc == RemoteTypeQuery.class) {
				// 查询类型请求
				final RemoteTypeQuery rtq = (RemoteTypeQuery) unserialzedObject;
				final DataType dt = attachment.owner.application.findDataType(rtq.typeID);
				final ObjectDataType odt = dt instanceof ObjectDataType ? (ObjectDataType) dt : null;
				attachment.postTypePackageObject(new RemoteTypeQueryResult(rtq, odt));
			} else if (oc == RemoteTypeQueryResult.class) {
				// 查询类型结果
				final RemoteTypeQueryResult rtqr = (RemoteTypeQueryResult) unserialzedObject;
				final String className = rtqr.getClassName();
				if (className != null && className.length() > 0) {
					try {
						// 远程有该类型
						final Class<?> clazz = attachment.owner.application.tryLoadClass(className);
						if (clazz != null) {
							// 本地有该类
							final DataType odi = DataTypeBase.dataTypeOfJavaClass(clazz);
							if (rtqr.typeID.equals(odi.getID())) {
								// 唤醒等待类型的请求
								attachment.onTypeQueryResult(rtqr.requestID, rtqr.remoteRequest, true);
								return;
							}
						}
						// 本地没有该类，或类型不符，则发起完整的类型请求
						attachment.postTypePackageObject(new RemoteTypeQuery(rtqr, false));
					} catch (Throwable e) {
						attachment.onTypeQueryResult(rtqr.requestID, rtqr.remoteRequest, false);
						throw new UnsupportedOperationException("远程调用：本地装载类型信息时出错，类型[" + className + "]", e);
					}
				} else {
					// 远程没有该类型，唤醒挂起的远程调用
					attachment.onTypeQueryResult(rtqr.requestID, rtqr.remoteRequest, false);
					throw new UnsupportedOperationException("远程调用：向远端主机请求类型信息失败，类型ID[" + rtqr.typeID + "]");
				}
			}
		}
	}

	private final static class TypePackageBuilder extends
			SerializedDataBuilder<NetNodeImpl> {
		public TypePackageBuilder(Object toPost) {
			super(toPost, INetPackageSign.TYPE_PACKAGE);
		}

		@Override
		public void onFragmentOutError(NetNodeImpl attachment) {
			try {
				Object toPost = this.objectToSerialize;
				if (toPost instanceof RemoteTypeQuery) {
					// 唤醒挂起的任务
					RemoteTypeQuery rtq = (RemoteTypeQuery) toPost;
					attachment.onTypeQueryResult(rtq.requestID, rtq.remoteRequest, false);
				} else if (toPost instanceof RemoteTypeQueryResult) {
					throw new IllegalStateException("远程调用：回复类型信息请求时发生错误，类型ID[" + ((RemoteTypeQueryResult) toPost).typeID + "]");
				}
			} finally {
				this.objectToSerialize = null;
			}
		}
	}

	final AsyncIOStub<NetNodeImpl> postTypePackageObject(Object toPost) {
		return this.channel.startSendingPackage(new TypePackageBuilder(toPost), this);
	}

	/**
	 * 查找类型或者请求远程
	 */
	final DataType findDataTypeOrQueryRemote(GUID typeID, int requestID,
			boolean remoteRequest) {
		final DataType dt = this.owner.application.findDataType(typeID);
		if (dt == null) {
			this.postTypePackageObject(new RemoteTypeQuery(typeID, true, requestID, remoteRequest));
		}
		return dt;
	}

	final void onTypePackageArriving(NetPackageReceivingStarter starter) {
		starter.startReceivingPackage(new TypePackageResolver(), this);
	}

	private final void onTypeQueryResult(int requestID, boolean remoteRequest,
			boolean succeed) {
		if (remoteRequest) {
			final RemoteRequestImpl rq;
			synchronized (this.requestsFromRemote) {
				rq = this.requestsFromRemote.get(requestID);
			}
			if (rq != null) {
				rq.typeResolved(succeed);
			}
		} else {
			final NetRequestImpl rq;
			synchronized (this.requestsToRemote) {
				rq = this.requestsToRemote.get(requestID);
			}
			if (rq != null) {
				rq.typeResolved(succeed);
			}
		}
	}

	// //// 类型请求有关/////////////////////////
	// //////////////////////////////////////////

	final void dispose() {
		final Throwable e = new IOException("网络通信：与远程节点断开连接，节点ID[" + this.channel.getRemoteNodeID() + "]索引号[" + this.channel.getRemoteNodeIndex() + "]");
		synchronized (this.requestsFromRemote) {
			if (!this.requestsFromRemote.isEmpty()) {
				this.requestsFromRemote.visitAll(new ValueVisitor<RemoteRequestImpl>() {
					public void visit(int key, RemoteRequestImpl value) {
						value.onNetNodeDisposed(e);
					}
				});
				this.requestsFromRemote.clear();
			}
		}
		synchronized (this.requestsToRemote) {
			if (!this.requestsToRemote.isEmpty()) {
				this.requestsToRemote.visitAll(new ValueVisitor<NetRequestImpl>() {
					public void visit(int key, NetRequestImpl value) {
						value.onNetNodeDisposed(e);
					}
				});
				this.requestsToRemote.clear();
			}
		}
	}

	@Override
	public String toString() {
		return String.format("NetNode[index = %d, channel = %s]", this.channel.getRemoteNodeIndex(), this.channel);
	}
}

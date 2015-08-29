package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.TransientContainer.TransientProvider;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.type.GUID;

final class NetNodeProviderImpl extends TransientProvider {
	private final boolean newTrans;

	private NetNodeProviderImpl(TransientProxy<?> proxy,
			Transaction transaction, String userName, GUID passwordMD5,
			boolean newTrans, NetNodeToken remoteNodeInfo,
			NetNodeImpl callerNode) {
		super(proxy, transaction);
		this.remoteNodeInfo = remoteNodeInfo;
		this.callerNode = callerNode;
		this.transaction = transaction;
		this.userName = (userName != null && userName.length() == 0) ? null : userName;
		this.passwordMD5 = passwordMD5;
		this.newTrans = newTrans;
		if (this.userName != null && this.userName.length() > 0) {
			NetProtocolVersion ver;
			if (this.callerNode != null) {
				ver = this.callerNode.channel.remoteNodeInfo.ver;
			} else {
				ver = remoteNodeInfo.ver;
			}
			if (ver.ver < NetProtocolVersion.VER_3_0_1.ver) {
				throw new UnsupportedOperationException("不支持指定用户的远程调用，[" + NetProtocolVersion.VER_3_0_1.tag + "]及以上版本才支持[username]和[passwordMD5]参数，当前远程服务版本为[" + ver.tag + "]");
			}
		}
	}

	NetNodeProviderImpl(TransientProxy<?> proxy, Transaction transaction,
			NetNodeToken remoteNodeInfo, String userName, GUID passwordMD5,
			boolean newTrans) {
		this(proxy, transaction, userName, passwordMD5, newTrans, remoteNodeInfo, null);
		if (remoteNodeInfo == null) {
			throw new NullArgumentException("info");
		}
		if (remoteNodeInfo.ncl == null) {
			throw new IllegalArgumentException("网络地址不可为空");
		}
	}

	NetNodeProviderImpl(TransientProxy<?> proxy, Transaction transaction,
			String userName, GUID passwordMD5, NetNodeImpl callerNode,
			boolean newTrans) {
		this(proxy, transaction, userName, passwordMD5, newTrans, null, callerNode);
		if (callerNode == null) {
			throw new NullArgumentException("callerNode");
		}
	}

	final Transaction transaction;
	final NetNodeToken remoteNodeInfo;
	final String userName;
	final GUID passwordMD5;
	private NetSessionImpl session;
	private final NetNodeImpl callerNode;

	@Override
	final Object getOwner() {
		return null;
	}

	final NetSessionImpl using() {
		if (this.session == null) {
			NetNodeImpl netNode;
			if (this.callerNode != null && this.callerNode.getState() != NetNodeImpl.STATE_DISABLED) {
				netNode = this.callerNode;
			} else {
				NetNodeManagerImpl nodeManager = this.transaction.site.application.netNodeManager;
				netNode = nodeManager.ensureNetNode(this.remoteNodeInfo);
			}
			netNode.incKeepAlive(true);
			this.session = netNode.newSession(this.transaction.site, this.userName, this.passwordMD5);
		}
		return this.session;
	}

	final <TResult, TKey1, TKey2, TKey3> NetQueryRequestImpl<TResult, TKey1, TKey2, TKey3> newRequest(
			byte resultType, Class<TResult> resultClass,
			Operation<? super TResult> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object[] otherKeys) {
		return this.using().newRequest(resultType, resultClass, operation, key1, key2, key3, otherKeys);
	}

	final <TResult, TKey1, TKey2, TKey3> NetQueryRequestImpl<TResult, TKey1, TKey2, TKey3> postAndWaitRequest(
			byte resultType, Class<TResult> resultClass,
			Operation<? super TResult> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object[] otherKeys) {
		final NetQueryRequestImpl<TResult, TKey1, TKey2, TKey3> req = this.using().newRequest(resultType, resultClass, operation, key1, key2, key3, otherKeys);
		req.waitAndTryThrow();
		return req;
	}

	final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> NetTaskRequestImpl<TTask, TMethod> newRequest(
			TTask task, TMethod method) {
		return this.newTrans ? this.using().newRequest(task, method) : this.using().newRemoteTransactionRequest(task, method, this.transaction);
	}

	final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> void postAndWaitRequest(
			TTask task, TMethod method) {
		final NetTaskRequestImpl<TTask, TMethod> req = this.newTrans ? this.using().newRequest(task, method) : this.using().newRemoteTransactionRequest(task, method, this.transaction);
		req.waitAndTryThrow();
	}

	@Override
	public final void unuse() {
		if (this.session != null) {
			this.session.netNode.incKeepAlive(false);
			this.session = null;
		}
	}
}

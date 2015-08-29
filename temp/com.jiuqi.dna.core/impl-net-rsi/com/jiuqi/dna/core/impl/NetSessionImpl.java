package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.type.GUID;

/**
 * 网络会话，为两点间网络传输数据确定上层会话的底层接口。
 * 
 * @author gaojingxin
 * 
 */
public class NetSessionImpl {
	final NetNodeImpl netNode;
	final String username;
	final GUID passwordMD5;
	private volatile long remoteSessionID;

	final long getRemoteSessionID() {
		return this.remoteSessionID;
	}

	final void setRemoteSessionID(long id) {
		this.remoteSessionID = id;
	}

	private boolean closed;
	final Site site;

	public NetSessionImpl(NetNodeImpl netNode, Site site, String username,
			GUID passwordMD5) {
		this.netNode = netNode;
		this.site = site;
		this.username = username;
		this.passwordMD5 = passwordMD5;
	}

	final void fillProperties(RemoteInvokeData data) {
		if (this.remoteSessionID == 0) {
			data.setProp(RemoteInvokeData.PROP_USERNAME, this.username);
			data.setProp(RemoteInvokeData.PROP_PASSWORDMD5, this.passwordMD5);
		}
	}

	/**
	 * 创建一个新请求
	 */
	final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> NetTaskRequestImpl<TTask, TMethod> newRequest(
			TTask task, TMethod method) {
		final NetTaskRequestImpl<TTask, TMethod> request = new NetTaskRequestImpl<TTask, TMethod>(this, task, method, null);
		if (this.netNode.cluster == this.site.getNetCluster()) {
			throw new UnsupportedOperationException("不支持集群内节点之间进行远程调用");
		}
		request.startSendingRequest();
		return request;
	}

	final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> NetTaskRequestImpl<TTask, TMethod> newRemoteTransactionRequest(
			TTask task, TMethod method, Transaction transaction) {
		transaction.markAsGlobal();
		final NetTaskRequestImpl<TTask, TMethod> request = new NetTaskRequestImpl<TTask, TMethod>(this, task, method, transaction.id, null);
		request.startSendingRequest();
		return request;
	}

	final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> NetTaskRequestImpl<TTask, TMethod> newRemoteTransactionRequest(
			TTask task, TMethod method, Transaction transaction,
			RSIPropertiesSetter setter) {
		transaction.markAsGlobal();
		final NetTaskRequestImpl<TTask, TMethod> request = new NetTaskRequestImpl<TTask, TMethod>(this, task, method, transaction.id, setter);
		request.startSendingRequest();
		return request;
	}

	final <TResult, TKey1, TKey2, TKey3> NetQueryRequestImpl<TResult, TKey1, TKey2, TKey3> newRequest(
			byte resultType, Class<TResult> resultClass,
			Operation<? super TResult> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object[] otherKeys) {
		final NetQueryRequestImpl<TResult, TKey1, TKey2, TKey3> request = new NetQueryRequestImpl<TResult, TKey1, TKey2, TKey3>(this, resultType, resultClass, operation, key1, key2, key3, otherKeys);
		request.startSendingRequest();
		return request;
	}

	/**
	 * 是否已经关闭
	 */
	public final boolean isClosed() {
		return this.closed;
	}

	/**
	 * 关闭会话。
	 */
	public synchronized void close() {
		if (!this.closed) {
			// TODO
			this.closed = true;
		}
	}
}

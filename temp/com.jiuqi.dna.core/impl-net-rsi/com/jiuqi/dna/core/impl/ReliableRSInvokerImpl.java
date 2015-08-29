package com.jiuqi.dna.core.impl;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.TreeNode;
import com.jiuqi.dna.core.TreeNodeFilter;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.misc.MissingObjectException;
import com.jiuqi.dna.core.misc.SortUtil;
import com.jiuqi.dna.core.service.ReliableRemoteServiceInvoker;
import com.jiuqi.dna.core.type.GUID;

final class ReliableRSInvokerImpl implements ReliableRemoteServiceInvoker,
		ReliableRSI {

	private static final Map<URL, ReliableRSINodeToken> REMOTE_NODE_TOKEN_MAP;

	static {
		REMOTE_NODE_TOKEN_MAP = Collections.synchronizedMap(new WeakHashMap<URL, ReliableRSINodeToken>());
	}

	ReliableRSInvokerImpl(final URL url, final String userName,
			final GUID passwordMD5, final Proxy proxy) {
		try {
			this.url = new URL(url, LISTEN_PATH);
		} catch (MalformedURLException e) {
			throw Utils.tryThrowException(e);
		}
		this.ordinalURL = url;
		this.userName = userName;
		this.passwordMD5 = passwordMD5;
		this.proxy = proxy;
		this.ensureRemoteNodeToken();
	}

	private final void ensureRemoteNodeToken() {
		final ReliableRSINodeToken remoteNodeToken = REMOTE_NODE_TOKEN_MAP.get(this.url);
		if (remoteNodeToken != null) {
			this.remoteNodeToken = remoteNodeToken;
		} else {
			try {
				final HttpURLConnection connection = (HttpURLConnection) (this.proxy == null ? this.url.openConnection() : this.url.openConnection(this.proxy));
				try {
					connection.setRequestMethod("GET");
					connection.setRequestProperty("User-Agent", DnaHttpClient.USER_AGENT);
					connection.setRequestProperty(HTTP_HEADER_ACTION, ACTION_GETINFOMATION);
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						final String responseAction = connection.getHeaderField(HTTP_HEADER_ACTION);
						if (ACTION_GETINFOMATION.equals(responseAction)) {
							this.resolveRemoteNodeToken(connection);
						} else {
							throw new IOException("收到无效的远程调用应答消息。动作为" + responseAction);
						}
					} else {
						throw new IOException("收到错误的HTTP回复:" + connection.getResponseCode() + connection.getResponseMessage());
					}
				} finally {
					connection.disconnect();
				}
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final <TMethod extends Enum<TMethod>> void handleTask(
			final Task<TMethod> task, final TMethod method) {
		final RemoteTaskDataEx taskInfo = new RemoteTaskDataEx(task, method, Transaction.INVALID_TRANSACTION_ID);
		taskInfo.setProp(RemoteInvokeData.PROP_USERNAME, this.userName);
		taskInfo.setProp(RemoteInvokeData.PROP_PASSWORDMD5, this.passwordMD5);
		this.remoteInvoke(taskInfo);
	}

	public final <TEvent extends Event> void occurEvent(final TEvent event) {
		final RemoteEventDataEx<TEvent> eventInfo = new RemoteEventDataEx<TEvent>(event);
		eventInfo.setProp(RemoteInvokeData.PROP_USERNAME, this.userName);
		eventInfo.setProp(RemoteInvokeData.PROP_PASSWORDMD5, this.passwordMD5);
		this.remoteInvoke(eventInfo);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final <TFacade, TKey1, TKey2, TKey3> Object execQuery(
			final byte resultType, final Class<TFacade> facadeClass,
			final TKey1 key1, final TKey2 key2, final TKey3 key3,
			final Object... otherKeys) {
		final RemoteQueryDataEx queryInfo = new RemoteQueryDataEx(resultType, facadeClass, null, key1, key2, key3, otherKeys);
		queryInfo.setProp(RemoteInvokeData.PROP_USERNAME, this.userName);
		queryInfo.setProp(RemoteInvokeData.PROP_PASSWORDMD5, this.passwordMD5);
		this.remoteInvoke(queryInfo);
		return queryInfo.result;
	}

	private final void remoteInvoke(final RemoteInvokeData invokeInfo) {
		if (this.remoteNodeToken == null) {
			this.ensureRemoteNodeToken();
		}
		try {
			for (;;) {
				final HttpURLConnection connection = (HttpURLConnection) (this.proxy == null ? this.url.openConnection() : this.url.openConnection(this.proxy));
				try {
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Cookie", String.format("%s=.%s", NetConnector.ROUTE_COOKIE_NAME, this.remoteNodeToken.indexInCluster));
					connection.setRequestProperty("User-Agent", DnaHttpClient.USER_AGENT);
					connection.setRequestProperty(HTTP_HEADER_ACTION, ACTION_INVOKE);
					connection.setRequestProperty(HTTP_HEADER_HIGTHEST_HANDLER_VERSION, this.remoteNodeToken.higthestHandlerVersion);
					connection.setRequestProperty(HTTP_HEADER_HIGTHEST_SERIALIZE_VERSION, this.remoteNodeToken.higthestSerializeVersion);
					connection.setRequestProperty(HTTP_HEADER_HANDLER_VERSION, String.valueOf(this.remoteNodeToken.handler.version));
					connection.setDoOutput(true);
					connection.setDoInput(true);
					this.remoteNodeToken.handler.sendRequest(this.remoteNodeToken.serializerFactory.newNSerializer(), connection.getOutputStream(), invokeInfo);
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						final String responseAction = connection.getHeaderField(HTTP_HEADER_ACTION);
						if (ACTION_INVOKE.equals(responseAction)) {
							final Object response = this.remoteNodeToken.handler.handleResponse(connection.getInputStream(), invokeInfo);
							if (response != invokeInfo) {
								if (response instanceof ExceptionInfo) {
									throw ((ExceptionInfo) response).toException();
								} else {
									throw new InvalidObjectException("收到无效的远程调用响应对象。对象为" + (response == null ? "null" : response.toString()));
								}
							}
							break;
						} else if (ACTION_GETINFOMATION.equals(responseAction)) {
							this.resolveRemoteNodeToken(connection);
							continue;
						} else {
							throw new IOException("收到无效的远程调用应答消息。动作为" + responseAction);
						}
					} else {
						throw new IOException("收到错误的HTTP回复:" + connection.getResponseCode() + connection.getResponseMessage());
					}
				} finally {
					connection.disconnect();
				}
			}
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final boolean resolveRemoteNodeToken(
			final HttpURLConnection connection) throws IOException {
		final String netNodeIndexString = connection.getHeaderField(NetConnector.HTTP_HEADER_NODE_INDEX);
		final String higthestHandlerVersionString = connection.getHeaderField(HTTP_HEADER_HIGTHEST_HANDLER_VERSION);
		final String higthestSerializeVersionString = connection.getHeaderField(HTTP_HEADER_HIGTHEST_SERIALIZE_VERSION);
		if (netNodeIndexString != null && higthestHandlerVersionString != null && higthestSerializeVersionString != null && netNodeIndexString.length() > 0 && higthestHandlerVersionString.length() > 0 && higthestSerializeVersionString.length() > 0) {
			final int remoteNodeIndex = Integer.valueOf(netNodeIndexString);
			final short remoteSerializeVersion = Short.valueOf(higthestSerializeVersionString);
			final short remoteRRSIVersion = Short.valueOf(higthestHandlerVersionString);
			final ReliableRSINodeToken remoteNodeToken = this.remoteNodeToken;
			if (remoteNodeToken == null || remoteNodeToken.indexInCluster != remoteNodeIndex || remoteNodeToken.handler.version != remoteRRSIVersion || remoteNodeToken.serializerFactory.version != remoteSerializeVersion) {
				if (remoteSerializeVersion < NSerializeBase_1_1.SERIALIZE_VERSION) {
					throw new RuntimeException(String.format("远程主机支持的网络协议版本过低，创建远程服务调用器失败。%x", remoteSerializeVersion));
				} else {
					this.remoteNodeToken = new ReliableRSINodeToken(remoteNodeIndex, remoteSerializeVersion, remoteRRSIVersion);
					REMOTE_NODE_TOKEN_MAP.put(this.url, this.remoteNodeToken);
				}
				return true;
			} else {
				return false;
			}
		} else {
			throw new IOException("接收到无效的HTTP应答。");
		}
	}

	private final <TFacade> void filteAndSortList(final List<TFacade> list,
			final Filter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator) {
		int ls = list.size();
		if (ls > 0) {
			if (filter != null) {
				int acceptedCount = 0;
				TFacade item;
				for (int i = 0; i < ls; i++) {
					item = list.get(i);
					if (filter.accept(item)) {
						if (acceptedCount != i) {
							list.set(acceptedCount, item);
						}
						acceptedCount++;
					}
				}
				if (acceptedCount == 0 && ls > 0) {
					list.clear();
					ls = 0;
				} else {
					for (int index = ls; ls >= acceptedCount; ls--) {
						list.remove(index);
					}
					ls = acceptedCount;
				}
			}
			if (ls > 0 && sortComparator != null) {
				SortUtil.sort(list, sortComparator);
			}
		}
	}

	private final <TFacade> void filteAndSortTree(final TreeNode<TFacade> root,
			final TreeNodeFilter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator) {
		((TreeNodeImpl<TFacade>) root).filterAndSortRecursively(filter, 0, 0, sortComparator);
	}

	final URL ordinalURL;
	final URL url;
	final String userName;
	final GUID passwordMD5;
	final Proxy proxy;
	private ReliableRSINodeToken remoteNodeToken;

	public final URL getURL() {
		return this.ordinalURL;
	}

	public final Proxy getProxy() {
		return this.proxy;
	}

	public final String getUserName() {
		return this.userName;
	}

	public final float getResistance() {
		return 3;
	}

	public final <TMethod extends Enum<TMethod>> void handle(
			final Task<TMethod> task, final TMethod method) {
		if (task == null) {
			throw new NullArgumentException("task");
		}
		if (method == null) {
			throw new NullArgumentException("method");
		}
		this.handleTask(task, method);
	}

	public final void handle(final SimpleTask task) {
		if (task == null) {
			throw new NullArgumentException("task");
		}
		this.handleTask(task, None.NONE);
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TFacade get(final Class<TFacade> facadeClass)
			throws UnsupportedOperationException, MissingObjectException {
		checkArgument(facadeClass);
		final Object result = this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, facadeClass, null, null, null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的单例对象");
		}
		return (TFacade) result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TFacade get(final Class<TFacade> facadeClass,
			final Object key) throws UnsupportedOperationException,
			MissingObjectException {
		checkArgument(facadeClass, key);
		final Object result = this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, facadeClass, key, null, null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为[" + key + "]对象");
		}
		return (TFacade) result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TFacade get(final Class<TFacade> facadeClass,
			final Object key1, final Object key2)
			throws UnsupportedOperationException, MissingObjectException {
		checkArgument(facadeClass, key1, key2);
		final Object result = this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, facadeClass, key1, key2, null);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为[" + key1 + ", " + key2 + "]对象");
		}
		return (TFacade) result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TFacade get(final Class<TFacade> facadeClass,
			final Object key1, final Object key2, final Object key3)
			throws UnsupportedOperationException, MissingObjectException {
		checkArgument(facadeClass, key1, key2, key3);
		final Object result = this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, facadeClass, key1, key2, key3);
		if (result == null) {
			throw new MissingObjectException("找不到[" + facadeClass + "]类的键为[" + key1 + ", " + key2 + ", " + key3 + "]对象");
		}
		return (TFacade) result;
	}

	public final <TFacade> TFacade get(final Class<TFacade> facadeClass,
			final Object key1, final Object key2, final Object key3,
			final Object... keys) throws UnsupportedOperationException,
			MissingObjectException {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TFacade find(final Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		checkArgument(facadeClass);
		return (TFacade) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, facadeClass, null, null, null));
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TFacade find(final Class<TFacade> facadeClass,
			final Object key) throws UnsupportedOperationException {
		checkArgument(facadeClass, key);
		return (TFacade) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, facadeClass, key, null, null));
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TFacade find(final Class<TFacade> facadeClass,
			final Object key1, final Object key2)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2);
		return (TFacade) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, facadeClass, key1, key2, null));
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TFacade find(final Class<TFacade> facadeClass,
			final Object key1, final Object key2, final Object key3)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2, key3);
		return (TFacade) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, facadeClass, key1, key2, key3));
	}

	public final <TFacade> TFacade find(final Class<TFacade> facadeClass,
			final Object key1, final Object key2, final Object key3,
			final Object... keys) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		checkArgument(facadeClass);
		return (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, null, null, null));
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Filter<? super TFacade> filter)
			throws UnsupportedOperationException {
		checkArgument(facadeClass);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		final List<TFacade> result = (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, null, null, null));
		this.filteAndSortList(result, filter, null);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		checkArgument(facadeClass);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final List<TFacade> result = (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, null, null, null));
		this.filteAndSortList(result, null, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Filter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		checkArgument(facadeClass);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final List<TFacade> result = (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, null, null, null));
		this.filteAndSortList(result, filter, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass, final Object key)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key);
		return (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, key, null, null));
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Filter<? super TFacade> filter, final Object key)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		final List<TFacade> result = (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, key, null, null));
		this.filteAndSortList(result, filter, null);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Comparator<? super TFacade> sortComparator, final Object key)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final List<TFacade> result = (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, key, null, null));
		this.filteAndSortList(result, null, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Filter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator, final Object key)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final List<TFacade> result = (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, key, null, null));
		this.filteAndSortList(result, filter, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass, final Object key1,
			final Object key2) throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2);
		return (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, key1, key2, null));
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Filter<? super TFacade> filter, final Object key1,
			final Object key2) throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		final List<TFacade> result = (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, key1, key2, null));
		this.filteAndSortList(result, filter, null);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Comparator<? super TFacade> sortComparator,
			final Object key1, final Object key2)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final List<TFacade> result = (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, key1, key2, null));
		this.filteAndSortList(result, null, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Filter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator,
			final Object key1, final Object key2)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final List<TFacade> result = (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, key1, key2, null));
		this.filteAndSortList(result, filter, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass, final Object key1,
			final Object key2, final Object key3)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2, key3);
		return (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, key1, key2, key3));
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Filter<? super TFacade> filter, final Object key1,
			final Object key2, final Object key3)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2, key3);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		final List<TFacade> result = (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, key1, key2, key3));
		this.filteAndSortList(result, filter, null);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Comparator<? super TFacade> sortComparator,
			final Object key1, final Object key2, final Object key3)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2, key3);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final List<TFacade> result = (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, key1, key2, key3));
		this.filteAndSortList(result, null, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Filter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator,
			final Object key1, final Object key2, final Object key3)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2, key3);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final List<TFacade> result = (List<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, facadeClass, key1, key2, key3));
		this.filteAndSortList(result, filter, sortComparator);
		return result;
	}

	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass, final Object key1,
			final Object key2, final Object key3, final Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Filter<? super TFacade> filter, final Object key1,
			final Object key2, final Object key3, final Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Comparator<? super TFacade> sortComparator,
			final Object key1, final Object key2, final Object key3,
			final Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	public final <TFacade> List<TFacade> getList(
			final Class<TFacade> facadeClass,
			final Filter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator,
			final Object key1, final Object key2, final Object key3,
			final Object... otherKeys) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		checkArgument(facadeClass);
		return (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, null, null, null));
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final TreeNodeFilter<? super TFacade> filter)
			throws UnsupportedOperationException {
		checkArgument(facadeClass);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		final TreeNode<TFacade> result = (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, null, null, null));
		this.filteAndSortTree(result, filter, null);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		checkArgument(facadeClass);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final TreeNode<TFacade> result = (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, null, null, null));
		this.filteAndSortTree(result, null, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final TreeNodeFilter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		checkArgument(facadeClass);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final TreeNode<TFacade> result = (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, null, null, null));
		this.filteAndSortTree(result, filter, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass, final Object key)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key);
		return (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, key, null, null));
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final TreeNodeFilter<? super TFacade> filter, final Object key)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		final TreeNode<TFacade> result = (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, key, null, null));
		this.filteAndSortTree(result, filter, null);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final Comparator<? super TFacade> sortComparator, final Object key)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final TreeNode<TFacade> result = (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, key, null, null));
		this.filteAndSortTree(result, null, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final TreeNodeFilter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator, final Object key)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final TreeNode<TFacade> result = (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, key, null, null));
		this.filteAndSortTree(result, filter, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass, final Object key1,
			final Object key2) throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2);
		return (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, key1, key2, null));
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final TreeNodeFilter<? super TFacade> filter, final Object key1,
			final Object key2) throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		final TreeNode<TFacade> result = (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, key1, key2, null));
		this.filteAndSortTree(result, filter, null);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final Comparator<? super TFacade> sortComparator,
			final Object key1, final Object key2)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final TreeNode<TFacade> result = (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, key1, key2, null));
		this.filteAndSortTree(result, null, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final TreeNodeFilter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator,
			final Object key1, final Object key2)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final TreeNode<TFacade> result = (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, key1, key2, null));
		this.filteAndSortTree(result, filter, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass, final Object key1,
			final Object key2, final Object key3)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2, key3);
		return (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, key1, key2, key3));
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final TreeNodeFilter<? super TFacade> filter, final Object key1,
			final Object key2, final Object key3)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2, key3);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		final TreeNode<TFacade> result = (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, key1, key2, key3));
		this.filteAndSortTree(result, filter, null);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final Comparator<? super TFacade> sortComparator,
			final Object key1, final Object key2, final Object key3)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2, key3);
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final TreeNode<TFacade> result = (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, key1, key2, key3));
		this.filteAndSortTree(result, null, sortComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final TreeNodeFilter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator,
			final Object key1, final Object key2, final Object key3)
			throws UnsupportedOperationException {
		checkArgument(facadeClass, key1, key2, key3);
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		final TreeNode<TFacade> result = (TreeNode<TFacade>) (this.execQuery(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, facadeClass, key1, key2, key3));
		this.filteAndSortTree(result, filter, sortComparator);
		return result;
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass, final Object key1,
			final Object key2, final Object key3, final Object... otherKeys)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final TreeNodeFilter<? super TFacade> filter, final Object key1,
			final Object key2, final Object key3, final Object... otherKeys)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final Comparator<? super TFacade> sortComparator,
			final Object key1, final Object key2, final Object key3,
			final Object... otherKeys) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			final Class<TFacade> facadeClass,
			final TreeNodeFilter<? super TFacade> filter,
			final Comparator<? super TFacade> sortComparator,
			final Object key1, final Object key2, final Object key3,
			final Object... otherKeys) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public final boolean isValid() {
		return true;
	}

	public final void checkValid() {
		// do nothing
	}

	private static final void checkArgument(final Class<?> facadeClass) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
	}

	private static final void checkArgument(final Class<?> facadeClass,
			final Object key) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
	}

	private static final void checkArgument(final Class<?> facadeClass,
			final Object key1, final Object key2) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
	}

	private static final void checkArgument(final Class<?> facadeClass,
			final Object key1, final Object key2, final Object key3) {
		if (facadeClass == null) {
			throw new NullArgumentException("facadeClass");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
	}

}

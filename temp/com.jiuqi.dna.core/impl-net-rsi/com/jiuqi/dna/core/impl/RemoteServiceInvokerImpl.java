package com.jiuqi.dna.core.impl;

import java.net.Proxy;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.TreeNode;
import com.jiuqi.dna.core.TreeNodeFilter;
import com.jiuqi.dna.core.exception.DeadLockException;
import com.jiuqi.dna.core.invoke.AsyncHandle;
import com.jiuqi.dna.core.invoke.AsyncTask;
import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.misc.MissingObjectException;
import com.jiuqi.dna.core.service.AsyncInfo;
import com.jiuqi.dna.core.service.RemoteServiceInvoker;
import com.jiuqi.dna.core.type.GUID;

final class RemoteServiceInvokerImpl implements
		TransientProxy<NetNodeProviderImpl>, RemoteServiceInvoker {

	public final NetNodeProviderImpl getProvider() {
		return this.provider;
	}

	public final URL getURL() {
		return this.provider.remoteNodeInfo.ncl;
	}

	public final String getUserName() {
		return this.provider.userName;
	}

	public final Proxy getProxy() {
		return this.provider.remoteNodeInfo.proxy;
	}

	final NetNodeProviderImpl provider;

	RemoteServiceInvokerImpl(Transaction transaction,
			NetNodeToken remoteNodeInfo, String userName, GUID passwordMD5,
			boolean newTrans) {
		this.provider = new NetNodeProviderImpl(this, transaction, remoteNodeInfo, userName, passwordMD5, newTrans);
	}

	RemoteServiceInvokerImpl(Transaction transaction, String userName,
			GUID passwordMD5, NetNodeImpl callerNode, boolean newTrans) {
		this.provider = new NetNodeProviderImpl(this, transaction, userName, passwordMD5, callerNode, newTrans);
	}

	public final void unuse() {
		this.provider.unuse();
	}

	public final <TResult> NetQueryRequestImpl<TResult, ?, ?, ?> asyncGet(
			Class<TResult> resultClass) {
		return this.provider.newRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, resultClass, null, null, null, null, null);
	}

	public final <TResult, TKey> NetQueryRequestImpl<TResult, TKey, ?, ?> asyncGet(
			Class<TResult> resultClass, TKey key) {
		return this.provider.newRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, resultClass, null, key, null, null, null);
	}

	public final <TResult, TKey1, TKey2> NetQueryRequestImpl<TResult, TKey1, TKey2, ?> asyncGet(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2) {
		return this.provider.newRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, resultClass, null, key1, key2, null, null);
	}

	public final <TResult, TKey1, TKey2, TKey3> NetQueryRequestImpl<TResult, TKey1, TKey2, TKey3> asyncGet(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		return this.provider.newRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, resultClass, null, key1, key2, key3, null);
	}

	public final <TResult> NetQueryRequestImpl<TResult, ?, ?, ?> asyncGetList(
			Class<TResult> resultClass) {
		return this.provider.newRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, null, null, null, null);
	}

	public final <TResult, TKey1> NetQueryRequestImpl<TResult, TKey1, ?, ?> asyncGetList(
			Class<TResult> resultClass, TKey1 key1) {
		return this.provider.newRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, null, null, null);
	}

	public final <TResult, TKey1, TKey2> NetQueryRequestImpl<TResult, TKey1, TKey2, ?> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2) {
		return this.provider.newRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, null, null);
	}

	public final <TResult, TKey1, TKey2, TKey3> NetQueryRequestImpl<TResult, TKey1, TKey2, TKey3> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		return this.provider.newRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, key3, null);
	}

	public final <TFacade> NetQueryRequestImpl<TFacade, ?, ?, ?> asyncGetTreeNode(
			Class<TFacade> resultClass) {
		return this.provider.newRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, null, null, null, null);
	}

	public final <TFacade, TKey> NetQueryRequestImpl<TFacade, TKey, ?, ?> asyncGetTreeNode(
			Class<TFacade> resultClass, TKey key) {
		return this.provider.newRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key, null, null, null);
	}

	public final <TFacade, TKey1, TKey2> NetQueryRequestImpl<TFacade, TKey1, TKey2, ?> asyncGetTreeNode(
			Class<TFacade> resultClass, TKey1 key1, TKey2 key2) {
		return this.provider.newRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, null, null);
	}

	public final <TFacade, TKey1, TKey2, TKey3> NetQueryRequestImpl<TFacade, TKey1, TKey2, TKey3> asyncGetTreeNode(
			Class<TFacade> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		return this.provider.newRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, key3, null);
	}

	public final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> NetTaskRequestImpl<TTask, TMethod> asyncHandle(
			TTask task, TMethod method) {
		return this.provider.newRequest(task, method);
	}

	public final <TSimpleTask extends SimpleTask> NetTaskRequestImpl<TSimpleTask, None> asyncHandle(
			TSimpleTask task) {
		return this.provider.newRequest(task, None.NONE);
	}

	public final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> asyncHandle(
			TTask task, TMethod method, AsyncInfo info) {
		throw new UnsupportedOperationException("远程调用暂不支持该方法");
	}

	public final <TSimpleTask extends SimpleTask> AsyncTask<TSimpleTask, None> asyncHandle(
			TSimpleTask task, AsyncInfo info) {
		throw new UnsupportedOperationException("远程调用暂不支持该方法");
	}

	public final AsyncHandle occur(Event event) {
		// NetEventRequestImpl<Event> request = new
		// NetEventRequestImpl<Event>(this.provider.using(), new
		// RemoteEventDataEx<Event>(event));
		// request.startSendingRequest();
		// return request;
		throw new UnsupportedOperationException("远程调用暂不支持该方法");
	}

	public final boolean dispatch(Event event) {
		throw new UnsupportedOperationException("远程调用暂不支持该方法");
	}

	public final boolean dispatch(Event event, Object key1) {
		throw new UnsupportedOperationException("远程调用暂不支持该方法");
	}

	public final float getResistance() {
		return 2;
	}

	public final <TMethod extends Enum<TMethod>> void handle(
			Task<TMethod> task, TMethod method) throws DeadLockException {
		this.provider.postAndWaitRequest(task, method);
	}

	public final void handle(SimpleTask task) throws DeadLockException {
		this.provider.postAndWaitRequest(task, None.NONE);
	}

	public final void waitFor(AsyncHandle one, AsyncHandle... others)
			throws InterruptedException {
		ContextImpl.internalWaitFor(0, one, others);
	}

	public final void waitFor(long timeout, AsyncHandle one,
			AsyncHandle... others) throws InterruptedException {
		ContextImpl.internalWaitFor(timeout, one, others);
	}

	public final <TFacade> TFacade find(Class<TFacade> resultClass)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, resultClass, null, null, null, null, null).getResult();
	}

	public final <TFacade> TFacade find(Class<TFacade> resultClass, Object key)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, resultClass, null, key, null, null, null).getResult();
	}

	public final <TFacade> TFacade find(Class<TFacade> resultClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, resultClass, null, key1, key2, null, null).getResult();
	}

	public final <TFacade> TFacade find(Class<TFacade> resultClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, resultClass, null, key1, key2, key3, null).getResult();
	}

	public final <TFacade> TFacade find(Class<TFacade> resultClass,
			Object key1, Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND, resultClass, null, key1, key2, key3, keys).getResult();
	}

	public final <TFacade> TFacade get(Class<TFacade> resultClass)
			throws UnsupportedOperationException, MissingObjectException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_GET, resultClass, null, null, null, null, null).getResult();
	}

	public final <TFacade> TFacade get(Class<TFacade> resultClass, Object key)
			throws UnsupportedOperationException, MissingObjectException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_GET, resultClass, null, key, null, null, null).getResult();
	}

	public final <TFacade> TFacade get(Class<TFacade> resultClass, Object key1,
			Object key2) throws UnsupportedOperationException,
			MissingObjectException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_GET, resultClass, null, key1, key2, null, null).getResult();
	}

	public final <TFacade> TFacade get(Class<TFacade> resultClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException,
			MissingObjectException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_GET, resultClass, null, key1, key2, key3, null).getResult();
	}

	public final <TFacade> TFacade get(Class<TFacade> resultClass, Object key1,
			Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException, MissingObjectException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_GET, resultClass, null, key1, key2, key3, keys).getResult();
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, null, null, null, null).getResultList();
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, null, null, null, null).getResultList(filter, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, null, null, null, null).getResultList(null, sortComparator);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, null, null, null, null).getResultList(filter, sortComparator);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Object key) throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key, null, null, null).getResultList();
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key, null, null, null).getResultList(filter, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key, null, null, null).getResultList(null, sortComparator);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key, null, null, null).getResultList(filter, sortComparator);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, null, null).getResultList();
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, null, null).getResultList(filter, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, null, null).getResultList(null, sortComparator);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, null, null).getResultList(filter, sortComparator);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, key3, null).getResultList();
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, key3, null).getResultList(filter, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, key3, null).getResultList(null, sortComparator);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, key3, null).getResultList(filter, sortComparator);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, key3, otherKeys).getResultList();
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys) {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, key3, otherKeys).getResultList(filter, null);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, key3, otherKeys).getResultList(null, sortComparator);
	}

	public final <TFacade> List<TFacade> getList(Class<TFacade> resultClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST, resultClass, null, key1, key2, key3, otherKeys).getResultList(filter, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass) throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, null, null, null, null).getResultTreeNode();
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, TreeNodeFilter<? super TFacade> filter)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, null, null, null, null).getResultTreeNode(filter, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, null, null, null, null).getResultTreeNode(null, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, null, null, null, null).getResultTreeNode(filter, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, Object key)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key, null, null, null).getResultTreeNode();
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, TreeNodeFilter<? super TFacade> filter,
			Object key) throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key, null, null, null).getResultTreeNode(filter, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key, null, null, null).getResultTreeNode(null, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key, null, null, null).getResultTreeNode(filter, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, Object key1, Object key2)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key1, key2, null, null).getResultTreeNode();
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, TreeNodeFilter<? super TFacade> filter,
			Object key1, Object key2) throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key1, key2, null, null).getResultTreeNode(filter, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key1, key2, null, null).getResultTreeNode(null, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key1, key2, null, null).getResultTreeNode(filter, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key1, key2, key3, null).getResultTreeNode();
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, TreeNodeFilter<? super TFacade> filter,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key1, key2, key3, null).getResultTreeNode(filter, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key1, key2, key3, null).getResultTreeNode(null, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key1, key2, key3, null).getResultTreeNode(filter, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, Object key1, Object key2, Object key3,
			Object... otherKeys) throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key1, key2, key3, otherKeys).getResultTreeNode();
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, TreeNodeFilter<? super TFacade> filter,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key1, key2, key3, otherKeys).getResultTreeNode(filter, null);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key1, key2, key3, otherKeys).getResultTreeNode(null, sortComparator);
	}

	public final <TFacade> TreeNode<TFacade> getTreeNode(
			Class<TFacade> resultClass, TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		return this.provider.postAndWaitRequest(NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE, resultClass, null, key1, key2, key3, otherKeys).getResultTreeNode(filter, sortComparator);
	}

	public final void checkValid() {
		this.provider.transaction.checkContextValid();
	}

	public final boolean isValid() {
		return this.provider.transaction.isContextValid();
	}

}

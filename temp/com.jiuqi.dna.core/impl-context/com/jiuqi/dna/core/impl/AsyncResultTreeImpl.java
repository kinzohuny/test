package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.TreeNode;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.ThreeKeyAsyncTreeNodeResult;
import com.jiuqi.dna.core.spi.work.WorkingThread;

/**
 * 异步列表请求实现
 * 
 * @author gaojingxin
 * 
 */
final class AsyncResultTreeNodeImpl<TResult, TKey1, TKey2, TKey3> extends
		AsyncServiceInvoke implements
		ThreeKeyAsyncTreeNodeResult<TResult, TKey1, TKey2, TKey3> {

	AsyncResultTreeNodeImpl(
			SessionImpl session,
			SpaceNode occurAt,
			Class<TResult> resultClass,
			TKey1 key1,
			TKey2 key2,
			TKey3 key3,
			ServiceInvokeeBase<TResult, Context, TKey1, TKey2, TKey3> resultTreeProvider) {
		super(session, occurAt);
		if (resultTreeProvider == null) {
			throw new NullArgumentException("resultTreeProvider");
		}
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		this.resultClass = resultClass;
		this.key1 = key1;
		this.key2 = key2;
		this.key3 = key3;
		this.resultTreeProvider = resultTreeProvider;
		super.beginAsync();
	}

	private final Class<TResult> resultClass;
	private final TKey1 key1;
	private final TKey2 key2;
	private final TKey3 key3;
	private final ServiceInvokeeBase<TResult, Context, TKey1, TKey2, TKey3> resultTreeProvider;
	private volatile TreeNode<TResult> resultTreeNode;

	@Override
	public
	final ConcurrentController getConcurrentController() {
		return this.resultTreeProvider.getConcurrentController();
	}

	@Override
	protected final void workDoing(WorkingThread thread) {
		this.context.serviceProvideTree(this.resultTreeNode = new TreeNodeImpl<TResult>(null, null), this.resultTreeProvider, this.key1, this.key2, this.key3);
	}

	public final TKey1 getKey1() {
		return this.key1;
	}

	public final TKey2 getKey2() {
		return this.key2;
	}

	public final TKey3 getKey3() {
		return this.key3;
	}

	public final TreeNode<TResult> getResultTreeNode()
			throws IllegalStateException {
		this.checkFinished();
		return this.resultTreeNode;
	}

	public final Class<TResult> getResultClass() {
		return this.resultClass;
	}
}
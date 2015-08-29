package com.jiuqi.dna.core.impl;

import java.util.Comparator;
import java.util.List;

import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.TreeNodeFilter;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.MoreKeyAsyncListResult;
import com.jiuqi.dna.core.invoke.MoreKeyAsyncResult;
import com.jiuqi.dna.core.invoke.MoreKeyAsyncTreeNodeResult;
import com.jiuqi.dna.core.invoke.Return;
import com.jiuqi.dna.core.misc.MissingObjectException;

final class NetQueryRequestImpl<TResult, TKey1, TKey2, TKey3> extends
		NetRequestImpl implements
		MoreKeyAsyncResult<TResult, TKey1, TKey2, TKey3>,
		MoreKeyAsyncListResult<TResult, TKey1, TKey2, TKey3>,
		MoreKeyAsyncTreeNodeResult<TResult, TKey1, TKey2, TKey3> {
	final static byte QUERY_RESULT_TYPE_FIND = 0;
	final static byte QUERY_RESULT_TYPE_GET = 1;
	final static byte QUERY_RESULT_TYPE_LIST = 2;
	final static byte QUERY_RESULT_TYPE_TREE = 3;

	private void checkIsObjectResult() {
		switch (this.queryData.resultType) {
		case QUERY_RESULT_TYPE_FIND:
		case QUERY_RESULT_TYPE_GET:
			this.checkFinished();
			return;
		}
		throw new UnsupportedOperationException();
	}

	private void checkIsListResult() {
		if (this.queryData.resultType != QUERY_RESULT_TYPE_LIST) {
			throw new UnsupportedOperationException();
		}
		this.checkFinished();
	}

	private void checkIsTreeNodeResult() {
		if (this.queryData.resultType != QUERY_RESULT_TYPE_TREE) {
			throw new UnsupportedOperationException();
		}
		this.checkFinished();
	}

	@Override
	public final Object getDataObject() {
		return this.queryData;
	}

	/**
	 * 查询任务，用以装载查询键和返回结果的任务
	 * 
	 * @author gaojingxin
	 * 
	 */
	@StructClass
	static class RemoteQueryData<TResult, TKey1, TKey2, TKey3> extends
			RemoteInvokeData {
		final byte resultType;
		final Class<TResult> resultClass;
		final Operation<? super TResult> operation;
		final TKey1 key1;
		final TKey2 key2;
		final TKey3 key3;
		final Object[] otherKeys;
		@Return
		Object result;

		@Override
		final int getTransactionID() {
			return Transaction.INVALID_TRANSACTION_ID;
		};

		RemoteQueryData(byte resultType, Class<TResult> resultClass,
				Operation<? super TResult> operation, TKey1 key1, TKey2 key2,
				TKey3 key3, Object[] otherKeys) {
			switch (resultType) {
			case QUERY_RESULT_TYPE_FIND:
			case QUERY_RESULT_TYPE_GET:
			case QUERY_RESULT_TYPE_LIST:
			case QUERY_RESULT_TYPE_TREE:
				break;
			default:
				throw new IllegalArgumentException("resultType: " + resultType);
			}
			if (resultClass == null) {
				throw new NullArgumentException("resultClass");
			}
			this.resultType = resultType;
			this.resultClass = resultClass;
			this.operation = operation;
			this.key1 = key1;
			this.key2 = key2;
			this.key3 = key3;
			this.otherKeys = otherKeys;
		}

		@Override
		void invoke(ContextImpl<?, ?, ?> context) throws Throwable {
			super.invoke(context);
			// 切换用户
			switch (this.resultType) {
			case NetQueryRequestImpl.QUERY_RESULT_TYPE_FIND:
			case NetQueryRequestImpl.QUERY_RESULT_TYPE_GET:
				this.result = context.internalFind(this.operation, this.resultClass, this.key1 != null ? this.key1.getClass() : null, this.key2 != null ? this.key2.getClass() : null, this.key3 != null ? this.key3.getClass() : null, this.key1, this.key2, this.key3, this.otherKeys);
				break;
			case NetQueryRequestImpl.QUERY_RESULT_TYPE_LIST:
				this.result = context.internalGetList(this.operation, this.resultClass, null, null, this.key1, this.key2, this.key3, this.otherKeys);
				break;
			case NetQueryRequestImpl.QUERY_RESULT_TYPE_TREE:
				if (this.key1 == null) {
					this.result = context.internalGetTreeNode(this.operation, this.resultClass, null, null);
				} else {
					this.result = context.internalGetTreeNode(this.operation, this.resultClass, null, null, this.key1.getClass(), this.key2 != null ? this.key2.getClass() : null, this.key3 != null ? this.key3.getClass() : null, this.key1, this.key2, this.key3, this.otherKeys);
				}
				break;
			default:
				throw new UnsupportedOperationException("resultType: " + this.resultType);
			}
		}
	}

	private final RemoteQueryData<TResult, TKey1, TKey2, TKey3> queryData;

	public final TResult getResult() throws IllegalStateException,
			MissingObjectException {
		this.checkIsObjectResult();
		@SuppressWarnings("unchecked")
		final TResult result = (TResult) this.queryData.result;
		if (this.queryData.resultType == QUERY_RESULT_TYPE_GET && result == null) {
			throw new MissingObjectException();
		}
		return result;
	}

	public final boolean isNull() throws IllegalStateException {
		this.checkIsObjectResult();
		return this.queryData.result == null;
	}

	public final TKey1 getKey1() {
		return this.queryData.key1;
	}

	public final TKey2 getKey2() {
		return this.queryData.key2;
	}

	public final TKey3 getKey3() {
		return this.queryData.key3;
	}

	public final Object[] getOtherKeys() {
		return this.queryData.otherKeys;
	}

	public final Class<TResult> getResultClass() {
		return this.queryData.resultClass;
	}

	NetQueryRequestImpl(NetSessionImpl session, byte resultType,
			Class<TResult> resultClass, Operation<? super TResult> operation,
			TKey1 key1, TKey2 key2, TKey3 key3, Object[] otherKeys) {
		super(session);
		if (session.netNode.channel.remoteNodeInfo.ver.ver >= NetProtocolVersion.VER_3_0_1.ver) {
			RemoteQueryDataEx<TResult, TKey1, TKey2, TKey3> data = new RemoteQueryDataEx<TResult, TKey1, TKey2, TKey3>(resultType, resultClass, operation, key1, key2, key3, otherKeys);
			this.session.fillProperties(data);
			this.queryData = data;
		} else {
			this.queryData = new RemoteQueryData<TResult, TKey1, TKey2, TKey3>(resultType, resultClass, operation, key1, key2, key3, otherKeys);
		}
	}

	@SuppressWarnings("unchecked")
	public final List<TResult> getResultList() throws IllegalStateException {
		this.checkIsListResult();
		return (List<TResult>) this.queryData.result;
	}

	@SuppressWarnings("unchecked")
	final List<TResult> getResultList(Filter<? super TResult> filter,
			Comparator<? super TResult> sortComparator)
			throws IllegalStateException {
		this.checkIsListResult();
		final DnaArrayList<TResult> listResult = (DnaArrayList<TResult>) this.queryData.result;
		listResult.adjust(filter, sortComparator);
		return listResult;
	}

	@SuppressWarnings("unchecked")
	final TreeNodeImpl<TResult> getResultTreeNode(
			TreeNodeFilter<? super TResult> filter,
			Comparator<? super TResult> sortComparator)
			throws IllegalStateException {
		this.checkIsTreeNodeResult();
		final TreeNodeImpl<TResult> treeNodeResult = (TreeNodeImpl<TResult>) this.queryData.result;
		treeNodeResult.filterAndSortRecursively(filter, 0, 0, sortComparator);
		return treeNodeResult;
	}

	@SuppressWarnings("unchecked")
	public final TreeNodeImpl<TResult> getResultTreeNode()
			throws IllegalStateException {
		this.checkIsTreeNodeResult();
		return (TreeNodeImpl<TResult>) this.queryData.result;
	}

}
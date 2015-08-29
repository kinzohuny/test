/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteOverlappedResultList.java
 * Date 2009-4-8
 */
package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.MoreKeyAsyncListResult;

/**
 * 远程查询结果列表。
 * 
 * @author LRJ
 * @version 1.0
 */
final class RemoteOverlappedResultList<TResult, TKey1, TKey2, TKey3> extends
		RemoteAsyncHandle implements
		MoreKeyAsyncListResult<TResult, TKey1, TKey2, TKey3> {

	/**
	 * 远程结果列表查询。
	 */
	final RemoteListQuery remoteListQuery;

	/**
	 * 远程查询结果列表的构造器。
	 * 
	 * @param remoteListQuery
	 *            远程结果列表查询。
	 * @param remoteListQueryStub
	 *            远程结果列表查询存根。
	 */
	RemoteOverlappedResultList(RemoteListQuery remoteListQuery,
			RemoteListQueryStubImpl remoteListQueryStub) {
		super(remoteListQueryStub);
		if (remoteListQuery == null) {
			throw new NullArgumentException("remoteListQuery");
		}
		this.remoteListQuery = remoteListQuery;
	}

	/**
	 * 获取远程结果列表查询存根对象。
	 * 
	 * @return 远程结果列表查询存根对象。
	 */
	private RemoteListQueryStubImpl remoteListQueryStub() {
		return (RemoteListQueryStubImpl) this.remoteRequestStub;
	}

	/**
	 * 获取查询结果列表中的对象的类型。
	 * 
	 * @return 查询结果列表中的对象的类型。
	 */
	@SuppressWarnings("unchecked")
	public Class<TResult> getResultClass() {
		return this.remoteListQuery.queryBy.getResultClass();
	}

	/**
	 * 获取查询凭据中的第一个键。
	 * 
	 * @return 查询凭据中的第一个键。
	 */
	@SuppressWarnings("unchecked")
	public TKey1 getKey1() {
		return (TKey1) this.remoteListQuery.queryBy.getKey1();
	}

	/**
	 * 获取查询凭据中的第二个键。
	 * 
	 * @return 查询凭据中的第二个键。
	 */
	@SuppressWarnings("unchecked")
	public TKey2 getKey2() {
		return (TKey2) this.remoteListQuery.queryBy.getKey2();
	}

	/**
	 * 获取查询凭据中的第三个键。
	 * 
	 * @return 查询凭据中的第三个键。
	 */
	@SuppressWarnings("unchecked")
	public TKey3 getKey3() {
		return (TKey3) this.remoteListQuery.queryBy.getKey3();
	}

	/**
	 * 获取查询凭据中前三个键之后的键。
	 * 
	 * @return 查询凭据中有三个键之后的键。
	 */
	public Object[] getOtherKeys() {
		return this.remoteListQuery.queryBy.getOtherKeys();
	}

	/**
	 * 获取远程查询执行完毕后返回的结果列表。
	 * 
	 * @return 远程查询执行完毕后返回的结果列表。
	 * @throws IllegalStateException
	 *             执行过程尚未完毕。
	 * @throws RemoteException
	 *             远程调用过程中出现异常。
	 */
	@SuppressWarnings("unchecked")
	public List<TResult> getResultList() throws IllegalStateException {
		this.internalCheckStateForResultOK();
		return this.remoteListQueryStub().getResultList();
	}
}

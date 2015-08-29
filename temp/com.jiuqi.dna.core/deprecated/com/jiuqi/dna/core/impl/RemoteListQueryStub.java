/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteListQueryStub.java
 * Date 2009-4-8
 */
package com.jiuqi.dna.core.impl;

import java.util.List;

/**
 * 远程结果列表查询存根。
 * 
 * @author LRJ
 * @version 1.0
 */
interface RemoteListQueryStub extends RemoteRequestStub {

	/**
	 * 获取远程查询结束后返回的结果列表。
	 * 
	 * @return 远程查询结束后返回的结果列表。
	 * @throws RemoteException
	 *             远程执行过程中出现异常。
	 */
	@SuppressWarnings("unchecked")
	List getResultList() throws RemoteException;
}

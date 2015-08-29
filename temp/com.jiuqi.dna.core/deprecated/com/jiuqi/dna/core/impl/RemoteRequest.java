/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File RemoteRequest.java
 * Date 2009-3-10
 */
package com.jiuqi.dna.core.impl;

/**
 * 远程请求。
 * 
 * @author LRJ
 * @version 1.0
 */
interface RemoteRequest<TRemoteRequestStub extends RemoteRequestStubBase>
// RemoteRequestStubImpl 这里最好使用接口，但内部为了方便，且RemoteRequest目前并不对外，所以先采用实现类了。
		extends RemoteCommand {
	RemoteReturn execute(ContextImpl<?, ?, ?> context) throws Throwable;

	TRemoteRequestStub newStub(NetConnection netConnection);
}

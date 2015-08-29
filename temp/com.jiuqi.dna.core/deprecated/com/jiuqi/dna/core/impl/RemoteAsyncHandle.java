/**
 * Copyright (C) 2007-2009 Beijing Join-Cheer Software Co., Ltd. All rights reserved.
 * 
 * File AbstractAsyncHandle.java
 * Date 2009-4-7
 */
package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.info.Info;
import com.jiuqi.dna.core.invoke.AsyncHandle;
import com.jiuqi.dna.core.invoke.AsyncState;
import com.jiuqi.dna.core.invoke.Waitable;

/**
 * 远程异步处理句柄。
 * 
 * @author LRJ
 * @version 1.0
 */
abstract class RemoteAsyncHandle implements AsyncHandle, Waitable {
	final RemoteRequestStubImpl remoteRequestStub;

	public int fetchInfos(List<Info> to) {
		return 0;
		// TODO
	};

	RemoteAsyncHandle(RemoteRequestStubImpl remoteRequestStub) {
		if (remoteRequestStub == null) {
			throw new NullArgumentException("remoteRequestStub");
		}
		this.remoteRequestStub = remoteRequestStub;
		this.state = AsyncState.PROCESSING;
	}

	// /////////////////////////

	/**
	 * 等待远程处理执行结束。
	 */
	final void waitToFinish() {
		boolean error = false;
		try {
			this.remoteRequestStub.syncWork();
		} catch (Throwable e) {
			error = true;
			throw Utils.tryThrowException(e);
		} finally {
			synchronized (this.forWAIT) {
				this.forWAIT.notifyAll();
				switch (this.state) {
				case CANCELING:
					this.state = AsyncState.CANCELED;
					if (error) {
						this.tempProgress = -0.5f;
					}
					break;
				case PROCESSING:
					if (error) {
						this.state = AsyncState.ERROR;
						this.tempProgress = -1;
					} else {
						this.state = AsyncState.FINISHED;
						this.tempProgress = 1;
					}
					break;
				}
			}
		}
	}

	final boolean noException() {
		return this.remoteRequestStub.noException();
	}

	final void internalCheckStateForResultOK() {
		// TODO check state
	}

	// /////////////////////////

	public void cancel() {
		this.tryFinish();
		synchronized (this.forWAIT) {
			switch (this.state) {
			case CANCELED:
			case CANCELING:
			case ERROR:
			case FINISHED:
				return;
			}
			this.state = AsyncState.CANCELING;
		}
		// XXX
		this.remoteRequestStub.cancel();
	}

	public Throwable getException() {
		return this.remoteRequestStub.getException();
	}

	private float tempProgress = 0; // XXX

	private volatile AsyncState state;

	public float getProgress() {
		this.tryFinish();
		return this.tempProgress;
	}

	private void tryFinish() {
		synchronized (this.forWAIT) {
			if (!this.remoteRequestStub.canSyncWork()) {
				return;
			}
		}
		this.waitToFinish();
	}

	public AsyncState getState() {
		this.tryFinish();
		return this.state;
	}

	// /////////////////////////////

	private final Object forWAIT = new Object();

	public void waitStop(long timeout) throws InterruptedException {
		throw new UnsupportedOperationException("不支持等待异步的远程调用");
	}
}

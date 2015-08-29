package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.service.AsyncInfo.AwaitSchedule;
import com.jiuqi.dna.core.service.AsyncInfo.SessionMode;

/**
 * 异步处理信息
 * 
 * @author gaojingxin
 * 
 */
public abstract class InternalAsyncInfo {

	/**
	 * 开始的时间
	 */
	protected long start;

	protected AwaitSchedule awaitSchedule;

	/**
	 * 重复执行的周期小于或等于0表示不重复
	 */
	protected long period;

	protected SessionMode sessionMode;

	/**
	 * 关心异步调用的中产生的信息
	 */
	protected boolean careInfos;

	public InternalAsyncInfo(long start, AwaitSchedule awaitSchedule,
			long period, SessionMode sessionMode) {
		if (sessionMode == null) {
			throw new NullArgumentException("sessionMode");
		}
		this.start = start;
		this.awaitSchedule = awaitSchedule;
		this.period = period;
		this.sessionMode = sessionMode;
	}
}
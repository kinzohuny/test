package com.jiuqi.dna.core.service;

import java.util.Date;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.InternalAsyncInfo;

/**
 * 异步调用信息
 * 
 * @author gaojingxin
 * 
 */
public final class AsyncInfo extends InternalAsyncInfo {

	/**
	 * 会话模式
	 * 
	 * @author gaojingxin
	 * 
	 */
	public enum SessionMode {

		/**
		 * 相同的会话中，
		 */
		SAME,

		/**
		 * 不同的会话，相同的用户，对于系统会话下发起的异步处理则会使用匿名用户
		 */
		INDIVIDUAL,

		/**
		 * 不同的会话，匿名用户
		 */
		INDIVIDUAL_ANONYMOUS
	}

	/**
	 * 调度等待
	 * 
	 * <p>
	 * 指示在指定的事件发生后，异步任务才会开始调度。
	 * 
	 * @author houchunlei
	 * 
	 */
	public enum AwaitSchedule {

		/**
		 * 不等待，立即开始调度
		 */
		NONE,

		/**
		 * 在当前请求结束后
		 */
		AFTER_CURRENT_CONTEXT_ALWAYS,

		/**
		 * 在当前请求成功结束后
		 */
		AFTER_CURRENT_CONTEXT_SUCCESS,

		/**
		 * 在当前请求异常结束后
		 */
		AFTER_CURRENT_CONTEXT_EXCEPTION;
	}

	/**
	 * 获取绘画模式
	 */
	public final SessionMode getSessionMode() {
		return super.sessionMode;
	}

	/**
	 * 设置会话模式
	 */
	public final void setSessionMode(SessionMode sessionMode) {
		if (sessionMode == null) {
			throw new NullArgumentException("sessionMode");
		}
		super.sessionMode = sessionMode;
	}

	/**
	 * 设置是否关心异步调用的中产生的信息
	 */
	public final AsyncInfo setCareInfos(boolean value) {
		super.careInfos = value;
		return this;
	}

	/**
	 * 获得是否关心异步调用的中产生的信息
	 */
	public final boolean isCareInfos() {
		return this.careInfos;
	}

	/**
	 * 开始执行的时间，小于当前时间表示立即执行
	 */
	public final long getStartTime() {
		return super.start;
	}

	/**
	 * 设置开始执行的时间，小于当前时间表示立即执行
	 */
	public final AsyncInfo setStartTime(long value) {
		super.start = value;
		return this;
	}

	/**
	 * 获取执行周期，小于等于0表示不周期执行
	 */
	public final long getPeriod() {
		return super.period;
	}

	/**
	 * 设置执行周期，小于等于0表示不周期执行
	 */
	public final AsyncInfo setPeiod(long value) {
		super.period = value;
		return this;
	}

	public final AsyncInfo setScheduleTrigger(AwaitSchedule scheduleTrigger) {
		// TODO
		return this;
	}

	/**
	 * 构造方法
	 * 
	 * @param delay
	 *            延迟开始时间
	 * @param period
	 *            执行周期，小于等于0表示不周期执行
	 */
	public AsyncInfo(long delay, long period) {
		super(System.currentTimeMillis() + delay, AwaitSchedule.NONE, period,
				SessionMode.SAME);
	}

	public AsyncInfo(long delay, long period, SessionMode sessionMode) {
		super(System.currentTimeMillis() + delay, AwaitSchedule.NONE, period,
				sessionMode);
	}

	/**
	 * 构造方法，无周期
	 * 
	 * @param delay
	 *            延迟开始时间
	 */
	public AsyncInfo(long delay) {
		super(System.currentTimeMillis() + delay, AwaitSchedule.NONE, 0,
				SessionMode.SAME);
	}

	public AsyncInfo(long delay, SessionMode sessionMode) {
		super(System.currentTimeMillis() + delay, AwaitSchedule.NONE, 0,
				sessionMode);
	}

	/**
	 * 构造方法，无周期
	 * 
	 * @param start
	 *            开始时间，小于当前时间表示立即执行
	 * @param period
	 *            执行周期，小于等于0表示不周期执行
	 */
	public AsyncInfo(Date start, long period) {
		super(start.getTime(), AwaitSchedule.NONE, period, SessionMode.SAME);
	}

	public AsyncInfo(Date start, long period, SessionMode sessionMode) {
		super(start.getTime(), AwaitSchedule.NONE, period, sessionMode);
	}

	/**
	 * 构造方法
	 * 
	 * @param start
	 *            开始时间，小于当前时间表示立即执行
	 */
	public AsyncInfo(Date start) {
		super(start.getTime(), AwaitSchedule.NONE, 0, SessionMode.SAME);
	}

	public AsyncInfo(Date start, SessionMode sessionMode) {
		super(start.getTime(), AwaitSchedule.NONE, 0, sessionMode);
	}

	public AsyncInfo() {
		super(0, AwaitSchedule.NONE, 0, SessionMode.SAME);
	}

	public AsyncInfo(SessionMode sessionMode) {
		super(0, AwaitSchedule.NONE, 0, sessionMode);
	}

	public AsyncInfo(AwaitSchedule awaitSchedule) {
		super(0, awaitSchedule, 0, SessionMode.SAME);
		if (awaitSchedule == null) {
			throw new NullArgumentException("awaitSchedule");
		}
	}

	public AsyncInfo(AwaitSchedule awaitSchedule, SessionMode sessionMode) {
		super(0, awaitSchedule, 0, sessionMode);
		if (awaitSchedule == null) {
			throw new NullArgumentException("awaitSchedule");
		}
	}

	public AsyncInfo(AwaitSchedule awaitSchedule, long period,
			SessionMode sessionMode) {
		super(0, awaitSchedule, period, sessionMode);
		if (awaitSchedule == null) {
			throw new NullArgumentException("awaitSchedule");
		}
	}
}

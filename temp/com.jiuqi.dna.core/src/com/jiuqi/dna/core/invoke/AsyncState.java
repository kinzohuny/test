package com.jiuqi.dna.core.invoke;

/**
 * 异步处理请求的状态
 * 
 * @author gaojingxin
 * 
 */
public enum AsyncState {
	/**
	 * 提交了异步请求（本地调用）
	 */
	POSTING(false, false),
	/**
	 * 等待调度线程的调度（本地调用）
	 */
	SCHEDULING(false, false),
	/**
	 * 因为并发控制的原因，进入并发控制列队中
	 */
	QUEUING(false, false),
	/**
	 * 已经进入执行列队，只要有空闲的线程就会开始工作
	 */
	STARTING(false, false),
	/**
	 * 正在处理
	 */
	PROCESSING(false, false),
	/**
	 * 正在取消中
	 */
	CANCELING(false, true),
	/**
	 * 完成异步处理
	 */
	FINISHED(true, false),
	/**
	 * 错误完成
	 */
	ERROR(true, false),
	/**
	 * 取消中止
	 */
	CANCELED(true, true),
	/**
	 * 正在取消中，且被等待结束。已经作废，此状态永远不会出现
	 */
	@Deprecated
	CANCELING_WAITED(false, true),
	/**
	 * 正在处理，且被等待结束。已经作废，此状态永远不会出现
	 */
	@Deprecated
	PROCESSING_WAITED(false, false),
	/**
	 * 已经进入执行列队，且被等待结束。已经作废，此状态永远不会出现
	 */
	@Deprecated
	STARTING_WAITED(false, false),
	/**
	 * 进入并发控制列队中，且被等待结束。已经作废，此状态永远不会出现
	 */
	@Deprecated
	QUEUING_WAITED(false, false);
	/**
	 * 是否已经停止
	 */
	public final boolean stopped;
	/**
	 * 是否有取消动作
	 */
	public final boolean canceling;

	AsyncState(boolean stopped, boolean canceling) {
		this.stopped = stopped;
		this.canceling = canceling;
	}

	/**
	 * 是否正被等待，已经作废，永远返回false
	 */
	@Deprecated
	public final boolean waited = false;
}

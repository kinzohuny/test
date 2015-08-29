package com.jiuqi.dna.core.impl;

/**
 * 异步控制器
 * 
 * @author gaojingxin
 * 
 */
public interface AsyncIOStub<TAttachment> {
	/**
	 * 取消异步操作
	 */
	public void cancel();

	/**
	 * 挂起
	 */
	public void suspend();

	/**
	 * 恢复
	 */
	public void resume();

	/**
	 * 获取所对应的附件
	 */
	public TAttachment getAttachment();
}

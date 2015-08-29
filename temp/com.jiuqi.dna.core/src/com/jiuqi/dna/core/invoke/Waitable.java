package com.jiuqi.dna.core.invoke;

/**
 * 可等待接口
 * 
 * @author gaojingxin
 */
public interface Waitable {

	/**
	 * 等待直到结束或超时
	 * 
	 * @param timeout
	 *            超时毫秒数，0表示无限时间
	 */
	public void waitStop(long timeout) throws InterruptedException;
}

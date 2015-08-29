package com.jiuqi.dna.core.spi.setl;

import com.jiuqi.dna.core.Context;

/**
 * SETL写锁接口
 * <p>
 * 通过锁防止和其他业务模块并发时发生死锁
 * </p>
 * 
 * @author niuhaifeng
 * 
 */
public interface SETLWriteLock {
	/**
	 * 尝试加锁，非阻塞，返回加锁是否成功
	 * 
	 * @return true成功，false失败
	 */
	public abstract boolean tryLock(Context context);

	/**
	 * 加锁，阻塞
	 */
	public abstract void lock(Context context);

	/**
	 * 解锁
	 */
	public abstract void release(Context context);
}
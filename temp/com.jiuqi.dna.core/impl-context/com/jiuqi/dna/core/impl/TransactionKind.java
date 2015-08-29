package com.jiuqi.dna.core.impl;

/**
 * 事务类型
 * 
 * @author niuhaifeng
 * 
 */
enum TransactionKind {
	/**
	 * 系统初始化事务
	 */
	SYSTEM_INIT,
	/**
	 * 资源初始化事务
	 */
	CACHE_INIT,
	/**
	 * 常规事务
	 */
	NORMAL,
	/**
	 * 由于异步调用/远程调用产生的本地事务
	 */
	TRANSIENT,
	/**
	 * 远程事务
	 */
	REMOTE,
	/**
	 * 模拟用途的事务，如单元测试。
	 */
	SIMULATION,
}

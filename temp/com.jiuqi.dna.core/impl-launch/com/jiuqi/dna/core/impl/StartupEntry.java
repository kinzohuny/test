package com.jiuqi.dna.core.impl;

/**
 * 启动项基类
 * 
 * @author gaojingxin
 * 
 */
public abstract class StartupEntry {

	/**
	 * 启动时使用，同一步中的下一个元素，环链表
	 */
	StartupEntry nextInStep;

	/**
	 * 获得条目的优先级
	 */
	float getPriority(StartupStep<?> step) {
		return 0.0f;
	}
}

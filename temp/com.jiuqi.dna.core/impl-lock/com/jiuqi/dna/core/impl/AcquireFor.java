package com.jiuqi.dna.core.impl;

/**
 * 锁属性
 */
enum AcquireFor {

	/**
	 * 添加
	 */
	ADD,
	/**
	 * 读
	 */
	READ,
	/**
	 * 修改
	 */
	MODIFY,
	/**
	 * 修改容器的子项
	 */
	MODIFY_ITEMS,
	/**
	 * 删除
	 */
	REMOVE,
	/**
	 * 提交
	 */
	COMMIT
}
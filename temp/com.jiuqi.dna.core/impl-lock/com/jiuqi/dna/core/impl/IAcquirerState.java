package com.jiuqi.dna.core.impl;

interface IAcquirerState {

	/**
	 * 锁状态掩码
	 */
	final static int MASK_LOCK = 0xff;
	/**
	 * 节点状态掩码
	 */
	final static int MASK_NODE = -1 ^ MASK_LOCK;
	/**
	 * 锁状态的长度
	 */
	final static int LOCK_LEN = 8;
	/**
	 * 共享锁类型
	 */
	final static byte LOCK_TYPE_S = 1;
	/**
	 * 可升级锁类型
	 */
	final static byte LOCK_TYPE_U = 2;
	/**
	 * 互斥锁类型
	 */
	final static byte LOCK_TYPE_X = 3;
	/**
	 * 锁类型
	 */
	final static byte LOCK_MASK_TYPE = LOCK_TYPE_S | LOCK_TYPE_U | LOCK_TYPE_X;
	/**
	 * 本地锁
	 */
	final static byte LOCK_SCOPE_L = 1 << 2;
	/**
	 * 全局锁
	 */
	final static byte LOCK_SCOPE_G = 2 << 2;
	/**
	 * 远程锁
	 */
	final static byte LOCK_SCOPE_R = 3 << 2;
	/**
	 * 锁范围
	 */
	final static byte LOCK_MASK_SCOPE = LOCK_SCOPE_L | LOCK_SCOPE_G | LOCK_SCOPE_R;
	/**
	 * 等待
	 */
	final static byte LOCK_STATE_WAITING = 1 << 4;
	/**
	 * 等待远程请求
	 */
	final static byte LOCK_STATE_REQUEST = 1 << 5;
	/**
	 * 准备好
	 */
	final static byte LOCK_STATE_ACQUIRED = 1 << 6;
	/**
	 * 锁状态
	 */
	final static byte LOCK_MASK_STATE = LOCK_STATE_WAITING | LOCK_STATE_REQUEST | LOCK_STATE_ACQUIRED;

	/**
	 * 没有锁（特殊状态）
	 */
	final static byte LOCK_N = 0;

	/**
	 * 本地共享锁(等待中)
	 */
	final static byte LOCK_LSW = LOCK_SCOPE_L | LOCK_TYPE_S | LOCK_STATE_WAITING;
	/**
	 * 本地共享锁
	 */
	final static byte LOCK_LS = LOCK_SCOPE_L | LOCK_TYPE_S | LOCK_STATE_ACQUIRED;
	/**
	 * 本地可升级锁(等待中)
	 */
	final static byte LOCK_LUW = LOCK_SCOPE_L | LOCK_TYPE_U | LOCK_STATE_WAITING;
	/**
	 * 本地可升级锁
	 */
	final static byte LOCK_LU = LOCK_SCOPE_L | LOCK_TYPE_U | LOCK_STATE_ACQUIRED;
	/**
	 * 本地独占锁(等待中)
	 */
	final static byte LOCK_LXW = LOCK_SCOPE_L | LOCK_TYPE_X | LOCK_STATE_WAITING;
	/**
	 * 本地独占锁
	 */
	final static byte LOCK_LX = LOCK_SCOPE_L | LOCK_TYPE_X | LOCK_STATE_ACQUIRED;
	/**
	 * 全局修改锁(等待中)
	 */
	final static byte LOCK_GUW = LOCK_SCOPE_G | LOCK_TYPE_U | LOCK_STATE_WAITING;
	/**
	 * 全局可升级锁(等待远程锁)
	 */
	final static byte LOCK_GUR = LOCK_SCOPE_G | LOCK_TYPE_U | LOCK_STATE_REQUEST;
	/**
	 * 全局可升级锁
	 */
	final static byte LOCK_GU = LOCK_SCOPE_G | LOCK_TYPE_U | LOCK_STATE_ACQUIRED;
	/**
	 * 全局独占锁(等待中)
	 */
	final static byte LOCK_GXW = LOCK_SCOPE_G | LOCK_TYPE_X | LOCK_STATE_WAITING;
	/**
	 * 全局独占锁(等待远程锁)
	 */
	final static byte LOCK_GXR = LOCK_SCOPE_G | LOCK_TYPE_X | LOCK_STATE_REQUEST;
	/**
	 * 全局独占锁
	 */
	final static byte LOCK_GX = LOCK_SCOPE_G | LOCK_TYPE_X | LOCK_STATE_ACQUIRED;
	/**
	 * 远程可升级锁
	 */
	final static byte LOCK_RU = LOCK_SCOPE_R | LOCK_TYPE_U | LOCK_STATE_ACQUIRED;
	/**
	 * 远程独占锁(等待中)
	 */
	final static byte LOCK_RXW = LOCK_SCOPE_R | LOCK_TYPE_X | LOCK_STATE_WAITING;
	/**
	 * 远程独占锁
	 */
	final static byte LOCK_RX = LOCK_SCOPE_R | LOCK_TYPE_X | LOCK_STATE_ACQUIRED;
}
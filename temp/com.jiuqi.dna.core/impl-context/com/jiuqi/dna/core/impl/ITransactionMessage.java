package com.jiuqi.dna.core.impl;

interface ITransactionMessage {
	/**
	 * 事务消息标识
	 */
	final static byte MSG_MASK_TRANS = 0x10;
	/**
	 * 锁消息标识
	 */
	final static byte MSG_MASK_LOCK = 0x20;
	/**
	 * 同步消息标识
	 */
	final static byte MSG_MASK_SYNC = 0x30;
	/**
	 * 消息：提交
	 */
	final static byte MSG_COMMIT = MSG_MASK_TRANS | 0x01;
	/**
	 * 消息：回滚
	 */
	final static byte MSG_ROLLBACK = MSG_MASK_TRANS | 0x02;
	/**
	 * 消息：重置事务状态
	 */
	final static byte MSG_RESET = MSG_MASK_TRANS | 0x03;
	/**
	 * 消息：销毁
	 */
	final static byte MSG_DISPOSE = MSG_MASK_TRANS | 0x04;
	/**
	 * 消息：查询事务的状态
	 */
	final static byte MSG_LOOKUP = MSG_MASK_TRANS | 0x05;
	/**
	 * 回复：事务状态
	 */
	final static byte MSG_LOOKUP_RESULT = MSG_MASK_TRANS | 0x06;
	/**
	 * 参数：未知状态
	 */
	final static byte RESULT_UNKNOWN = 0;
	/**
	 * 参数：已提交
	 */
	final static byte RESULT_COMMIT = 0x01;
	/**
	 * 参数：已回滚
	 */
	final static byte RESULT_ROLLBACK = 0x02;
	/**
	 * 消息：加锁
	 */
	final static byte MSG_ACQUIRE = MSG_MASK_LOCK | 0x01;
	/**
	 * 参数：资源类型：资源定义
	 */
	final static byte PARAM_TYPE_CACHE = 0x01;
	/**
	 * 参数：资源类型：资源组
	 */
	final static byte PARAM_TYPE_GROUP = 0x02;
	/**
	 * 参数：资源类型：资源项
	 */
	final static byte PARAM_TYPE_ITEM = 0x03;
	/**
	 * 参数：操作方式：添加
	 */
	final static byte PARAM_METHOD_ADD = 0x01;
	/**
	 * 参数：操作方式：读
	 */
	final static byte PARAM_METHOD_READ = 0x02;
	/**
	 * 参数：操作方式：修改
	 */
	final static byte PARAM_METHOD_MODIFY = 0x03;
	/**
	 * 参数：操作方式：修改容器的子项
	 */
	final static byte PARAM_METHOD_MODIFY_ITEMS = 0x04;
	/**
	 * 参数：操作方式：删除
	 */
	final static byte PARAM_METHOD_REMOVE = 0x05;
	/**
	 * 参数：操作方式：提交
	 */
	final static byte PARAM_METHOD_COMMIT = 0x06;
	/**
	 * 回复：加锁
	 */
	final static byte MSG_ACQUIRE_RESULT = MSG_MASK_LOCK | 0x02;
	/**
	 * 参数：操作成功
	 */
	final static byte RESULT_SUCCEED = 0x01;
	/**
	 * 参数：操作失败
	 */
	final static byte RESULT_FAIL = 0x02;
	/**
	 * 消息：锁升级
	 */
	final static byte MSG_UPGRADE = MSG_MASK_LOCK | 0x03;
	/**
	 * 消息：解锁
	 */
	final static byte MSG_RELEASE = MSG_MASK_LOCK | 0x04;
}

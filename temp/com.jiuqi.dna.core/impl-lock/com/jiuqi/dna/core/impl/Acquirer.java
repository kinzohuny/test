package com.jiuqi.dna.core.impl;

/**
 * 资源请求者
 * 
 * <p>
 * <ol>
 * <li>所有的资源请求者形成一个双向链表。
 * <li>tail表示最后的请求者，其prev指针指向更早些得请求者。
 * <li>在所有hold请求者中，U锁总是排在S锁之后。
 * <li>tail的next指向最后一个持有锁的对象或者等待中的远程锁存根（RXW）。所以加远程锁时根据tail.next的状态判断竞争是否成功。
 * </ol>
 * <code>
 * <pre>
 * +------+ →prev→ +------+ →prev→ ... +------+ →prev→ +------+ →prev→ +------+ →prev→ (null)  
 * | tail |        | wait |            | wait |        | hold |        | hold |  
 * +------+ ←next← +------+ ... ←next← +------+ ←next← +------+ ←next← +------+ 
 *    ↓                                                  ↑
 *    +-next---last-hold-node-or-null--------------------+  
 * </pre>
 * </code><br/>
 * 通过SUX锁解决并发问题<br/>
 * <p>
 * 请参考{@link Acquirable}<br/>
 * </p>
 * <p>
 * 本地锁请求者的状态按照以下规则进行转换：
 * <ul>
 * <li>S锁：LOCK_LSW -> LOCK_LS</li>
 * <li>U锁：LOCK_LUW -> LOCK_LU</li>
 * <li>X锁：LOCK_LXW -> LOCK_LX</li>
 * </ul>
 * 远程锁请求者的状态按照以下规则进行转换：
 * <ul>
 * <li>U锁：LOCK_GUW -> LOCK_GUR -> LOCK_GU</li>
 * <li>X锁：LOCK_GXW -> LOCK_GXR -> LOCK_GX</li>
 * </ul>
 * </p>
 * <p>
 * 锁状态名称由三个部分组成：
 * <ul>
 * <li>
 * 范围：
 * <ul>
 * <li>L：本地</li>
 * <li>G：全局。</li>
 * <li>R：远程，表示锁请求者在远程节点上。R锁是G锁在远程节点上的存根。</li>
 * </ul>
 * </li>
 * <li>
 * 类型
 * <ul>
 * <li>S：共享锁</li>
 * <li>U：可升级锁</li>
 * <li>X：独占锁</li>
 * </ul>
 * </li>
 * <li>
 * 等待状态
 * <ul>
 * <li>空：已经持有锁</li>
 * <li>W：等待加锁</li>
 * <li>R：持有本地锁，并且正在请求远程锁。tail.next不包含R状态的请求者。</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * 
 * @author gaojingxin
 * 
 */
abstract class Acquirer implements IAcquirerState {

	Acquirer(final Acquirable acquirable) {
		this.acquirable = acquirable;
	}

	/**
	 * 被请求的对象
	 */
	final Acquirable acquirable;

	/**
	 * 优先锁（前一个锁）
	 */
	Acquirer prev;

	/**
	 * 后续锁（下一个锁）
	 */
	Acquirer next;

	/**
	 * 在Holder中的下一个
	 */
	Acquirer nextInHolder;

	/**
	 * 当前请求者的锁状态
	 * 
	 * <p>
	 * 0-1位，锁类型；2-3位，锁范围；4位，等待状态；5位，请求状态；6位，获取状态。
	 * 
	 * <p>
	 * 8~63位表示消息回复状态
	 * 
	 * @see IAcquirerState
	 */
	volatile long state;

	abstract Transaction getOwner();

	abstract void broadcastAcquire();

	abstract void broadcastUpgrade();

	abstract void broadcastRelease();

	abstract void postAcquireResult();

	final boolean isAcquired() {
		return (this.state & LOCK_MASK_STATE) == LOCK_STATE_ACQUIRED;
	}

	final String self() {
		return String.format("[%x:%s]", this.getOwner().id, AcquirableAccessor.getStateText(this.state));
	}
}
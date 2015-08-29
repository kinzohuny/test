package com.jiuqi.dna.core.impl;

/**
 * ��Դ������
 * 
 * <p>
 * <ol>
 * <li>���е���Դ�������γ�һ��˫������
 * <li>tail��ʾ���������ߣ���prevָ��ָ�����Щ�������ߡ�
 * <li>������hold�������У�U����������S��֮��
 * <li>tail��nextָ�����һ���������Ķ�����ߵȴ��е�Զ���������RXW�������Լ�Զ����ʱ����tail.next��״̬�жϾ����Ƿ�ɹ���
 * </ol>
 * <code>
 * <pre>
 * +------+ ��prev�� +------+ ��prev�� ... +------+ ��prev�� +------+ ��prev�� +------+ ��prev�� (null)  
 * | tail |        | wait |            | wait |        | hold |        | hold |  
 * +------+ ��next�� +------+ ... ��next�� +------+ ��next�� +------+ ��next�� +------+ 
 *    ��                                                  ��
 *    +-next---last-hold-node-or-null--------------------+  
 * </pre>
 * </code><br/>
 * ͨ��SUX�������������<br/>
 * <p>
 * ��ο�{@link Acquirable}<br/>
 * </p>
 * <p>
 * �����������ߵ�״̬�������¹������ת����
 * <ul>
 * <li>S����LOCK_LSW -> LOCK_LS</li>
 * <li>U����LOCK_LUW -> LOCK_LU</li>
 * <li>X����LOCK_LXW -> LOCK_LX</li>
 * </ul>
 * Զ���������ߵ�״̬�������¹������ת����
 * <ul>
 * <li>U����LOCK_GUW -> LOCK_GUR -> LOCK_GU</li>
 * <li>X����LOCK_GXW -> LOCK_GXR -> LOCK_GX</li>
 * </ul>
 * </p>
 * <p>
 * ��״̬����������������ɣ�
 * <ul>
 * <li>
 * ��Χ��
 * <ul>
 * <li>L������</li>
 * <li>G��ȫ�֡�</li>
 * <li>R��Զ�̣���ʾ����������Զ�̽ڵ��ϡ�R����G����Զ�̽ڵ��ϵĴ����</li>
 * </ul>
 * </li>
 * <li>
 * ����
 * <ul>
 * <li>S��������</li>
 * <li>U����������</li>
 * <li>X����ռ��</li>
 * </ul>
 * </li>
 * <li>
 * �ȴ�״̬
 * <ul>
 * <li>�գ��Ѿ�������</li>
 * <li>W���ȴ�����</li>
 * <li>R�����б�������������������Զ������tail.next������R״̬�������ߡ�</li>
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
	 * ������Ķ���
	 */
	final Acquirable acquirable;

	/**
	 * ��������ǰһ������
	 */
	Acquirer prev;

	/**
	 * ����������һ������
	 */
	Acquirer next;

	/**
	 * ��Holder�е���һ��
	 */
	Acquirer nextInHolder;

	/**
	 * ��ǰ�����ߵ���״̬
	 * 
	 * <p>
	 * 0-1λ�������ͣ�2-3λ������Χ��4λ���ȴ�״̬��5λ������״̬��6λ����ȡ״̬��
	 * 
	 * <p>
	 * 8~63λ��ʾ��Ϣ�ظ�״̬
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
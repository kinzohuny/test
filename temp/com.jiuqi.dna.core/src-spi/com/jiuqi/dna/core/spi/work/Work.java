package com.jiuqi.dna.core.spi.work;

import com.jiuqi.dna.core.impl.ConcurrentController;
import com.jiuqi.dna.core.invoke.AsyncState;

/**
 * �첽����
 * 
 * @author gaojingxin
 * 
 */
public abstract class Work {

	/**
	 * �����жӵ���һ��
	 */
	private Work next;

	/**
	 * �ӻ��а����
	 */
	public final Work removeNext() {
		Work work = this.next;
		this.next = work.next;
		work.next = null;
		return work;
	}

	/**
	 * ״̬
	 * 
	 * <p>
	 * ��Ҫͬ����
	 */
	private volatile AsyncState state = AsyncState.POSTING;

	/**
	 * ���뻷���ж���
	 * 
	 * @param queueTail
	 */
	public final void putToQ(Work queueTail) {
		if (queueTail == null) {
			this.next = this;
		} else {
			this.next = queueTail.next;
			queueTail.next = this;
		}
	}

	protected final IllegalStateException illegalState() {
		return new IllegalStateException("��Ч״̬: " + this.state);
	}

	/**
	 * ���봦���ж�״̬
	 */
	final boolean putToStartQ(Work queueTail) {
		switch (this.state) {
		case POSTING:
		case SCHEDULING:
		case QUEUING:
			this.state = AsyncState.STARTING;
			break;
		case CANCELED:
			return false;
		default:
			throw this.illegalState();
		}
		this.putToQ(queueTail);
		return true;
	}

	final boolean putToWorkThread() {
		switch (this.state) {
		case POSTING:
		case SCHEDULING:
		case QUEUING:
			this.state = AsyncState.PROCESSING;
			break;
		case CANCELED:
			return false;
		default:
			throw this.illegalState();
		}
		return true;
	}

	/**
	 * ���벢�������ж�״̬
	 */
	public final boolean putToCuncerringQ(Work queueTail) {
		switch (this.state) {
		case POSTING:
		case SCHEDULING:
			this.state = AsyncState.QUEUING;
			break;
		case CANCELED:
			return false;
		default:
			throw this.illegalState();
		}
		this.putToQ(queueTail);
		return true;
	}

	final static int WORK_LAST_MASK = 1;
	final static int WORK_FIRST_MASK = 2;

	/**
	 * ���붨ʱ�ж�
	 * 
	 * @param regeneration
	 *            ����Ƿ������µ���
	 * @param queueTail
	 *            ʱ���Ķ�β
	 * @return �����Ƿ����ж�β����
	 */
	final int putToSchedulingQ(Work queueTail) {
		switch (this.state) {
		case POSTING:
			this.state = AsyncState.SCHEDULING;
			break;
		case CANCELED:
			return 0;
		default:
			throw this.illegalState();
		}
		if (queueTail == null) {
			this.next = this;
			return WORK_LAST_MASK | WORK_FIRST_MASK;
		} else {
			final long startTime = this.getStartTime();
			// ���ڶ�β�ĸ��ʸߣ���˵����ж�
			if (startTime >= queueTail.getStartTime()) {
				this.next = queueTail.next;
				queueTail.next = this;
				return WORK_LAST_MASK;
			}
			Work last = queueTail;
			Work work = queueTail.next;
			while (work != queueTail && work.getStartTime() <= startTime) {
				last = work;
				work = work.next;
			}
			this.next = work;
			last.next = this;
			return last == queueTail ? WORK_FIRST_MASK : 0;
		}
	}

	protected final void checkFinished() throws IllegalStateException {
		if (!this.state.stopped) {
			throw new IllegalStateException("�첽������δ���");
		}
	}

	// long startime;

	/**
	 * ��ò���������
	 * 
	 * <p>
	 * ���ؿձ�ʾ����Ҫ�������ơ�
	 */
	public ConcurrentController getConcurrentController() {
		return null;
	}

	/**
	 * ���Խ��벢����������
	 * 
	 * <p>
	 * ���û�в������ƣ����߳ɹ����뷵��true��
	 * 
	 * <p>
	 * �漰��������״̬�ĸı䣬��ǰ�߳��빤�����ύ�̲߳���ͬһ���߳�ʱ��Ҫ�ⲿ����������
	 */
	final boolean tryEnterConcurrentControlScope() {
		ConcurrentController ccr = this.getConcurrentController();
		return (ccr == null || ccr.enterScope(this));
	}

	/**
	 * ����������ʱ��Ĺ���<br>
	 * 
	 * @return �������ĵȴ�ʱ��
	 */
	final long tryTailStartScheduledWorks(WorkingManager manager) {
		long timeout;
		final long curr = System.currentTimeMillis();
		for (Work first = this.next; (timeout = first.getStartTime() - curr) <= 0; first = this.next) {
			if (first != this) {
				this.next = first.next;
				first.next = null;
			}
			// ��ǰ�߳��빤�����ύ�̲߳�ͬ����Ҫ��������
			synchronized (first) {
				if (first.tryEnterConcurrentControlScope()) {
					manager.startWork(first);
				}
			}
			if (first == this) {
				return 0;// ȫ������
			}
		}
		return timeout;
	}

	/**
	 * ֪ͨ�����������Ƿ�������ͨ������ģʽʵ�����ڹ�����
	 */
	protected boolean regeneration() {
		return false;
	}

	/**
	 * ��������Ŀ�ʼʱ��
	 */
	protected long getStartTime() {
		return 0l;
	}

	/**
	 * ��ʼǰ������false��ʾ��Ҫ��ֹ
	 */
	protected boolean workBeginning() {
		return true;
	}

	/**
	 * ��ʼ����
	 * 
	 * @throws Throwable
	 */
	protected void workDoing(WorkingThread thread) throws Throwable {
	}

	/**
	 * ȡ����
	 */
	protected void workCanceling() {
		this.thread.interrupt();
	}

	/**
	 * �����
	 * 
	 * @param e
	 *            ���쳣ʱΪnull
	 */
	protected void workFinalizing(Throwable e) {

	}

	private WorkingThread thread;

	/**
	 * ����false��ʾ��Ҫ��ֹ
	 */
	private final boolean beforeDoingNoSync(WorkingThread thread,
			ConcurrentController ccr) {
		switch (this.state) {
		case CANCELED:
			if (ccr != null) {
				// �뿪��������
				ccr.leaveScope(thread.manager);
			}
			return false;// ��ǰ�ͱ���ֹ
		case POSTING:
		case STARTING:
			this.state = AsyncState.PROCESSING;
		case PROCESSING:
			break;
		default:
			throw this.illegalState();
		}
		this.thread = thread;
		return this.workBeginning();
	}

	private final void afterDoingNoSync(WorkingThread thread,
			ConcurrentController ccr, Throwable ex) {
		this.thread = null;
		try {
			this.workFinalizing(ex);
		} finally {
			if (ccr != null) {
				// �뿪��������
				ccr.leaveScope(thread.manager);
			}
			this.notifyAll();
			// ȷ������״̬
			if (ex == null) {
				this.state = AsyncState.FINISHED;
			} else if (ex instanceof InterruptedException && this.state.canceling) {
				this.state = AsyncState.CANCELED;
				return;
			} else {
				this.state = AsyncState.ERROR;
				return;
			}
			// ���������Թ���
			if (this.regeneration()) {
				this.state = AsyncState.POSTING;
				thread.manager.postWork(this);
			}
		}
	}

	/**
	 * ���У�����false��ʾҪ���߳���ֹ
	 * 
	 * @param thread
	 * @return
	 * @throws Throwable
	 */
	protected void doWork(WorkingThread thread) throws Throwable {
		final ConcurrentController ccr = this.getConcurrentController();
		Throwable ex = null;
		beforeDoing: synchronized (this) {
			try {
				if (this.beforeDoingNoSync(thread, ccr)) {
					break beforeDoing;
				}
			} catch (Throwable e) {
				ex = e;
			}
			this.afterDoingNoSync(thread, ccr, ex);
			if (ex != null) {
				throw ex;
			} else {
				return;
			}
		}
		try {
			final ClassLoader oldCl = thread.getContextClassLoader();
			ClassLoader globalCl = thread.manager.getContextClassLoader();
			if (globalCl != oldCl) {
				thread.setContextClassLoader(globalCl);
			} else {
				globalCl = null;
			}
			try {
				this.workDoing(thread);
			} finally {
				if (globalCl !=null) {
					thread.setContextClassLoader(oldCl);
				}
			}
		} catch (Throwable e) {
			ex = e;
		}
		synchronized (this) {
			this.afterDoingNoSync(thread, ccr, ex);
		}
		if (ex != null) {
			throw ex;
		}
	}

	/**
	 * ����ȡ��
	 * 
	 * <p>
	 * ��һ���ܹ��ɹ���ȡ������ȡ����ʵ�����Ƿ�������Ӧ��֧�֡�
	 */
	public synchronized final void cancel() {
		switch (this.state) {
		// ����֮ǰ
		case SCHEDULING:
		case QUEUING:
			// TODO ����
		case POSTING:
		case STARTING:
			this.state = AsyncState.CANCELED;
			this.notifyAll();
			return;
			// ����֮��
		case PROCESSING:
			this.state = AsyncState.CANCELING;
			break;
		// �������
		case FINISHED:
		case ERROR:
		case CANCELED:
		case CANCELING:
			return;// �Ѿ����������ڽ���
		default:
			throw this.illegalState();
		}
		this.workCanceling();
	}

	/**
	 * �ȴ�����
	 */
	public synchronized void waitStop(long timeout) throws InterruptedException {
		long outTime = 0;
		for (;;) {
			switch (this.state) {
			// ���²��ֱ�ʾ�������ڵȴ�����������ֱ�ӷ���
			case FINISHED:
			case ERROR:
			case CANCELED:
				return;// ��������
				// ���±�ʾ��Ч״̬
			case SCHEDULING: // ��������̳߳�ʱ��ȴ�����˲���֧��
				throw this.illegalState();
			}
			if (timeout != 0) {
				if (outTime == 0) {
					outTime = System.nanoTime() / 1000000L + timeout;
				} else {
					timeout = outTime - System.nanoTime() / 1000000L;
					if (timeout <= 0) {
						return;// ��ʱ
					}
				}
			}
			this.wait(timeout);
		}
	}

	public final AsyncState getState() {
		return this.state;
	}

	final void putToAwaitScheduleQ(Work tail) {
		switch (this.state) {
		case POSTING:
			this.state = AsyncState.SCHEDULING;
			break;
		case CANCELED:
			return;
		default:
			throw this.illegalState();
		}
		this.putToQ(tail);
	}
}
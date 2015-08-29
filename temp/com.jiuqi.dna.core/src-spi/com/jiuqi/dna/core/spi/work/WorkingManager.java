package com.jiuqi.dna.core.spi.work;

import java.util.HashMap;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.ApplicationImpl;
import com.jiuqi.dna.core.internal.management.Managements;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.log.Logger;
import com.jiuqi.dna.core.ws.impl.CXFHelper;

/**
 * �첽���ù�����
 * 
 * @author gaojingxin
 * 
 */
public final class WorkingManager implements WorkingManagerMBean {

	public static final ClassLoader DEFAULT_CL;
	
	static {
		ClassLoader loader = null;
		try {
			loader = CXFHelper.CLASS_LOADER;
		} catch (NoClassDefFoundError e) {
			loader = WorkingThread.class.getClassLoader();
		}
		DEFAULT_CL = loader;
	}
	
	/**
	 * Ĭ�ϵ���С�߳�����
	 */
	static final int DEFAULT_MIN_THREADS = 4;
	/**
	 * Ĭ�ϵ��߳��������ʱ��
	 */
	static final int DEFAULT_MAX_IDLE_LIFE = 60 * 1000;
	/**
	 * ������
	 */
	private Scheduler scheduler;

	public final boolean isActive() {
		return this.scheduler != null;
	}

	final ApplicationImpl application;
	
	private ClassLoader contextClassLoader;

	public WorkingManager(ApplicationImpl application) {
		if (application == null) {
			throw new NullArgumentException("application");
		}
		this.application = application;
		this.startWork(this.scheduler = new Scheduler());
		Managements.registerMBean(this, "Thread Pool");
		this.contextClassLoader = DEFAULT_CL;
	}

	public ClassLoader getContextClassLoader() {
		return contextClassLoader;
	}

	public void setContextClassLoader(ClassLoader contextClassLoader) {
		this.contextClassLoader = contextClassLoader;
	}

	/**
	 * �����첽����<br>
	 * �÷����ĵ��ñ����빤���������߳���ͬһ���̣߳�������������
	 * 
	 * @param invoke
	 */
	public final void postWork(Work work) {
		if (work == null) {
			throw new NullArgumentException("work");
		}
		final Scheduler schdlr = this.scheduler;
		if (schdlr != null) {
			if (work.getStartTime() <= System.currentTimeMillis()) {
				if (work.tryEnterConcurrentControlScope()) {
					this.startWork(work);
				}
			} else {
				schdlr.putScheduledWork(work);
			}
		}
	}

	// TODO ���ʵ�ֺܲ��á����ȴ����ȵ�workӦ�÷��õ�context�ϣ�����ȫЩ��
	private final HashMap<Object, Work> awaitingScheduled = new HashMap<Object, Work>();

	public final void postWork(Work work, Object await) {
		if (await == null) {
			this.postWork(work);
			return;
		}
		synchronized (this.awaitingScheduled) {
			Work tail = this.awaitingScheduled.put(await, work);
			work.putToAwaitScheduleQ(tail);
		}
	}

	public final void signalAwaitingScheduled(Object await, boolean post) {
		Work tail;
		synchronized (this.awaitingScheduled) {
			tail = this.awaitingScheduled.remove(await);
		}
		if (tail != null && post) {
			for (Work work = tail.removeNext(); work != tail; work = tail.removeNext()) {
				this.postWork(work);
			}
			this.postWork(tail);
		}
	}

	public final void doDispose() {
		try {
			synchronized (this) {
				if (this.threads == 0) {
					return;
				}
				this.scheduler = null;
				this.idleThreads = null;
				this.startingWorksTail = null;
				this.threadGroup.interrupt();
				this.wait();
			}
		} catch (InterruptedException e) {
		}
	}

	final ThreadGroup threadGroup = new ThreadGroup("dna");
	/**
	 * ��С�߳�����
	 */
	private int minIdleThreads = DEFAULT_MIN_THREADS;
	/**
	 * ������ʱ��
	 */
	private int maxIdleLife = DEFAULT_MAX_IDLE_LIFE;

	/**
	 * �����߳�����
	 * 
	 * @return
	 */
	public final int getIdleThreads() {
		return this.idles;
	}

	/**
	 * �Ƿ��̲߳�����
	 */
	public final boolean isLowOnThreads() {
		return this.threads == this.maxThreads && this.idleThreads == null;
	}

	/**
	 * �̵߳�������
	 */
	private int maxThreads = Integer.MAX_VALUE;

	/**
	 * �߳�����
	 */
	private int threads;

	final int getThreadCount() {
		return this.threads;
	}

	/**
	 * ��Ҫ����ִ�еĹ��������̲߳���ʱ������жӡ�
	 */
	private Work startingWorksTail;
	/**
	 * ���õ��߳�
	 */
	private WorkingThread idleThreads;
	/**
	 * ���õ��߳�����
	 */
	private int idles;

	/**
	 * �̱߳��
	 */
	private int threadNum;

	// �漰��������״̬�ĸı䣬<br>
	// ��ǰ�߳��빤�����ύ�̲߳���ͬһ���߳�ʱ��Ҫ�ⲿ��������<br>
	// ��ǰ��֪�Ĳ���ͬһ�̵߳ĵط���activeWork();ConcurrentController.leaveScope(
	// OverlappedManager)

	/**
	 * ��ʼ�첽��ִ�й���
	 * 
	 * <p>
	 * �÷���������<code>ExecutorService</code>��<code>submit</code>������
	 * 
	 * @param work
	 * @return �Ƿ�ɹ����ύ�˹�����
	 */
	public final synchronized boolean startWork(Work work) {
		if (this.scheduler == null) {
			return false;
		}
		WorkingThread idle = this.idleThreads;
		if (idle != null) {
			if (work.putToWorkThread()) {
				this.idleThreads = idle.nextIdle;
				this.idles--;
				synchronized (idle) {
					idle.work = work;
					idle.notify();
				}
				return true;
			}
		} else if (work.putToStartQ(this.startingWorksTail)) {
			this.startingWorksTail = work;
			if (this.threads < this.maxThreads) {
				this.threads++;
				new WorkingThread(this, "dna-".concat(Integer.toString(++this.threadNum)));
			}
			return true;
		}
		return false;
	}

	private final void threadExitNoSync(WorkingThread thread, boolean idleThread) {
		if (idleThread) {
			for (WorkingThread first = this.idleThreads, last = null; first != null; last = first, first = first.nextIdle) {
				if (first == thread) {
					// ���ж����Ƴ�
					if (last == null) {
						this.idleThreads = thread.nextIdle;
					} else {
						last.nextIdle = thread.nextIdle;
					}
					this.idles--;
					break;
				}
			}
		}
		thread.nextIdle = null;
		if (--this.threads == 0) {
			this.notify();
		}
	}

	final synchronized void threadExit(WorkingThread thread) {
		this.threadExitNoSync(thread, false);
	}

	// version 2
	// private final Object startingWorksLock = new Object();
	// private int startings;
	// private int waitingThreads;
	//
	// final void threadExit2(WorkingThread thread) {
	// synchronized (this.startingWorksLock) {
	// this.threads--;
	// }
	// }
	//
	// final boolean startWork2(Work work) {
	// synchronized (this.startingWorksLock) {
	// if (this.scheduler == null) {
	// return false;
	// }
	// if (work.putToStartQ(this.startingWorksTail)) {
	// this.startings++;
	// this.startingWorksTail = work;
	// if (this.threads > this.maxThreads) {
	// return true;
	// } else if (this.idles < this.startings) {
	// this.threads++;
	// this.idles++;
	// new WorkingThread(this, "dna-".concat(Integer
	// .toString(++this.threadNum)));
	// } else if (this.waitingThreads > 0) {
	// this.startingWorksLock.notify();
	// }
	// return true;
	// }
	// return false;
	// }
	// }
	//
	// final Work getWorkToDo2(WorkingThread thread, boolean doneWork) {
	// synchronized (this.startingWorksLock) {
	// for (;;) {
	// if (doneWork) {
	// doneWork = false;
	// this.idles++;
	// }
	// if (this.scheduler == null) {
	// // ˵���Ѿ�ֹͣ
	// this.idles--;
	// this.threads--;
	// return null;
	// }
	// // ��������߳���ʱ��ֹ
	// if (this.threads > this.maxThreads) {
	// this.idles--;
	// this.threads--;
	// if (this.waitingThreads > 0
	// && this.threads > this.maxThreads) {
	// this.startingWorksLock.notify();
	// }
	// return null;
	// }
	// final Work tail = this.startingWorksTail;
	// if (tail != null) {
	// if (--this.startings == 0) {
	// this.startingWorksTail = null;
	// } else if (this.waitingThreads > 0) {
	// // ֪ͨ�����̹߳���
	// this.startingWorksLock.notify();
	// }
	// final Work todo = tail.removeNext();
	// if (todo.getStartTime() <= System.currentTimeMillis()) {
	// this.idles--;
	// return todo;
	// }
	// this.scheduler.putScheduledWork(todo);
	// } else {// û��Ҫ���Ĺ���
	// this.waitingThreads++;
	// try {
	// this.startingWorksLock.wait(this.maxIdleLife);
	// } catch (InterruptedException e) {
	// // ����ֹ��������ֹͣ����ʱ���ÿ��ǹ������Լ��ӿ����ж����Ƴ�
	// this.threads--;
	// this.idles--;
	// return null;
	// } finally {
	// this.waitingThreads--;
	// }
	// }
	// }
	// }
	// }

	/**
	 * ��ȡ��Ҫִ�еĹ���
	 * 
	 * <p>
	 * �÷����ᵼ���߳̽���ȴ���
	 * 
	 * @param thread
	 *            ִ�й������̡߳�
	 * @return ���ؿձ�ʾҪ�˳��̡߳�
	 */
	final Work getWorkToDo(WorkingThread thread) {
		for (;;) {
			Work todo = null;
			findWorkToDo: {
				// ����ȡ���ȴ�ִ�еĹ����������̷߳�������ж�
				synchronized (this) {
					// ��������߳���ʱ��Ҫ����
					if (this.scheduler == null || this.threads > this.maxThreads) {
						this.threadExitNoSync(thread, false);
						return null;
					}
					// ����ж������򷵻�
					final Work tail = this.startingWorksTail;
					if (tail != null) {
						final Work first = tail.removeNext();
						if (first == tail) {
							this.startingWorksTail = null;
						}
						todo = first;
						break findWorkToDo;
					}
					// �����������ж�
					thread.nextIdle = this.idleThreads;
					this.idleThreads = thread;
					this.idles++;
				}
				//
				for (;;) {
					// ���е�����ʱ�߳��Ѿ��ǿ����߳�
					try {
						synchronized (thread) {
							// �������������ͬ������Ƿ񱻷����˹���
							todo = thread.work;
							if (todo == null) {
								// ����ȴ�
								thread.wait(this.maxIdleLife);
								todo = thread.work;
							}
							thread.work = null;
						}
					} catch (InterruptedException e) {
						if (this.isActive()) {
							Logger logger = DNALogManager.getLogger("core/system");
							logger.logFatal(null, "��ͼ��ֹidle���̡߳�work: " + todo, false);
							continue;
						}
						synchronized (this) {
							this.threadExitNoSync(thread, true);
						}
						// ����ֹ��������ֹͣ����ʱ���ÿ��ǹ������Լ��ӿ����ж����Ƴ�
						return null;
					}
					if (todo != null) {
						break findWorkToDo;
					}
					// ��ʱ��������������Ƿ񱻷����˹������Բ���ȷ��
					synchronized (this) {
						// ���������ó�ʱ�̣߳�����������ǰ�߳���Ȼ���ܻᱻ���乤��
						todo = thread.work;
						if (todo != null) {
							thread.work = null;
							break findWorkToDo;
						}
						// ȷʵ����
						if (this.idles > this.minIdleThreads) {
							// ������С�߳�������˴������ж����Ƴ���������ֹ�߳�
							this.threadExitNoSync(thread, true);
							return null;
						}
					}
					// �̻߳����Ա�������˴��µȴ�
				}
			}
			if (todo.getStartTime() <= System.currentTimeMillis()) {
				return todo;
			}
			final Scheduler schdlr = this.scheduler;
			if (schdlr != null) {
				schdlr.putScheduledWork(todo);
			}
		}
	}

	/**
	 * ������
	 * 
	 * @author gaojingxin
	 * 
	 */
	private static final class Scheduler extends Work {
		/**
		 * ��ʱ�����жӵĶ�β������ж���Ĺ�������û�е�������ʱ��
		 */
		private Work scheduledWorksTail;

		/**
		 * ����ʱ���������ж�
		 */
		final void putScheduledWork(Work work) {
			synchronized (this) {
				int m = work.putToSchedulingQ(this.scheduledWorksTail);
				if ((m & Work.WORK_LAST_MASK) != 0) {
					this.scheduledWorksTail = work;
				}
				if ((m & Work.WORK_FIRST_MASK) != 0) {
					this.notify();
				}
			}
		}

		@Override
		protected final void doWork(WorkingThread thread) {
			final String oldThreadName = thread.getName();
			try {
				thread.setName("dna-scheduler");
				synchronized (this) {
					for (;;) {
						long timeout;
						if (this.scheduledWorksTail != null) {
							timeout = this.scheduledWorksTail.tryTailStartScheduledWorks(thread.manager);
							if (timeout == 0) {
								this.scheduledWorksTail = null;
							}
						} else {
							timeout = 0;
						}
						try {
							this.wait(timeout);
						} catch (InterruptedException e) {
							return;
						}
					}
				}
			} finally {
				thread.setName(oldThreadName);
			}
		}
	}

	public final int getCount() {
		return this.threads;
	}

	public final int getIdleCount() {
		return this.idles;
	}
}
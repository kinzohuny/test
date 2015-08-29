package com.jiuqi.dna.core.spi.work;

import java.util.HashMap;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.ApplicationImpl;
import com.jiuqi.dna.core.internal.management.Managements;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.log.Logger;
import com.jiuqi.dna.core.ws.impl.CXFHelper;

/**
 * 异步调用管理器
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
	 * 默认的最小线程数量
	 */
	static final int DEFAULT_MIN_THREADS = 4;
	/**
	 * 默认的线程最大闲置时间
	 */
	static final int DEFAULT_MAX_IDLE_LIFE = 60 * 1000;
	/**
	 * 调度器
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
	 * 启动异步调用<br>
	 * 该方法的调用必须与工作的启动线程在同一个线程，或者锁定工作
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

	// TODO 这个实现很不好——等待调度的work应该放置到context上，更安全些。
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
	 * 最小线程数量
	 */
	private int minIdleThreads = DEFAULT_MIN_THREADS;
	/**
	 * 最大空闲时间
	 */
	private int maxIdleLife = DEFAULT_MAX_IDLE_LIFE;

	/**
	 * 空闲线程数量
	 * 
	 * @return
	 */
	public final int getIdleThreads() {
		return this.idles;
	}

	/**
	 * 是否线程不够用
	 */
	public final boolean isLowOnThreads() {
		return this.threads == this.maxThreads && this.idleThreads == null;
	}

	/**
	 * 线程的最大个数
	 */
	private int maxThreads = Integer.MAX_VALUE;

	/**
	 * 线程总数
	 */
	private int threads;

	final int getThreadCount() {
		return this.threads;
	}

	/**
	 * 需要马上执行的工作，当线程不够时进入该列队。
	 */
	private Work startingWorksTail;
	/**
	 * 闲置的线程
	 */
	private WorkingThread idleThreads;
	/**
	 * 闲置的线程数量
	 */
	private int idles;

	/**
	 * 线程编号
	 */
	private int threadNum;

	// 涉及到工作的状态的改变，<br>
	// 当前线程与工作的提交线程不是同一个线程时需要外部锁定工作<br>
	// 当前已知的不在同一线程的地方是activeWork();ConcurrentController.leaveScope(
	// OverlappedManager)

	/**
	 * 开始异步的执行工作
	 * 
	 * <p>
	 * 该方法类似于<code>ExecutorService</code>的<code>submit</code>方法。
	 * 
	 * @param work
	 * @return 是否成功的提交了工作。
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
					// 从列队中移除
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
	// // 说明已经停止
	// this.idles--;
	// this.threads--;
	// return null;
	// }
	// // 大于最大线程数时终止
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
	// // 通知其他线程工作
	// this.startingWorksLock.notify();
	// }
	// final Work todo = tail.removeNext();
	// if (todo.getStartTime() <= System.currentTimeMillis()) {
	// this.idles--;
	// return todo;
	// }
	// this.scheduler.putScheduledWork(todo);
	// } else {// 没有要做的工作
	// this.waitingThreads++;
	// try {
	// this.startingWorksLock.wait(this.maxIdleLife);
	// } catch (InterruptedException e) {
	// // 被终止，管理器停止，这时不用考虑工作，以及从空闲列队中移除
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
	 * 获取需要执行的工作
	 * 
	 * <p>
	 * 该方法会导致线程进入等待。
	 * 
	 * @param thread
	 *            执行工作的线程。
	 * @return 返回空表示要退出线程。
	 */
	final Work getWorkToDo(WorkingThread thread) {
		for (;;) {
			Work todo = null;
			findWorkToDo: {
				// 尝试取出等待执行的工作，否则将线程放入空闲列队
				synchronized (this) {
					// 大于最大线程数时就要收缩
					if (this.scheduler == null || this.threads > this.maxThreads) {
						this.threadExitNoSync(thread, false);
						return null;
					}
					// 如果列队中有则返回
					final Work tail = this.startingWorksTail;
					if (tail != null) {
						final Work first = tail.removeNext();
						if (first == tail) {
							this.startingWorksTail = null;
						}
						todo = first;
						break findWorkToDo;
					}
					// 否则放入空闲列队
					thread.nextIdle = this.idleThreads;
					this.idleThreads = thread;
					this.idles++;
				}
				//
				for (;;) {
					// 运行到这里时线程已经是空闲线程
					try {
						synchronized (thread) {
							// 检查在上下两个同步块间是否被分派了工作
							todo = thread.work;
							if (todo == null) {
								// 否则等待
								thread.wait(this.maxIdleLife);
								todo = thread.work;
							}
							thread.work = null;
						}
					} catch (InterruptedException e) {
						if (this.isActive()) {
							Logger logger = DNALogManager.getLogger("core/system");
							logger.logFatal(null, "试图终止idle的线程。work: " + todo, false);
							continue;
						}
						synchronized (this) {
							this.threadExitNoSync(thread, true);
						}
						// 被终止，管理器停止，这时不用考虑工作，以及从空闲列队中移除
						return null;
					}
					if (todo != null) {
						break findWorkToDo;
					}
					// 超时，但其闲置与否（是否被分配了工作）仍不可确定
					synchronized (this) {
						// 即便是闲置超时线程，但进入这里前线程仍然可能会被分配工作
						todo = thread.work;
						if (todo != null) {
							thread.work = null;
							break findWorkToDo;
						}
						// 确实闲置
						if (this.idles > this.minIdleThreads) {
							// 超过最小线程数，因此从闲置列队中移除，并且终止线程
							this.threadExitNoSync(thread, true);
							return null;
						}
					}
					// 线程还可以保留，因此从新等待
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
	 * 调度器
	 * 
	 * @author gaojingxin
	 * 
	 */
	private static final class Scheduler extends Work {
		/**
		 * 定时工作列队的队尾，这个列队里的工作都还没有到工作的时间
		 */
		private Work scheduledWorksTail;

		/**
		 * 将定时工作放入列队
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
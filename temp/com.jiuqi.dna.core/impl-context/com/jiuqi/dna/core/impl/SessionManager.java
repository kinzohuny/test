package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.jiuqi.dna.core.SessionKind;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.exception.SessionDisposedException;
import com.jiuqi.dna.core.exception.SessionDisposedException.SessionDisposedKind;
import com.jiuqi.dna.core.internal.management.Managements;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.application.Session;
import com.jiuqi.dna.core.spi.application.SessionIniter;
import com.jiuqi.dna.core.spi.work.Work;
import com.jiuqi.dna.core.spi.work.WorkingThread;
import com.jiuqi.dna.core.type.GUID;

/**
 * 会话池
 * 
 * @author gaojingxin
 * 
 */
public class SessionManager implements SessionManagerMBean {

	final ApplicationImpl application;

	private void disposeOrReset(boolean disposeOrReset) {
		final ArrayList<SessionImpl> sessions;
		this.writeLock.lock();
		try {
			if (this.size > 0) {
				sessions = new ArrayList<SessionImpl>(this.size);
				for (SessionImpl session : this.sessionHashTable) {
					while (session != null) {
						sessions.add(session);
						session = session.nextInHashTable;
					}
				}
			} else {
				sessions = null;
			}
			if (disposeOrReset) {
				this.systemSession.internalDispose(0l);
			} else {
				this.systemSession = this.newSystemSession();
			}
		} finally {
			this.writeLock.unlock();
		}
		if (sessions != null) {
			for (SessionImpl session : sessions) {
				try {
					session.internalDispose(disposeOrReset ? 0l : 1l);
				} catch (Throwable e) {
					this.application.catcher.catchException(e, session);
				}
			}
		}
	}

	/**
	 * 重置会话，用于参数同步后重置会话
	 */
	final void doReset() {
		this.disposeOrReset(false);
	}

	final void doDispose() {
		this.disposeOrReset(true);
	}

	/**
	 * 会话超时时间（分钟），三十分钟
	 */
	private int sessionTimeoutMinutes;
	/**
	 * 会话心跳时间（秒），5分钟
	 */
	private int sessionHeartbeatSeconds;

	private SessionImpl newSystemSession() {
		return new SessionImpl(this.application, this.application.timeRelatedSequence.next(), SessionKind.SYSTEM, BuildInUser.system, null, null, this.sessionTimeoutMinutes, this.sessionHeartbeatSeconds);
	}

	final static String xml_element_session = "session";
	final static String xml_element_sessionTimeoutMins = "timeout-m";
	final static String xml_element_sessionHeartbeatSeconds = "heartbeat-s";

	SessionManager(ApplicationImpl application, SXElement sessionConfig) {
		if (application == null) {
			throw new NullArgumentException("application");
		}
		this.sessionTimeoutMinutes = sessionConfig != null ? sessionConfig.getInt(xml_element_sessionTimeoutMins, Session.DEFAULT_TIMEOUT_MINUTEs) : Session.DEFAULT_TIMEOUT_MINUTEs;
		this.sessionHeartbeatSeconds = sessionConfig != null ? sessionConfig.getInt(xml_element_sessionHeartbeatSeconds, Session.DEFAULT_HEARTBEAT_SECs) : Session.DEFAULT_HEARTBEAT_SECs;
		this.application = application;
		ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
		this.readLock = rwl.readLock();
		this.writeLock = rwl.writeLock();
		this.systemSession = this.newSystemSession();
		application.overlappedManager.postWork(new RepeatWork(5000) {
			@Override
			protected void workDoing(WorkingThread thread) throws Throwable {
				SessionManager.this.clearExpiredSessions();
			}
		});
		Managements.registerMBean(this, "Session");
	}

	private final void clearExpiredSessions() {
		final long now = System.currentTimeMillis();
		ArrayList<SessionImpl> expiredOrTimeOuts = null;
		this.readLock.lock();
		try {
			if (this.size > 0) {
				for (SessionImpl session : this.sessionHashTable) {
					while (session != null) {
						if (session.itsTimeToDispose(now)) {
							if (expiredOrTimeOuts == null) {
								expiredOrTimeOuts = new ArrayList<SessionImpl>();
							}
							expiredOrTimeOuts.add(session);
						}
						session = session.nextInHashTable;
					}
				}
			}
		} finally {
			this.readLock.unlock();
		}
		if (expiredOrTimeOuts != null) {
			for (int i = 0, c = expiredOrTimeOuts.size(); i < c; i++) {
				final SessionImpl session = expiredOrTimeOuts.get(i);
				this.application.overlappedManager.startWork(new Work() {
					@Override
					protected void doWork(WorkingThread thread)
							throws Throwable {
						session.doDispose();
					}
				});
			}
		}
	}

	final void remove(SessionImpl session) {
		this.writeLock.lock();
		try {
			final SessionImpl[] sessionHashTable = this.sessionHashTable;
			if (sessionHashTable == null) {
				return;
			}
			final int index = TimeRelatedSequenceImpl.hash(session.id) & (sessionHashTable.length - 1);
			for (SessionImpl s = sessionHashTable[index], prov = null; s != null; prov = s, s = s.nextInHashTable) {
				if (s == session) {
					if (prov == null) {
						sessionHashTable[index] = session.nextInHashTable;
					} else {
						prov.nextInHashTable = session.nextInHashTable;
					}
					this.size--;
					if (session.kind == SessionKind.NORMAL) {
						this.normalSessionCount--;
						if (!session.getUser().isBuildInUser()) {
							this.addNoneBuildinUseNormalrSessionCount(-1);
						}
					}
					break;
				}
			}
		} finally {
			this.writeLock.unlock();
			session.nextInHashTable = null;
		}
	}

	private volatile SessionImpl systemSession;

	final SessionImpl getSystemSession() {
		return this.systemSession;
	}

	private final ReadLock readLock;
	private final WriteLock writeLock;

	final <TUserData> SessionImpl newSession(SessionKind kind, User user,
			SessionIniter<TUserData> sessionIniter, TUserData userData) {
		if (kind == null) {
			throw new NullArgumentException("kind");
		}
		if (user == null) {
			throw new NullArgumentException("user");
		}
		final int hash;
		final long sessionID;
		if (kind == SessionKind.SYSTEM) {
			throw new IllegalArgumentException("不支持创建系统会话");
		}
		sessionID = this.application.timeRelatedSequence.next();
		hash = TimeRelatedSequenceImpl.hash(sessionID);
		final SessionImpl session;
		this.writeLock.lock();
		try {
			SessionImpl[] sessionHashTable = this.sessionHashTable;
			if (sessionHashTable == null) {
				this.sessionHashTable = sessionHashTable = new SessionImpl[256];
			}
			final int tableL = sessionHashTable.length;
			final int index;
			if (++this.size > tableL * 0.75) {
				final int newLen = tableL * 2;
				final int newHigh = newLen - 1;
				final SessionImpl[] newTable = new SessionImpl[newLen];
				for (int j = 0; j < tableL; j++) {
					for (SessionImpl s = sessionHashTable[j], next; s != null; s = next) {
						next = s.nextInHashTable;
						final int newIndex = TimeRelatedSequenceImpl.hash(s.id) & newHigh;
						s.nextInHashTable = newTable[newIndex];
						newTable[newIndex] = s;
					}
				}
				this.sessionHashTable = sessionHashTable = newTable;
				index = hash & newHigh;
			} else {
				index = hash & (tableL - 1);
			}
			session = sessionHashTable[index] = new SessionImpl(this.application, sessionID, kind, user, null, sessionHashTable[index], this.sessionTimeoutMinutes, this.sessionHeartbeatSeconds);
			if (kind == SessionKind.NORMAL) {
				this.normalSessionCount++;
			}
		} finally {
			this.writeLock.unlock();
		}
		if (sessionIniter != null) {
			try {
				sessionIniter.initSession(session, userData);
			} catch (Throwable e) {
				session.internalDispose(0);
				throw Utils.tryThrowException(e);
			}
		}
		return session;
	}

	final SessionImpl getOrFindSession(long sessionID, boolean get)
			throws SessionDisposedException {
		final SessionImpl systemSession;
		SessionImpl session;
		find: {
			this.readLock.lock();
			try {
				systemSession = this.systemSession;
				if (sessionID == systemSession.id) {
					return systemSession;
				}
				final int hash = TimeRelatedSequenceImpl.hash(sessionID);
				if (this.size > 0) {
					final SessionImpl[] sessionHashTable = this.sessionHashTable;
					for (session = sessionHashTable[hash & (sessionHashTable.length - 1)]; session != null; session = session.nextInHashTable) {
						if (session.id == sessionID) {
							if (session.isInvalid()) {
								throw new SessionDisposedException(SessionDisposedKind.USERINVALID);
							}
							if (session.disposingOrDisposed()) {
								if (get) {
									break find;
								} else {
									return null;
								}
							} else {
								return session;
							}
						}
					}
				}
			} finally {
				this.readLock.unlock();
			}
			if (!get) {
				return null;
			}
		}
		throw new SessionDisposedException(sessionID < systemSession.id ? SessionDisposedKind.OBSOLETE : SessionDisposedKind.NORMAL);
	}

	private volatile SessionImpl[] sessionHashTable;
	private volatile int size;
	private volatile int normalSessionCount;
	private volatile int noneBuildinUserNormalSessionCount;
	private static final AtomicIntegerFieldUpdater<SessionManager> noneBuildinUserNormalSessionCountUpdater = AtomicIntegerFieldUpdater.newUpdater(SessionManager.class, "noneBuildinUserNormalSessionCount");

	final int addNoneBuildinUseNormalrSessionCount(int delta) {
		return noneBuildinUserNormalSessionCountUpdater.addAndGet(this, delta);
	}

	@SuppressWarnings("unchecked")
	public List<? extends SessionImpl> getNormalSessions(GUID byUserID) {
		if (byUserID != null && byUserID.isEmpty()) {
			byUserID = null;
		}
		ArrayList<SessionImpl> to = null;
		if (this.normalSessionCount > 0 && (byUserID == null || this.noneBuildinUserNormalSessionCount > 0)) {
			this.readLock.lock();
			try {
				if (this.normalSessionCount > 0 && (byUserID == null || this.noneBuildinUserNormalSessionCount > 0)) {
					for (SessionImpl session : this.sessionHashTable) {
						while (session != null) {
							if (session.kind == SessionKind.NORMAL && (byUserID == null || byUserID.equals(session.getUser().getID()))) {
								if (to == null) {
									to = new ArrayList<SessionImpl>(byUserID == null ? this.normalSessionCount : 4);
								}
								to.add(session);
							}
							session = session.nextInHashTable;
						}
					}
				}
			} finally {
				this.readLock.unlock();
			}
		}
		return to == null ? Collections.EMPTY_LIST : to;
	}

	public final int getNormalSessionCount(boolean excludeBuildInUser) {
		return excludeBuildInUser ? this.noneBuildinUserNormalSessionCount : this.normalSessionCount;
	}

	public final int getSessionCount() {
		return this.normalSessionCount;
	}
}
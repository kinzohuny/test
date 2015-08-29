package com.jiuqi.dna.core.internal.db.datasource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.impl.ConsoleLog;
import com.jiuqi.dna.core.impl.ResolveHelper;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.internal.db.support.db2.Db2Metadata;
import com.jiuqi.dna.core.internal.db.support.dm.DmMetadata;
import com.jiuqi.dna.core.internal.db.support.hana.HanaMetadata;
import com.jiuqi.dna.core.internal.db.support.mysql.MysqlMetadata;
import com.jiuqi.dna.core.internal.db.support.oracle.OracleMetadata;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;
import com.jiuqi.dna.core.internal.db.sync.DbSync;
import com.jiuqi.dna.core.internal.management.Managements;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.application.Application;

/**
 * ���ӳ�
 * 
 * @author gaojingxin
 * 
 */
public final class ComboDataSource extends ComboDataSourceConf implements
		ComboDataSourceMBean {

	public final DataSourceManager manager;
	public final ExceptionCatcher catcher;
	private final DataSource inner;

	ComboDataSource(DataSourceManager manager, SXElement element) {
		super(element);
		this.manager = manager;
		this.catcher = manager.application.catcher;
		this.inner = this.defineDataSource();
		if (Application.IN_DEBUG_MODE) {
			this.listeners.add(SqlConsolePrinter.INSTANCE);
		}
		this.prepare();
		Managements.registerMBean(this, "Connection Pool", this.name);
	}

	private final DataSource defineDataSource() {
		final JdbcDriverDataSource jdbc = JdbcDriverManager.INSTANCE.tryJdbcDataSource(this.location, this.user, this.password);
		if (jdbc != null) {
			jdbc.initPropsUsingConfig(this);
			return jdbc;
		}
		final DataSource jndi = WebContainer.lookupEach(this, this.manager.application.contextFinder);
		if (jndi != null) {
			return jndi;
		}
		throw new RuntimeException("��Ч������Դ���ã�" + this.location);
	}

	private volatile DbMetadata metadata;

	private final void prepare() {
		if (this.metadata == null) {
			synchronized (this) {
				if (this.metadata == null) {
					if (this.inner == null) {
						return;
					}
					try {
						final Connection conn = this.inner.getConnection();
						try {
							DatabaseMetaData dbmd = conn.getMetaData();
							this.metadata = DbProduct.detect(dbmd).callback(METADATA_FACTORY, conn);
							ResolveHelper.logStartInfo("��������Դ[" + this.name + "],���ݿ�[" + dbmd.getDatabaseProductName() + ", " + dbmd.getDatabaseProductVersion() + "],����[" + dbmd.getDriverName() + ", " + dbmd.getDriverVersion() + "]");
							if (this.inner instanceof JdbcDriverDataSource) {
								JdbcDriverDataSource ds = (JdbcDriverDataSource) this.inner;
								ds.initPropsUsingMetadata(this.metadata);
							}
							this.initDatabase();
						} finally {
							conn.close();
						}
					} catch (Throwable th) {
						this.manager.application.catcher.catchException(th, this.manager);
					}
				}
			}
		}
	}

	private static final DbProduct.Callee<Connection, DbMetadata> METADATA_FACTORY = new DbProduct.Callee<Connection, DbMetadata>() {

		@Override
		public DbMetadata onOracle(Connection data) throws SQLException {
			return new OracleMetadata(data);
		}

		@Override
		public DbMetadata onDB2(Connection data) throws Throwable {
			return new Db2Metadata(data);
		}

		@Override
		public DbMetadata onMySQL(Connection data) throws Throwable {
			return new MysqlMetadata(data);
		}

		@Override
		public DbMetadata onSQLServer(Connection data) throws Throwable {
			return new SqlserverMetadata(data);
		}

		@Override
		public DbMetadata onPostgre(Connection data) throws Throwable {
			throw new UnsupportedOperationException();
		}

		@Override
		public DbMetadata onDameng(Connection data) throws Throwable {
			return new DmMetadata(data);
		}

		@Override
		public DbMetadata onHana(Connection data) throws Throwable {
			return new HanaMetadata(data);
		}

		@Override
		public DbMetadata onUnknown(Connection data) throws Throwable {
			throw new UnsupportedOperationException("��֧�ֵ����ݿ⡣");
		}
	};

	private final void ensurePrepared() {
		this.prepare();
		if (this.metadata == null) {
			throw new RuntimeException("��Ч������Դ���ã�" + this.location);
		}
	}

	public final DbMetadata getMetadata() {
		this.ensurePrepared();
		return this.metadata;
	}

	private final void initDatabase() throws Throwable {
		final PooledConnection conn = this.alloc();
		try {
			DbSync refactor = this.metadata.newDbRefactor(conn, this.catcher);
			try {
				refactor.initDb();
			} finally {
				refactor.unuse();
			}
		} finally {
			conn.release();
		}
	}

	/**
	 * ��ǰ�������
	 */
	public final int getActiveCount() {
		return this.activeCount;
	}

	/**
	 * ����ʹ�õ�������
	 */
	public final int getUsingCount() {
		return this.usingCount;
	}

	public final int getWaitingCount() {
		return this.waitingCount;
	}

	private volatile int activeCount;
	private volatile int usingCount;
	private volatile int waitingCount;
	private PooledConnection idles;

	final CopyOnWriteArrayList<ComboDataSourceListener> listeners = new CopyOnWriteArrayList<ComboDataSourceListener>();

	private volatile boolean disposed;

	synchronized final void dispose() {
		this.disposed = true;
		if (this.activeCount > 0) {
			PooledConnection ce = this.idles;
			if (ce != null) {
				do {
					ce.close();
					this.activeCount--;
					ce = ce.next;
				} while (ce != null);
				this.idles = null;
			}
			if (this.waitingCount > 0) {
				this.notifyAll();
			}
		}
	}

	/**
	 * ����������Ӧ���׳��쳣
	 */
	final void adjust() {
		if (this.disposed) {
			return;
		}
		if (this.metadata == null) {
			return;
		}
		// ��¼���������
		for (PooledConnection needCheck = null;;) {
			final long now = System.currentTimeMillis();
			final long lastValidTime = now - this.idleLifeMS;
			final long lastTrustTime = now - this.trustLifeMS;
			PooledConnection needClose = null;
			sync: synchronized (this) {
				if (this.disposed) {
					if (needCheck != null) {
						// ����ͬ�����ͷ�
						needClose = needCheck;
						needCheck = null;
						break sync;
					}
					return;
				}
				PooledConnection ce = this.idles;
				PooledConnection last;
				if (needCheck != null) {
					// ����һ��ѭ�������ϵ����ӷŻؿ����ж�
					needCheck.next = ce;
					this.idles = needCheck;
					last = needCheck;
					needCheck = null;
				} else {
					last = null;
				}
				while (ce != null) {
					final PooledConnection next = ce.next;
					final long lastAccess = ce.lastAccess();
					if (lastAccess < lastValidTime && this.activeCount > this.minConnCount) {
						// ժ����������
						if (last == null) {
							this.idles = next;
						} else {
							last.next = next;
						}
						ce.next = needClose;
						needClose = ce;
					} else if (lastAccess < lastTrustTime) {
						// ժ����Ҫ��֤������
						if (last == null) {
							this.idles = next;
						} else {
							last.next = next;
						}
						needCheck = ce;
						// һ�δ���һ��
						break sync;
					} else {
						last = ce;
					}
					ce = next;
				}
				if (needClose == null) {
					// ������С���������ӣ����õ�Ƶ�ȷǳ��٣����Բ��ÿ������ٽ����ⲿ����
					while (this.activeCount < this.minConnCount) {
						try {
							ce = this.connect();
						} catch (Throwable e) {
							this.catcher.catchException(e, this);
							return;
						}
						ce.next = this.idles;
						this.idles = ce;
						// ��������������������ӻ����Ҳ����
						this.activeCount++;
					}
				}
			}
			// �˳��������
			// �رն��������
			while (needClose != null) {
				needClose.close();
				synchronized (this) {
					this.activeCount--;
					if (this.waitingCount > 0) {
						this.notify();
					}
				}
				needClose = needClose.next;
			}
			if (needCheck != null) {
				try {
					needCheck.check();
				} catch (Throwable e) {
					needCheck.close();
					needCheck = null;
					synchronized (e) {
						this.activeCount--;
						if (this.waitingCount > 0) {
							this.notify();
						}
					}
				}
			} else {
				break;
			}
		}
	}

	private final AtomicLong waitIdGen = new AtomicLong();

	public final PooledConnection alloc() {
		this.ensurePrepared();
		PooledConnection conn;
		do {
			synchronized (this) {
				for (;;) {
					if (this.disposed) {
						throw new IllegalStateException("���ӳ��Ѿ��ͷš�");
					}
					conn = this.idles;
					if (conn != null) {
						this.idles = conn.next;
						conn.next = null;
						break;
					} else if (this.activeCount < this.maxConnCount) {
						this.activeCount++;
						break;
					} else {
						InterruptedException th = null;
						final long waitId = this.waitIdGen.getAndIncrement();
						this.waitingCount++;
						for (ComboDataSourceListener listener : this.listeners) {
							listener.startWait(waitId);
						}
						try {
							this.wait();
						} catch (InterruptedException e) {
							th = e;
							throw Utils.tryThrowException(e);
						} finally {
							for (ComboDataSourceListener listener : this.listeners) {
								listener.finishWait(waitId, th);
							}
							this.waitingCount--;
						}
					}
				}
			}
			if (conn == null) {
				try {
					conn = this.connect();
				} catch (Throwable e) {
					synchronized (this) {
						this.activeCount--;
					}
					throw Utils.tryThrowException(e);
				}
			} else if (System.currentTimeMillis() - conn.lastAccess() > this.trustLifeMS) {
				try {
					conn.check();
				} catch (Throwable e) {
					conn.close();
					synchronized (this) {
						this.activeCount--;
					}
					this.catcher.catchException(e, conn);
					continue;
				}
			}
			synchronized (this) {
				this.usingCount++;
			}
			for (ComboDataSourceListener listener : this.listeners) {
				listener.afterAlloc(conn);
			}
		} while (false);
		return conn;
	}

	/**
	 * ֻ���ڻ���using������
	 * 
	 * @param conn
	 */
	final void revoke(PooledConnection conn) {
		for (ComboDataSourceListener listener : this.listeners) {
			listener.beforeRevoke(conn);
		}
		boolean close;
		synchronized (this) {
			close = this.disposed;
		}
		if (!close && conn.lastAccessException()) {
			try {
				conn.check();
			} catch (SQLException e) {
				this.catcher.catchException(e, conn);
				close = true;
			}
		}
		synchronized (this) {
			this.usingCount--;
			if (close || this.activeCount > this.maxConnCount) {
				conn.close();
				this.activeCount--;
			} else {
				conn.next = this.idles;
				this.idles = conn;
				if (this.waitingCount > 0) {
					this.notify();
				}
			}
		}
	}

	private final AtomicLong connIdGen = new AtomicLong();

	/**
	 * �������ӣ��ᴥ������Դ�ļ����¼���
	 * 
	 * <p>
	 * �̰߳�ȫ
	 * 
	 * @return
	 * @throws SQLException
	 */
	private final PooledConnection connect() throws SQLException {
		final long connId = this.connIdGen.getAndIncrement();
		final PooledConnection conn;
		for (ComboDataSourceListener listener : this.listeners) {
			listener.startConnect(connId);
		}
		try {
			conn = new PooledConnection(this, connId, this.connectPhysical());
		} catch (SQLException e) {
			for (ComboDataSourceListener listener : this.listeners) {
				listener.finishConnect(connId, e);
			}
			throw e;
		}
		for (ComboDataSourceListener listener : this.listeners) {
			listener.finishConnect(conn);
		}
		return conn;
	}

	private final Connection connectPhysical() throws SQLException {
		final Connection conn = this.inner.getConnection();
		this.metadata.init(conn);
		try {
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		} catch (SQLException e) {
			conn.close();
			throw e;
		}
		return conn;
	}

	@Override
	public final String getXMLTagName() {
		return DataSourceManager.XML_EL_DATASOURCE;
	}
}
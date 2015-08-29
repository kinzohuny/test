package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.exception.DisposedException;
import com.jiuqi.dna.core.impl.Cache.CustomGroupSpace;
import com.jiuqi.dna.core.impl.CacheDefine.KeyDefine;
import com.jiuqi.dna.core.impl.CacheDefine.KeyDefine.Entry;
import com.jiuqi.dna.core.impl.CacheDefine.PutPolicy;
import com.jiuqi.dna.core.impl.CacheDefine.ReferenceDefine;
import com.jiuqi.dna.core.impl.SessionImpl.SessionCacheGroupContainer;
import com.jiuqi.dna.core.misc.HashUtil;
import com.jiuqi.dna.core.misc.SortUtil;
import com.jiuqi.dna.core.type.GUID;

class CacheGroup<TFacade, TImplement extends TFacade, TKeysHolder> extends
		Acquirable {

	final void onTransactionCommitII(final Transaction transaction) {
		if (this.initalizeState == INITIALIZE_STATE_FINISH) {
			this.initalizeState = INITIALIZE_STATE_COMMITED;
		}
		switch (this.state) {
		case STATE_REMOVED:
			this.ownSpace.asCustomGroupSpace().disposeGroup(this, transaction);
		case STATE_DISPOSED:
			return;
		case STATE_CREATED:
			this.resolve(transaction);
		case STATE_RESOLVED:
			break;
		default:
			throw new UnsupportedOperationException();
		}
		if (this.bindTree != null) {
			this.bindTree.onTransactionCommit(transaction);
		}
	}

	final void onTransactionRollbackII(final Transaction transaction) {
		if (this.initalizeState == INITIALIZE_STATE_FINISH) {
			this.initalizeState = INITIALIZE_STATE_NONE;
		}
		switch (this.state) {
		case CacheGroup.STATE_CREATED:
			this.ownSpace.asCustomGroupSpace().disposeGroup(this, transaction);
		case CacheGroup.STATE_DISPOSED:
			return;
		case CacheGroup.STATE_REMOVED:
			this.resolve(transaction);
		case CacheGroup.STATE_RESOLVED:
			break;
		default:
			throw new UnsupportedOperationException();
		}
		if (this.state == STATE_DISPOSED) {
			return;
		} else {
			if (this.bindTree != null) {
				this.bindTree.onTransactionRollback(transaction);
			}
		}
	}

	final void ensureInitializedII(final Transaction transaction) {
		// 删除initializeException属性
		// 删除INITIALIZE_STATE_COMMITED、INITIALIZE_STATE_ERROR状态
		// 将INITIALIZE_STATE_STARTING改为INITIALIZE_STATE_DOING
		// 将INITIALIZE_STATE_FINISH改为INITIALIZE_STATE_DONE
		if (this.initalizeState != INITIALIZE_STATE_COMMITED) {
			transaction.handleAcquirable(this, AcquireFor.MODIFY);
			if ((this.initalizeState & INITIALIZE_STATE_FINISH) == 0) {
				try {
					if (this.initalizeState == INITIALIZE_STATE_STARTING) {
						transaction.getExceptionCatcher().catchException(new GroupInitialiazeException(this.define.facadeClass, this.ownSpace.identifier, "缓存中出现了循环依赖，请修改程序"), this);
					} else {
						this.initalizeState = INITIALIZE_STATE_STARTING;
						transaction.getCurrentContext().initializeCacheGroup(this, transaction);
					}
				} catch (Throwable e) {
					transaction.getExceptionCatcher().catchException(new GroupInitialiazeException(this.define.facadeClass, this.ownSpace.identifier, e), this);
				} finally {
					this.initalizeState = INITIALIZE_STATE_FINISH;
				}
			}
		}
	}

	private final static int DEFAULT_ITEM_MAP_CAPACITY = 8;

	static final byte STATE_CREATED = 0;

	static final byte STATE_RESOLVED = 1;

	static final byte STATE_REMOVED = 2;

	static final byte STATE_DISPOSED = 3;

	static final byte STATE_RESETTING = 4;

	static final byte INITIALIZE_STATE_NONE = 0;
	// 开始初始资源
	static final byte INITIALIZE_STATE_STARTING = 1;
	// 资源初始完毕但还未提交事务
	static final byte INITIALIZE_STATE_FINISH = 2;
	// 资源初始完毕且提交了事务
	static final byte INITIALIZE_STATE_COMMITED = INITIALIZE_STATE_FINISH | 0x4;
	// 资源初始完毕但出现了错误
	static final byte INITIALIZE_STATE_ERROR = INITIALIZE_STATE_FINISH | 0x8;

	static final class AccessControlInformation<TFacade, TImplement extends TFacade, TKeysHolder> {

		private AccessControlInformation(
				final GUID ACGUIDIdentifier,
				final long ACLongIdentifier,
				final CacheHolderIndex<TFacade, TImplement, TKeysHolder> accessControlIndex,
				final AccessControlCacheHolderOfGroup accessControlCacheItem) {
			this.ACGUIDIdentifier = ACGUIDIdentifier;
			this.ACLongIdentifier = ACLongIdentifier;
			this.accessControlIndex = accessControlIndex;
			this.accessControlCacheItem = accessControlCacheItem;
		}

		final GUID ACGUIDIdentifier;

		final long ACLongIdentifier;

		final CacheHolderIndex<TFacade, TImplement, TKeysHolder> accessControlIndex;

		final AccessControlCacheHolderOfGroup accessControlCacheItem;

	}

	CacheGroup(final CacheGroupSpace ownSpace,
			final CacheDefine<TFacade, TImplement, TKeysHolder> define,
			final String title, final Long fixLongIdentifier,
			final Byte fixInitializeState, final Throwable initializeException) {
		this.define = define;
		this.title = title;
		this.inCluster = define.kind.inCluster;
		this.ownSpace = ownSpace;
		this.indexs = this.initializeIndexs(define);
		this.holderCount = 0;
		this.version = Byte.MIN_VALUE;
		this.state = STATE_CREATED;
		this.longIdentifier = define.ownCache.clusterGroupContainer.onCreatedGroup(this, fixLongIdentifier);
		if (define.isAccessControlDefine()) {
			final GUID ACGUIDIdentifier = CacheGroupSpace.generateGroupACIdentifier(define.resourceService.getClass().getName(), define.facadeClass, ownSpace.identifier);
			final AccessControlCacheHolderOfGroup accessControlCacheHolder = new AccessControlCacheHolderOfGroup(this);
			Index<TFacade, TImplement, TKeysHolder> accessControlIndex = null;
			for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
				if (index.keyDefine == define.accessControlDefine) {
					accessControlIndex = index;
					break;
				}
			}
			final long ACLongIdentifier = define.ownCache.ACIdentifierGenerator.next(define.kind.inCluster);
			this.accessControlInformation = new AccessControlInformation<TFacade, TImplement, TKeysHolder>(ACGUIDIdentifier, ACLongIdentifier, accessControlIndex, accessControlCacheHolder);
			define.ownCache.ACGroupContainer.onCreatedGroup(accessControlCacheHolder, ACGUIDIdentifier);
		} else {
			this.accessControlInformation = null;
		}
		if (fixInitializeState == null) {
			this.initalizeState = INITIALIZE_STATE_NONE;
			this.initalizeException = null;
		} else {
			this.initalizeState = fixInitializeState;
			this.initalizeException = initializeException;
		}
		final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		this.readLock = lock.readLock();
		this.writeLock = lock.writeLock();
	}

	@Override
	public final String toString() {
		return String.format("CacheGroup[longId=%x, title=%s, cluster=%b, group=%s]", this.longIdentifier, this.title, this.inCluster, this.ownSpace.toString());
	}

	@Override
	final boolean needSynchronizeInCluster() {
		return this.inCluster;
	}

	@Override
	final void onTransactionCommit(final Transaction transaction) {
		if (Cache.INITIALIZEGROUP_INSAMETRANS) {
			this.onTransactionCommitII(transaction);
			return;
		}
		switch (this.state) {
		case STATE_REMOVED:
			this.ownSpace.asCustomGroupSpace().disposeGroup(this, transaction);
		case STATE_DISPOSED:
			return;
		case STATE_CREATED:
			this.resolve(transaction);
		case STATE_RESOLVED:
			break;
		default:
			throw new UnsupportedOperationException();
		}
		if (this.bindTree != null) {
			this.bindTree.onTransactionCommit(transaction);
		}
		initalizeStateUpdater.compareAndSet(this, INITIALIZE_STATE_FINISH, INITIALIZE_STATE_COMMITED);
	}

	@Override
	final void onTransactionRollback(final Transaction transaction) {
		if (Cache.INITIALIZEGROUP_INSAMETRANS) {
			this.onTransactionRollbackII(transaction);
			return;
		}
		initalizeStateUpdater.compareAndSet(this, INITIALIZE_STATE_FINISH, this.initalizeException == null ? INITIALIZE_STATE_NONE : INITIALIZE_STATE_ERROR);
		// if (initalizeStateUpdater.compareAndSet(this, INITIALIZE_STATE_FNISH,
		// INITIALIZE_STATE_ERROR) && this.initalizeException == null) {
		// initalizeExceptionUpdater.compareAndSet(this, null,
		// new GroupInitialiazeException(this.define.facadeClass,
		// this.ownSpace.identifier, "缓存组初始过程发生未知错误。"));
		// }
		switch (this.state) {
		case CacheGroup.STATE_CREATED:
			this.ownSpace.asCustomGroupSpace().disposeGroup(this, transaction);
		case CacheGroup.STATE_DISPOSED:
			return;
		case CacheGroup.STATE_REMOVED:
			this.resolve(transaction);
		case CacheGroup.STATE_RESOLVED:
			break;
		default:
			throw new UnsupportedOperationException();
		}
		if (this.state == STATE_DISPOSED) {
			return;
		} else {
			if (this.bindTree != null) {
				this.bindTree.onTransactionRollback(transaction);
			}
		}
	}

	final Index<TFacade, TImplement, TKeysHolder> findIndex(
			final Class<?> key1Class, final Class<?> key2Class,
			final Class<?> key3Class) {
		for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
			if (index.keyDefine.equalKeyClass(key1Class, key2Class, key3Class)) {
				return index;
			}
		}
		return null;
	}

	final Index<TFacade, TImplement, TKeysHolder> getIndex(
			final Class<?> key1Class, final Class<?> key2Class,
			final Class<?> key3Class) {
		final Index<TFacade, TImplement, TKeysHolder> index = this.findIndex(key1Class, key2Class, key3Class);
		if (index == null) {
			throw new UnsupportedOperationException("缓存服务[" + this.define.resourceService.getClass() + "]中未定义键组类型为[" + key1Class + ", " + key2Class + ", " + key3Class + "]的资源提供器。");
		} else {
			return index;
		}
	}

	CacheHolder<TFacade, TImplement, TKeysHolder> newHolder(
			final TImplement value, final TKeysHolder keysHolder,
			final Long fixLongIdentifier) {
		if (this.define.isAccessControlDefine()) {
			return new AccessControlCacheHolder<TFacade, TImplement, TKeysHolder>(this, value, keysHolder, fixLongIdentifier);
		} else {
			return new CacheHolder<TFacade, TImplement, TKeysHolder>(this, value, keysHolder, fixLongIdentifier);
		}
	}

	final CacheTree<TFacade, TImplement, TKeysHolder> getBindTree() {
		this.readLock.lock();
		try {
			if (this.state == STATE_DISPOSED) {
				throw this.disposedException();
			}
			if (this.bindTree == null) {
				synchronized (this) {
					if (this.bindTree == null) {
						this.bindTree = new CacheTree<TFacade, TImplement, TKeysHolder>(this);
					}
				}
			}
			return this.bindTree;
		} finally {
			this.readLock.unlock();
		}
	}

	final CacheTree<TFacade, TImplement, TKeysHolder> tryGetBindTree() {
		this.readLock.lock();
		try {
			if (this.isDisposed()) {
				return null;
			} else {
				if (this.bindTree == null) {
					synchronized (this) {
						if (this.bindTree == null) {
							this.bindTree = new CacheTree<TFacade, TImplement, TKeysHolder>(this);
						}
					}
				}
			}
			return this.bindTree;
		} finally {
			this.readLock.unlock();
		}
	}

	final CacheTree<TFacade, TImplement, TKeysHolder> forceGetBindTree() {
		return this.bindTree;
	}

	final byte getState() {
		return this.state;
	}

	final boolean isNew() {
		return this.state == STATE_CREATED;
	}

	private final boolean isResetting() {
		return this.state == STATE_RESETTING;
	}

	final boolean isDisposed() {
		return this.state == STATE_DISPOSED;
	}

	final void forceSetResolved() {
		this.state = STATE_RESOLVED;
	}

	final void forceSetInitializing() {
		this.initalizeState = INITIALIZE_STATE_STARTING;
	}

	final void forceSetInitialized() {
		this.initalizeState = INITIALIZE_STATE_FINISH;
	}

	final void forceSetInitializeState(final byte initializeState,
			final boolean catchedInitializeException) {
		this.initalizeState = initializeState;
		if (catchedInitializeException) {
			this.initalizeException = new GroupInitialiazeException(this.define.facadeClass, this.ownSpace.identifier);
		}
	}

	/**
	 * 只有会话级缓存组使用该方法，需保证在缓存组重置未完成之前，会话无新的事务产生
	 */
	final void reset(final Transaction transaction) {
		CacheTree<TFacade, TImplement, TKeysHolder> oldBindTree = null;
		this.writeLock.lock();
		try {
			try {
				this.state = STATE_RESETTING;
			} finally {
				this.updateVersion();
			}
		} finally {
			this.writeLock.unlock();
		}
		if (this.initalizeState != INITIALIZE_STATE_NONE) {
			this.indexs[0].removeHolders(transaction);
		}
		this.writeLock.lock();
		try {
			try {
				if (this.bindTree != null) {
					oldBindTree = this.bindTree;
					this.bindTree = null;
				}
				for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
					index.reset(DEFAULT_ITEM_MAP_CAPACITY);
				}
				this.initalizeState = INITIALIZE_STATE_NONE;
				this.initalizeException = null;
				this.holderCount = 0;
				this.capacity = DEFAULT_ITEM_MAP_CAPACITY;
				this.state = STATE_RESOLVED;
			} finally {
				this.updateVersion();
			}
		} finally {
			this.writeLock.unlock();
		}
		if (oldBindTree != null) {
			oldBindTree.dispose();
		}
	}

	final void resolve(final Transaction transaction) {
		if (!this.isModifiableOnTransaction(transaction)) {
			throw new CacheStateError();
		}
		this.writeLock.lock();
		try {
			try {
				this.state = STATE_RESOLVED;
			} finally {
				this.updateVersion();
			}
		} finally {
			this.writeLock.unlock();
		}
	}

	final boolean localRemove(final Transaction transaction) {
		transaction.handleAcquirable(this, AcquireFor.REMOVE);
		this.writeLock.lock();
		try {
			try {
				switch (this.state) {
				case STATE_CREATED:
					this.state = STATE_DISPOSED;
					return true;
				case STATE_RESOLVED:
					if (this.initalizeState != INITIALIZE_STATE_NONE) {
						this.indexs[0].removeHolders(transaction);
					}
					this.state = STATE_REMOVED;
				case STATE_RESETTING:
				case STATE_REMOVED:
				case STATE_DISPOSED:
					return false;
				default:
					throw new UnsupportedOperationException();
				}
			} finally {
				this.updateVersion();
			}
		} finally {
			this.writeLock.unlock();
		}
	}

	final void remoteRemove(final Transaction transaction) {
		this.writeLock.lock();
		try {
			try {
				switch (this.state) {
				case STATE_RESOLVED:
					this.state = STATE_REMOVED;
				case STATE_RESETTING:
				case STATE_REMOVED:
				case STATE_DISPOSED:
					return;
				case STATE_CREATED:
					throw new CacheStateError();
				default:
					throw new UnsupportedOperationException();
				}
			} finally {
				this.updateVersion();
			}
		} finally {
			this.writeLock.unlock();
		}
	}

	final void dispose(final Transaction transaction) {
		this.writeLock.lock();
		try {
			try {
				this.state = STATE_DISPOSED;
				if (this.define.kind.inSession) {
					return;
				}
				if (this.bindTree != null) {
					this.bindTree.dispose();
					this.bindTree = null;
				}
				this.define.ownCache.clusterGroupContainer.onDisposedGroup(this);
				if (this.define.isAccessControlDefine()) {
					this.define.ownCache.ACGroupContainer.onDisposedGroup(this.accessControlInformation.ACGUIDIdentifier);
				}
			} finally {
				this.updateVersion();
			}
		} finally {
			this.writeLock.unlock();
		}
		// if (this.initalizeState != INITIALIZE_STATE_NONE) {
		// this.indexs[0].removeHolders(transaction);
		// }
	}

	@SuppressWarnings({ "rawtypes" })
	final static AtomicReferenceFieldUpdater<CacheGroup, Throwable> initalizeExceptionUpdater = AtomicReferenceFieldUpdater.newUpdater(CacheGroup.class, Throwable.class, "initalizeException");
	@SuppressWarnings({ "rawtypes" })
	final static AtomicIntegerFieldUpdater<CacheGroup> initalizeStateUpdater = AtomicIntegerFieldUpdater.newUpdater(CacheGroup.class, "initalizeState");

	final void ensureInitialized(final Transaction transaction) {
		if (Cache.INITIALIZEGROUP_INSAMETRANS) {
			this.ensureInitializedII(transaction);
			return;
		}
		if ((this.initalizeState & INITIALIZE_STATE_FINISH) == 0 || (this.initalizeState == INITIALIZE_STATE_FINISH && !this.isModifiableOnTransaction(transaction))) {
			if (this.define.kind.inCluster) {
				// 集群下的组初始化
				final Transaction initializeTransaction;
				final ContextImpl<?, ?, ?> initializeContext;
				final boolean needFinishInitializeTransaction;
				if (transaction.kind != TransactionKind.CACHE_INIT) {
					final ContextImpl<?, ?, ?> context = transaction.getCurrentContext();
					initializeTransaction = transaction.site.newTransaction(TransactionKind.CACHE_INIT, transaction);
					needFinishInitializeTransaction = true;
					try {
						initializeContext = context.session.newContext(initializeTransaction);
					} catch (Throwable e) {
						initializeTransaction.dispose();
						throw Utils.tryThrowException(e);
					}
				} else {
					initializeTransaction = transaction;
					initializeContext = initializeTransaction.getCurrentContext();
					needFinishInitializeTransaction = false;
				}
				boolean commit = false;
				initializeTransaction.handleAcquirable(this, AcquireFor.MODIFY);
				try {
					if ((this.initalizeState & INITIALIZE_STATE_FINISH) == 0) {
						Throwable initialzeException = null;
						try {
							if (this.initalizeState == INITIALIZE_STATE_STARTING) {
								initialzeException = new GroupInitialiazeException(this.define.facadeClass, this.ownSpace.identifier, "缓存中出现了循环依赖，请修改程序");
							} else {
								this.initalizeState = INITIALIZE_STATE_STARTING;
								initializeContext.initializeCacheGroup(this, initializeTransaction);
								commit = true;
							}
						} catch (Throwable e) {
							initialzeException = e;
						} finally {
							if (initialzeException != null) {
								this.initalizeState = INITIALIZE_STATE_ERROR;
								initalizeExceptionUpdater.compareAndSet(this, null, initialzeException);
								initializeTransaction.getExceptionCatcher().catchException(initialzeException, this);
							} else {
								this.initalizeState = INITIALIZE_STATE_FINISH;
							}
						}
					}
				} finally {
					if (needFinishInitializeTransaction) {
						try {
							initializeTransaction.finish(commit);
						} finally {
							try {
								initializeContext.dispose();
							} finally {
								initializeTransaction.dispose();
							}
						}
					}
				}
			} else {
				// 非集群下的组初始化
				this.writeLock.lock();
				try {
					if ((this.initalizeState & INITIALIZE_STATE_FINISH) == 0) {
						Throwable initialzeException = null;
						try {
							if (this.initalizeState == INITIALIZE_STATE_STARTING) {
								initialzeException = new GroupInitialiazeException(this.define.facadeClass, this.ownSpace.identifier, "缓存中出现了循环依赖，请修改程序。");
							} else {
								this.initalizeState = INITIALIZE_STATE_STARTING;
								transaction.getCurrentContext().initializeCacheGroup(this, transaction);
							}
						} catch (Throwable e) {
							initialzeException = e;
						} finally {
							if (initialzeException != null) {
								initalizeExceptionUpdater.compareAndSet(this, null, initialzeException);
								this.initalizeState = INITIALIZE_STATE_ERROR;
								transaction.getExceptionCatcher().catchException(initialzeException, this);
							} else {
								this.initalizeState = INITIALIZE_STATE_COMMITED;
							}
						}
					}
				} finally {
					this.writeLock.unlock();
				}
			}
		}
		if (this.initalizeException != null) {
			throw new GroupInitialiazeException(this.define.facadeClass, this.ownSpace.identifier, this.initalizeException);
		}
	}

	/**
	 * @return 返回null表示缓存组已经被销毁
	 */
	final List<? extends TFacade> tryGetValueList(
			final Transaction transaction,
			final Filter<? super TFacade> filter,
			final Comparator<? super TImplement> comparator) {
		this.ensureInitialized(transaction);
		// 检查当前事务是否修改了组中资源项
		boolean useCache = !transaction.handledCacheItemInCacheGroup(this);
		List<TImplement> valueList;
		this.readLock.lock();
		try {
			if (this.isDisposed()) {
				return null;
			}
			valueList = this.cachedValueList;
			if (!useCache || valueList == null) {
				if (this.holderCount == 0) {
					return new DnaArrayList<TImplement>();
				}
				valueList = this.indexs[0].getValueList(transaction);
				if (useCache) {
					this.processValueListUseDefaultConfig(valueList);
					this.cachedValueList = valueList;
				}
			}
		} finally {
			this.readLock.unlock();
		}
		this.processValueList(valueList = new DnaArrayList<TImplement>(valueList), filter, comparator, useCache);
		return valueList;
	}

	/**
	 * @return 返回null表示缓存组已经被销毁
	 */
	final List<? extends TFacade> tryGetValueList(
			final AccessController accessController,
			final Operation<? super TFacade> operation,
			final Transaction transaction,
			final Filter<? super TFacade> filter,
			final Comparator<? super TImplement> comparator) {
		if (!this.define.isAccessControlDefine()) {
			throw new UnsupportedAccessControlException(this.define.facadeClass);
		}
		final List<TImplement> valueList;
		this.ensureInitialized(transaction);
		this.readLock.lock();
		try {
			if (this.isDisposed()) {
				return null;
			} else {
				if (this.holderCount == 0) {
					return new ArrayList<TImplement>(0);
				} else {
					// final ContextImpl<?, ?, ?> context = transaction
					// .getCurrentContext();
					// this.define.resourceService
					// .callBeforeAccessAuthorityResource(context);
					// try {
					valueList = this.indexs[0].getValueList(accessController, OperationEntry.operationEntryOf(operation, this.define.accessControlDefine.operationEntrys), transaction);
					// } finally {
					// this.define.resourceService
					// .callEndAccessAuthorityResource(context);
					// }
				}
			}
		} finally {
			this.readLock.unlock();
		}
		this.processValueList(valueList, filter, comparator, false);
		return valueList;
	}

	final void resolveHolder(
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
			final Transaction transaction) {
		if (holder.ownGroup != this) {
			throw new IllegalArgumentException();
		}
		this.writeLock.lock();
		try {
			try {
				holder.resolve(transaction);
				this.cachedValueList = null;
			} finally {
				this.updateVersion();
			}
		} finally {
			this.writeLock.unlock();
		}
	}

	final void disposeHolder(
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
			final Transaction transaction) {
		if (holder.ownGroup != this) {
			throw new IllegalArgumentException();
		}
		final TKeysHolder keysHolder = holder.forceGetKeysHolder();
		this.writeLock.lock();
		try {
			try {
				if (!this.isDisposed() && !this.isResetting()) {
					for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
						index.removeEntry(keysHolder, holder);
					}
				}
				holder.dispose(transaction);
				this.holderCount--;
				this.cachedValueList = null;
			} finally {
				this.updateVersion();
			}
		} finally {
			this.writeLock.unlock();
		}
		holder.doDispose(transaction.getExceptionCatcher());
	}

	final void collectGroupData(final CacheInitializeCollector collector,
			Transaction transaction) {
		collector.addCreateGroupData(this);
		final List<CacheHolder<TFacade, TImplement, TKeysHolder>> holderList = this.tryGetHolderList(transaction);
		for (CacheHolder<TFacade, TImplement, TKeysHolder> holder : holderList) {
			holder.collectHolderData(collector);
		}
		if (this.bindTree != null) {
			this.bindTree.collectTreeData(collector);
		}
	}

	final void collectModifiedGroupData(
			final CacheSynchronizeCollector collector) {
		if (Cache.INITIALIZEGROUP_INSAMETRANS) {
			this.collectModifiedGroupDataII(collector);
			return;
		}
		switch (this.state) {
		case STATE_REMOVED:
			collector.addRemoveGroupData(this);
		case STATE_DISPOSED:
			return;
		case STATE_CREATED:
			collector.addCreateGroupData(this);
		case STATE_RESOLVED:
			break;
		default:
			throw new UnsupportedOperationException();
		}
		if (this.bindTree != null) {
			this.bindTree.collectModifiedTreeData(collector);
		}
	}

	final void collectModifiedGroupDataII(
			final CacheSynchronizeCollector collector) {
		switch (this.state) {
		case STATE_REMOVED:
			collector.addRemoveGroupData(this);
		case STATE_DISPOSED:
			return;
		case STATE_CREATED:
			collector.addCreateGroupData(this);
		case STATE_RESOLVED:
			if (this.initalizeState != INITIALIZE_STATE_COMMITED) {
				collector.addModifyGroupData(this);
			}
			break;
		default:
			throw new UnsupportedOperationException();
		}
		if (this.bindTree != null) {
			this.bindTree.collectModifiedTreeData(collector);
		}
	}

	final byte forceGetInitialzieState() {
		return (byte) this.initalizeState;
	}

	final Throwable forceGetInitializeException() {
		return this.initalizeException;
	}

	final CacheHolder<TFacade, TImplement, TKeysHolder> localCreateHolderAndCommit(
			final TImplement value, final TKeysHolder keysHolder) {
		CacheHolder<TFacade, TImplement, TKeysHolder> existHolder = null;
		for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
			final CacheHolder<TFacade, TImplement, TKeysHolder> sameHolder = index.findSameHolder(keysHolder);
			if (sameHolder != null) {
				if (existHolder == null) {
					existHolder = sameHolder;
				} else if (existHolder != sameHolder) {
					throw new ConflictingKeyValueException(this.define.facadeClass, index.keyDefine, keysHolder);
				}
			}
		}
		if (existHolder != null) {
			final TKeysHolder existKeysHolder = existHolder.forceGetKeysHolder();
			for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
				index.removeEntry(existKeysHolder);
			}
		}
		final CacheHolder<TFacade, TImplement, TKeysHolder> newHolder;
		if (existHolder == null) {
			newHolder = this.newHolder(value, keysHolder, null);
			newHolder.forceSetResolved();
			if ((this.holderCount++) == this.capacity) {
				for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
					index.reHash(this.capacity *= 2);
				}
			}
		} else {
			existHolder.forceSetValueAndKeysHolder(value, keysHolder);
			newHolder = existHolder;
		}
		for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
			index.createEntry(keysHolder, newHolder);
		}
		return newHolder;
	}

	final CacheHolder<TFacade, TImplement, TKeysHolder> localCreateHolderWhenInitialize(
			final TImplement value, final TKeysHolder keysHolder,
			final Transaction transaction) {
		CacheHolder<TFacade, TImplement, TKeysHolder> existHolder = null;
		for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
			final CacheHolder<TFacade, TImplement, TKeysHolder> sameHolder = index.findSameHolder(keysHolder);
			if (sameHolder != null) {
				if (existHolder == null) {
					existHolder = sameHolder;
				} else if (existHolder != sameHolder) {
					throw new ConflictingKeyValueException(this.define.facadeClass, index.keyDefine, keysHolder);
				}
			}
		}
		if (existHolder != null) {
			final TKeysHolder existKeysHolder = existHolder.tryGetKeysHolder(transaction);
			for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
				index.removeEntry(existKeysHolder);
			}
		}
		final CacheHolder<TFacade, TImplement, TKeysHolder> newHolder;
		if (existHolder == null) {
			newHolder = this.newHolder(value, keysHolder, null);
			transaction.handleAcquirable(newHolder, AcquireFor.ADD);
			if ((this.holderCount++) == this.capacity) {
				for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
					index.reHash(this.capacity *= 2);
				}
			}
		} else {
			existHolder.forceSetValueAndKeysHolder(value, keysHolder);
			newHolder = existHolder;
		}
		for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
			index.createEntry(keysHolder, newHolder);
		}
		return newHolder;
	}

	/**
	 * @return 返回null表示缓存组已经被销毁
	 */
	final CacheHolder<TFacade, TImplement, TKeysHolder> localTryCreateHolder(
			final TImplement value, final TKeysHolder keysHolder,
			final PutPolicy policy, final Transaction transaction) {
		CacheHolder<TFacade, TImplement, TKeysHolder> existHolder = null;
		Index<TFacade, TImplement, TKeysHolder> existIndex = null;
		Object keyValue1 = null;
		Object keyValue2 = null;
		Object keyValue3 = null;
		final byte currentVersion;
		this.ensureInitialized(transaction);
		this.readLock.lock();
		try {
			if (this.isDisposed()) {
				return null;
			} else {
				for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
					final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine = index.keyDefine;
					final Object tempKeyValue1 = keyDefine.getKeyValue1(keysHolder);
					final Object tempKeyValue2 = keyDefine.getKeyValue2(keysHolder);
					final Object tempKeyValue3 = keyDefine.getKeyValue3(keysHolder);
					final CacheHolder<TFacade, TImplement, TKeysHolder> holder = index.findSameHolder(tempKeyValue1, tempKeyValue2, tempKeyValue3, transaction);
					if (holder != null) {
						if (!holder.isModifiableOnTransaction(transaction) || holder.getHolderState() != CacheHolder.STATE_REMOVED) {
							switch (policy) {
							case IGNORE:
								return holder;
							case REPLACE:
								if (existHolder == null) {
									existHolder = holder;
									existIndex = index;
									keyValue1 = tempKeyValue1;
									keyValue2 = tempKeyValue2;
									keyValue3 = tempKeyValue3;
									break;
								} else if (existHolder == holder) {
									break;
								}
							case THROW_EXCEPTION:
								throw new ConflictingKeyValueException(this.define.facadeClass, index.keyDefine, keysHolder);
							default:
								throw new UnsupportedOperationException();
							}
						}
					}
				}
				currentVersion = this.version;
			}
		} finally {
			this.readLock.unlock();
		}
		if (existHolder == null) {
			this.writeLock.lock();
			try {
				try {
					if (currentVersion != this.version) {
						if (this.isDisposed()) {
							return null;
						} else {
							for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
								final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine = index.keyDefine;
								final Object tempKeyValue1 = keyDefine.getKeyValue1(keysHolder);
								final Object tempKeyValue2 = keyDefine.getKeyValue2(keysHolder);
								final Object tempKeyValue3 = keyDefine.getKeyValue3(keysHolder);
								final CacheHolder<TFacade, TImplement, TKeysHolder> holder = index.findSameHolder(tempKeyValue1, tempKeyValue2, tempKeyValue3, transaction);
								if (holder != null) {
									if (!holder.isModifiableOnTransaction(transaction) || holder.getHolderState() != CacheHolder.STATE_REMOVED) {
										switch (policy) {
										case IGNORE:
											return holder;
										case REPLACE:
											if (existHolder == null) {
												existHolder = holder;
												existIndex = index;
												keyValue1 = tempKeyValue1;
												keyValue2 = tempKeyValue2;
												keyValue3 = tempKeyValue3;
												break;
											} else if (existHolder == holder) {
												break;
											}
										case THROW_EXCEPTION:
											throw new ConflictingKeyValueException(this.define.facadeClass, index.keyDefine, keysHolder);
										default:
											throw new UnsupportedOperationException();
										}
									}
								}
							}
						}
					}
					if (existHolder == null) {
						final CacheHolder<TFacade, TImplement, TKeysHolder> newHolder = this.newHolder(value, keysHolder, null);
						transaction.handleAcquirable(newHolder, AcquireFor.ADD);
						for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
							index.createEntry(keysHolder, newHolder);
						}
						if ((this.holderCount++) == this.capacity) {
							for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
								index.reHash(this.capacity *= 2);
							}
						}
						return newHolder;
					}
				} finally {
					this.updateVersion();
				}
			} finally {
				this.writeLock.unlock();
			}
		}
		// 需要覆盖的情况
		transaction.handleAcquirable(existHolder, AcquireFor.MODIFY);
		if (existHolder.trySetModifyingFromRemoved(transaction, value, keysHolder)) {
			return existHolder;
		} else {
			if (existHolder.isDisposed() || !existIndex.keyDefine.equalKey(existHolder.tryGetKeysHolder(transaction), keyValue1, keyValue2, keyValue3)) {
				transaction.releaseAcquirable(existHolder);
				return this.localTryCreateHolder(value, keysHolder, policy, transaction);
			} else {
				existHolder.tryGetModifiableValue();
				existHolder.tryPostModifiedValueWithoutCheck(value, keysHolder, transaction);
				return existHolder;
			}
		}
	}

	final TImplement localTryModifyHolder(final Class<?> keyValueClass1,
			final Class<?> keyValueClass2, final Class<?> keyValueClass3,
			final Object keyValue1, final Object keyValue2,
			final Object keyValue3, final Transaction transaction) {
		final Index<TFacade, TImplement, TKeysHolder> index = this.getIndex(keyValueClass1, keyValueClass2, keyValueClass3);
		while (true) {
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder = index.findHolder(keyValue1, keyValue2, keyValue3, transaction);
			if (holder == null) {
				break;
			} else {
				transaction.handleAcquirable(holder, AcquireFor.MODIFY);
				if (holder.isDisposed() || !index.keyDefine.equalKey(holder.tryGetKeysHolder(transaction), keyValue1, keyValue2, keyValue3)) {
					transaction.releaseAcquirable(holder);
					continue;
				} else {
					return holder.tryGetModifiableValue();
				}
			}
		}
		return null;
	}

	final CacheHolder<TFacade, TImplement, TKeysHolder> localTryFindHolder(
			final Class<?> keyValueClass1, final Class<?> keyValueClass2,
			final Class<?> keyValueClass3, final Object keyValue1,
			final Object keyValue2, final Object keyValue3,
			final Transaction transaction) {
		final Index<TFacade, TImplement, TKeysHolder> index = this.getIndex(keyValueClass1, keyValueClass2, keyValueClass3);
		while (true) {
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder = index.findHolder(keyValue1, keyValue2, keyValue3, transaction);
			if (holder == null) {
				break;
			} else {
				transaction.handleAcquirable(holder, AcquireFor.MODIFY);
				if (holder.isDisposed() || !index.keyDefine.equalKey(holder.tryGetKeysHolder(transaction), keyValue1, keyValue2, keyValue3)) {
					transaction.releaseAcquirable(holder);
					continue;
				} else {
					return holder;
				}
			}
		}
		return null;
	}

	final TImplement localTryModifyHolder(
			final AccessController accessController,
			final Operation<? super TFacade> operation,
			final Class<?> keyValueClass1, final Class<?> keyValueClass2,
			final Class<?> keyValueClass3, final Object keyValue1,
			final Object keyValue2, final Object keyValue3,
			final Transaction transaction) {
		final Index<TFacade, TImplement, TKeysHolder> index = this.getIndex(keyValueClass1, keyValueClass2, keyValueClass3);
		while (true) {
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder = index.findHolder(accessController, operation, keyValue1, keyValue2, keyValue3, transaction);
			if (holder == null) {
				break;
			} else {
				transaction.handleAcquirable(holder, AcquireFor.MODIFY);
				if (holder.isDisposed() || !index.keyDefine.equalKey(holder.tryGetKeysHolder(transaction), keyValue1, keyValue2, keyValue3)) {
					transaction.releaseAcquirable(holder);
					continue;
				} else {
					return holder.tryGetModifiableValue();
				}
			}
		}
		return null;
	}

	final TImplement localTryRemoveHolder(final Class<?> keyValueClass1,
			final Class<?> keyValueClass2, final Class<?> keyValueClass3,
			final Object keyValue1, final Object keyValue2,
			final Object keyValue3, final Transaction transaction) {
		final Index<TFacade, TImplement, TKeysHolder> index = this.getIndex(keyValueClass1, keyValueClass2, keyValueClass3);
		while (true) {
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder = index.findHolder(keyValue1, keyValue2, keyValue3, transaction);
			if (holder == null) {
				break;
			} else {
				transaction.handleAcquirable(holder, AcquireFor.REMOVE);
				if (holder.isDisposed()) {
					transaction.releaseAcquirable(holder);
					return null;
				} else {
					if (index.keyDefine.equalKey(holder.tryGetKeysHolder(transaction), keyValue1, keyValue2, keyValue3)) {
						final TImplement value = holder.tryGetValue(transaction);
						if (holder.localRemove(transaction)) {
							this.disposeHolder(holder, transaction);
						}
						return value;
					} else {
						transaction.releaseAcquirable(holder);
					}
				}
			}
		}
		return null;
	}

	final TImplement localTryRemoveHolder(
			final AccessController accessController,
			final Operation<? super TFacade> operation,
			final Class<?> keyValueClass1, final Class<?> keyValueClass2,
			final Class<?> keyValueClass3, final Object keyValue1,
			final Object keyValue2, final Object keyValue3,
			final Transaction transaction) {
		final Index<TFacade, TImplement, TKeysHolder> index = this.getIndex(keyValueClass1, keyValueClass2, keyValueClass3);
		while (true) {
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder = index.findHolder(accessController, operation, keyValue1, keyValue2, keyValue3, transaction);
			if (holder == null) {
				break;
			} else {
				transaction.handleAcquirable(holder, AcquireFor.REMOVE);
				if (holder.isDisposed()) {
					transaction.releaseAcquirable(holder);
					return null;
				} else {
					if (index.keyDefine.equalKey(holder.tryGetKeysHolder(transaction), keyValue1, keyValue2, keyValue3)) {
						final TImplement value = holder.tryGetValue(transaction);
						if (holder.localRemove(transaction)) {
							this.disposeHolder(holder, transaction);
						}
						return value;
					} else {
						transaction.releaseAcquirable(holder);
					}
				}
			}
		}
		return null;
	}

	final void localTryInvalidHolder(final Class<?> keyValueClass1,
			final Class<?> keyValueClass2, final Class<?> keyValueClass3,
			final Object keyValue1, final Object keyValue2,
			final Object keyValue3, final Transaction transaction) {
		final Index<TFacade, TImplement, TKeysHolder> index = this.getIndex(keyValueClass1, keyValueClass2, keyValueClass3);
		this.ensureInitialized(transaction);
		for (;;) {
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder;
			this.readLock.lock();
			try {
				if (this.isDisposed()) {
					return;
				}
				holder = index.internalFindHolder(keyValue1, keyValue2, keyValue3, transaction);
				if (holder == null) {
					return;
				}
			} finally {
				this.readLock.unlock();
			}
			transaction.handleAcquirable(holder, AcquireFor.MODIFY);
			if (!holder.isDisposed() && index.keyDefine.equalKey(holder.tryGetKeysHolder(transaction), keyValue1, keyValue2, keyValue3)) {
				holder.forceSetModifyingWithNull();
				try {
					transaction.getCurrentContext().loadCacheHolder(index.keyDefine, this, keyValue1, keyValue2, keyValue3);
				} catch (Throwable exception) {
					transaction.getExceptionCatcher().catchException(exception, this);
				}
				if (holder.tryGetModifyingValue(transaction) == null && holder.localRemove(transaction)) {
					this.disposeHolder(holder, transaction);
				}
				return;
			}
			transaction.releaseAcquirable(holder);
		}
	}

	final void localTryInvalidHolder(final AccessController accessController,
			final Operation<? super TFacade> operation,
			final Class<?> keyValueClass1, final Class<?> keyValueClass2,
			final Class<?> keyValueClass3, final Object keyValue1,
			final Object keyValue2, final Object keyValue3,
			final Transaction transaction) {
		final Index<TFacade, TImplement, TKeysHolder> index = this.getIndex(keyValueClass1, keyValueClass2, keyValueClass3);
		this.ensureInitialized(transaction);
		for (;;) {
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder;
			this.readLock.lock();
			try {
				if (this.isDisposed()) {
					return;
				}
				holder = index.internalFindHolder(accessController, operation, keyValue1, keyValue2, keyValue3, transaction);
				if (holder == null) {
					return;
				}
			} finally {
				this.readLock.unlock();
			}
			transaction.handleAcquirable(holder, AcquireFor.MODIFY);
			if (!holder.isDisposed() && index.keyDefine.equalKey(holder.tryGetKeysHolder(transaction), keyValue1, keyValue2, keyValue3)) {
				holder.forceSetModifyingWithNull();
				try {
					transaction.getCurrentContext().loadCacheHolder(index.keyDefine, this, keyValue1, keyValue2, keyValue3);
				} catch (Throwable exception) {
					transaction.getExceptionCatcher().catchException(exception, this);
				}
				if (holder.tryGetModifyingValue(transaction) == null && holder.localRemove(transaction)) {
					this.disposeHolder(holder, transaction);
				}
				return;
			}
			transaction.releaseAcquirable(holder);
		}
	}

	final void localTryReloadHolder(final Class<?> keyValueClass1,
			final Class<?> keyValueClass2, final Class<?> keyValueClass3,
			final Object keyValue1, final Object keyValue2,
			final Object keyValue3, final Transaction transaction) {
		final Index<TFacade, TImplement, TKeysHolder> index = this.getIndex(keyValueClass1, keyValueClass2, keyValueClass3);
		this.ensureInitialized(transaction);
		for (;;) {
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder;
			this.readLock.lock();
			try {
				if (this.isDisposed()) {
					return;
				}
				holder = index.internalFindHolder(keyValue1, keyValue2, keyValue3, transaction);
			} finally {
				this.readLock.unlock();
			}
			if (holder == null) {
				try {
					transaction.getCurrentContext().loadCacheHolder(index.keyDefine, this, keyValue1, keyValue2, keyValue3);
				} catch (Throwable exception) {
					transaction.getExceptionCatcher().catchException(exception, this);
				}
				return;
			} else {
				transaction.handleAcquirable(holder, AcquireFor.MODIFY);
				if (!holder.isDisposed() && index.keyDefine.equalKey(holder.tryGetKeysHolder(transaction), keyValue1, keyValue2, keyValue3)) {
					holder.forceSetModifyingWithNull();
					try {
						transaction.getCurrentContext().loadCacheHolder(index.keyDefine, this, keyValue1, keyValue2, keyValue3);
					} catch (Throwable exception) {
						transaction.getExceptionCatcher().catchException(exception, this);
					}
					if (holder.tryGetModifyingValue(transaction) == null && holder.localRemove(transaction)) {
						this.disposeHolder(holder, transaction);
					}
					return;
				}
				transaction.releaseAcquirable(holder);
			}
		}
	}

	final void localTryReloadHolder(final AccessController accessController,
			final Operation<? super TFacade> operation,
			final Class<?> keyValueClass1, final Class<?> keyValueClass2,
			final Class<?> keyValueClass3, final Object keyValue1,
			final Object keyValue2, final Object keyValue3,
			final Transaction transaction) {
		final Index<TFacade, TImplement, TKeysHolder> index = this.getIndex(keyValueClass1, keyValueClass2, keyValueClass3);
		this.ensureInitialized(transaction);
		for (;;) {
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder;
			this.readLock.lock();
			try {
				if (this.isDisposed()) {
					return;
				}
				holder = index.internalFindHolder(accessController, operation, keyValue1, keyValue2, keyValue3, transaction);
			} finally {
				this.readLock.unlock();
			}
			if (holder == null) {
				try {
					transaction.getCurrentContext().loadCacheHolder(index.keyDefine, this, keyValue1, keyValue2, keyValue3);
				} catch (Throwable exception) {
					transaction.getExceptionCatcher().catchException(exception, this);
				}
				return;
			} else {
				transaction.handleAcquirable(holder, AcquireFor.MODIFY);
				if (!holder.isDisposed() && index.keyDefine.equalKey(holder.tryGetKeysHolder(transaction), keyValue1, keyValue2, keyValue3)) {
					holder.forceSetModifyingWithNull();
					if (holder.tryGetModifyingValue(transaction) == null && holder.localRemove(transaction)) {
						this.disposeHolder(holder, transaction);
					}
					return;
				}
				transaction.releaseAcquirable(holder);
			}
		}
	}

	final boolean onPostModifiedHolder(
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
			final TKeysHolder oldKeysHolder, final TKeysHolder newKeysHolder) {
		if (holder.ownGroup != this) {
			throw new IllegalArgumentException();
		}
		if (oldKeysHolder != newKeysHolder) {
			final ArrayList<Index<TFacade, TImplement, TKeysHolder>> needModifyIndexList = new ArrayList<Index<TFacade, TImplement, TKeysHolder>>();
			for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
				if (index.keyDefine.compareKeyValues(oldKeysHolder, newKeysHolder)) {
					continue;
				} else {
					needModifyIndexList.add(index);
				}
			}
			if (needModifyIndexList.size() > 0) {
				this.writeLock.lock();
				try {
					try {
						switch (holder.getHolderState()) {
						case CacheHolder.STATE_RESOLVED:
							for (Index<TFacade, TImplement, TKeysHolder> index : needModifyIndexList) {
								index.moveEntry(oldKeysHolder, newKeysHolder, holder);
							}
							return true;
						case CacheHolder.STATE_CREATED:
							for (Index<TFacade, TImplement, TKeysHolder> index : needModifyIndexList) {
								index.removeEntry(oldKeysHolder, holder);
								index.createEntry(newKeysHolder, holder);
							}
						case CacheHolder.STATE_REMOVED:
						case CacheHolder.STATE_DISPOSED:
							break;
						default:
							throw new UnsupportedOperationException();
						}
					} finally {
						this.updateVersion();
					}
				} finally {
					this.writeLock.unlock();
				}
			}
		}
		return false;
	}

	final void finishModifyHolder(
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
			final Transaction transaction) {
		// if (holder.ownGroup != this) {
		// throw new IllegalArgumentException();
		// }
		if (holder.haveModifedKeyValues(transaction)) {
			final TKeysHolder keysHolder = holder.tryGetKeysHolder(transaction);
			this.writeLock.lock();
			try {
				try {
					for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
						index.finishMoveEntry(keysHolder, holder);
					}
				} finally {
					this.updateVersion();
				}
			} finally {
				this.writeLock.unlock();
			}
		}
	}

	final void cancelModifyHolder(
			final CacheHolder<TFacade, TImplement, TKeysHolder> holder,
			final Transaction transaction) {
		// if (holder.ownGroup != this) {
		// throw new IllegalArgumentException();
		// }
		if (holder.haveModifedKeyValues(transaction)) {
			final TKeysHolder keysHolder = holder.tryGetKeysHolder(transaction);
			this.writeLock.lock();
			try {
				try {
					for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
						index.cancelMoveEntry(keysHolder, holder);
					}
				} finally {
					this.updateVersion();
				}
			} finally {
				this.writeLock.unlock();
			}
		}
	}

	/**
	 * @return 返回null表示缓存组已经被销毁
	 */
	final CacheHolder<TFacade, TImplement, TKeysHolder> remoteCreateHolder(
			final TImplement value, final TKeysHolder keysHolder,
			final Long fixLongIdentifier, final Transaction transaction) {
		final byte currentVersion;
		this.readLock.lock();
		try {
			if (this.isDisposed()) {
				throw this.disposedException();
			} else {
				for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
					final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine = index.keyDefine;
					final Object keyValue1 = keyDefine.getKeyValue1(keysHolder);
					final Object keyValue2 = keyDefine.getKeyValue2(keysHolder);
					final Object keyValue3 = keyDefine.getKeyValue3(keysHolder);
					final CacheHolder<TFacade, TImplement, TKeysHolder> holder = index.findSameHolder(keyValue1, keyValue2, keyValue3, transaction);
					if (holder != null && (!holder.isModifiableOnTransaction(transaction) || holder.getHolderState() != CacheHolder.STATE_REMOVED)) {
						throw new ConflictingKeyValueException(this.define.facadeClass, index.keyDefine, keysHolder);
					}
				}
				currentVersion = this.version;
			}
		} finally {
			this.readLock.unlock();
		}
		this.writeLock.lock();
		try {
			try {
				if (currentVersion != this.version) {
					if (this.isDisposed()) {
						throw this.disposedException();
					} else {
						for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
							final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine = index.keyDefine;
							final Object keyValue1 = keyDefine.getKeyValue1(keysHolder);
							final Object keyValue2 = keyDefine.getKeyValue2(keysHolder);
							final Object keyValue3 = keyDefine.getKeyValue3(keysHolder);
							final CacheHolder<TFacade, TImplement, TKeysHolder> holder = index.findSameHolder(keyValue1, keyValue2, keyValue3, transaction);
							if (holder != null && (!holder.isModifiableOnTransaction(transaction) || holder.getHolderState() != CacheHolder.STATE_REMOVED)) {
								throw new ConflictingKeyValueException(this.define.facadeClass, index.keyDefine, keysHolder);
							}
						}
					}
				}
				final CacheHolder<TFacade, TImplement, TKeysHolder> newHolder = this.newHolder(value, keysHolder, fixLongIdentifier);
				transaction.remoteHandleNewAcquirable(newHolder);
				for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
					index.createEntry(keysHolder, newHolder);
				}
				if ((this.holderCount++) == this.capacity) {
					for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
						index.reHash(this.capacity *= 2);
					}
				}
				return newHolder;
			} finally {
				this.updateVersion();
			}
		} finally {
			this.writeLock.unlock();
		}
	}

	final CacheHolder<TFacade, TImplement, TKeysHolder> remoteCreateHolderAndCommit(
			final TImplement value, final TKeysHolder keysHolder,
			final Long fixLongIdentifier) {
		final CacheHolder<TFacade, TImplement, TKeysHolder> newHolder = this.newHolder(value, keysHolder, fixLongIdentifier);
		newHolder.forceSetResolved();
		for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
			index.createEntry(keysHolder, newHolder);
		}
		if ((this.holderCount++) == this.capacity) {
			for (Index<TFacade, TImplement, TKeysHolder> index : this.indexs) {
				index.reHash(this.capacity *= 2);
			}
		}
		return newHolder;
	}

	final DisposedException disposedException() {
		return new DisposedException("缓存组已被销毁。外观类型为[" + this.define.facadeClass + "]，组空间标识为[" + this.ownSpace.identifier + "]");
	}

	final void ensureReferenceCacheInitialized(
			final ReferenceDefine<?> referenceDefine,
			final Transaction transaction) {
		if (referenceDefine != null) {
			final Class<?> referenceFacadeClass = referenceDefine.referenceCacheDefine.facadeClass;
			final CacheDefine<?, ?, ?> define = referenceDefine.referenceCacheDefine;
			final CustomGroupSpace customSpace = this.ownSpace.asCustomGroupSpace();
			final CacheGroup<?, ?, ?> group;
			if (customSpace == null) {
				// 默认组空间
				if (define.kind.inSession) {
					group = transaction.getCurrentContext().session.getCacheGroupContainer().getDefaultGroup(define, transaction);
				} else {
					group = this.define.ownCache.defaultGroupSpace.findGroup(referenceFacadeClass, transaction);
				}
			} else {
				if (define.kind.inSession) {
					final SessionCacheGroupContainer sessionGroupContainer = transaction.getCurrentContext().session.tryGetCacheGroupContainer();
					if (sessionGroupContainer != null) {
						group = sessionGroupContainer.findGroup(referenceFacadeClass, this.ownSpace.identifier, transaction);
					} else {
						return;
					}
				} else {
					group = customSpace.findGroup(referenceFacadeClass, transaction);
				}
			}
			if (group != null) {
				group.ensureInitialized(transaction);
			}
		}
	}

	final int getHolderCount() {
		return this.holderCount;
	}

	/**
	 * @return 返回null表示缓存组已经被销毁
	 */
	private final List<CacheHolder<TFacade, TImplement, TKeysHolder>> tryGetHolderList(
			final Transaction transaction) {
		if (this.initalizeState == INITIALIZE_STATE_NONE) {
			return new ArrayList<CacheHolder<TFacade, TImplement, TKeysHolder>>(0);
		}
		final List<CacheHolder<TFacade, TImplement, TKeysHolder>> holderList;
		this.readLock.lock();
		try {
			if (this.isDisposed()) {
				return null;
			} else {
				if (this.holderCount == 0) {
					return new ArrayList<CacheHolder<TFacade, TImplement, TKeysHolder>>(0);
				} else {
					holderList = this.indexs[0].getHolderList(transaction);
				}
			}
		} finally {
			this.readLock.unlock();
		}
		return holderList;
	}

	private final void processValueListUseDefaultConfig(
			final List<TImplement> valueList) {
		if (valueList.size() != 0) {
			final Filter<? super TImplement> defaultFilter = this.define.defaultFilter;
			final Comparator<? super TImplement> defaultComparator = this.define.defaultComparator;
			if (defaultFilter != null) {
				int acceptCount = 0;
				for (int index = 0, endIndex = valueList.size(); index < endIndex; index++) {
					final TImplement value = valueList.get(index);
					if (defaultFilter.accept(value)) {
						valueList.set(acceptCount++, value);
					}
				}
				if (acceptCount == 0) {
					valueList.clear();
				} else if (acceptCount == 1) {
					final TImplement value = valueList.get(0);
					valueList.clear();
					valueList.add(value);
				} else {
					for (int index = valueList.size() - 1, endIndex = acceptCount; index >= endIndex; index--) {
						valueList.remove(index);
					}
				}
			}
			if (defaultComparator != null && valueList.size() > 1) {
				SortUtil.sort(valueList, defaultComparator);
			}
		}
	}

	private final void processValueList(final List<TImplement> valueList,
			final Filter<? super TFacade> filter,
			Comparator<? super TImplement> comparator, final boolean useCache) {
		if (valueList.size() != 0) {
			int acceptCount = 0;
			final boolean filted;
			if (useCache) {
				if (filter != null) {
					for (int index = 0, endIndex = valueList.size(); index < endIndex; index++) {
						final TImplement value = valueList.get(index);
						if (filter.accept(value)) {
							valueList.set(acceptCount++, value);
						}
					}
					filted = true;
				} else {
					filted = false;
				}
			} else {
				final Filter<? super TImplement> defaultFilter = this.define.defaultFilter;
				if (filter != null) {
					if (defaultFilter == null) {
						for (int index = 0, endIndex = valueList.size(); index < endIndex; index++) {
							final TImplement value = valueList.get(index);
							if (filter.accept(value)) {
								valueList.set(acceptCount++, value);
							}
						}
					} else {
						for (int index = 0, endIndex = valueList.size(); index < endIndex; index++) {
							final TImplement value = valueList.get(index);
							if (filter.accept(value) && defaultFilter.accept(value)) {
								valueList.set(acceptCount++, value);
							}
						}
					}
					filted = true;
				} else if (defaultFilter != null) {
					for (int index = 0, endIndex = valueList.size(); index < endIndex; index++) {
						final TImplement value = valueList.get(index);
						if (defaultFilter.accept(value)) {
							valueList.set(acceptCount++, value);
						}
					}
					filted = true;
				} else {
					filted = false;
				}
				if (comparator == null) {
					comparator = this.define.defaultComparator;
				}
			}
			if (filted) {
				if (acceptCount == 0) {
					valueList.clear();
				} else if (acceptCount == 1) {
					final TImplement value = valueList.get(0);
					valueList.clear();
					valueList.add(value);
				} else {
					for (int index = valueList.size() - 1, endIndex = acceptCount; index >= endIndex; index--) {
						valueList.remove(index);
					}
				}
			}
			if (comparator != null && valueList.size() > 1) {
				SortUtil.sort(valueList, comparator);
			}
		}
	}

	private final void updateVersion() {
		if (this.version == Byte.MAX_VALUE) {
			this.version = Byte.MIN_VALUE;
		} else {
			this.version++;
		}
	}

	@SuppressWarnings("unchecked")
	private final Index<TFacade, TImplement, TKeysHolder>[] initializeIndexs(
			final CacheDefine<TFacade, TImplement, TKeysHolder> define) {
		final int keyDefineCount = define.keyDefines.length;
		final Index<TFacade, TImplement, TKeysHolder>[] indexs = new Index[keyDefineCount];
		for (int index = 0; index < keyDefineCount; index++) {
			indexs[index] = new Index<TFacade, TImplement, TKeysHolder>(this, define.keyDefines[index], DEFAULT_ITEM_MAP_CAPACITY);
		}
		this.capacity = DEFAULT_ITEM_MAP_CAPACITY;
		return indexs;
	}

	final long longIdentifier;

	final CacheGroupSpace ownSpace;

	final String title;

	final CacheDefine<TFacade, TImplement, TKeysHolder> define;

	final boolean inCluster;

	final AccessControlInformation<TFacade, TImplement, TKeysHolder> accessControlInformation;

	private volatile byte state;

	private volatile CacheTree<TFacade, TImplement, TKeysHolder> bindTree;

	private volatile int initalizeState;

	private volatile Throwable initalizeException;

	private final ReentrantReadWriteLock.ReadLock readLock;

	private final ReentrantReadWriteLock.WriteLock writeLock;

	private final Index<TFacade, TImplement, TKeysHolder>[] indexs;

	private volatile int holderCount;

	private volatile List<TImplement> cachedValueList;

	private volatile int capacity;

	private volatile byte version;

	volatile CacheGroup<?, ?, ?> nextInSpace;

	volatile CacheGroup<?, ?, ?> nextInContainer;

	private static final class Index<TFacade, TImplement extends TFacade, TKeysHolder>
			extends CacheHolderIndex<TFacade, TImplement, TKeysHolder> {

		private Index(final CacheGroup<TFacade, TImplement, TKeysHolder> group,
				final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine,
				final int initializeCapacity) {
			super(keyDefine);
			this.group = group;
			this.reset(initializeCapacity);
		}

		@Override
		final CacheHolder<TFacade, TImplement, TKeysHolder> findHolder(
				final Object keyValue1, final Object keyValue2,
				final Object keyValue3, final Transaction transaction) {
			this.group.ensureInitialized(transaction);
			this.group.readLock.lock();
			try {
				if (this.group.isDisposed()) {
					return null;
				} else {
					final CacheHolder<TFacade, TImplement, TKeysHolder> holder = this.internalFindHolder(keyValue1, keyValue2, keyValue3, transaction);
					if (holder != null || super.keyDefine.provider.notHaveCustomProvideMethod) {
						return holder;
					}
				}
			} finally {
				this.group.readLock.unlock();
			}
			try {
				transaction.getCurrentContext().loadCacheHolder(super.keyDefine, this.group, keyValue1, keyValue2, keyValue3);
			} catch (Throwable exception) {
				transaction.getExceptionCatcher().catchException(exception, null);
			}
			this.group.readLock.lock();
			try {
				if (this.group.isDisposed()) {
					return null;
				} else {
					return this.internalFindHolder(keyValue1, keyValue2, keyValue3, transaction);
				}
			} finally {
				this.group.readLock.unlock();
			}
		}

		@Override
		final CacheHolder<TFacade, TImplement, TKeysHolder> findHolder(
				final AccessController accessController,
				final Operation<? super TFacade> operation,
				final Object keyValue1, final Object keyValue2,
				final Object keyValue3, final Transaction transaction) {
			this.group.ensureInitialized(transaction);
			this.group.readLock.lock();
			try {
				if (this.group.isDisposed()) {
					return null;
				} else {
					final CacheHolder<TFacade, TImplement, TKeysHolder> holder = this.internalFindHolder(accessController, operation, keyValue1, keyValue2, keyValue3, transaction);
					if (holder != null || super.keyDefine.provider.notHaveCustomProvideMethod) {
						return holder;
					}
				}
			} finally {
				this.group.readLock.unlock();
			}
			try {
				transaction.getCurrentContext().loadCacheHolder(super.keyDefine, this.group, keyValue1, keyValue2, keyValue3);
			} catch (Throwable exception) {
				transaction.getExceptionCatcher().catchException(exception, null);
			}
			this.group.readLock.lock();
			try {
				if (this.group.isDisposed()) {
					return null;
				} else {
					return this.internalFindHolder(accessController, operation, keyValue1, keyValue2, keyValue3, transaction);
				}
			} finally {
				this.group.readLock.unlock();
			}
		}

		@Override
		final AccessControlCacheHolder<TFacade, TImplement, TKeysHolder> findAccessControlHolder(
				final GUID keyValue, final Transaction transaction) {
			if (super.keyDefine.asAccessControlDefine() == null) {
				return null;
			} else {
				this.group.ensureInitialized(transaction);
				this.group.readLock.lock();
				try {
					if (this.group.isDisposed()) {
						return null;
					} else {
						final CacheHolder<TFacade, TImplement, TKeysHolder> holder = this.internalFindHolder(keyValue, null, null, transaction);
						if (holder != null || super.keyDefine.provider.notHaveCustomProvideMethod) {
							return holder == null ? null : holder.asAccessControlHolder();
						}
					}
				} finally {
					this.group.readLock.unlock();
				}
				this.group.writeLock.lock();
				try {
					if (this.group.isDisposed()) {
						return null;
					} else {
						CacheHolder<TFacade, TImplement, TKeysHolder> holder = this.internalFindHolder(keyValue, null, null, transaction);
						if (holder == null) {
							try {
								transaction.getCurrentContext().loadCacheHolder(super.keyDefine, this.group, keyValue, null, null);
							} catch (Throwable exception) {
								transaction.getExceptionCatcher().catchException(exception, null);
							}
							holder = this.internalFindHolder(keyValue, null, null, transaction);
							return holder == null ? null : holder.asAccessControlHolder();
						} else {
							return holder.asAccessControlHolder();
						}
					}
				} finally {
					this.group.writeLock.unlock();
				}
			}
		}

		private final CacheHolder<TFacade, TImplement, TKeysHolder> internalFindHolder(
				final Object keyValue1, final Object keyValue2,
				final Object keyValue3, Transaction transaction) {
			Entry<TFacade, TImplement, TKeysHolder> existEntry = this.entryMap[HashUtil.hash(keyValue1, keyValue2, keyValue3) & this.hashMask];
			while (existEntry != null) {
				if (existEntry.equalKeyValues(keyValue1, keyValue2, keyValue3) && existEntry.holder.isVisibleIn(transaction)) {
					if (existEntry.asPlaceHolder() != null) {
						if (existEntry.holder.isModifiableOnTransaction(transaction)) {
							return existEntry.holder;
						}
					} else if (!existEntry.havePlaceHolder || !existEntry.holder.isModifiableOnTransaction(transaction)) {
						return existEntry.holder;
					}
				}
				existEntry = existEntry.next;
			}
			return null;
		}

		private final CacheHolder<TFacade, TImplement, TKeysHolder> internalFindHolder(
				final AccessController accessController,
				final Operation<? super TFacade> operation,
				final Object keyValue1, final Object keyValue2,
				final Object keyValue3, Transaction transaction) {
			Entry<TFacade, TImplement, TKeysHolder> existEntry = this.entryMap[HashUtil.hash(keyValue1, keyValue2, keyValue3) & this.hashMask];
			while (existEntry != null) {
				if (existEntry.equalKeyValues(keyValue1, keyValue2, keyValue3) && existEntry.holder.isVisibleIn(transaction)) {
					if (existEntry.asPlaceHolder() != null) {
						if (existEntry.holder.isModifiableOnTransaction(transaction)) {
							if (accessController.hasAuthority(operation, existEntry.holder)) {
								return existEntry.holder;
							} else {
								return null;
							}
						}
					} else if (!existEntry.havePlaceHolder || !existEntry.holder.isModifiableOnTransaction(transaction)) {
						if (accessController.hasAuthority(operation, existEntry.holder)) {
							return existEntry.holder;
						} else {
							return null;
						}
					}
				}
				existEntry = existEntry.next;
			}
			return null;
		}

		private final CacheHolder<TFacade, TImplement, TKeysHolder> findSameHolder(
				final TKeysHolder keysHolder) {
			final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine = super.keyDefine;
			final Object keyValue1 = keyDefine.getKeyValue1(keysHolder);
			final Object keyValue2 = keyDefine.getKeyValue2(keysHolder);
			final Object keyValue3 = keyDefine.getKeyValue3(keysHolder);
			Entry<TFacade, TImplement, TKeysHolder> existEntry = this.entryMap[HashUtil.hash(keyValue1, keyValue2, keyValue3) & this.hashMask];
			while (existEntry != null) {
				if (existEntry.equalKeyValues(keyValue1, keyValue2, keyValue3)) {
					return existEntry.holder;
				} else {
					existEntry = existEntry.next;
				}
			}
			return null;
		}

		private final CacheHolder<TFacade, TImplement, TKeysHolder> findSameHolder(
				final Object keyValue1, final Object keyValue2,
				final Object keyValue3, final Transaction transaction) {
			final int hash = HashUtil.hash(keyValue1, keyValue2, keyValue3);
			final int index = hash & this.hashMask;
			Entry<TFacade, TImplement, TKeysHolder> existEntry = this.entryMap[index];
			while (existEntry != null) {
				if (existEntry.equalKeyValues(keyValue1, keyValue2, keyValue3)) {
					if (existEntry.holder.isModifiableOnTransaction(transaction)) {
						if (!existEntry.havePlaceHolder) {
							return existEntry.holder;
						}
					} else if (existEntry.asPlaceHolder() == null && !existEntry.holder.isDisposed()) {
						return existEntry.holder;
					}
				}
				existEntry = existEntry.next;
			}
			return null;
		}

		private final List<CacheHolder<TFacade, TImplement, TKeysHolder>> getHolderList(
				final Transaction transaction) {
			final ArrayList<CacheHolder<TFacade, TImplement, TKeysHolder>> holderList = new ArrayList<CacheHolder<TFacade, TImplement, TKeysHolder>>();
			for (Entry<TFacade, TImplement, TKeysHolder> existEntry : this.entryMap) {
				while (existEntry != null) {
					if (existEntry.holder.isVisibleIn(transaction)) {
						holderList.add(existEntry.holder);
					}
					existEntry = existEntry.next;
				}
			}
			return holderList;
		}

		private final List<TImplement> getValueList(
				final Transaction transaction) {
			final ArrayList<TImplement> valueList = new ArrayList<TImplement>();
			for (Entry<TFacade, TImplement, TKeysHolder> existEntry : this.entryMap) {
				while (existEntry != null) {
					final TImplement value = existEntry.holder.tryGetValue(transaction);
					if (value != null) {
						valueList.add(value);
					}
					existEntry = existEntry.next;
				}
			}
			return valueList;
		}

		private final List<TImplement> getValueList(
				final AccessController accessController,
				final OperationEntry operation, final Transaction transaction) {
			final ArrayList<TImplement> valueList = new ArrayList<TImplement>();
			for (Entry<TFacade, TImplement, TKeysHolder> existEntry : this.entryMap) {
				while (existEntry != null) {
					if (accessController.internalHasAuthority(operation, existEntry.holder.asAccessControlHolder())) {
						final TImplement value = existEntry.holder.tryGetValue(transaction);
						if (value != null) {
							valueList.add(value);
						}
					}
					existEntry = existEntry.next;
				}
			}
			return valueList;
		}

		private final void createEntry(final TKeysHolder keysHolder,
				final CacheHolder<TFacade, TImplement, TKeysHolder> holder) {
			final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine = super.keyDefine;
			final Object keyValue1 = keyDefine.getKeyValue1(keysHolder);
			final Object keyValue2 = keyDefine.getKeyValue2(keysHolder);
			final Object keyValue3 = keyDefine.getKeyValue3(keysHolder);
			final Entry<TFacade, TImplement, TKeysHolder> newEntry = this.keyDefine.newIndexEntry(holder, keyValue1, keyValue2, keyValue3);
			final int hashIndex = HashUtil.hash(keyValue1, keyValue2, keyValue3) & this.hashMask;
			newEntry.next = this.entryMap[hashIndex];
			this.entryMap[hashIndex] = newEntry;
		}

		private final void removeEntry(final TKeysHolder keysHolder) {
			final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine = super.keyDefine;
			final Object keyValue1 = keyDefine.getKeyValue1(keysHolder);
			final Object keyValue2 = keyDefine.getKeyValue2(keysHolder);
			final Object keyValue3 = keyDefine.getKeyValue3(keysHolder);
			final int hashIndex = HashUtil.hash(keyValue1, keyValue2, keyValue3) & this.hashMask;
			Entry<TFacade, TImplement, TKeysHolder> existEntry = this.entryMap[hashIndex];
			Entry<TFacade, TImplement, TKeysHolder> lastExistEntry = null;
			while (existEntry != null) {
				if (existEntry.equalKeyValues(keyValue1, keyValue2, keyValue3)) {
					if (lastExistEntry == null) {
						this.entryMap[hashIndex] = existEntry.next;
					} else {
						lastExistEntry.next = existEntry.next;
					}
					existEntry.next = null;
					return;
				} else {
					lastExistEntry = existEntry;
					existEntry = existEntry.next;
				}
			}
		}

		private final void removeEntry(final TKeysHolder keysHolder,
				final CacheHolder<TFacade, TImplement, TKeysHolder> holder) {
			final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine = super.keyDefine;
			final Object keyValue1 = keyDefine.getKeyValue1(keysHolder);
			final Object keyValue2 = keyDefine.getKeyValue2(keysHolder);
			final Object keyValue3 = keyDefine.getKeyValue3(keysHolder);
			int hashIndex = HashUtil.hash(keyValue1, keyValue2, keyValue3) & this.hashMask;
			Entry<TFacade, TImplement, TKeysHolder> existEntry = this.entryMap[hashIndex];
			Entry<TFacade, TImplement, TKeysHolder> lastExistEntry = null;
			while (existEntry != null) {
				if (existEntry.equalKeyValues(keyValue1, keyValue2, keyValue3) && existEntry.holder == holder) {
					if (lastExistEntry == null) {
						this.entryMap[hashIndex] = existEntry.next;
					} else {
						lastExistEntry.next = existEntry.next;
					}
					existEntry.next = null;
					final Entry<TFacade, TImplement, TKeysHolder>.PlaceHolder placeHolderEntry = existEntry.asPlaceHolder();
					if (placeHolderEntry != null) {
						final Entry<TFacade, TImplement, TKeysHolder> oldEntry = placeHolderEntry.getBaseEntry();
						hashIndex = oldEntry.hashCode() & this.hashMask;
						existEntry = this.entryMap[hashIndex];
						lastExistEntry = null;
						while (existEntry != null) {
							if (existEntry == oldEntry) {
								if (lastExistEntry == null) {
									this.entryMap[hashIndex] = oldEntry.next;
								} else {
									lastExistEntry.next = oldEntry.next;
								}
								oldEntry.next = null;
								break;
							} else {
								lastExistEntry = existEntry;
								existEntry = existEntry.next;
							}
						}
					}
					return;
				} else {
					lastExistEntry = existEntry;
					existEntry = existEntry.next;
				}
			}
		}

		private final void moveEntry(final TKeysHolder oldKeysHolder,
				final TKeysHolder newKeysHolder,
				final CacheHolder<TFacade, TImplement, TKeysHolder> holder) {
			final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine = super.keyDefine;
			final Object oldKeyValue1 = keyDefine.getKeyValue1(oldKeysHolder);
			final Object oldKeyValue2 = keyDefine.getKeyValue2(oldKeysHolder);
			final Object oldKeyValue3 = keyDefine.getKeyValue3(oldKeysHolder);
			final int oldHashIndex = HashUtil.hash(oldKeyValue1, oldKeyValue2, oldKeyValue3) & this.hashMask;
			Entry<TFacade, TImplement, TKeysHolder> existEntry = this.entryMap[oldHashIndex];
			Entry<TFacade, TImplement, TKeysHolder> lastExistEntry = null;
			while (existEntry != null) {
				if (existEntry.equalKeyValues(oldKeyValue1, oldKeyValue2, oldKeyValue3) && existEntry.holder == holder) {
					final Entry<TFacade, TImplement, TKeysHolder>.PlaceHolder oldPlaceHolderEntry = existEntry.asPlaceHolder();
					if (oldPlaceHolderEntry != null) {
						if (lastExistEntry == null) {
							this.entryMap[oldHashIndex] = oldPlaceHolderEntry.next;
						} else {
							lastExistEntry.next = oldPlaceHolderEntry.next;
						}
						oldPlaceHolderEntry.next = null;
						existEntry = oldPlaceHolderEntry.getBaseEntry();
					}
					final Object newKeyValue1 = keyDefine.getKeyValue1(newKeysHolder);
					final Object newKeyValue2 = keyDefine.getKeyValue2(newKeysHolder);
					final Object newKeyValue3 = keyDefine.getKeyValue3(newKeysHolder);
					final Entry<TFacade, TImplement, TKeysHolder>.PlaceHolder newPlaceHolderEntry = existEntry.newPlaceHolder(newKeyValue1, newKeyValue2, newKeyValue3);
					existEntry.havePlaceHolder = true;
					final int newHashIndex = newPlaceHolderEntry.hashCode() & this.hashMask;
					newPlaceHolderEntry.next = this.entryMap[newHashIndex];
					this.entryMap[newHashIndex] = newPlaceHolderEntry;
					return;
				} else {
					lastExistEntry = existEntry;
					existEntry = existEntry.next;
				}
			}
		}

		private final void finishMoveEntry(final TKeysHolder keysHolder,
				final CacheHolder<TFacade, TImplement, TKeysHolder> holder) {
			final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine = super.keyDefine;
			final Object keyValue1 = keyDefine.getKeyValue1(keysHolder);
			final Object keyValue2 = keyDefine.getKeyValue2(keysHolder);
			final Object keyValue3 = keyDefine.getKeyValue3(keysHolder);
			int hashIndex = HashUtil.hash(keyValue1, keyValue2, keyValue3) & this.hashMask;
			Entry<TFacade, TImplement, TKeysHolder> existEntry = this.entryMap[hashIndex];
			Entry<TFacade, TImplement, TKeysHolder> lastExistEntry = null;
			while (existEntry != null) {
				if (existEntry.equalKeyValues(keyValue1, keyValue2, keyValue3) && existEntry.holder == holder) {
					final Entry<TFacade, TImplement, TKeysHolder>.PlaceHolder placeHolderEntry = existEntry.asPlaceHolder();
					if (placeHolderEntry != null) {
						final Entry<TFacade, TImplement, TKeysHolder> newEntry = this.keyDefine.newIndexEntry(holder, keyValue1, keyValue2, keyValue3);
						newEntry.next = placeHolderEntry.next;
						if (lastExistEntry == null) {
							this.entryMap[hashIndex] = newEntry;
						} else {
							lastExistEntry.next = newEntry;
						}
						placeHolderEntry.next = null;
						final Entry<TFacade, TImplement, TKeysHolder> oldEntry = placeHolderEntry.getBaseEntry();
						hashIndex = oldEntry.hashCode() & this.hashMask;
						existEntry = this.entryMap[hashIndex];
						lastExistEntry = null;
						while (existEntry != null) {
							if (existEntry == oldEntry) {
								if (lastExistEntry == null) {
									this.entryMap[hashIndex] = oldEntry.next;
								} else {
									lastExistEntry.next = oldEntry.next;
								}
								oldEntry.next = null;
								oldEntry.havePlaceHolder = false;
								break;
							} else {
								lastExistEntry = existEntry;
								existEntry = existEntry.next;
							}
						}
					}
					return;
				} else {
					lastExistEntry = existEntry;
					existEntry = existEntry.next;
				}
			}
		}

		private final void cancelMoveEntry(final TKeysHolder keysHolder,
				final CacheHolder<TFacade, TImplement, TKeysHolder> holder) {
			final KeyDefine<TFacade, TImplement, TKeysHolder> keyDefine = super.keyDefine;
			final Object keyValue1 = keyDefine.getKeyValue1(keysHolder);
			final Object keyValue2 = keyDefine.getKeyValue2(keysHolder);
			final Object keyValue3 = keyDefine.getKeyValue3(keysHolder);
			final int oldHashIndex = HashUtil.hash(keyValue1, keyValue2, keyValue3) & this.hashMask;
			Entry<TFacade, TImplement, TKeysHolder> existEntry = this.entryMap[oldHashIndex];
			Entry<TFacade, TImplement, TKeysHolder> lastExistEntry = null;
			while (existEntry != null) {
				if (existEntry.equalKeyValues(keyValue1, keyValue2, keyValue3) && existEntry.holder == holder) {
					final Entry<TFacade, TImplement, TKeysHolder>.PlaceHolder oldPlaceHolderEntry = existEntry.asPlaceHolder();
					if (oldPlaceHolderEntry != null) {
						if (lastExistEntry == null) {
							this.entryMap[oldHashIndex] = oldPlaceHolderEntry.next;
						} else {
							lastExistEntry.next = oldPlaceHolderEntry.next;
						}
						oldPlaceHolderEntry.next = null;
						oldPlaceHolderEntry.getBaseEntry().havePlaceHolder = false;
					}
					return;
				} else {
					lastExistEntry = existEntry;
					existEntry = existEntry.next;
				}
			}
		}

		private final void removeHolders(final Transaction transaction) {
			for (Entry<TFacade, TImplement, TKeysHolder> existEntry : this.entryMap) {
				while (existEntry != null) {
					existEntry.holder.localRemove(transaction);
					existEntry = existEntry.next;
				}
			}
		}

		@SuppressWarnings("unchecked")
		private final void reHash(final int newCapacity) {
			final Entry<TFacade, TImplement, TKeysHolder>[] newEntryMap = new Entry[newCapacity];
			final int newHashMask = newCapacity - 1;
			for (Entry<TFacade, TImplement, TKeysHolder> existEntry : this.entryMap) {
				while (existEntry != null) {
					final int newHashIndex = existEntry.hashCode() & newHashMask;
					final Entry<TFacade, TImplement, TKeysHolder> tempEntry = existEntry;
					existEntry = existEntry.next;
					tempEntry.next = newEntryMap[newHashIndex];
					newEntryMap[newHashIndex] = tempEntry;
				}
			}
			this.entryMap = newEntryMap;
			this.hashMask = newHashMask;
		}

		@SuppressWarnings("unchecked")
		private final void reset(int mapCapacity) {
			this.entryMap = new Entry[mapCapacity];
			this.hashMask = mapCapacity - 1;
		}

		private final CacheGroup<TFacade, TImplement, TKeysHolder> group;

		private volatile Entry<TFacade, TImplement, TKeysHolder>[] entryMap;

		private volatile int hashMask;
	}
}

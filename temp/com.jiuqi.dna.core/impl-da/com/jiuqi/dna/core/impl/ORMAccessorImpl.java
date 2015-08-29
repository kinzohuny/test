package com.jiuqi.dna.core.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.jiuqi.dna.core.def.obja.DynamicObject;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.da.sql.execute.LimitQuerier;
import com.jiuqi.dna.core.internal.da.sql.execute.Querier;
import com.jiuqi.dna.core.internal.da.sql.execute.RowCountQuerier;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleSqlExecutor;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlModifier;
import com.jiuqi.dna.core.internal.da.sql.execute.TopQuerier;
import com.jiuqi.dna.core.internal.da.sql.render.ObjByLpkDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.ObjByLpkQuerySql;
import com.jiuqi.dna.core.internal.da.sql.render.ObjByRecidDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.ObjByRecidQuerySql;
import com.jiuqi.dna.core.internal.da.sql.render.ObjByRecidsDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.ObjRecverDeleteSql;
import com.jiuqi.dna.core.internal.da.sql.render.ObjRecverUpdateSql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.misc.ObjectBuilder;
import com.jiuqi.dna.core.type.GUID;

/**
 * 实体绑定数据库访问对象
 * 
 * @author houchunlei
 * 
 * @param <TEntity>
 *            绑定实体
 */
public final class ORMAccessorImpl<TEntity> extends
		StatementHolder<ORMAccessorProxy<TEntity>> {

	public final void insert(TEntity entity) {
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		this.ensureInserter().updateRow(entity);
	}

	public final void insert(TEntity entity, TEntity... others) {
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		SqlModifier inserter = this.ensureInserter();
		inserter.updateRow(entity);
		if (others != null) {
			for (TEntity other : others) {
				if (other == null) {
					throw new NullArgumentException("other");
				}
				inserter.updateRow(other);
			}
		}
	}

	public final void insert(TEntity[] entities) {
		if (entities == null) {
			throw new NullArgumentException("entities");
		}
		SqlModifier inserter = this.ensureInserter();
		for (TEntity entity : entities) {
			if (entity == null) {
				throw new NullArgumentException("entity");
			}
			inserter.updateRow(entity);
		}
	}

	public final void insert(Iterable<TEntity> entities) {
		if (entities == null) {
			throw new NullArgumentException("entities");
		}
		SqlModifier inserter = this.ensureInserter();
		for (TEntity entity : entities) {
			if (entity == null) {
				throw new NullArgumentException("entity");
			}
			inserter.updateRow(entity);
		}
	}

	public final boolean delete(GUID recid) {
		this.adapter.checkAccessible();
		if (recid == null) {
			throw new NullArgumentException("recid");
		}
		return this.ensureByRecidDeleter().delete(recid) > 0;
	}

	public final int delete(GUID recid, GUID... others) {
		this.adapter.checkAccessible();
		if (recid == null) {
			throw new NullArgumentException("recid");
		}
		if (others == null || others.length == 0) {
			return this.delete(recid) ? 1 : 0;
		} else {
			return this.ensureByRecidsDeleter().delete(recid, others);
		}
	}

	public final int delete(GUID[] recids) {
		this.adapter.checkAccessible();
		if (recids == null) {
			throw new NullArgumentException("recids");
		}
		if (recids.length > 0) {
			return this.ensureByRecidsDeleter().delete(null, recids);
		} else {
			return 0;
		}
	}

	public final boolean delete(TEntity entity) {
		this.adapter.checkAccessible();
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		return this.ensureDeleter().updateRow(entity);
	}

	public final boolean delete(GUID recid, long expectRECVER) {
		this.adapter.checkAccessible();
		if (recid == null) {
			throw new NullArgumentException("recid");
		}
		if (this.entityValueObj == null) {
			this.entityValueObj = this.mStatement.newEntity(null);
		}
		return this.ensureRecveredDeleter().update(this.entityValueObj, recid, expectRECVER);
	}

	public final int delete(TEntity entity, TEntity... others) {
		this.adapter.checkAccessible();
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		int r = 0;
		SqlModifier deleter = this.ensureDeleter();
		if (deleter.updateRow(entity)) {
			r++;
		}
		if (others != null) {
			for (TEntity other : others) {
				if (other == null) {
					throw new NullArgumentException("other");
				}
				if (deleter.updateRow(other)) {
					r++;
				}
			}
		}
		return r;
	}

	public final int delete(TEntity[] entities) {
		this.adapter.checkAccessible();
		if (entities == null) {
			throw new NullArgumentException("entities");
		}
		SqlModifier deleter = this.ensureDeleter();
		int r = 0;
		for (TEntity entity : entities) {
			if (entity == null) {
				throw new NullArgumentException("entity");
			}
			if (deleter.updateRow(entity)) {
				r++;
			}
		}
		return r;
	}

	public final int delete(Iterable<TEntity> entities) {
		this.adapter.checkAccessible();
		if (entities == null) {
			throw new NullArgumentException("entities");
		}
		SqlModifier deleter = this.ensureDeleter();
		int r = 0;
		for (TEntity entity : entities) {
			if (entity == null) {
				throw new NullArgumentException("entity");
			}
			if (deleter.updateRow(entity)) {
				r++;
			}
		}
		return r;
	}

	public final int deleteByPKey(Object... keys) {
		this.adapter.checkAccessible();
		if (keys == null || keys.length == 0) {
			throw new NullArgumentException("逻辑主键值");
		}
		if (this.entityValueObj == null) {
			this.entityValueObj = this.mStatement.newEntity(null);
		}
		return this.ensureByLpkDeleter().executeUpdate(this.entityValueObj, keys);
	}

	public final boolean update(TEntity entity) {
		this.adapter.checkAccessible();
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		return this.ensureUpdater().updateRow(entity);
	}

	public final int update(TEntity entity, TEntity... others) {
		this.adapter.checkAccessible();
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		SqlModifier updater = this.ensureUpdater();
		int r = updater.updateRow(entity) ? 1 : 0;
		if (others != null) {
			for (TEntity other : others) {
				if (other == null) {
					throw new NullArgumentException("other");
				}
				if (updater.updateRow(other)) {
					r++;
				}
			}
		}
		return r;
	}

	public final int update(TEntity[] entities) {
		this.adapter.checkAccessible();
		if (entities == null) {
			throw new NullArgumentException("entities");
		}
		SqlModifier updater = this.ensureUpdater();
		int r = 0;
		for (TEntity entity : entities) {
			if (entity == null) {
				throw new NullArgumentException("entity");
			}
			if (updater.updateRow(entity)) {
				r++;
			}
		}
		return r;
	}

	public final int update(Iterable<TEntity> entities) {
		this.adapter.checkAccessible();
		if (entities == null) {
			throw new NullArgumentException("entities");
		}
		SqlModifier updater = this.ensureUpdater();
		int r = 0;
		for (TEntity entity : entities) {
			if (entity == null) {
				throw new NullArgumentException("entity");
			}
			if (updater.updateRow(entity)) {
				r++;
			}
		}
		return r;
	}

	public final boolean update(TEntity entity, long expectedRECVER) {
		this.adapter.checkAccessible();
		if (entity == null) {
			throw new NullArgumentException("entity");
		}
		return this.ensureRecveredUpdater().update(entity, expectedRECVER);
	}

	public final List<TEntity> fetch(Object... argValues) {
		return this.internalFetch(null, null, argValues);
	}

	public final List<TEntity> fetch(List<Object> argValues) {
		return this.internalFetch(null, null, argValues.toArray(new Object[argValues.size()]));
	}

	public final List<TEntity> fetch(ObjectBuilder<TEntity> entityFactory,
			Object... argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetch(null, entityFactory, argValues);
	}

	public final List<TEntity> fetch(ObjectBuilder<TEntity> entityFactory,
			List<Object> argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetch(null, entityFactory, argValues.toArray(new Object[argValues.size()]));
	}

	public final List<TEntity> fetchLimit(long offset, long rowCount,
			Object... argValues) {
		return this.internalFetchLimit(null, offset, rowCount, null, argValues);
	}

	public final List<TEntity> fetchLimit(long offset, long rowCount,
			List<Object> argValues) {
		return this.internalFetchLimit(null, offset, rowCount, null, argValues.toArray(new Object[argValues.size()]));
	}

	public final List<TEntity> fetchLimit(long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, Object... argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetchLimit(null, offset, rowCount, entityFactory, argValues);
	}

	public final List<TEntity> fetchLimit(long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, List<Object> argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetchLimit(null, offset, rowCount, entityFactory, argValues.toArray(new Object[argValues.size()]));
	}

	public final long fetchInto(List<TEntity> into, Object... argValues) {
		return this.internalFetchInto(into, null, argValues);
	}

	public final long fetchInto(List<TEntity> into, List<Object> argValues) {
		return this.internalFetchInto(into, null, argValues.toArray(new Object[argValues.size()]));
	}

	public final long fetchInto(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, Object... argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetchInto(into, entityFactory, argValues);
	}

	public final long fetchInto(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, List<Object> argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetchInto(into, entityFactory, argValues.toArray(new Object[argValues.size()]));
	}

	public final long fetchLimitInto(List<TEntity> into, long offset,
			long rowCount, Object... argValues) {
		return this.internalFetchLimitInto(into, offset, rowCount, null, argValues);
	}

	public final long fetchLimitInto(List<TEntity> into, long offset,
			long rowCount, List<Object> argValues) {
		return this.internalFetchLimitInto(into, offset, rowCount, null, argValues.toArray(new Object[argValues.size()]));
	}

	public final long fetchLimitInto(List<TEntity> into, long offset,
			long rowCount, ObjectBuilder<TEntity> entityFactory,
			Object... argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetchLimitInto(into, offset, rowCount, entityFactory, argValues);
	}

	public final long fetchLimitInto(List<TEntity> into, long offset,
			long rowCount, ObjectBuilder<TEntity> entityFactory,
			List<Object> argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFetchLimitInto(into, offset, rowCount, entityFactory, argValues.toArray(new Object[argValues.size()]));
	}

	public final long rowCountOf(Object... argValues) {
		final RowCountQuerier querier = this.ensureRowCountQuerier();
		setArgumentValues(this.argValueObj, this.mStatement, argValues);
		return querier.longScalar(this.argValueObj);
	}

	public final long rowCountOf(List<Object> argValues) {
		return this.rowCountOf(argValues.toArray(new Object[argValues.size()]));
	}

	public final TEntity first(Object... argValues) {
		return this.internalFirst(null, argValues);
	}

	public final TEntity first(List<Object> argValues) {
		return this.internalFirst(null, argValues.toArray(new Object[argValues.size()]));
	}

	public final TEntity first(ObjectBuilder<TEntity> entityFactory,
			Object... argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFirst(entityFactory, argValues);
	}

	public final TEntity first(ObjectBuilder<TEntity> entityFactory,
			List<Object> argValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFirst(entityFactory, argValues.toArray(new Object[argValues.size()]));
	}

	public final TEntity findByRECID(GUID recid) {
		return this.internalFindByRecid(null, recid);
	}

	public final TEntity findByRECID(ObjectBuilder<TEntity> entityFactory,
			GUID recid) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFindByRecid(entityFactory, recid);
	}

	public final TEntity findByPKey(Object... keyValues) {
		return this.internalFindByPKey(null, keyValues);
	}

	public final TEntity findByPKey(ObjectBuilder<TEntity> entityFactory,
			Object... keyValues) {
		if (entityFactory == null) {
			throw new NullPointerException();
		}
		return this.internalFindByPKey(entityFactory, keyValues);
	}

	// public final List<TEntity> getChildren(HierarchyDefine hierarchy, GUID
	// recid) {
	// if (recid == null) {
	// throw new NullPointerException();
	// }
	// return this.getChildren0(null, this.checkHierarchy(hierarchy), recid);
	// }
	//
	// public final List<TEntity> getChildren(
	// ObjectBuilder<TEntity> entityFactory, HierarchyDefine hierarchy,
	// GUID recid) {
	// if (entityFactory == null) {
	// throw new NullPointerException();
	// }
	// if (recid == null) {
	// throw new NullPointerException();
	// }
	// return this.getChildren0(entityFactory, this.checkHierarchy(hierarchy),
	// recid);
	// }
	//
	// public final TreeNode<TEntity> getDescendant(HierarchyDefine hierarchy,
	// GUID recid) {
	// if (recid == null) {
	// throw new NullPointerException();
	// }
	// return this.getDescendant0(null, this.checkHierarchy(hierarchy), recid,
	// -1);
	// }
	//
	// public final TreeNode<TEntity> getDescendant(
	// ObjectBuilder<TEntity> entityFactory, HierarchyDefine hierarchy,
	// GUID recid) {
	// if (entityFactory == null) {
	// throw new NullPointerException();
	// }
	// if (recid == null) {
	// throw new NullPointerException();
	// }
	// return this.getDescendant0(entityFactory, this
	// .checkHierarchy(hierarchy), recid, -1);
	// }
	//
	// public final TreeNode<TEntity> getDescendant(HierarchyDefine hierarchy,
	// GUID recid, int range) {
	// if (recid == null) {
	// throw new NullPointerException();
	// }
	// if (range <= 0) {
	// throw new IllegalArgumentException();
	// }
	// return this.getDescendant0(null, this.checkHierarchy(hierarchy), recid,
	// range);
	// }
	//
	// public final TreeNode<TEntity> getDescendant(
	// ObjectBuilder<TEntity> entityFactory, HierarchyDefine hierarchy,
	// GUID recid, int range) {
	// if (entityFactory == null) {
	// throw new NullPointerException();
	// }
	// if (recid == null) {
	// throw new NullPointerException();
	// }
	// if (range <= 0) {
	// throw new IllegalArgumentException();
	// }
	// return this.getDescendant0(entityFactory, this
	// .checkHierarchy(hierarchy), recid, range);
	// }

	@Override
	public final void unuse() {
		if (this.querier != null) {
			this.querier.unuse();
		}
		if (this.topQuerier != null) {
			this.topQuerier.unuse();
		}
		if (this.limitQuerier != null) {
			this.limitQuerier.unuse();
		}
		if (this.rowCountQuerier != null) {
			this.rowCountQuerier.unuse();
		}
		if (this.lpkQuerier != null) {
			this.lpkQuerier.unuse();
		}
		if (this.inserter != null) {
			this.inserter.unuse();
		}
		if (this.deleter != null) {
			this.deleter.unuse();
		}
		if (this.updater != null) {
			this.updater.unuse();
		}
		if (this.recverUpdater != null) {
			this.recverUpdater.unuse();
		}
		if (this.recverDeleter != null) {
			this.recverDeleter.unuse();
		}
		if (this.recidDeleter != null) {
			this.recidDeleter.unuse();
		}
		if (this.recidsDeleter != null) {
			this.recidsDeleter.unuse();
		}
		if (this.lpkDeleter != null) {
			this.lpkDeleter.unuse();
		}
		if (this.recidQuerier != null) {
			this.recidQuerier.unuse();
		}
		if (this.recidQuerier != null) {
			this.recidQuerier.unuse();
		}
	}

	final MappingQueryStatementImpl mStatement;

	private DynamicObject argValueObj;

	private TEntity entityValueObj;

	ORMAccessorImpl(ContextImpl<?, ?, ?> context,
			MappingQueryStatementImpl statement, ORMAccessorProxy<TEntity> proxy) {
		super(context, proxy);
		this.argValueObj = new DynamicObject();
		this.mStatement = statement;
	}

	// ------------------------------- internal -------------------------------

	private final List<TEntity> internalFetch(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, Object[] argValues) {
		this.adapter.checkAccessible();
		try {
			Querier querier = this.ensureQuerier();
			setArgumentValues(this.argValueObj, this.mStatement, argValues);
			ResultSet resultSet = querier.query(this.argValueObj);
			try {
				return ResultSetReader.readEntities(entityFactory, into, this.mStatement, resultSet);
			} finally {
				resultSet.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final int internalFetchInto(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, Object[] argValues) {
		if (into == null) {
			throw new NullArgumentException("into");
		}
		int s = into.size();
		this.internalFetch(into, entityFactory, argValues);
		return into.size() - s;
	}

	private final List<TEntity> internalFetchLimit(List<TEntity> into,
			long offset, long limit, ObjectBuilder<TEntity> entityFactory,
			Object[] argValues) {
		this.adapter.checkAccessible();
		try {
			ResultSet rs = null;
			if (offset == 0) {
				TopQuerier querier = this.ensureTopQuerier();
				setArgumentValues(this.argValueObj, this.mStatement, argValues);
				rs = querier.query(this.argValueObj, limit);
			} else {
				LimitQuerier querier = this.ensureLimitQuerier();
				setArgumentValues(this.argValueObj, this.mStatement, argValues);
				rs = querier.query(this.argValueObj, limit, offset);
			}
			try {
				return ResultSetReader.readEntities(entityFactory, into, this.mStatement, rs);
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final int internalFetchLimitInto(List<TEntity> into, long offset,
			long rowCount, ObjectBuilder<TEntity> entityFactory,
			Object[] argValues) {
		if (into == null) {
			throw new NullArgumentException("into");
		}
		int s = into.size();
		this.internalFetchLimit(into, offset, rowCount, entityFactory, argValues);
		return into.size() - s;
	}

	private final TEntity internalFirst(ObjectBuilder<TEntity> entityFactory,
			Object[] argValues) {
		this.adapter.checkAccessible();
		try {
			Querier querier = this.ensureQuerier();
			setArgumentValues(this.argValueObj, this.mStatement, argValues);
			ResultSet resultSet = querier.query(this.argValueObj);
			try {
				return ResultSetReader.readNextEntity(entityFactory, this.mStatement, resultSet);
			} finally {
				resultSet.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final TEntity internalFindByRecid(
			ObjectBuilder<TEntity> entityFactory, GUID recid) {
		this.adapter.checkAccessible();
		if (recid == null) {
			throw new NullArgumentException("行标识");
		}
		try {
			ResultSet resultSet = this.ensureByRecidQuerier().query(recid);
			try {
				return ResultSetReader.readNextEntity(entityFactory, this.mStatement, resultSet);
			} finally {
				resultSet.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private final TEntity internalFindByPKey(
			ObjectBuilder<TEntity> entityFactory, Object[] keys) {
		this.adapter.checkAccessible();
		if (keys == null || keys.length == 0) {
			throw new NullPointerException();
		}
		if (this.entityValueObj == null) {
			this.entityValueObj = this.mStatement.newEntity(null);
		}
		try {
			ResultSet resultSet = this.ensureByLpkQuerier().executeQuery(this.entityValueObj, keys);
			try {
				return ResultSetReader.readNextEntity(entityFactory, this.mStatement, resultSet);
			} finally {
				resultSet.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private Querier querier;

	private TopQuerier topQuerier;

	private LimitQuerier limitQuerier;

	private RowCountQuerier rowCountQuerier;

	private SqlModifier inserter;

	private SqlModifier deleter;

	private SqlModifier updater;

	private RecverUpdater recverUpdater;

	private RecverDeleter recverDeleter;

	private RecidDeleter recidDeleter;

	private RecidsDeleter recidsDeleter;

	private LpkDeleter lpkDeleter;

	private RecidQuerier recidQuerier;

	private LpkQuerier lpkQuerier;

	private final Querier ensureQuerier() {
		if (this.querier == null) {
			this.querier = this.mStatement.getSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.querier;
	}

	private final TopQuerier ensureTopQuerier() {
		if (this.topQuerier == null) {
			this.topQuerier = this.mStatement.getQueryTopSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.topQuerier;
	}

	private final LimitQuerier ensureLimitQuerier() {
		if (this.limitQuerier == null) {
			this.limitQuerier = this.mStatement.getQueryLimitSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.limitQuerier;
	}

	private final RowCountQuerier ensureRowCountQuerier() {
		if (this.rowCountQuerier == null) {
			this.rowCountQuerier = this.mStatement.getQueryRowCountSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.rowCountQuerier;
	}

	private final SqlModifier ensureInserter() {
		if (this.inserter == null) {
			this.inserter = this.mStatement.getRowInsertSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.inserter;
	}

	private final SqlModifier ensureDeleter() {
		if (this.deleter == null) {
			this.deleter = this.mStatement.getRowDeleteSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.deleter;
	}

	private final SqlModifier ensureUpdater() {
		if (this.updater == null) {
			this.updater = this.mStatement.getRowUpdateSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.updater;
	}

	private final RecverDeleter ensureRecveredDeleter() {
		if (this.recverDeleter == null) {
			this.recverDeleter = this.mStatement.getObjRecveredDeleteSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.recverDeleter;
	}

	private final RecverUpdater ensureRecveredUpdater() {
		if (this.recverUpdater == null) {
			this.recverUpdater = this.mStatement.getObjRecveredUpdateSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.recverUpdater;
	}

	private final RecidQuerier ensureByRecidQuerier() {
		if (this.recidQuerier == null) {
			this.recidQuerier = this.mStatement.getByRecidQuerySql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.recidQuerier;
	}

	private final LpkQuerier ensureByLpkQuerier() {
		if (this.lpkQuerier == null) {
			this.lpkQuerier = this.mStatement.getByLpkQuerySql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.lpkQuerier;
	}

	private final RecidDeleter ensureByRecidDeleter() {
		if (this.recidDeleter == null) {
			this.recidDeleter = this.mStatement.getByRecidDeleteSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.recidDeleter;
	}

	private final RecidsDeleter ensureByRecidsDeleter() {
		if (this.recidsDeleter == null) {
			this.recidsDeleter = this.mStatement.getByRecidsDeleteSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.recidsDeleter;
	}

	private final LpkDeleter ensureByLpkDeleter() {
		if (this.lpkDeleter == null) {
			this.lpkDeleter = this.mStatement.getByLpkDeleteSql(this.adapter).newExecutor(this.adapter, this);
		}
		return this.lpkDeleter;
	}

	// private final HierarchyPathQuerier ensureHierarchyPathQuerier(
	// HierarchyDefineImpl hierarchy) {
	// for (PsExecutor<?> ps = this.stmts; ps != null; ps = ps.next) {
	// if (ps instanceof HierarchyPathQuerier
	// && ((HierarchyPathQuerier) ps).hierarchy == hierarchy) {
	// return (HierarchyPathQuerier) ps;
	// }
	// }
	// HierarchyPathQuerier querier = new HierarchyPathQuerier(this, hierarchy);
	// querier.next = this.stmts;
	// this.stmts = querier;
	// return querier;
	// }
	//
	// private final ChildrenQuerier getChildrenQuerier(
	// HierarchyDefineImpl hierarchy) {
	// for (PsExecutor<?> ps = this.stmts; ps != null; ps = ps.next) {
	// if (ps instanceof ChildrenQuerier
	// && ((ChildrenQuerier) ps).hierarchy == hierarchy) {
	// return (ChildrenQuerier) ps;
	// }
	// }
	// ChildrenQuerier querier = new ChildrenQuerier(this, hierarchy);
	// querier.next = this.stmts;
	// this.stmts = querier;
	// return querier;
	// }
	//
	// private final DescendantQuerier getDescendantQuerier(
	// HierarchyDefineImpl hierarchy) {
	// for (PsExecutor<?> ps = this.stmts; ps != null; ps = ps.next) {
	// if (ps instanceof DescendantQuerier
	// && ((DescendantQuerier) ps).hierarchy == hierarchy) {
	// return (DescendantQuerier) ps;
	// }
	// }
	// DescendantQuerier querier = new DescendantQuerier(this, hierarchy);
	// querier.next = this.stmts;
	// this.stmts = querier;
	// return querier;
	// }
	//
	// private final RangeDescendantQuerier getRangeDescendantQuerier(
	// HierarchyDefineImpl hierarchy, int range) {
	// for (PsExecutor<?> ps = this.stmts; ps != null; ps = ps.next) {
	// if (ps instanceof RangeDescendantQuerier) {
	// RangeDescendantQuerier q = (RangeDescendantQuerier) ps;
	// if (q.hierarchy == hierarchy && q.range == range) {
	// return q;
	// }
	// }
	// }
	// RangeDescendantQuerier querier = new RangeDescendantQuerier(this,
	// hierarchy, range);
	// querier.next = this.stmts;
	// this.stmts = querier;
	// return querier;
	// }

	public static final class RecverUpdater extends
			SimpleSqlExecutor<ObjRecverUpdateSql, RecverUpdater> {

		public RecverUpdater(DBAdapterImpl adapter, ObjRecverUpdateSql sql,
				ActiveChangable notify) {
			super(adapter, sql, notify);
		}

		final boolean update(Object entity, long expectRecver) {
			try {
				super.use(true);
				this.flushParameters(entity);
				this.sql.recver.setLong(this.pstmt, this.sql.parameters, expectRecver);
				return this.pstmt.executeUpdate() > 0;
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	public static final class RecverDeleter extends
			SimpleSqlExecutor<ObjRecverDeleteSql, RecverDeleter> {

		public RecverDeleter(DBAdapterImpl adapter, ObjRecverDeleteSql sql,
				ActiveChangable notify) {
			super(adapter, sql, notify);
		}

		boolean update(Object entity, GUID recid, long expectRecver) {
			try {
				super.use(true);
				this.sql.arg_recid.setFieldValueAsGUID(entity, recid);
				this.flushParameters(entity);
				this.sql.recver.setLong(this.pstmt, this.sql.parameters, expectRecver);
				return this.pstmt.executeUpdate() > 0;
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	public static final class RecidQuerier extends
			SimpleSqlExecutor<ObjByRecidQuerySql, RecidQuerier> {

		public RecidQuerier(DBAdapterImpl adapter, ObjByRecidQuerySql sql,
				ActiveChangable notify) {
			super(adapter, sql, notify);
		}

		final ResultSet query(GUID id) {
			try {
				super.use(false);
				this.sql.recid.setBytes(this.pstmt, this.sql.parameters, id.toBytes());
				return this.pstmt.executeQuery();
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	public static final class RecidDeleter extends
			SimpleSqlExecutor<ObjByRecidDeleteSql, RecidDeleter> {

		public RecidDeleter(DBAdapterImpl adapter, ObjByRecidDeleteSql sql,
				ActiveChangable notify) {
			super(adapter, sql, notify);
		}

		final int delete(GUID recid) {
			try {
				super.use(true);
				this.sql.recid.setBytes(this.pstmt, this.sql.parameters, recid.toBytes());
				return this.pstmt.executeUpdate();
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	public static final class RecidsDeleter extends
			SimpleSqlExecutor<ObjByRecidsDeleteSql, RecidsDeleter> {

		public RecidsDeleter(DBAdapterImpl adapter, ObjByRecidsDeleteSql sql,
				ActiveChangable notify) {
			super(adapter, sql, notify);
		}

		final int delete(GUID recid, GUID[] others) {
			try {
				int r = 0;
				int pi = 1;
				boolean used = false;
				if (recid != null) {
					if (!used) {
						super.use(true);
						used = true;
					}
					super.pstmt.setBytes(pi++, recid.toBytes());
				}
				for (int oi = 0; oi < others.length; oi++) {
					if (pi > ContextVariableIntl.ORM_PER_BYIDS_DELETE) {
						r += this.pstmt.executeUpdate();
						pi = 1;
					}
					recid = others[oi];
					if (recid != null) {
						if (!used) {
							super.use(true);
							used = true;
						}
						super.pstmt.setBytes(pi++, recid.toBytes());
					}
				}
				if (pi > 1) {
					while (pi <= ContextVariableIntl.ORM_PER_BYIDS_DELETE) {
						super.pstmt.setBytes(pi++, null);
					}
					r += this.pstmt.executeUpdate();
				}
				return r;
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	public static final class LpkDeleter extends
			SimpleSqlExecutor<ObjByLpkDeleteSql, LpkDeleter> {

		public LpkDeleter(DBAdapterImpl adapter, ObjByLpkDeleteSql sql,
				ActiveChangable notify) {
			super(adapter, sql, notify);
		}

		final int executeUpdate(Object entityArg, Object... keys) {
			try {
				this.use(true);
				for (int i = 0, c = Math.min(keys.length, this.sql.args.length); i < c; i++) {
					ArgumentPlaceholder ar = this.sql.args[i];
					ar.arg.setFieldValueAsObject(entityArg, keys[i]);
				}
				this.flushParameters(entityArg);
				return this.pstmt.executeUpdate();
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}

	}

	public static final class LpkQuerier extends
			SimpleSqlExecutor<ObjByLpkQuerySql, LpkQuerier> {

		public LpkQuerier(DBAdapterImpl adapter, ObjByLpkQuerySql sql,
				ActiveChangable notify) {
			super(adapter, sql, notify);
		}

		final ResultSet executeQuery(Object entityArg, Object... keys) {
			try {
				super.use(false);
				for (int i = 0, c = Math.min(keys.length, this.sql.parameters.size()); i < c; i++) {
					ParameterPlaceholder pr = this.sql.parameters.get(i);
					if (pr instanceof ArgumentPlaceholder) {
						ArgumentPlaceholder ar = (ArgumentPlaceholder) pr;
						ar.arg.setFieldValueAsObject(entityArg, keys[i]);
					}
				}
				this.flushParameters(entityArg);
				return this.pstmt.executeQuery();
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}
}
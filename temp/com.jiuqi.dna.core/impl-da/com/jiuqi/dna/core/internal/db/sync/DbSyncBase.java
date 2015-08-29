package com.jiuqi.dna.core.internal.db.sync;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.db.sync.DbTableNameReservedException;
import com.jiuqi.dna.core.db.sync.DbTableNameTooLongException;
import com.jiuqi.dna.core.db.sync.TableSyncException;
import com.jiuqi.dna.core.db.sync.UniqueIndexValueDuplicatedException;
import com.jiuqi.dna.core.db.sync.UnsupportedNotNullFieldException;
import com.jiuqi.dna.core.db.sync.UnsupportedTypeConversionException;
import com.jiuqi.dna.core.def.table.IndexType;
import com.jiuqi.dna.core.def.table.TableType;
import com.jiuqi.dna.core.impl.ContextVariableIntl;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IndexDefineImpl;
import com.jiuqi.dna.core.impl.NameUtl;
import com.jiuqi.dna.core.impl.NumericDBType;
import com.jiuqi.dna.core.impl.ResolveHelper;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.common.Charsets;
import com.jiuqi.dna.core.internal.common.Strings;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlPredicate;
import com.jiuqi.dna.core.internal.db.datasource.PooledConnection;
import com.jiuqi.dna.core.internal.db.datasource.SqlSource;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.type.TypeFactory;

public abstract class DbSyncBase<TMetadata extends DbMetadata, TStructCtl extends DbStructCtl<TMetadata, TTable, TColumn, TDataType, TIndex>, TTable extends DbTable<TTable, TColumn, TDataType, TIndex>, TColumn extends DbColumn<TTable, TColumn, TDataType, TIndex>, TDataType extends DbDataType<TTable, TColumn, TDataType, TIndex>, TIndex extends DbIndex<TTable, TColumn, TDataType, TIndex>>
		implements DbSync {

	@Override
	public String toString() {
		return "数据库结构维护器";
	}

	public final void sync(TableDefineImpl table) throws TableSyncException {
		this.utl.executed.clear();
		final long start = System.currentTimeMillis();
		final TableType tableType = table.getTableType();
		try {
			for (DBTableDefineImpl define : table.dbTables) {
				TTable dbtable = this.utl.loadTable(define.namedb());
				TableType loadTableType = this.utl.loadTableType(define.namedb());//获取数据库中表的类型
				define.setTableType(tableType);
				if (dbtable == null) {
					this.createTable(define);
				} else if (tableType.equals(TableType.NORMAL) && loadTableType.equals(TableType.NORMAL)){
					this.sync(define, dbtable, false);
				} else if (tableType.equals(TableType.GLOBAL_TEMPORARY) && loadTableType.equals(TableType.NORMAL)){
					// 不进行普通表类型转换成临时表，防止数据丢失
					this.sync(define, dbtable, false);
					throw new RuntimeException("不能把普通表\"" + table.name + "\"转换成全局临时表.");
				} else {
					this.utl.dropTableSilently(define.namedb());
					this.createTable(define);
				}
			}
		} catch (Throwable e) {
			if (e instanceof TableSyncException) {
				throw (TableSyncException) e;
			} else {
				throw new TableSyncException(table, e);
			}
		} finally {
			this.utl.logExecutedSql(true);
			if (ContextVariableIntl.PRINT_SYNC_TABLE) {
				ResolveHelper.logStartInfo("同步表," + table.name + "," + (System.currentTimeMillis() - start));
			}
		}
	}

	public final void post(TableDefineImpl post, TableDefineImpl runtime)
			throws TableSyncException {
		this.utl.executed.clear();
		final TableType tableType = post.getTableType();
		try {
			for (DBTableDefineImpl define : post.dbTables) {
				TTable dbtable = this.utl.loadTable(define.namedb());
				TableEmptyCache emptyCache = new TableEmptyCache(define.namedb());
				TableType loadTableType = this.utl.loadTableType(define.namedb());//获取数据库中标的类型
				define.setTableType(tableType);
				if (dbtable==null) {
					this.createTable(define);
				} else if (tableType.equals(TableType.NORMAL) && loadTableType.equals(TableType.NORMAL)){
					this.sync(define, dbtable, false);
				} else {
					this.utl.dropTableSilently(define.namedb());
					this.createTable(define);
				}
				if (!define.isPrimary() && !emptyCache.isEmpty()) {
					this.syncSlave(post.primary, define);
				}
			}
			this.dropUndefinedTableAndHierarchy(post, runtime);
		} catch (Throwable e) {
			if (e instanceof TableSyncException) {
				throw (TableSyncException) e;
			} else {
				throw new TableSyncException(post, e);
			}
		} finally {
			this.utl.logExecutedSql(false);
		}
	}

	private final void syncSlave(DBTableDefineImpl p, DBTableDefineImpl s) {
		ISqlInsertBuffer insert = this.dbMetadata.sqlbuffers().insert(s.namedb());
		insert.newField(TableDefineImpl.FIELD_DBNAME_RECID);
		ISqlSelectBuffer select = insert.select();
		select.newTableRef(p.namedb(), "o");
		select.newColumn("z").loadColumnRef("o", TableDefineImpl.FIELD_DBNAME_RECID);
		ISqlExprBuffer where = select.where();
		ISqlSelectBuffer exists = where.subquery();
		exists.newTableRef(s.namedb(), "i");
		exists.newColumn("m").load(1);
		exists.where().loadColumnRef("i", TableDefineImpl.FIELD_DBNAME_RECID).loadColumnRef("o", TableDefineImpl.FIELD_DBNAME_RECID).eq();
		where.predicate(SqlPredicate.EXISTS, 1).not();
		try {
			this.utl.execute(insert.build(null), SqlSource.CORE_DML);
		} catch (SQLException e) {
			throw new TableSyncException(p.owner, e);
		}
	}

	public final void drop(TableDefineImpl table) throws SQLException {
		try {
			this.utl.executed.clear();
			for (DBTableDefineImpl define : table.dbTables) {
				this.utl.dropTable(define.namedb());
			}
		} catch (Throwable e) {
			throw new TableSyncException(table, e);
		} finally {
			this.utl.logExecutedSql(true);
		}
	}

	public final void restore(TableDefineImpl table) throws SQLException {
		for (DBTableDefineImpl dbTable : table.dbTables) {
			this.utl.dropTableSilently(dbTable.namedb());
		}
		for (DBTableDefineImpl dbTable : table.dbTables) {
			if (dbTable.getPkeyName().length() > this.dbMetadata.getMaxIndexNameLength()) {
				dbTable.setPkeyName(NameUtl.build(dbTable.getPkeyName(), this.dbMetadata.databaseCharset(), this.dbMetadata.getMaxIndexNameLength(), null));
			}
			dbTable.setTableType(table.getTableType());//设置表类型
			this.utl.createTable(dbTable);
		}
	}

	public void unuse() {
		this.utl.dispose();
	}

	protected final TMetadata dbMetadata;

	protected final PooledConnection conn;

	protected DbSyncBase(PooledConnection conn, TMetadata dbMetadata,
			ExceptionCatcher catcher) {
		this.dbMetadata = dbMetadata;
		this.conn = conn;
		this.catcher = catcher;
		this.utl = this.newStuctCtl();
	}

	protected final TStructCtl utl;
	protected final ExceptionCatcher catcher;

	protected abstract TStructCtl newStuctCtl();

	final <T extends Appendable> T quoteId(T sql, String name) {
		this.dbMetadata.quoteId(sql, name);
		return sql;
	}

	void createTable(DBTableDefineImpl define) throws SQLException {
		this.ensureValid(define);
		this.utl.createTable(define);
		define.removeDuplicatedIndex();
		this.createIndexes(define);
	}

	final void dropUndefinedTableAndHierarchy(TableDefineImpl post,
			TableDefineImpl runtime) throws SQLException {
		for (DBTableDefineImpl t : runtime.dbTables) {
			final String name = t.namedb();
			if (post.dbTables.find(name) == null) {
				this.utl.dropTable(name);
			}
		}
	}

	final void createIndexes(DBTableDefineImpl define) throws SQLException {
		IndexDefineImpl lk = define.owner.logicalKey;
		if (define.isPrimary() && lk != null) {
			this.createIndex(lk);
		}
		for (IndexDefineImpl index : define.owner.indexes) {
			if (index.dbTable == define) {
				this.createIndex(index);
			}
		}
	}

	/**
	 * 创建索引，并更新命名空间
	 * 
	 * @param index
	 * @throws SQLException
	 */
	protected abstract void createIndex(IndexDefineImpl index)
			throws SQLException;

	/**
	 * 创建索引，更新命名空间，并增加新物理表的索引对象。
	 * 
	 * @param index
	 * @param dbtable
	 * @param forceNonUnqiue
	 * @throws SQLException
	 */
	protected abstract TIndex createIndex(IndexDefineImpl index,
			TTable dbtable, boolean forceNonUnqiue) throws SQLException;

	/**
	 * 删除索引，更新命名空间，更新物理表对象。
	 * 
	 * @param index
	 * @throws SQLException
	 */
	protected final void dropIndex(TIndex index) throws SQLException {
		this.utl.dropIndex(index);
	}

	final void ensureValid(DBTableDefineImpl dbTable) {
		final String namedb = dbTable.namedb();
		final Charset cs = this.dbMetadata.databaseCharset();
		if (NameUtl.length(namedb, cs) > 30) {
			throw new DbTableNameTooLongException(dbTable, 30);
		}
		if (this.dbMetadata.filterKeyword(namedb)) {
			System.err.println(TableSyncException.message(dbTable.owner) + DbTableNameReservedException.message(dbTable));
		}
		for (int i = 0, c = dbTable.owner.fields.size(); i < c; i++) {
			TableFieldDefineImpl field = dbTable.owner.fields.get(i);
			if (field.dbTable == dbTable) {
				this.ensureValid(field);
			}
		}
		if (dbTable.getPkeyName().length() > this.dbMetadata.getMaxIndexNameLength()) {
			dbTable.setPkeyName(NameUtl.build(dbTable.getPkeyName(), cs, this.dbMetadata.getMaxIndexNameLength(), null));
		}
	}

	/**
	 * 确保字段定义的namedb合法.
	 * 
	 * <p>
	 * 重复性已经由map保证;检查是否为关键字;检查是否超过数据库长度限制.
	 * 
	 * @param field
	 * @return 定义是否发生修改
	 */
	final boolean ensureValid(final TableFieldDefineImpl field) {
		final Charset cs = this.dbMetadata.databaseCharset();
		final int maxlen = this.dbMetadata.getMaxColumnNameLength();
		final String namedb = field.namedb();
		if (NameUtl.length(namedb, cs) > maxlen || this.dbMetadata.filterKeyword(namedb)) {
			this.adjustNamedb(field);
			return true;
		}
		return false;
	}

	final void adjustNamedb(final TableFieldDefineImpl field) {
		field.setNamedb(NameUtl.build(field.namedb(), this.dbMetadata.databaseCharset(), this.dbMetadata.getMaxColumnNameLength(), new Filter<String>() {
			public boolean accept(String item) {
				return field.dbTable.fields.containsKey(item) || DbSyncBase.this.dbMetadata.filterKeyword(item);
			}
		}));
	}

	protected static final boolean ensureValidAsSchemaObject(
			final IndexDefineImpl index, final DbMetadata dbMetadata,
			final DbNamespace namespace) {
		final String namedb = index.namedb();
		final int maxlen = dbMetadata.getMaxIndexNameLength();
		final Charset cs = dbMetadata.databaseCharset();
		if (NameUtl.length(namedb, cs) > maxlen || namespace.contains(namedb) || dbMetadata.filterKeyword(namedb)) {
			final String rebuild = NameUtl.build(namedb, cs, maxlen, new Filter<String>() {
				public boolean accept(String item) {
					return namespace.contains(item) || dbMetadata.filterKeyword(item);
				}
			});
			index.setNamedb(rebuild);
			return true;
		}
		return false;
	}

	protected final boolean ensureValid(final IndexDefineImpl index,
			final TTable dbtable) {
		final String namedb = index.namedb();
		final int maxlen = this.dbMetadata.getMaxIndexNameLength();
		final Charset cs = this.dbMetadata.databaseCharset();
		if (NameUtl.length(namedb, cs) > maxlen || this.dbMetadata.filterKeyword(index.namedb()) || dbtable.indexes.containsKey(index.namedb())) {
			String rename = NameUtl.build(namedb, cs, maxlen, new Filter<String>() {
				public boolean accept(String item) {
					return DbSyncBase.this.dbMetadata.filterKeyword(item) || dbtable.indexes.containsKey(item);
				}
			});
			index.setNamedb(rename);
			return true;
		}
		return false;
	}

	public final class AddFieldState {

		@Override
		public final String toString() {
			return this.field.namedb();
		}

		public final TableFieldDefineImpl field;

		/**
		 * false表示使用field定义的为空属性,true表示强制设置字段可为空.
		 */
		public final boolean forceNullable;

		AddFieldState(TableFieldDefineImpl field, boolean forceNullable) {
			this.field = field;
			this.forceNullable = forceNullable;
		}
	}

	public final class ModifyFieldState {

		public final TableFieldDefineImpl field;
		public final TColumn column;

		ModifyFieldState(TableFieldDefineImpl field, TColumn column) {
			this.field = field;
			this.column = column;
		}

		@Override
		public final String toString() {
			return this.field.namedb();
		}

		/**
		 * 列所需要修改的属性.
		 */
		int state;

		final void set(int mod) {
			this.state |= mod;
		}

		public final boolean get(int mod) {
			return (this.state & mod) != 0;
		}

	}

	final void sync(DBTableDefineImpl define, TTable dbtable, boolean post)
			throws SQLException {
		TableEmptyCache emptyCache = new TableEmptyCache(define.namedb());
		try {
			this.compareColumns(define, dbtable, post, this.compareCache, emptyCache);
			this.refactorColumnes(define, dbtable, this.compareCache);
		} finally {
			this.compareCache.reset();
		}
		try {
			this.utl.loadIndexes(dbtable);
			this.syncIndexes(define, dbtable, post, emptyCache);
		} finally {
			this.definedIndexes.clear();
		}
	}

	final void compareColumns(final DBTableDefineImpl define,
			final TTable dbtable, final boolean post,
			ColumnCompareCache compareCache, TableEmptyCache emptyCache) {
		final boolean tolerateUnsupportedNulls = !post;
		if (post) {
			for (TColumn column : dbtable.columns) {
				if (column.isRecid() || column.isRecver()) {
					continue;
				}
				final TableFieldDefineImpl field = define.fields.find(column.name);
				if (field == null) {
					TableFieldDefineImpl across = define.owner.findFieldUsingNamedb(column.name);
					if (emptyCache.isEmpty() || across == null) {
						compareCache.dropQueue.add(column);
					} else {
						throw new UnsupportedOperationException("表发布在[" + define.owner.name + "." + define.name + "],字段定义物理名在物理表[" + across.name + "]上已经存在,不支持跨物理表增加同名字段.");
					}
				} else {
					final TypeCompatiblity compatible = this.utl.typeCompatible(field, column);
					final TypeAlterability alterable = column.typeAlterable(field.getType());
					switch (compatible) {
					case Exactly:
					case Overflow:
					case NotSuggest:
						continue;
					case Unable: {
						if (this.supportsModifyColumnType(dbtable, alterable, emptyCache)) {
							continue;
						} else if (emptyCache.isEmpty()) {
							compareCache.dropQueue.add(column);
						} else {
							throw new UnsupportedTypeConversionException(field, column.typeString());
						}
					}
					}
				}
			}
		}
		compareNext: for (TableFieldDefineImpl field : define.owner.fields) {
			for (; field.dbTable == define || field.isRECID();) {
				TColumn column = dbtable.findColumn(field.namedb());
				if (compareCache.dropQueue.contains(column) || column == null) {
					if (DbSyncBase.this.ensureValid(field)) {
						continue;
					} else {
						compareCache.enqueueAddFieldCheckNullable(dbtable, field, tolerateUnsupportedNulls, emptyCache);
						continue compareNext;
					}
				} else {
					final TypeCompatiblity compatible = this.utl.typeCompatible(field, column);
					final TypeAlterability alterable = column.typeAlterable(field.getType());
					switch (compatible) {
					case NotSuggest:
						if (this.supportsModifyColumnType(dbtable, alterable, emptyCache)) {
							compareCache.enqueueModifyField(field, column).set(MOD_TYPE);
						}
					case Exactly:
					case Overflow:
						this.compareMatchedField(field, column, tolerateUnsupportedNulls, compareCache, emptyCache);
						continue compareNext;
					case Unable: {
						if (this.supportsModifyColumnType(dbtable, alterable, emptyCache) || this.tryExtendNumericPrecision(field, column, post)) {
							compareCache.enqueueModifyField(field, column).set(MOD_TYPE);
							this.compareMatchedField(field, column, tolerateUnsupportedNulls, compareCache, emptyCache);
						} else if (emptyCache.isEmpty()) {
							compareCache.dropQueue.add(column);
							compareCache.enqueueAddFieldCheckNullable(dbtable, field, false, emptyCache);
						} else if (this.dbMetadata.supportsRenameColumn()) {
							// 不能修改类型,表也不为空,可以修改列名,则重命令到废弃.
							compareCache.unuseQueue.add(column);
							compareCache.enqueueAddFieldCheckNullable(dbtable, field, tolerateUnsupportedNulls, emptyCache);
						} else {
							// 不能修改类型,表也不为空,也不能修改列名,则调整新增字段的名称.
							compareCache.unuseQueue.add(column);
							DbSyncBase.this.adjustNamedb(field);
						}
					}
					}
					continue compareNext;
				}
			}
		}
	}

	/**
	 * 比较匹配字段的为空属性和默认值属性
	 * 
	 * <p>
	 * 对于启动同步,需要尽量容忍异常,保证表定义的加载成功,则需要忽略为空属性.
	 * 
	 * @param field
	 * @param column
	 * @param tolerateUnsupportedNulls
	 *            当新增非空字段或修改字段非空不能支持时,是否忽略为空属性.
	 */
	final void compareMatchedField(TableFieldDefineImpl field, TColumn column,
			boolean tolerateUnsupportedNulls, ColumnCompareCache compareCache,
			TableEmptyCache emptyCache) {
		if (field.isKeepValid() != column.notNull) {
			if (field.isKeepValid()) {
				if (this.supportsModifyColumnToNotNull(field, column, emptyCache)) {
					compareCache.enqueueModifyField(field, column).set(MOD_NULLABLE);
				} else if (tolerateUnsupportedNulls) {
					System.err.println(TableSyncException.message(field.owner) + UnsupportedNotNullFieldException.message(field, false));
				} else {
					throw new UnsupportedNotNullFieldException(field, false);
				}
			} else {
				compareCache.enqueueModifyField(field, column).set(MOD_NULLABLE);
			}
		}
		if (this.utl.defaultChanged(field, column)) {
			compareCache.enqueueModifyField(field, column).set(MOD_DEFAULT);
		}
	}

	protected final ColumnCompareCache compareCache = new ColumnCompareCache();

	protected final boolean supportsModifyColumnType(TTable dbtable,
			TypeAlterability alterable, TableEmptyCache emptyCache) {
		return alterable == TypeAlterability.Always || (alterable == TypeAlterability.ColumnNull && emptyCache.isEmpty());
	}

	/**
	 * 能否将列修改为不可为空
	 * 
	 * <p>
	 * 当包含默认值,表为空,列不包含null值时返回true.
	 * 
	 * @param field
	 * @param column
	 * @return
	 */
	final boolean supportsModifyColumnToNotNull(TableFieldDefineImpl field,
			TColumn column, TableEmptyCache emptyCache) {
		try {
			if (emptyCache.isEmpty()) {
				return true;
			} else if (this.utl.columnContainNull(column)) {
				return false;
			}
			return true;
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	protected abstract boolean tryExtendNumericPrecision(
			TableFieldDefineImpl field, TColumn column, boolean post);

	protected final boolean tryExtendNumericPrecisionRefered(
			TableFieldDefineImpl field, TColumn column, TDataType numeric,
			boolean post) {
		if (post) {
			return false;
		}
		if (field.getType() instanceof NumericDBType && column.type == numeric) {
			NumericDBType nt = (NumericDBType) field.getType();
			int maxScale = Math.max(nt.scale, column.scale);
			int maxDecimal = Math.max(nt.precision - nt.scale, column.precision - column.scale);
			if (maxScale <= 30 && (maxDecimal + maxScale) <= 31) {
				field.adjustType(TypeFactory.NUMERIC(maxDecimal + maxScale, maxScale));
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据比较结果,执行新增,修改,废弃列等操作
	 * 
	 * @throws SQLException
	 */
	protected abstract void refactorColumnes(DBTableDefineImpl define,
			TTable dbtable, ColumnCompareCache compareCache)
			throws SQLException;

	/**
	 * 需要修改类型
	 */
	public static final int MOD_TYPE = 1 << 0;

	/**
	 * 需要修改默认值
	 */
	public static final int MOD_DEFAULT = 1 << 1;

	/**
	 * 需要修改为空属性
	 */
	public static final int MOD_NULLABLE = 1 << 2;

	static final String UNUSED_COLUMN_PREFIX = "UNUSED_";

	/**
	 * 重命名废弃字段,对应的定义字段添加到新增字段列表
	 * 
	 * <p>
	 * 在处理add列表前调用
	 * 
	 * @throws SQLException
	 */
	protected final void renameUnuseColumns(final DBTableDefineImpl define,
			final TTable dbtable, ColumnCompareCache compareCache)
			throws SQLException {
		for (TColumn column : compareCache.unuseQueue) {
			String rename = UNUSED_COLUMN_PREFIX.concat(column.name);
			final int maxlen = DbSyncBase.this.dbMetadata.getMaxColumnNameLength();
			Filter<String> filter = new Filter<String>() {

				public boolean accept(String item) {
					// 列名称在定义表以及数据库表中都不存在
					return dbtable.columns.containsKey(item) || define.fields.containsKey(item);
				}
			};
			if (rename.length() > maxlen || filter.accept(rename)) {
				rename = NameUtl.buildIdentityName(rename, maxlen, filter);
			}
			this.utl.renameColumnAndSetNullable(column, rename);
			dbtable.columns.remove(column.name);
			column.name = rename;
			dbtable.columns.put(rename, column);
		}
	}

	public final class ColumnCompareCache {

		final void reset() {
			this.dropQueue.clear();
			this.addQueue.clear();
			this.modifyQueue.clear();
			this.unuseQueue.clear();
			this.indexesCache.clear();
		}

		/**
		 * 删除列,必须在addQueue之前处理
		 */
		public final ArrayList<TColumn> dropQueue = new ArrayList<TColumn>();

		/**
		 * 新增列
		 */
		public final ArrayList<AddFieldState> addQueue = new ArrayList<AddFieldState>();

		/**
		 * 修改列
		 */
		public final LinkedHashMap<String, ModifyFieldState> modifyQueue = new LinkedHashMap<String, ModifyFieldState>();

		/**
		 * 废弃列,必须在addQueue之前处理
		 */
		public final ArrayList<TColumn> unuseQueue = new ArrayList<TColumn>();

		/**
		 * 数据库索引缓存.暂存需要删除的索引.
		 */
		public final ArrayList<TIndex> indexesCache = new ArrayList<TIndex>();

		final void enqueueAddFieldCheckNullable(TTable dbtable,
				TableFieldDefineImpl field, boolean tolerateUnsupportedNulls,
				TableEmptyCache emptyCache) {
			if (field.isKeepValid() && field.getDefault() == null) {
				if (emptyCache.isEmpty() && DbSyncBase.this.dbMetadata.supportsAddNotNullColumnWithoutDefaultOnEmptyTable()) {
					this.enqueueAddField(field, false);
				} else if (tolerateUnsupportedNulls) {
					this.enqueueAddField(field, true);
				} else {
					throw new UnsupportedNotNullFieldException(field, true);
				}
			} else {
				this.enqueueAddField(field, false);
			}
		}

		final void enqueueAddField(TableFieldDefineImpl field,
				boolean forceNullable) {
			AddFieldState state = new AddFieldState(field, forceNullable);
			this.addQueue.add(state);
		}

		final ModifyFieldState enqueueModifyField(TableFieldDefineImpl field,
				TColumn column) {
			if (!field.namedb().equals(column.name)) {
				throw new IllegalStateException();
			}
			ModifyFieldState state = this.modifyQueue.get(column.name);
			if (state == null) {
				state = new ModifyFieldState(field, column);
				this.modifyQueue.put(column.name, state);
			}
			return state;
		}

	}

	static final String NONE_DNA_INDEX_PREFIX = "UIX_";

	/**
	 * 需要保留的物理表索引
	 */
	final HashMap<TIndex, IndexDefineImpl> definedIndexes = new HashMap<TIndex, IndexDefineImpl>();

	final void syncIndexes(DBTableDefineImpl define, TTable dbtable,
			boolean post, TableEmptyCache emptyCache) throws SQLException {
		this.definedIndexes.clear();
		if (dbtable.primaryKey == null) {
			IndexDefineImpl ixRecid = new IndexDefineImpl(define.owner, define, define.getPkeyName(), IndexType.B_TREE);
			ixRecid.setUnique(true);
			ixRecid.addItem(define.owner.f_recid);
			TIndex find = dbtable.findStructEqualIndex(ixRecid);
			if (find != null) {
				this.definedIndexes.put(find, ixRecid);
				// HCL 修改索引为主键约束
			} else {
				this.syncIndex(ixRecid, dbtable, post, emptyCache);
			}
		} else {
			define.setPkeyName(dbtable.primaryKey.name);
		}
		IndexDefineImpl lk = define.owner.logicalKey;
		if (define.isPrimary() && lk != null) {
			this.syncIndex(lk, dbtable, post, emptyCache);
		}
		for (IndexDefineImpl left : define.owner.indexes) {
			if (left.dbTable == define) {
				this.syncIndex(left, dbtable, post, emptyCache);
			}
		}
		for (int i = dbtable.indexes.size() - 1; i >= 0; i--) {
			TIndex index = dbtable.indexes.get(i);
			if (index.isPrimaryKey()) {
				continue;
			}
			if (define.isPrimary() && lk != null && index.name.equals(lk.namedb())) {
				continue;
			}
			if (index.name.startsWith(NONE_DNA_INDEX_PREFIX)) {
				continue;
			}
			if (this.definedIndexes.containsKey(index)) {
				continue;
			}
			DbSyncBase.this.dropIndex(index);
		}
	}

	/**
	 * 创建结构不存在的索引,或者重新映射结构相同的索引.
	 * 
	 * @param index
	 * @param dbtable
	 * @param emptyCache
	 * @throws SQLException
	 */
	private final void syncIndex(IndexDefineImpl index, TTable dbtable,
			boolean post, TableEmptyCache emptyCache) throws SQLException {
		if (index.getType() == IndexType.BITMAP) {
			if (this.dbMetadata.supportesBitmapIndex()) {
				TIndex byStruct = dbtable.findStructEqualIndex(index);
				if (byStruct == null) {
					TIndex create = this.createIndex(index, dbtable, false);
					this.definedIndexes.put(create, index);
				} else if (!byStruct.bitmap) {
					this.dropIndex(byStruct);
					dbtable.indexes.remove(byStruct.name);
					TIndex create = this.createIndex(index, dbtable, false);
					this.definedIndexes.put(create, index);
				} else {
					this.definedIndexes.put(byStruct, index);
				}
			} else {
				return;
			}
		} else {
			TIndex byStruct = dbtable.findStructEqualIndex(index);
			if (byStruct != null) {
				if (this.definedIndexes.containsKey(byStruct)) {
					// HCL 已经被其他索引定义关联，即定义重复
				} else if (byStruct.bitmap) {
					this.dropIndex(byStruct);
					dbtable.indexes.remove(byStruct.name);
					TIndex create = this.createIndex(index, dbtable, false);
					this.definedIndexes.put(create, index);
				} else if (index.isUnique() != byStruct.unique) {
					if (emptyCache.isEmpty()) {
						this.dropIndex(byStruct);
						dbtable.indexes.remove(byStruct.name);
						TIndex create = this.createIndex(index, dbtable, false);
						this.definedIndexes.put(create, index);
					} else if (index.isUnique()) {
						if (this.utl.indexValueDuplicated(index)) {
							if (post) {
								throw new UniqueIndexValueDuplicatedException(index);
							} else {
								System.err.println(UniqueIndexValueDuplicatedException.message0(index));
							}
						} else {
							this.dropIndex(byStruct);
							dbtable.indexes.remove(byStruct.name);
							TIndex create = this.createIndex(index, dbtable, false);
							this.definedIndexes.put(create, index);
						}
					}
				} else {
					index.setNamedb(byStruct.name);
					this.definedIndexes.put(byStruct, index);
				}
			} else if (index.isUnique() && !emptyCache.isEmpty() && this.utl.indexValueDuplicated(index)) {
				if (post) {
					throw new UniqueIndexValueDuplicatedException(index);
				} else {
					System.err.println(UniqueIndexValueDuplicatedException.message0(index));
					this.createIndex(index, dbtable, true);
				}
			} else {
				TIndex create = this.createIndex(index, dbtable, false);
				this.definedIndexes.put(create, index);
			}
		}
	}

	protected final void execSqls(Class<?> locator, String resource,
			boolean replace) {
		String[] sqls = null;
		try {
			InputStream is = locator.getResourceAsStream(resource);
			if (is == null) {
				this.dbInitErr("dna包的安装配置文件[" + resource + "]不存在。");
				return;
			}
			try {
				sqls = Strings.readLines(is, Charsets.GBK);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			this.dbInitErr("读取dna包的安装配置文件[" + resource + "]错误。", e);
			return;
		}
		if (sqls == null || sqls.length == 0) {
			return;
		}
		for (String sql : sqls) {
			String ddl = null;
			try {
				InputStream is = locator.getResourceAsStream(sql);
				if (is == null) {
					this.dbInitErr("dna包的安装脚本[" + sql + "]不存在。");
					continue;
				}
				try {
					ddl = Strings.readString(is, Charsets.GBK);
				} finally {
					is.close();
				}
			} catch (IOException e) {
				this.dbInitErr("读取dna包的安装脚本[" + sql + "]错误。", e);
				continue;
			}
			if (ddl == null || ddl.trim().length() == 0) {
				continue;
			}
			if (replace) {
				ddl = ddl.replace("\r", "");
			}
			try {
				this.utl.execute(ddl, SqlSource.CORE_DDL);
			} catch (SQLException e) {
				this.dbInitErr("执行dna包的安装脚本[" + sql + "]错误。", e);
				continue;
			}
		}
	}

	protected final void dbInitErr(String message) {
		this.catcher.catchException(new DbInstanceInitializationException(message), this);
	}

	protected final void dbInitErr(String message, Throwable th) {
		this.catcher.catchException(new DbInstanceInitializationException(message, th), this);
	}

	final class TableEmptyCache {

		private TableEmptyCache(String table) {
			this.table = table;
		}

		final String table;
		private boolean checked;
		private boolean isEmpty;

		final boolean isEmpty() {
			if (this.checked) {
				return this.isEmpty;
			}
			this.checked = true;
			try {
				return this.isEmpty = !DbSyncBase.this.utl.tableContainRows(this.table);
			} catch (SQLException e) {
				throw Utils.tryThrowException(e);
			}
		}
	}

	public final TableDefineImpl synchroTableDefine(String tableName,
			String title, String category) {
		TableDefineImpl define = this.utl.synchroTableDefine(tableName, title, category);
		return define;
	}
}

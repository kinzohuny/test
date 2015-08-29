package com.jiuqi.dna.core.internal.db.monitor;

import static com.jiuqi.dna.core.da.DbProduct.Oracle;

import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.da.DbProduct;
import com.jiuqi.dna.core.da.DbProduct.Callee;
import com.jiuqi.dna.core.db.monitor.VariationContext;
import com.jiuqi.dna.core.db.monitor.VariationMonitor;
import com.jiuqi.dna.core.db.monitor.VariationMonitorCreateTask;
import com.jiuqi.dna.core.db.monitor.VariationMonitorLockTargetTask;
import com.jiuqi.dna.core.db.monitor.VariationMonitorModifyTask;
import com.jiuqi.dna.core.db.monitor.VariationMonitorRemoveTask;
import com.jiuqi.dna.core.def.MetaElementType;
import com.jiuqi.dna.core.def.table.DBTableDeclare;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.TableDefineBroker;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.db.datasource.SqlSource;
import com.jiuqi.dna.core.internal.db.datasource.StatementWrap;
import com.jiuqi.dna.core.invoke.AsyncTask;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.log.Logger;
import com.jiuqi.dna.core.misc.MissingObjectException;
import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceService;
import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.service.AsyncInfo;
import com.jiuqi.dna.core.service.AsyncInfo.SessionMode;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.spi.def.DeclarePostTask;
import com.jiuqi.dna.core.spi.def.DeclareRemoveTask;
import com.jiuqi.dna.core.type.GUID;

final class VariationMonitorService
		extends
		ResourceService<VariationMonitor, VariationMonitorImpl, VariationMonitorImpl> {

	VariationMonitorService(VariationMonitorORM monitorORM) {
		super("监视器服务");
		this.monitorORM = monitorORM;
	}

	private static final Logger logger = DNALogManager.getLogger("core/db/monitor");

	final VariationMonitorORM monitorORM;

	@Override
	protected final void init(Context context) throws Throwable {
		context.ensureResourceInited(VariationMonitor.class);
	}

	@Override
	protected void initResources(
			Context context,
			ResourceInserter<VariationMonitor, VariationMonitorImpl, VariationMonitorImpl> initializer)
			throws Throwable {
		if (!context.isDBAccessible()) {
			return;
		}
		final boolean support;
		try {
			support = context.dbProduct().callback(SUPPORTS, null);
		} catch (Throwable e) {
			return;
		}
		final List<VariationMonitorMetadata> list = context.newORMAccessor(this.monitorORM).fetch();
		for (VariationMonitorMetadata mmd : list) {
			if (!support) {
				logger.logError(null, "当前数据库类型[" + context.dbProduct() + "]不支持变化量监视器，未能加载变化量监视器[" + mmd.name + "]。", false);
				continue;
			}
			final VariationMonitorImpl monitor;
			try {
				monitor = VariationMonitorImpl.load(context, mmd);
			} catch (Throwable e) {
				logger.logError(null, "加载变化量监视器[" + mmd.name + "]时发生异常。", e, false);
				continue;
			}
			initializer.putResource(monitor);
			StringBuilder info = new StringBuilder();
			info.append("从持久化元数据装载监视器[" + monitor.name + "]\n");
			info.append("目标表:" + monitor.target + "\n");
			info.append("变化量:" + monitor.variation + "\n");
			info.append("触发器:" + monitor.trigger + "\n");
			for (VariationMonitorFieldImpl mf : monitor.watches) {
				info.append(mf.toString() + "\n");
			}
			logger.logDebug(null, info.toString(), false);
		}
	}

	@Publish
	protected final class ById extends OneKeyResourceProvider<GUID> {

		@Override
		protected GUID getKey1(VariationMonitorImpl keysHolder) {
			return keysHolder.id;
		}
	}

	@Publish
	protected final class ByName extends OneKeyResourceProvider<String> {

		@Override
		protected String getKey1(VariationMonitorImpl keysHolder) {
			return keysHolder.name;
		}
	}

	private static final AsyncInfo ASYNC_INFO_FOR_DDL = new AsyncInfo(SessionMode.SAME);

	private static final class CreateMonitorException extends RuntimeException {

		private static final long serialVersionUID = 4565415947823686134L;

		static final String prefix(String monitor) {
			return "创建监视器[" + monitor + "]时错误：";
		}

		private CreateMonitorException(String monitor, String message) {
			super(prefix(monitor) + message);
		}

		private CreateMonitorException(String monitor, String message,
				Throwable cause) {
			super(prefix(monitor) + message, cause);
		}

		private CreateMonitorException(String monitor, Throwable cause) {
			super(prefix(monitor) + "未知异常。", cause);
		}

		private CreateMonitorException(VariationMonitorImpl monitor,
				String message, Throwable cause) {
			super(prefix(monitor.getName()) + message, cause);
		}
	}

	@Publish
	protected final class Create extends
			SimpleTaskMethodHandler<VariationMonitorCreateTask> {

		@Override
		protected final void handle(
				ResourceContext<VariationMonitor, VariationMonitorImpl, VariationMonitorImpl> context,
				VariationMonitorCreateTask task) throws Throwable {
			VariationMonitorCreatingLockService.critical(context);
			if (context.find(VariationMonitor.class, task.name) != null) {
				throw new CreateMonitorException(task.name, "重复的名称。");
			}
			ResourceToken<TableDefineBroker> token = context.findResourceToken(TableDefineBroker.class, task.target.getName());
			if (token == null) {
				throw new CreateMonitorException(task.name, "指定的监视目标表[" + task.target.getName() + "]不存在。");
			}
			// XXX DNA的读锁有Bug。这里就不考虑存在并发删除逻辑表的可能性了。
			context.lockResourceS(token);
			final TableDefineImpl target = (TableDefineImpl) context.find(TableDefine.class, task.target.getName());
			if (context.find(TableDefine.class, task.variation) != null) {
				throw new CreateMonitorException(task.name, "指定的变化量表名称为[" + task.variation + "]，但已经存在相同名称的表定义。");
			}
			final Boolean support = context.dbProduct().callback(SUPPORTS, null);
			if (support == null || support.booleanValue() == false) {
				throw new CreateMonitorException(task.name, "当前监视器框架不支持的数据库[" + context.dbProduct().name() + "]。");
			}
			final VariationMonitorImpl monitor = new VariationMonitorImpl(context.newRECID(), task.name, target.name, task.variation, task.trigger);
			try {
				monitor.initializeWatches(task.getWatches());
			} catch (Throwable e) {
				throw new CreateMonitorException(monitor, "监视字段配置错误。", e);
			}
			try {
				final TableDefineImpl variation;
				try {
					variation = VariationStruct.build(context, monitor, target);
				} catch (Throwable e) {
					throw new CreateMonitorException(monitor, "监视字段配置错误。", e);
				}
				try {
					asyncPostVariation(context, monitor, variation);
				} catch (Throwable e) {
					throw new CreateMonitorException(monitor, "创建变化量表[" + monitor.variation + "]时错误。", e);
				}
				try {
					try {
						asyncCreateTriggerCheckState(context, monitor, target, variation);
					} catch (Throwable e) {
						throw new CreateMonitorException(monitor, "创建触发器时错误。", e);
					}
					try {
						monitor.control = new VariationControl(monitor, target, variation);
						context.newORMAccessor(VariationMonitorService.this.monitorORM).insert(monitor.getMetadata());
						context.putResource(monitor);
						task.monitor = monitor;
					} catch (Throwable e) {
						this.asyncTryRemoveTrigger(context, monitor);
						throw e;
					}
				} catch (Throwable e) {
					this.asyncTryRemoveVariation(context, monitor);
					throw e;
				}
			} catch (CreateMonitorException e) {
				throw e;
			} catch (Throwable th) {
				throw new CreateMonitorException(task.name, th);
			}
		}

		private final void asyncTryRemoveTrigger(Context context,
				VariationMonitorImpl monitor) {
			try {
				asyncRemoveTrigger(context, monitor);
			} catch (Throwable e) {
				logger.logFatal(context, "在创建监视器[" + monitor.name + "]过程中发生错误，尝试清理已经创建的数据库触发器[" + monitor.trigger + "]时仍然发生了异常。", e, false);
			}
		}

		private final void asyncTryRemoveVariation(Context context,
				VariationMonitorImpl monitor) {
			try {
				asyncRemoveVariation(context, monitor);
			} catch (Throwable e) {
				logger.logFatal(context, "在创建监视器[" + monitor.name + "]过程中发生错误，尝试清理已经创建的变化量表仍然发生了异常。", e, false);
			}
		}
	}

	private static final void asyncPostVariation(Context context,
			VariationMonitorImpl monitor, TableDefineImpl variation) {
		final DeclarePostTask post = new DeclarePostTask(variation);
		final AsyncTask<DeclarePostTask, None> async = context.asyncHandle(post, ASYNC_INFO_FOR_DDL);
		try {
			context.waitFor(async);
		} catch (InterruptedException e) {
			throw new CreateMonitorException(monitor.name, "发布变化量表时由于超时而被中断。", e);
		}
		if (async.getException() != null) {
			throw new CreateMonitorException(monitor.name, "发布变化量表时错误。", async.getException());
		}
	}

	private static final void asyncRemoveVariation(Context context,
			VariationMonitorImpl monitor) throws Throwable {
		final DeclareRemoveTask task = new DeclareRemoveTask(MetaElementType.TABLE, monitor.variation, true);
		final AsyncTask<DeclareRemoveTask, None> async = context.asyncHandle(task, ASYNC_INFO_FOR_DDL);
		context.waitFor(async);
		if (async.getException() != null) {
			throw async.getException();
		}
	}

	private static final void asyncCreateTriggerCheckState(Context context,
			VariationMonitorImpl monitor, TableDefineImpl target,
			TableDefineImpl variation) throws Throwable {
		final CreateMonitorTriggerTask create = new CreateMonitorTriggerTask(monitor, target, variation);
		final AsyncTask<CreateMonitorTriggerTask, None> async = context.asyncHandle(create, ASYNC_INFO_FOR_DDL);
		try {
			context.waitFor(async);
		} catch (InterruptedException e) {
			throw e;
		}
		if (async.getException() != null) {
			throw async.getException();
		}
	}

	private static final void asyncRemoveTrigger(Context context,
			VariationMonitorImpl monitor) throws Throwable {
		final TryRemoveTriggerTask task = new TryRemoveTriggerTask(monitor.trigger);
		final AsyncTask<TryRemoveTriggerTask, None> async = context.asyncHandle(task, ASYNC_INFO_FOR_DDL);
		context.waitFor(async);
		if (async.getException() != null) {
			throw async.getException();
		}
	}

	private static final class CreateMonitorTriggerTask extends SimpleTask {

		private final VariationMonitorImpl monitor;
		private final TableDefineImpl target;
		private final TableDefineImpl variation;
		private final TableFieldDefineImpl date;
		private final TableFieldDefineImpl operation;
		private final TableFieldDefineImpl version;

		private CreateMonitorTriggerTask(VariationMonitorImpl monitor,
				TableDefineImpl target, TableDefineImpl variation) {
			this.monitor = monitor;
			this.target = target;
			this.variation = variation;
			this.date = variation.getColumn(VariationStruct.VAR_DATE);
			this.operation = variation.getColumn(VariationStruct.VAR_OPERATION);
			this.version = variation.getColumn(VariationStruct.VAR_VERSION);
		}
	}

	@Publish
	protected final class PrivateCreateTrigger extends
			SimpleTaskMethodHandler<CreateMonitorTriggerTask> {

		@Override
		protected void handle(
				ResourceContext<VariationMonitor, VariationMonitorImpl, VariationMonitorImpl> context,
				CreateMonitorTriggerTask task) throws Throwable {
			final DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
			final StatementWrap stmt = adapter.createStatement();
			try {
				String createSql = context.dbProduct().callback(CREATE_TRIGGER, task);
				if (createSql == null || createSql.length() == 0) {
					throw new IllegalStateException();
				}
				stmt.execute(createSql, SqlSource.USER_DDL);
				// TODO check status, not invalid
			} finally {
				adapter.freeStatement(stmt);
			}
		}
	}

	private static final DbProduct.Callee<Object, Boolean> SUPPORTS = new DbProduct.Callee<Object, Boolean>() {

		@Override
		public Boolean onOracle(Object data) throws Throwable {
			return true;
		}

		@Override
		public Boolean onDB2(Object data) throws Throwable {
			return false;
		}

		@Override
		public Boolean onSQLServer(Object data) throws Throwable {
			return false;
		}

		@Override
		public Boolean onHana(Object data) throws Throwable {
			return false;
		}

		@Override
		public Boolean onMySQL(Object data) throws Throwable {
			return false;
		}

		@Override
		public Boolean onPostgre(Object data) throws Throwable {
			return false;
		}

		@Override
		public Boolean onUnknown(Object data) throws Throwable {
			return false;
		}
	};

	private static final DbProduct.Callee<CreateMonitorTriggerTask, String> CREATE_TRIGGER = new DbProduct.Callee<CreateMonitorTriggerTask, String>() {

		@Override
		public String onOracle(CreateMonitorTriggerTask task) throws Throwable {
			final StringBuilder sql = new StringBuilder();
			sql.append("create or replace trigger ");
			Oracle.quote(sql, task.monitor.trigger);
			sql.append(" after insert or delete or update on ");
			final DBTableDeclare p = task.target.primary;
			Oracle.quote(sql, p.getNameInDB());
			sql.append(" for each row declare tid number;begin dna.current_transaction_id(tid);if inserting then ");
			this.insert(sql, task, Oracle);
			for (VariationMonitorFieldImpl mf : task.monitor.watches) {
				sql.append(',');
				Oracle.quote(sql, task.variation.getColumn(mf.newValueFN).namedb());
			}
			sql.append(") values (dna.new_recid, sysdate, tid, 'I'");
			for (VariationMonitorFieldImpl mf : task.monitor.watches) {
				sql.append(",:NEW.");
				Oracle.quote(sql, task.target.getColumn(mf.watchFN).namedb());
			}
			sql.append("); elsif deleting then ");
			this.insert(sql, task, Oracle);
			for (VariationMonitorFieldImpl mf : task.monitor.watches) {
				sql.append(',');
				Oracle.quote(sql, task.variation.getColumn(mf.oldValueFN).namedb());
			}
			sql.append(") values (dna.new_recid, sysdate, tid, 'D'");
			for (VariationMonitorFieldImpl mf : task.monitor.watches) {
				sql.append(",:OLD.");
				Oracle.quote(sql, task.target.getColumn(mf.watchFN).namedb());
			}
			sql.append("); else ");
			this.insert(sql, task, Oracle);
			for (VariationMonitorFieldImpl mf : task.monitor.watches) {
				sql.append(',');
				Oracle.quote(sql, task.variation.getColumn(mf.oldValueFN).namedb());
				sql.append(',');
				Oracle.quote(sql, task.variation.getColumn(mf.newValueFN).namedb());
			}
			sql.append(") values (dna.new_recid, sysdate, tid, 'U'");
			for (VariationMonitorFieldImpl mf : task.monitor.watches) {
				sql.append(",:OLD.");
				Oracle.quote(sql, task.target.getColumn(mf.watchFN).namedb());
				sql.append(",:NEW.");
				Oracle.quote(sql, task.target.getColumn(mf.watchFN).namedb());
			}
			sql.append("); end if;end;");
			return sql.toString();
		}

		private final void insert(StringBuilder sql,
				CreateMonitorTriggerTask task, DbProduct db) {
			sql.append("insert into ");
			db.quote(sql, task.variation.primary.namedb());
			sql.append('(');
			db.quote(sql, task.variation.f_recid.namedb());
			sql.append(',');
			db.quote(sql, task.date.namedb());
			sql.append(',');
			db.quote(sql, task.version.namedb());
			sql.append(',');
			db.quote(sql, task.operation.namedb());
		}
	};

	private static final DbProduct.Callee<String, String> DROP_TRIGGER = new DbProduct.Callee<String, String>() {

		@Override
		public String onOracle(String trigger) throws Throwable {
			StringBuilder sql = new StringBuilder();
			sql.append("drop trigger ");
			Oracle.quote(sql, trigger);
			return sql.toString();
		}
	};

	private static final class TryRemoveTriggerTask extends SimpleTask {

		private final String trigger;

		private TryRemoveTriggerTask(String trigger) {
			this.trigger = trigger;
		}
	}

	@Publish
	protected final class PrivateTryRemoveTrigger extends
			SimpleTaskMethodHandler<TryRemoveTriggerTask> {

		@Override
		protected void handle(
				ResourceContext<VariationMonitor, VariationMonitorImpl, VariationMonitorImpl> context,
				TryRemoveTriggerTask task) throws Throwable {
			final DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
			final StatementWrap stmt = adapter.createStatement();
			try {
				String dropSql = context.dbProduct().callback(DROP_TRIGGER, task.trigger);
				if (dropSql == null || dropSql.length() == 0) {
					throw new IllegalStateException();
				}
				stmt.execute(dropSql, SqlSource.USER_DDL);
			} finally {
				adapter.freeStatement(stmt);
			}
		}
	}

	@Publish
	protected final class Modify extends
			SimpleTaskMethodHandler<VariationMonitorModifyTask> {

		@Override
		protected void handle(
				ResourceContext<VariationMonitor, VariationMonitorImpl, VariationMonitorImpl> context,
				VariationMonitorModifyTask task) throws Throwable {
			if (task.getWatches().size() == 0 && task.getUnwatches().size() == 0) {
				return;
			}
			final VariationMonitorImpl monitor;
			try {
				monitor = context.modifyResource(task.monitor.getId());
			} catch (MissingObjectException e) {
				throw new IllegalArgumentException("修改监视器时错误：标识为[" + task.monitor.getId() + "]的监视器不存在。");
			}
			final TableDefineImpl target = (TableDefineImpl) context.get(TableDefine.class, monitor.target);
			boolean postModified = false;
			if (task.getWatches().size() > 0) {
				for (VariationMonitorFieldMapping map : task.getWatches()) {
					if (monitor.watches.find(map.field.getName()) != null) {
						throw new IllegalArgumentException();
					}
					monitor.watch(map);
					postModified = true;
				}
			}
			if (task.getUnwatches().size() > 0) {
				for (VariationMonitorFieldMapping map : task.getUnwatches()) {
					VariationMonitorFieldImpl monitorField = monitor.watches.find(map.field.getName());
					if (monitorField != null) {
						monitor.watches.remove(monitorField);
						postModified = true;
					}
				}
			}
			if (postModified) {
				TableDefineImpl variation = VariationStruct.build(context, monitor, target);
				try {
					asyncPostVariation(context, monitor, variation);
				} catch (Throwable e) {
					throw new CreateMonitorException(monitor, "创建变化量表[" + monitor.variation + "]时错误。", e);
				}
				try {
					asyncCreateTriggerCheckState(context, monitor, target, variation);
				} catch (Throwable e) {
					throw new CreateMonitorException(monitor, "创建触发器时错误。", e);
				}
				variation = (TableDefineImpl) context.get(TableDefine.class, monitor.variation);
				// XXX
				monitor.control = new VariationControl(monitor, target, variation);
				context.newORMAccessor(VariationMonitorService.this.monitorORM).update(monitor.getMetadata());
				context.postModifiedResource(monitor);
			}
		}
	}

	@Publish
	protected final class Remove extends
			SimpleTaskMethodHandler<VariationMonitorRemoveTask> {

		@Override
		protected void handle(
				ResourceContext<VariationMonitor, VariationMonitorImpl, VariationMonitorImpl> context,
				VariationMonitorRemoveTask task) throws Throwable {
			final VariationMonitorImpl monitor;
			try {
				monitor = context.removeResource(task.monitorId);
			} catch (MissingObjectException e) {
				throw new RuntimeException("删除监视器时错误：标识为[" + task.monitorId + "]的监视器不存在。");
			}
			try {
				if (!context.newORMAccessor(VariationMonitorService.this.monitorORM).delete(monitor.id)) {
					logger.logFatal(context, "删除监视器[" + monitor.name + "]过程中发生错误：无法成功的删除其持久化元数据。", false);
				}
			} catch (Throwable e) {
				logger.logError(context, "删除监视器[" + monitor.name + "]过程中发生错误：删除监视器的持久化元数据时发生了异常。", e, false);
			}
			try {
				asyncRemoveTrigger(context, monitor);
			} catch (Throwable e) {
				logger.logError(context, "删除监视器[" + monitor.name + "]过程中发生错误：删除数据库触发器[" + monitor.trigger + "]时发生了异常。", e, false);
			}
			try {
				asyncRemoveVariation(context, monitor);
			} catch (Throwable e) {
				logger.logError(context, "删除监视器[" + monitor.name + "]过程中发生错误：删除变化量表[" + monitor.variation + "]时发生了异常。", e, false);
			}
		}
	}

	@Publish
	protected final class LockCallback extends
			SimpleTaskMethodHandler<VariationMonitorLockTargetTask> {

		@Override
		protected void handle(
				ResourceContext<VariationMonitor, VariationMonitorImpl, VariationMonitorImpl> context,
				VariationMonitorLockTargetTask task) throws Throwable {
			final VariationMonitorImpl monitor;
			try {
				monitor = context.modifyResource(task.monitorId);
			} catch (MissingObjectException e) {
				throw new RuntimeException();
			}
			final TableDefine target = context.get(TableDefine.class, monitor.target);
			final DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
			final StatementWrap stmt = adapter.createStatement();
			try {
				stmt.execute(context.dbProduct().callback(LOCK_SQL, target.getPrimaryDBTable().getNameInDB()), SqlSource.USER_DDL);
			} finally {
				adapter.freeStatement(stmt);
			}
		}
	}

	private static final DbProduct.Callee<String, String> LOCK_SQL = new Callee<String, String>() {

		@Override
		public String onOracle(String data) throws Throwable {
			final StringBuilder sql = new StringBuilder();
			sql.append("lock table ");
			Oracle.quote(sql, data);
			sql.append(" in exclusive mode nowait");
			return sql.toString();
		}
	};

	@Publish
	protected final class ProvideVariationContext extends
			OneKeyResultProvider<VariationContext, GUID> {

		@Override
		protected VariationContext provide(
				ResourceContext<VariationMonitor, VariationMonitorImpl, VariationMonitorImpl> context,
				GUID key) throws Throwable {
			final VariationMonitor m = context.find(VariationMonitor.class, key);
			if (m != null) {
				return new VariationContextImpl(context, ((VariationMonitorImpl) m).control);
			}
			return null;
		}
	}
}
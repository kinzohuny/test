package com.jiuqi.dna.core.impl;

import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.ContextKind;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.SiteState;
import com.jiuqi.dna.core.da.DbProduct.Callee;
import com.jiuqi.dna.core.def.IllegalRuntimeClonedCopyException;
import com.jiuqi.dna.core.def.table.TableDeclare;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableRelationDefine;
import com.jiuqi.dna.core.impl.TableDefineBroker.Operation;
import com.jiuqi.dna.core.impl.TableDefineBrokerService.CreateTableDefineBrokerTask;
import com.jiuqi.dna.core.impl.TableDefineBrokerService.ModifyTableDefineBrokerTask;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.service.Publish.Mode;
import com.jiuqi.dna.core.spi.application.NumericOverflowOptionTask;
import com.jiuqi.dna.core.spi.auth.ChangeSessionUserTask;
import com.jiuqi.dna.core.spi.def.DeclarePostTask;
import com.jiuqi.dna.core.spi.def.DeclareRemoveTask;
import com.jiuqi.dna.core.spi.def.SynchroTableDefineTask;
import com.jiuqi.dna.core.spi.metadata.LoadAllMetaDataTask;
import com.jiuqi.dna.core.spi.metadata.LoadMetaDataEvent;

/**
 * ϵͳ����
 * 
 * @author gaojingxin
 * 
 */
final class SystemService extends ServiceBase<ContextImpl<?, ?, ?>> {

	final CoreMetadataUtl coreMetadataUtl;

	SystemService(TD_CoreMetaData coreMetadata) {
		super("ϵͳ����");
		this.coreMetadataUtl = new CoreMetadataUtl(coreMetadata);
	}

	private static class PendingTableEntry {

		TableDefineImpl table;
		boolean forRemove;

		PendingTableEntry(TableDefineImpl table, boolean forRemove) {
			this.table = table;
			this.forRemove = forRemove;
		}
	}

	private final HashMap<String, PendingTableEntry> pendingTables = new HashMap<String, PendingTableEntry>();

	final boolean synchroTableDefine(ContextImpl<?, ?, ?> context,
			String tableName, String title, String category) throws Throwable {
		boolean flag = false;
		TableDefineImpl post = context.getDBAdapter().synchroTableDefine(tableName, title, category);
		if (post != null) {
			this.postTable(context, post, true);
			flag = true;
		}
		return flag;
	}

	/**
	 * ��������
	 * 
	 * @param context
	 * @param post
	 * @param broadcast
	 *            �Ƿ�㲥��������Ⱥ�ڵ㣬ֻ���������ڵ�ı���Դ�����������ݿ������
	 * @throws Throwable
	 */
	final void postTable(ContextImpl<?, ?, ?> context, TableDefineImpl post,
			boolean broadcast) throws Throwable {
		if (reservedTable(post.name)) {
			throw new UnsupportedOperationException("��֧���޸��߼���[" + post.name + "]��");
		}
		synchronized (this.pendingTables) {
			TableDefineImpl runtime = (TableDefineImpl) this.site.findNamedDefine(TableDefine.class, post.name);
			if (runtime == null) {
				runtime = post.clone(context);
				if (broadcast) {
					// �������ú�id���ſ���ͨ����������Ⱥ�ڵ㡣
					runtime.id = context.newRECID();
					// ʹ�ò�ͬ���ڴ�������ͬ�����ݿ����񡣲��Ҵӵ�ǰվ�㴴������
					final Transaction createTrans = this.site.newTransaction(context.transaction.kind, context.transaction);
					final ContextImpl<?, ?, ?> createContext = context.session.newContext(createTrans);
					boolean commit = true;
					try {
						createContext.getDBAdapter().syncTable(runtime);
						// �����ݿ�ͬ�����������쳣ʱ������ִ����ӻ�����Դ�Ĳ�����
						// ���ø��»���Ĵ�������
						createContext.handle(new CreateTableDefineBrokerTask(runtime));
					} catch (Throwable ex) {
						DNALogManager.getLogger("core/db/sync").logError(null, "�������߼�����[" + runtime.name + "]ʱ�쳣��", ex, false);
						commit = false;
						// �����׳��쳣���ж�ע��Ԫ���ݺ��޸����ݿ�Ĳ�����
						throw ex;
					} finally {
						try {
							// ֪ͨ������Ⱥ�ڵ㣬�����߼���Ļ�����Դ��
							createContext.dispose();
						} finally {
							if (!createTrans.disposed()) {
								try {
									createTrans.finish(commit);
								} finally {
									createTrans.dispose();
								}
							}
						}
					}
				}
				if (this.site.state == SiteState.LOADING_METADATA) {
					this.pendingTables.put(runtime.name, new PendingTableEntry(runtime, false));
				} else if (broadcast) {
					if (this.coreMetadataUtl.save(context, runtime)) {
						StringBuilder s = new StringBuilder();
						s.append("�������߼�����[" + runtime.name + "]��Ԫ����Ϊ��\r\n");
						SXElement xml = SXElement.newDoc();
						runtime.renderInto(xml);
						s.append(xml.toString());
						DNALogManager.getLogger("core/db/sync").logInfo(null, s.toString(), null, false);
					} else {
						StringBuilder s = new StringBuilder();
						s.append("�������߼�����[" + runtime.name + "]��������Ԫ����ʱʧ�ܡ�");
						DNALogManager.getLogger("core/db/sync").logInfo(null, s.toString(), null, false);
					}
				}
				this.site.regNamedDefineToSpace(TableDefine.class, runtime, context.catcher);
			} else {
				// �����Ѿ����ڵ��߼���
				checkValidCloneCopy(runtime, post);
				SXElement beforeXML = SXElement.newDoc();
				runtime.renderInto(beforeXML);
				String before = beforeXML.toString();
				if (broadcast) {
					// �����Ƿ�Ϸ����߼���ṹ�޸ġ�
					runtime.clone(context).assignFrom(post, context);
					final Transaction createTrans = this.site.newTransaction(context.transaction.kind, context.transaction);
					final ContextImpl<?, ?, ?> createContext = context.session.newContext(createTrans);
					boolean commit = true;
					try {
						createContext.lockResourceU(createContext.getResourceToken(TableDefineBroker.class, runtime.name));
						createContext.getDBAdapter().postTable(post, runtime);
						runtime.assignFrom(post, createContext);
						createContext.handle(new ModifyTableDefineBrokerTask(runtime, Operation.MODIFY));
					} catch (Throwable ex) {
						DNALogManager.getLogger("core/db/sync").logError(null, "�����߼�����[" + runtime.name + "]ʱ�쳣��", ex, false);
						commit = false;
						throw ex;
					} finally {
						try {
							createContext.dispose();
						} finally {
							if (!createTrans.disposed()) {
								try {
									createTrans.finish(commit);
								} finally {
									createTrans.dispose();
								}
							}
						}
					}
				} else {
					runtime.assignFrom(post, context);
				}
				if (this.site.state == SiteState.LOADING_METADATA) {
					this.pendingTables.put(runtime.name, new PendingTableEntry(runtime, false));
				} else if (broadcast) {
					StringBuilder s = new StringBuilder();
					if (this.coreMetadataUtl.save(context, runtime)) {
						s.append("�����߼�����[" + runtime.name + "]��\r\n");
					} else {
						s.append("�����߼�����[" + runtime.name + "]��������Ԫ����ʱʧ�ܡ�");
					}
					SXElement afterXML = SXElement.newDoc();
					runtime.renderInto(afterXML);
					String after = afterXML.toString();
					if (!before.equals(after)) {
						s.append("ԭԪ����Ϊ��\r\n");
						s.append(before);
						s.append("\r\n");
						s.append("��Ԫ����Ϊ��\r\n");
						s.append(after);
						RuntimeException stack = new RuntimeException("NOT AN EXCEPTION");
						DNALogManager.getLogger("core/db/sync").logInfo(null, s.toString(), stack, false);
					}
				}
			}
		}
	}

	static final void checkValidCloneCopy(TableDefineImpl runtime,
			TableDefineImpl clone) {
		if (!runtime.name.equals(clone.name) || (runtime.id == null && clone.id != null) || (runtime.id != null && !runtime.id.equals(clone.id))) {
			throw new IllegalRuntimeClonedCopyException(runtime, clone);
		}
	}

	private final void postPendingTables(ContextImpl<?, ?, ?> context)
			throws Throwable {
		synchronized (this.pendingTables) {
			if (this.pendingTables.isEmpty()) {
				return;
			}
			DBAdapterImpl dbAdapter = context.getDBAdapter();
			try {
				for (PendingTableEntry entry : this.pendingTables.values()) {
					if (entry.forRemove) {
						this.coreMetadataUtl.delete(context, entry.table.id);
					} else {
						this.coreMetadataUtl.save(context, entry.table);
					}
					if (entry.forRemove && entry.table.id != null) {
						dbAdapter.refactor().drop(entry.table);
					}
				}
			} finally {
				this.pendingTables.clear();
			}
		}
	}

	@Publish
	final class LoadMetaEventListener extends EventListener<LoadMetaDataEvent> {

		protected LoadMetaEventListener() {
			super(Float.MAX_VALUE);
		}

		@Override
		protected void occur(ContextImpl<?, ?, ?> context,
				LoadMetaDataEvent event) throws Throwable {
			SystemService.this.postPendingTables(context);
		}
	}

	@Publish
	final class DeclarePostHandler extends
			TaskMethodHandler<DeclarePostTask, None> {

		protected DeclarePostHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context, DeclarePostTask task)
				throws Throwable {
			NamedDefineImpl declare = (NamedDefineImpl) task.designed;
			if (declare instanceof TableDefineImpl) {
				SystemService.this.postTable(context, (TableDefineImpl) declare, true);
			}
			// else if (declare instanceof ModelDefineImpl) {
			// ModelDefineImpl model = (ModelDefineImpl) declare;
			// model.ensurePrepared(context, true);
			// saveModel(context, SystemService.this.td_coremd, model);
			// NamedDefineImpl define = SystemService.this.site
			// .findNamedDefine(ModelDefine.class, declare.name);
			// if (define != declare) {
			// SystemService.this.site.regNamedDefineToSpace(
			// ModelDefine.class, declare, context.catcher);
			// }
			// }
			else {
				throw new UnsupportedOperationException();
			}
		}
	}

	@Publish
	final class SynchroTableDefineHandler extends
			TaskMethodHandler<SynchroTableDefineTask, None> {

		protected SynchroTableDefineHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				SynchroTableDefineTask task) throws Throwable {
			boolean synchroSuccessed = SystemService.this.synchroTableDefine(context, task.dbTableName, task.title, task.category);
			task.setSynchroSuccessed(synchroSuccessed);
		}

	}

	final void removeTable(ContextImpl<?, ?, ?> context, String tableName,
			boolean broadcast) throws Throwable {
		if (reservedTable(tableName)) {
			throw new UnsupportedOperationException("��֧��ɾ���߼���[" + tableName + "]��");
		}
		synchronized (SystemService.this.pendingTables) {
			if (context.find(TableDefine.class, tableName) == null) {
				return;
			}
			for (TableDefine another : context.getList(TableDefine.class)) {
				if (another.getName().equalsIgnoreCase(tableName)) {
					continue;
				}
				for (TableRelationDefine relation : another.getRelations()) {
					if (relation.getTarget().getName().equalsIgnoreCase(tableName)) {
						throw new UnsupportedOperationException("������ɾ����ע��ı���[" + tableName + "]�����߼���[" + another.getName() + "]�д��ڵ��ñ�ı��ϵ����[" + relation.getName() + "]��");
					}
				}
			}
			TableDefineImpl runtime = (TableDefineImpl) SystemService.this.site.unRegNamedDefineFromSpace(TableDefine.class, tableName);
			if (runtime == null) {
				return;
			}
			if (SystemService.this.site.state == SiteState.LOADING_METADATA) {
				if (runtime.id != null) {
					// �����������һ�����ύ
					SystemService.this.pendingTables.put(runtime.name, new PendingTableEntry(runtime, true));
				} else {
					SystemService.this.pendingTables.remove(runtime.name);
				}
			} else if (broadcast) {
				final Transaction createTrans = this.site.newTransaction(context.transaction.kind, context.transaction);
				final ContextImpl<?, ?, ?> createContext = context.session.newContext(createTrans);
				boolean commit = true;
				try {
					createContext.lockResourceU(createContext.getResourceToken(TableDefineBroker.class, runtime.name));
					this.coreMetadataUtl.delete(createContext, runtime.id);
					DNALogManager.getLogger("core/db/sync").logInfo(null, "ɾ���߼���[" + tableName + "]Ԫ���ݣ�id=" + runtime.id.toString() + "��name=" + tableName + "��", null, false);
					createContext.getDBAdapter().refactor().drop(runtime);
					createContext.handle(new ModifyTableDefineBrokerTask(runtime, Operation.REMOVE));
				} catch (Throwable ex) {
					DNALogManager.getLogger("core/db/sync").logError(null, "ɾ���߼�����[" + tableName + "]ʱ�쳣��", ex, false);
					commit = false;
					throw ex;
				} finally {
					try {
						createContext.dispose();
					} finally {
						if (!createTrans.disposed()) {
							try {
								createTrans.finish(commit);
							} finally {
								createTrans.dispose();
							}
						}
					}
				}
			}
		}
	}

	@Publish
	final class DeclareRemoveHandler extends
			TaskMethodHandler<DeclareRemoveTask, None> {

		protected DeclareRemoveHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				DeclareRemoveTask task) throws Throwable {
			switch (task.type) {
			case TABLE:
				SystemService.this.removeTable(context, task.name, true);
				break;
			default:
				throw new UnsupportedOperationException("��ʱ����֧��[" + task.type + "]ԭ��������");
			}
		}
	}

	@Publish
	final class TableDeclareByNameProvider extends
			OneKeyResultProvider<TableDeclare, String> {

		@Override
		protected TableDeclare provide(ContextImpl<?, ?, ?> context, String name)
				throws Throwable {
			TableDeclare runtime = (TableDeclare) SystemService.this.site.findNamedDefine(TableDefine.class, name);
			if (runtime == null) {
				return new TableDefineImpl(name, null);
			}
			return ((TableDefineImpl) runtime).clone(context);
		}
	}

	@Publish
	final class LoadAllMetaDataTaskHandler
			extends
			TaskMethodHandler<LoadAllMetaDataTask, LoadAllMetaDataTask.LoadMode> {

		protected LoadAllMetaDataTaskHandler() {
			super(LoadAllMetaDataTask.LoadMode.MERGE, new LoadAllMetaDataTask.LoadMode[] { LoadAllMetaDataTask.LoadMode.REPLACE });
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				LoadAllMetaDataTask task) throws Throwable {
			if (context.kind != ContextKind.TRANSIENT) {
				throw new UnsupportedOperationException("��������ʱ��������ִ�и�����Զ�̵��û��첽���ã�");
			}
			context.session.application.reLoadRootSite(context, task);
		}
	}

	@Publish
	final class NumericOverflowOptionTaskHandler extends
			TaskMethodHandler<NumericOverflowOptionTask, None> {
		protected NumericOverflowOptionTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				NumericOverflowOptionTask task) throws Throwable {
			DoubleFieldAccessor.numeric_overflow_mode = task.mode;
		}
	}

	@Publish
	final class ChangeSessionUserTaskHandler extends
			TaskMethodHandler<ChangeSessionUserTask, None> {

		protected ChangeSessionUserTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final ChangeSessionUserTask task) throws Throwable {
			task.sessionUserBeforeChange = context.changeLoginUser(task.user);
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class GetClusterSessionCountTaskHandler extends
			TaskMethodHandler<GetClusterSessionCountTask, None> {

		protected GetClusterSessionCountTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				GetClusterSessionCountTask task) throws Throwable {
			task.setCount(context.occorAt.site.application.sessionManager.getNormalSessionCount(task.excludeBuildinUser));
		}

	}

	private static final HashSet<String> reservedTable = new HashSet<String>();
	static {
		reservedTable.add(TD_CoreMetaData.NAME.toUpperCase());
		reservedTable.add(TD_CoreSiteInfo.NAME.toUpperCase());
		reservedTable.add(TD_CoreAuthACL.NAME.toUpperCase());
		reservedTable.add(TD_CoreAuthAuthACL.NAME.toUpperCase());
		reservedTable.add(TD_CoreAuthRA.NAME.toUpperCase());
		reservedTable.add(TD_CoreAuthRole.NAME.toUpperCase());
		reservedTable.add(TD_CoreAuthUOM.NAME.toUpperCase());
		// reservedTable.add(TD_CoreAuthUser.NAME.toUpperCase());
		reservedTable.add(TableDefineImpl.DUMMY_NAME.toUpperCase());
	}

	static final boolean reservedTable(String tableName) {
		return reservedTable.contains(tableName.toUpperCase());
	}
	
	@Override
	protected void init(Context context) throws Throwable {
		String createSql = context.dbProduct().callback(create_trigger, null);
		if (createSql != null) {
			try {
				final Statement stmt = context.get(Statement.class);
				try {
					stmt.execute(createSql);
				} finally {
					stmt.close();
				}
			} catch (Throwable e) {
				DNALogManager.getLogger("core/db/sync").logError(null, "����CoreMetadata��ʷ���ݴ�����ʧ�ܡ�", e, false);
			}
		}
	}

	static final Callee<Object, String> create_trigger = new Callee<Object, String>() {

		@Override
		public String onDameng(Object data) throws Throwable {
			return null;
		}

		@Override
		public String onOracle(Object data) throws Throwable {
			return "create or replace trigger dna_core_metadata_hist_writter after delete or update on core_metadata for each row begin if deleting then insert into core_metadata_hist (recid, kind, name, finish_time, operation, xml) values (dna.new_recid, :OLD.kind, :OLD.name, sysdate, 'D', :OLD.xml); else insert into core_metadata_hist (recid, kind, name, finish_time, operation, xml) values (dna.new_recid, :OLD.kind, :OLD.name, sysdate, 'U', :OLD.xml); end if; end;";
		}

		@Override
		public String onSQLServer(Object data) throws Throwable {
			return null;
		}

		@Override
		public String onDB2(Object data) throws Throwable {
			return null;
		}

		@Override
		public String onMySQL(Object data) throws Throwable {
			return null;
		}
	};
}
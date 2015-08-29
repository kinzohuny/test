package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.impl.TableDefineBroker.Operation;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXMergeHelper;
import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceKind;
import com.jiuqi.dna.core.service.Publish;

final class TableDefineBrokerService
		extends
		ResourceServiceBase<TableDefineBroker, TableDefineBroker, TableDefineBroker> {

	protected TableDefineBrokerService() {
		super("表定义资源代理服务", ResourceKind.SINGLETON_IN_CLUSTER);
	}

	@Override
	protected float getPriority() {
		return -216;
	}

	@Override
	protected final void init(Context context) {
		context.getList(TableDefineBroker.class);
	}

	@Override
	protected final void initResources(
			final Context context,
			final ResourceInserter<TableDefineBroker, TableDefineBroker, TableDefineBroker> initializer)
			throws Throwable {
		// TODO 只能获取root空间的表定义
		final List<TableDefine> tableDefineList = context.getList(TableDefine.class);
		for (TableDefine tableDefine : tableDefineList) {
			TableDefineBroker broker = new TableDefineBroker((TableDefineImpl) tableDefine);
			broker.operation = Operation.INITIALIZE_CREATE;
			initializer.putResource(broker);
		}
	}

	@Override
	protected final Object extractSerialUserData(final TableDefineBroker impl,
			final TableDefineBroker keys) {
		final Operation operation = impl.operation;
		if (operation == null) {
			throw new NullPointerException();
		}
		switch (operation) {
		case INITIALIZE_CREATE:
			return null;
		case CREATE:
		case MODIFY:
			return this.serializeTableDefine(impl.tableDefine);
		case REMOVE:
			return None.NONE;
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	protected final void restoreSerialUserData(final Object userData,
			final TableDefineBroker impl, final TableDefineBroker keys,
			ObjectQuerier querier) {
		final Operation operation = impl.operation;
		if (operation == null) {
			throw new NullPointerException();
		}
		SystemService service = querier.get(SystemService.class);
		final ContextImpl<?, ?, ?> context = (ContextImpl<?, ?, ?>) querier;
		if (operation == Operation.REMOVE) {
			try {
				service.removeTable(context, impl.name, false);
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
			return;
		} else {
			final TableDefineImpl table = this.unserializeTableDefine(querier, userData);
			impl.tableDefine = table;
			switch (operation) {
			case CREATE:
			case MODIFY: {
				try {
					service.postTable(context, table, false);
				} catch (Throwable e) {
					throw Utils.tryThrowException(e);
				}
				return;
			}
			case INITIALIZE_CREATE:
			case REMOVE:
				throw new IllegalStateException();
			default:
				throw new UnsupportedOperationException();
			}
		}
	}

	private final Object serializeTableDefine(final TableDefineImpl tableDefine) {
		SXElement element = SXElement.newDoc();
		tableDefine.renderInto(element);
		return element;
	}

	private final TableDefineImpl unserializeTableDefine(
			final ObjectQuerier querier, final Object userData) {
		final SXElement element = (SXElement) userData;
		final SXElement tableElement = element.firstChild(TableDefineImpl.table_tag);
		final String name = tableElement.getAttribute(NamedDefineImpl.xml_attr_name);
		final TableDefineImpl table = new TableDefineImpl(name, null);
		final SXMergeHelper helper = new SXMergeHelper(querier);
		table.merge(tableElement, helper);
		helper.resolveDelayAction(CoreMetadataTableLoadStep.class, null);
		return table;
	}

	static final class CreateTableDefineBrokerTask extends SimpleTask {

		final TableDefineImpl table;

		CreateTableDefineBrokerTask(TableDefineImpl table) {
			this.table = table;
		}
	}

	static final class ModifyTableDefineBrokerTask extends SimpleTask {

		final TableDefineImpl table;

		final Operation operation;

		ModifyTableDefineBrokerTask(TableDefineImpl table,
				final Operation operation) {
			this.table = table;
			this.operation = operation;
		}
	}

	static final class RemoveTableDefineBrokerTask extends SimpleTask {

		final String name;

		RemoveTableDefineBrokerTask(String name) {
			this.name = name;
		}

	}

	protected class TableDefineBrokerProvider extends
			OneKeyResourceProvider<String> {

		@Override
		protected String getKey1(TableDefineBroker keysHolder) {
			return keysHolder.name;
		}

	}

	@Publish
	protected class CreateTableDefineBrokerTaskHandler extends
			TaskMethodHandler<CreateTableDefineBrokerTask, None> {

		protected CreateTableDefineBrokerTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(
				ResourceContext<TableDefineBroker, TableDefineBroker, TableDefineBroker> context,
				CreateTableDefineBrokerTask task) throws Throwable {
			TableDefineBroker broker = new TableDefineBroker(task.table);
			broker.operation = Operation.CREATE;
			context.putResource(broker);
		}
	}

	@Publish
	protected class ModifyTableDefineBrokerTaskHandler extends
			TaskMethodHandler<ModifyTableDefineBrokerTask, None> {

		protected ModifyTableDefineBrokerTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(
				ResourceContext<TableDefineBroker, TableDefineBroker, TableDefineBroker> context,
				ModifyTableDefineBrokerTask task) throws Throwable {
			TableDefineBroker broker = context.modifyResource(task.table.name);
			broker.operation = task.operation;
			broker.tableDefine = task.table;
			context.postModifiedResource(broker);
			if (task.operation == Operation.REMOVE) {
				context.asyncHandle(new RemoveTableDefineBrokerTask(task.table.name));
			}
		}
	}

	@Publish
	protected class RemoveTableDefineBrokerTaskHandler extends
			TaskMethodHandler<RemoveTableDefineBrokerTask, None> {

		protected RemoveTableDefineBrokerTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(
				ResourceContext<TableDefineBroker, TableDefineBroker, TableDefineBroker> context,
				RemoveTableDefineBrokerTask task) throws Throwable {
			context.removeResource(task.name);
		}

	}

}

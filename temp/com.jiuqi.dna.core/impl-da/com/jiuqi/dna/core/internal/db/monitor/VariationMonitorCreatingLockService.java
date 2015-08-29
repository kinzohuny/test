package com.jiuqi.dna.core.internal.db.monitor;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.internal.db.monitor.VariationMonitorCreatingLockService.Locket;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceKind;
import com.jiuqi.dna.core.resource.ResourceService;
import com.jiuqi.dna.core.service.Publish;

/**
 * 用于阻塞并发的创建监视器，直到DNA的事务提交。
 * 
 * @author houchunlei
 * 
 */
final class VariationMonitorCreatingLockService extends
		ResourceService<Locket, Locket, Locket> {

	protected VariationMonitorCreatingLockService() {
		super("MonitorCreatingLockService", ResourceKind.SINGLETON_IN_CLUSTER);
		this.locker = Locket.INSTANCE;
	}

	@Override
	protected void initResources(Context context,
			ResourceInserter<Locket, Locket, Locket> initializer)
			throws Throwable {
		initializer.putResource(this.locker);
	}

	private Locket locker;

	@StructClass
	static final class Locket {

		private final String name;

		private Locket() {
			this.name = NAME;
		}

		private static final Locket INSTANCE = new Locket();
	}

	private static final String NAME = "X";

	protected final class ByName extends OneKeyResourceProvider<String> {

		@Override
		protected String getKey1(Locket keysHolder) {
			return keysHolder.name;
		}
	}

	private static final class LockTask extends SimpleTask {

		private LockTask() {
		}
	}

	@Publish
	protected final class LockHandler extends SimpleTaskMethodHandler<LockTask> {

		@Override
		protected void handle(ResourceContext<Locket, Locket, Locket> context,
				LockTask task) throws Throwable {
			VariationMonitorCreatingLockService.this.locker = context.modifyResource(NAME);
		}
	}

	/**
	 * 进去临界区
	 * 
	 * <p>
	 * 临界区到DNA事务结束。
	 * 
	 * @param context
	 */
	static final void critical(Context context) {
		context.handle(new LockTask());
	}
}
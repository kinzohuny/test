package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.ContextKind;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.service.Publish.Mode;
import com.jiuqi.dna.core.spi.application.RestartAppTask;
import com.jiuqi.dna.core.spi.application.RestartRootSiteTask;
import com.jiuqi.dna.core.spi.application.ShutdownAppTask;

/**
 * ϵͳ����
 * 
 * @author gaojingxin
 * 
 */
final class RebootService extends ServiceBase<ContextImpl<?, ?, ?>> {
	protected static final String xml_element_reboot = "reboot";

	RebootService() {
		super("��������");
	}

	@Override
	protected void init(Context context) throws Throwable {
		final SXElement xReboot = this.site.application.getDNAConfig(xml_element_reboot);
		if (xReboot != null) {
			context.asyncHandle(new StartStrategyTask(xReboot), None.NONE);
		}
	}

	static class StartStrategyTask extends Task<None> {
		public final SXElement config;

		public StartStrategyTask(SXElement config) {
			this.config = config;
		}
	}

	@Publish
	final class StartStrategyHandler extends
			TaskMethodHandler<StartStrategyTask, None> {
		public StartStrategyHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				StartStrategyTask task) throws Throwable {
			for (SXElement e = task.config.firstChild(); e != null; e = e.nextSibling()) {
				try {
					RebootStrategyDefine define = context.find(RebootStrategyDefine.class, e.name);
					if (define == null) {
						throw new UnsupportedOperationException("ϵͳ����[" + xml_element_reboot + "]������֧�ֵı��[" + e + "]");
					}
					RebootStrategy strategy = (RebootStrategy) define.getStrategyClass().newInstance();
					strategy.init(context, e);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@Deprecated
	@Publish
	final class RestartRootSiteTaskHandler extends
			TaskMethodHandler<RestartRootSiteTask, None> {

		protected RestartRootSiteTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				RestartRootSiteTask task) throws Throwable {
			if (context.kind != ContextKind.TRANSIENT) {
				throw new UnsupportedOperationException("��������ʱ��������ִ�и�����Զ�̵��û��첽���ã�");
			}
			context.session.application.restartRootSite(context);
		}
	}

	@Publish
	final class RestartAppTaskHandler extends
			TaskMethodHandler<RestartAppTask, None> {
		public RestartAppTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context, RestartAppTask task)
				throws Throwable {
			context.lockResourceU(context.getResourceToken(SynClusterClock.class));
			context.occorAt.site.shutdown(context, true);
		}
	}

	@Publish
	final class ShutdownAppTaskHandler extends
			TaskMethodHandler<ShutdownAppTask, None> {
		public ShutdownAppTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context, ShutdownAppTask task)
				throws Throwable {
			context.lockResourceU(context.getResourceToken(SynClusterClock.class));
			context.occorAt.site.shutdown(context, false);
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class ClockedRestartAppTaskHandler extends
			TaskMethodHandler<ClockedRestartAppTask, None> {

		protected ClockedRestartAppTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				ClockedRestartAppTask task) throws Throwable {
			task.strategy.doReboot(context);
		}
	}
}

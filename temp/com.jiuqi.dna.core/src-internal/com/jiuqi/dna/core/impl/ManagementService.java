package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.service.Service;
import com.jiuqi.dna.core.spi.publish.SpaceToken;

@SuppressWarnings("rawtypes")
final class ManagementService extends Service {

	protected ManagementService() {
		super("DNA管理服务");
	}

	@Publish
	protected final class GetSite extends ResultProvider<SpaceToken> {

		@Override
		protected SpaceToken provide(Context context) throws Throwable {
			final ContextImpl<?, ?, ?> c = (ContextImpl<?, ?, ?>) context;
			return c.occorAt.site;
		}
	}

	@Publish
	protected final class GetSiteServqice extends
			OneKeyResultListProvider<Service, Site> {

		@Override
		protected void provide(Context context, Site key,
				List<Service> resultList) throws Throwable {
			key.fillService(resultList);
		}
	}

	@Publish
	protected final class GetSpaceService extends
			OneKeyResultListProvider<Service, Space> {

		@Override
		protected void provide(Context context, Space key,
				List<Service> resultList) throws Throwable {
			key.fillService(resultList);
		}
	}

	@Publish
	protected final class GetSiteResultProvider extends
			OneKeyResultListProvider<ResultProvider, Site> {

		@Override
		protected void provide(Context context, Site key,
				List<ResultProvider> resultList) throws Throwable {
			key.fillInvokees(resultList, ResultProvider.class);
		}
	}

	@Publish
	protected final class GetSpaceResultProvider extends
			OneKeyResultListProvider<ResultProvider, Space> {

		@Override
		protected void provide(Context context, Space key,
				List<ResultProvider> resultList) throws Throwable {
			key.fillInvokees(resultList, ResultProvider.class);
		}
	}

	@Publish
	protected final class GetSiteOneKeyResultProvider extends
			OneKeyResultListProvider<OneKeyResultProvider, Site> {

		@Override
		protected void provide(Context context, Site key,
				List<OneKeyResultProvider> resultList) throws Throwable {
			key.fillInvokees(resultList, OneKeyResultProvider.class);
		}
	}

	@Publish
	protected final class GetSpaceOneKeyResultProvider extends
			OneKeyResultListProvider<OneKeyResultProvider, Space> {

		@Override
		protected void provide(Context context, Space key,
				List<OneKeyResultProvider> resultList) throws Throwable {
			key.fillInvokees(resultList, OneKeyResultProvider.class);
		}
	}

	@Publish
	protected final class GetSiteTwoKeyResultProvider extends
			OneKeyResultListProvider<TwoKeyResultProvider, Site> {

		@Override
		protected void provide(Context context, Site key,
				List<TwoKeyResultProvider> resultList) throws Throwable {
			key.fillInvokees(resultList, TwoKeyResultProvider.class);
		}
	}

	@Publish
	protected final class GetSpaceTwoKeyResultProvider extends
			OneKeyResultListProvider<TwoKeyResultProvider, Space> {

		@Override
		protected void provide(Context context, Space key,
				List<TwoKeyResultProvider> resultList) throws Throwable {
			key.fillInvokees(resultList, TwoKeyResultProvider.class);
		}
	}

	@Publish
	protected final class GetSiteHandlerProvider extends
			OneKeyResultListProvider<TaskMethodHandler, Site> {

		@Override
		protected void provide(Context context, Site key,
				List<TaskMethodHandler> resultList) throws Throwable {
			key.fillInvokees(resultList, TaskMethodHandler.class);
		}
	}

	@Publish
	protected final class GetSpaceHandlerProvider extends
			OneKeyResultListProvider<TaskMethodHandler, Space> {

		@Override
		protected void provide(Context context, Space key,
				List<TaskMethodHandler> resultList) throws Throwable {
			key.fillInvokees(resultList, TaskMethodHandler.class);
		}
	}

	@Publish
	protected final class GetSiteSimpleHandlerProvider extends
			OneKeyResultListProvider<SimpleTaskMethodHandler, Site> {

		@Override
		protected void provide(Context context, Site key,
				List<SimpleTaskMethodHandler> resultList) throws Throwable {
			key.fillInvokees(resultList, SimpleTaskMethodHandler.class);
		}
	}

	@Publish
	protected final class GetSpaceSimpleHandlerProvider extends
			OneKeyResultListProvider<SimpleTaskMethodHandler, Space> {

		@Override
		protected void provide(Context context, Space key,
				List<SimpleTaskMethodHandler> resultList) throws Throwable {
			key.fillInvokees(resultList, SimpleTaskMethodHandler.class);
		}
	}
}
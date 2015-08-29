package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.impl.DistCacheFilterFactoryGatherer.FilterMetadata;
import com.jiuqi.dna.core.impl.DistributedEnvironment.App;
import com.jiuqi.dna.core.impl.DistributedEnvironment.CacheFilter;
import com.jiuqi.dna.core.impl.DistributedEnvironment.Node;
import com.jiuqi.dna.core.impl.DistributedEnvironment.Type;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceService;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.spi.dist.DistCacheFilterFactory;

final class DistCacheFilterResourceService
		extends
		ResourceService<DistCacheFilterImpl, DistCacheFilterImpl, DistCacheFilterImpl> {

	protected DistCacheFilterResourceService() {
		super("分布式DNA的缓存过滤器的资源服务");
	}

	@Override
	protected void init(Context context) throws Throwable {
		final ContextImpl<?, ?, ?> c = (ContextImpl<?, ?, ?>) context;
		context.ensureResourceInited(DistCacheFilterImpl.class);
		final DistributedEnvironment distenv = c.occorAt.site.application.distenv;
		// 如果当前为参数节点，则为每个远程的App和SSO节点创建分组。
		// 保证查找缓存过滤器时，usingCategory方法总是成功。
		// 实际上只有App节点才会加载缓存过滤器。
		if (distenv != null && distenv.enableRepl()) {
			for (Node node : distenv.ssos) {
				this.registerCategory(node.space, node.space);
			}
			for (App app : distenv.apps) {
				if (app.containSelf()) {
					continue;
				}
				this.registerCategory(app.space(), app.space());
			}
		}
	}

	@Override
	protected void initResources(
			Context context,
			ResourceInserter<DistCacheFilterImpl, DistCacheFilterImpl, DistCacheFilterImpl> initializer)
			throws Throwable {
		final ContextImpl<?, ?, ?> c = (ContextImpl<?, ?, ?>) context;
		final DistributedEnvironment distenv = c.occorAt.site.application.distenv;
		if (distenv == null) {
			return;
		}
		if (initializer.getCategory() == null || initializer.getCategory() == None.NONE) {
			// 默认空间的分组，应用节点加载自身的过滤器
			if (distenv.type == Type.APP) {
				for (App app : distenv.apps) {
					if (app.containSelf()) {
						this.initialize0(context, initializer, "", app.filter);
						break;
					}
				}
			}
		} else {
			// 自定义空间的分组，参数节点加载对指定应用节点的过滤器
			final String target = (String) initializer.getCategory();
			for (App app : distenv.apps) {
				if (app.space().equals(target)) {// 使用URL来标识空间
					this.initialize0(context, initializer, target, app.filter);
					break;
				}
			}
		}
	}

	private final void initialize0(
			Context context,
			ResourceInserter<DistCacheFilterImpl, DistCacheFilterImpl, DistCacheFilterImpl> initializer,
			String target, CacheFilter filter) {
		if (filter == null) {
			return;
		}
		// 遍历注册的缓存过滤器
		for (FilterMetadata metadata : DistCacheFilterFactoryGatherer.map.values()) {
			// filter.template为当前配置的模板
			// metadata.template为程序中注册的模板
			if (metadata.template.equals(filter.template)) {
				// 如果一致则实例化过滤器
				final DistCacheFilterFactory<?> factory;
				try {
					factory = (DistCacheFilterFactory<?>) metadata.factoryClass.newInstance();
				} catch (Throwable e) {
					throw Utils.tryThrowException(new RuntimeException("初始化分布式DNA的缓存过滤器时错误：实例化过滤器工厂时错误。", e));
				}
				final Filter<?> f;
				try {
					f = factory.newInstance(context, filter.conf);
				} catch (Throwable e) {
					throw Utils.tryThrowException(new RuntimeException("初始化分布式DNA的缓存过滤器时错误：实例化过滤器时错误。", e));
				}
				System.out.println("初始化分布式DNA的缓存过滤器：缓存类型[" + metadata.facadeClass.getName() + "]，过滤器模板[" + metadata.template + "]。");
				initializer.putResource(new DistCacheFilterImpl(target, metadata.facadeClass, metadata.template, f));
			} else {
				// 如果程序包和配置的不一致，则提示。
				DNALogManager.getLogger("dna/dist/filter").logWarn(context, "为缓存资源[" + metadata.facadeClass.getName() + "]指定的分布式环境的缓存过滤器，其规则模板[" + metadata.template + "]当前没有对应的配置，未能启用生效。", false);
			}
		}
	}

	@Publish
	protected final class ByClass extends OneKeyResourceProvider<Class<?>> {

		@Override
		protected Class<?> getKey1(DistCacheFilterImpl keysHolder) {
			return keysHolder.facadeClass;
		}
	}
}
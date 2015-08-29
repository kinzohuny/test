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
		super("�ֲ�ʽDNA�Ļ������������Դ����");
	}

	@Override
	protected void init(Context context) throws Throwable {
		final ContextImpl<?, ?, ?> c = (ContextImpl<?, ?, ?>) context;
		context.ensureResourceInited(DistCacheFilterImpl.class);
		final DistributedEnvironment distenv = c.occorAt.site.application.distenv;
		// �����ǰΪ�����ڵ㣬��Ϊÿ��Զ�̵�App��SSO�ڵ㴴�����顣
		// ��֤���һ��������ʱ��usingCategory�������ǳɹ���
		// ʵ����ֻ��App�ڵ�Ż���ػ����������
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
			// Ĭ�Ͽռ�ķ��飬Ӧ�ýڵ��������Ĺ�����
			if (distenv.type == Type.APP) {
				for (App app : distenv.apps) {
					if (app.containSelf()) {
						this.initialize0(context, initializer, "", app.filter);
						break;
					}
				}
			}
		} else {
			// �Զ���ռ�ķ��飬�����ڵ���ض�ָ��Ӧ�ýڵ�Ĺ�����
			final String target = (String) initializer.getCategory();
			for (App app : distenv.apps) {
				if (app.space().equals(target)) {// ʹ��URL����ʶ�ռ�
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
		// ����ע��Ļ��������
		for (FilterMetadata metadata : DistCacheFilterFactoryGatherer.map.values()) {
			// filter.templateΪ��ǰ���õ�ģ��
			// metadata.templateΪ������ע���ģ��
			if (metadata.template.equals(filter.template)) {
				// ���һ����ʵ����������
				final DistCacheFilterFactory<?> factory;
				try {
					factory = (DistCacheFilterFactory<?>) metadata.factoryClass.newInstance();
				} catch (Throwable e) {
					throw Utils.tryThrowException(new RuntimeException("��ʼ���ֲ�ʽDNA�Ļ��������ʱ����ʵ��������������ʱ����", e));
				}
				final Filter<?> f;
				try {
					f = factory.newInstance(context, filter.conf);
				} catch (Throwable e) {
					throw Utils.tryThrowException(new RuntimeException("��ʼ���ֲ�ʽDNA�Ļ��������ʱ����ʵ����������ʱ����", e));
				}
				System.out.println("��ʼ���ֲ�ʽDNA�Ļ������������������[" + metadata.facadeClass.getName() + "]��������ģ��[" + metadata.template + "]��");
				initializer.putResource(new DistCacheFilterImpl(target, metadata.facadeClass, metadata.template, f));
			} else {
				// �������������õĲ�һ�£�����ʾ��
				DNALogManager.getLogger("dna/dist/filter").logWarn(context, "Ϊ������Դ[" + metadata.facadeClass.getName() + "]ָ���ķֲ�ʽ�����Ļ���������������ģ��[" + metadata.template + "]��ǰû�ж�Ӧ�����ã�δ��������Ч��", false);
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
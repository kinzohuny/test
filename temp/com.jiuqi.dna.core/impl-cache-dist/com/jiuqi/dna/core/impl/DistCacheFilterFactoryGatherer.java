package com.jiuqi.dna.core.impl;

import java.util.HashMap;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.TypeArgFinder;
import com.jiuqi.dna.core.spi.dist.DistCacheFilter;
import com.jiuqi.dna.core.spi.dist.DistCacheFilterFactory;
import com.jiuqi.dna.core.spi.publish.BundleToken;
import com.jiuqi.dna.core.spi.publish.PublishedElementGatherer;

final class DistCacheFilterFactoryGatherer extends
		PublishedElementGatherer<DistCacheFilterFactoryElement> {

	static final class FilterMetadata {

		final Class<?> facadeClass;
		final String template;
		final Class<?> factoryClass;

		private FilterMetadata(Class<?> facadeClass, String template,
				Class<?> factoryClass) {
			this.template = template;
			this.factoryClass = factoryClass;
			this.facadeClass = facadeClass;
		}
	}

	static final String ATTR_CLASS = "class";

	@Override
	protected DistCacheFilterFactoryElement parseElement(SXElement element,
			BundleToken bundle) throws Throwable {
		final String factoryClassName = element.getString(ATTR_CLASS);
		if (factoryClassName == null || factoryClassName.length() == 0) {
			System.err.println("�ռ��ֲ�ʽDNA�Ļ��������ʱ����û�й������Ͷ��塣");
			return null;
		}
		final Class<?> factoryClass;
		try {
			factoryClass = bundle.loadClass(factoryClassName, DistCacheFilterFactory.class);
		} catch (Throwable e) {
			System.err.println("�ռ��ֲ�ʽDNA�Ļ��������ʱ����װ�ع������Ĺ���ʱ�쳣��[" + e.getMessage() + "]��");
			return null;
		}
		final Class<?> facadeClass;
		try {
			facadeClass = TypeArgFinder.get(factoryClass, DistCacheFilterFactory.class, 0);
		} catch (Throwable e) {
			System.err.println("�ռ��ֲ�ʽDNA�Ļ��������ʱ���󣺹���������û����Ч�Ļ�����۵Ľӿڷ��͡�");
			return null;
		}
		final DistCacheFilter annotation = factoryClass.getAnnotation(DistCacheFilter.class);
		if (annotation == null) {
			System.err.println("�ռ��ֲ�ʽDNA�Ļ��������ʱ���󣺹���������ȱ��DistCacheFilterע�͡�");
			return null;
		}
		final String template = annotation.template();
		if (template == null || template.length() == 0) {
			System.err.println("�ռ��ֲ�ʽDNA�Ļ��������ʱ���󣺹�����������template���ԷǷ���");
			return null;
		}
		register(facadeClass, template, factoryClass);
		return null;
	}

	static final HashMap<Class<?>, FilterMetadata> map = new HashMap<Class<?>, DistCacheFilterFactoryGatherer.FilterMetadata>();

	private static final void register(Class<?> facadeClass, String template,
			Class<?> factoryClass) {
		// ÿ����Դ����ֻ����һ�ֹ��˵Ĺ���
		final FilterMetadata metadata = new FilterMetadata(facadeClass, template, factoryClass);
		final FilterMetadata old = map.put(facadeClass, metadata);
		if (old != null) {
			map.put(facadeClass, old);
			throw new IllegalArgumentException();
		}
	}
}
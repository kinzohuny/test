package com.jiuqi.dna.core.impl;

import java.util.HashMap;
import java.util.HashSet;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.publish.BundleToken;
import com.jiuqi.dna.core.spi.publish.PublishedElementGatherer;

final class DistCacheBarrierGatherer extends
		PublishedElementGatherer<DistCacheBarrierElement> {

	static final String ATTR_TYPE = "type";
	static final String ATTR_FACADE_CLASS = "facade-class";

	static final String TYPE_NONE = "none";

	static final String ATTR_CAT = "category";
	static final String ATTR_CAT_TYPE = "cat_type";

	@Override
	protected DistCacheBarrierElement parseElement(SXElement element,
			BundleToken bundle) throws Throwable {
		final String facadeClassName = element.getString(ATTR_FACADE_CLASS);
		if (facadeClassName == null || facadeClassName.length() == 0) {
			throw new IllegalArgumentException("分布式DNA的缓存修改屏蔽错误：未指定facadeClass。");
		}
		final String type = element.getString(ATTR_TYPE);
		if (TYPE_NONE.equals(type)) {
			if (element.attrIndexOf(ATTR_CAT) < 0) {
				setOpen(facadeClassName);
			} else if (element.attrIndexOf(ATTR_CAT_TYPE) < 0) {
				final String category = element.getString(ATTR_CAT);
				if (category == null || category.length() == 0) {
					throw new IllegalArgumentException("分布式DNA的缓存修改屏蔽错误：为facadeClass[" + facadeClassName + "]指定的category不能为空。");
				}
				Barrier barrier = map.get(facadeClassName);
				if (barrier == null) {
					LooselySpecificNoneBarrier b = new LooselySpecificNoneBarrier();
					map.put(facadeClassName, b);
					b.set.add(category);
				} else if (barrier instanceof LooselySpecificNoneBarrier) {
					LooselySpecificNoneBarrier b = (LooselySpecificNoneBarrier) barrier;
					b.set.add(category);
				} else {
					throw new IllegalArgumentException("分布式DNA的缓存修改屏蔽错误：为facadeClass[" + facadeClassName + "]同时指定了不同的屏蔽模式。");
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}
		return null;
	}

	private static final void setOpen(String facadeClassName) {
		if (map.containsKey(facadeClassName)) {
			throw new IllegalArgumentException("分布式DNA的缓存修改屏蔽错误：重复声明facadeClass[" + facadeClassName + "]。");
		}
		map.put(facadeClassName, OPEN);
	}

	static final HashMap<String, Barrier> map = new HashMap<String, DistCacheBarrierGatherer.Barrier>();

	static abstract class Barrier {

		abstract boolean isModifiable(Object space);
	}

	static final Barrier FORBIDDEN = new Barrier() {

		@Override
		boolean isModifiable(Object space) {
			return false;
		}

		@Override
		public String toString() {
			return "FORBIDDEN";
		}
	};

	static final Barrier OPEN = new Barrier() {

		@Override
		boolean isModifiable(Object space) {
			return true;
		}

		@Override
		public String toString() {
			return "OPEN";
		}
	};

	static final class LooselySpecificNoneBarrier extends Barrier {

		final HashSet<String> set = new HashSet<String>();

		@Override
		boolean isModifiable(Object space) {
			return this.set.contains(space.toString());
		}
	}
}
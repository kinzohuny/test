package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.spi.publish.BundleToken;

/**
 * 发布的元素的收集器,负责收集在dna.xml的publish标签下的各元素.
 * 
 * <p>
 * 各实现类,必须提供无参数的构造方法,或参数仅有SXElment的构造方法.
 * 
 * <p>
 * 主要方法parseElement,负责根据发布项在dna.xml中的的定义信息,实例化发布元素信息定义.并根据发布元素的类型,在收集完定义信息后,
 * 通过afterGatherElement方法,注册启动项.
 * 
 * @author gaojingxin
 * 
 * @param <TElement>
 *            元素类型
 * @param <TPublishedElement>
 *            元素的发布信息
 */
public abstract class PublishedElementGatherer<TPublishedElement extends PublishedElement> {

	/**
	 * 下一个收集同组同元素的收集器
	 */
	PublishedElementGatherer<?> nextGatherer;

	/**
	 * 解析dna.xml定义的发布元素,
	 * 
	 * @param element
	 * @param bundle
	 * @return
	 * @throws Throwable
	 */
	protected abstract TPublishedElement parseElement(SXElement element,
			BundleToken bundle) throws Throwable;

	final static String xml_attr_space = "space";
	final static String xml_attr_visibility = "visibility";

	void afterGatherElement(TPublishedElement pe, ResolveHelper helper) {
	}

	final boolean gatherElement(Site site, BundleStub bundle,
			SXElement element, ResolveHelper helper) {
		try {
			TPublishedElement pe = this.parseElement(element, bundle);
			if (pe != null) {
				pe.publishMode = element.getEnum(Publish.Mode.class, PublishedElementGatherer.xml_attr_visibility, Publish.Mode.DEFAULT);
				pe.space = site.ensureSpace(element.getAttribute(PublishedElementGatherer.xml_attr_space), '/');
				pe.bundle = bundle;
				this.afterGatherElement(pe, helper);
				return true;
			}
		} catch (Throwable e) {
			helper.catcher.catchException(e, this);
		}
		return false;
	}

	final static String xml_element_gathering = "gathering";
	final static String xml_element_gatherer = "gatherer";
	final static String xml_attr_class = "class";
	final static String xml_attr_group = "group";
	final static String xml_attr_element = "element";

}

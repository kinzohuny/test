package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.spi.publish.BundleToken;

/**
 * ������Ԫ�ص��ռ���,�����ռ���dna.xml��publish��ǩ�µĸ�Ԫ��.
 * 
 * <p>
 * ��ʵ����,�����ṩ�޲����Ĺ��췽��,���������SXElment�Ĺ��췽��.
 * 
 * <p>
 * ��Ҫ����parseElement,������ݷ�������dna.xml�еĵĶ�����Ϣ,ʵ��������Ԫ����Ϣ����.�����ݷ���Ԫ�ص�����,���ռ��궨����Ϣ��,
 * ͨ��afterGatherElement����,ע��������.
 * 
 * @author gaojingxin
 * 
 * @param <TElement>
 *            Ԫ������
 * @param <TPublishedElement>
 *            Ԫ�صķ�����Ϣ
 */
public abstract class PublishedElementGatherer<TPublishedElement extends PublishedElement> {

	/**
	 * ��һ���ռ�ͬ��ͬԪ�ص��ռ���
	 */
	PublishedElementGatherer<?> nextGatherer;

	/**
	 * ����dna.xml����ķ���Ԫ��,
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

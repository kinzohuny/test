package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.DeclareBase;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXMergeHelper;

/**
 * ����ʵ����
 * 
 * @author gaojingxin
 * 
 */
public abstract class DefineBaseImpl extends MetaBase implements DeclareBase {

	@Override
	public final String getDescription() {
		return this.description;
	}

	public final void setDescription(String description) {
		this.description = Utils.noneNull(description);
	}

	/**
	 * ��������
	 */
	String description;

	DefineBaseImpl() {
		this.description = "";
	}

	/**
	 * ��������ȥ����Ĺ��캯��
	 * 
	 * @param sample
	 */
	DefineBaseImpl(DefineBaseImpl sample) {
		super(sample);
		this.description = sample.description;
	}

	DefineBaseImpl(SXElement element) {
		super(element);
		this.description = element.getAttribute(xml_attr_description, "");
	}

	/**
	 * ����ǰ������ȫ����ΪĿ�����
	 * 
	 * @param sample
	 *            ����Ϊ��ǰ��������
	 */
	void assignFrom(Object sample) {
		this.description = ((DefineBaseImpl) sample).description;
	}

	static final String xml_attr_description = "description";

	@Override
	public void render(SXElement element) {
		super.render(element);
		DefineBaseImpl.render(this, element);
	}

	/**
	 * ��Ŀ��xml����ṹ�ϲ�����ǰ����
	 * 
	 * @param element
	 * @param helper
	 */
	void merge(SXElement element, SXMergeHelper helper) {
		DefineBaseImpl.merge(this, element);
	}

	static final void render(DefineBaseImpl define, SXElement element) {
		if (define.description != null && define.description.length() > 0) {
			element.setAttribute(xml_attr_description, define.description);
		}
	}

	static final void merge(DefineBaseImpl define, SXElement element) {
		define.description = element.getAttribute(xml_attr_description, define.description);
	}

}

package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.DeclareBase;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXMergeHelper;

/**
 * 定义实现类
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
	 * 定义描述
	 */
	String description;

	DefineBaseImpl() {
		this.description = "";
	}

	/**
	 * 根据例子去构造的构造函数
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
	 * 将当前对象完全复制为目标对象
	 * 
	 * @param sample
	 *            必须为当前对象类型
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
	 * 将目标xml定义结构合并到当前对象
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

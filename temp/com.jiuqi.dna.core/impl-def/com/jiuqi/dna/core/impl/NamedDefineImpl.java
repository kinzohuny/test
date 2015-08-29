package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.NamedDeclare;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXMergeHelper;
import com.jiuqi.dna.core.type.Digester;

/**
 * 含名称定义的基础类
 * 
 * @author gaojingxin
 * 
 */
public abstract class NamedDefineImpl extends DefineBaseImpl implements
		NamedDeclare {

	public final String getName() {
		return this.name;
	}

	/**
	 * 是否大小写不敏感
	 * 
	 * @return 默认false，即大小写敏感
	 */
	protected boolean isNameCaseSensitive() {
		return true;
	}

	public final String getTitle() {
		return this.title;
	}

	public final void setTitle(String title) {
		this.title = Utils.noneNull(title);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[name:").append(this.name);
		if (!this.title.equals(this.name)) {
			sb.append(",title:").append(this.title);
		}
		if (this instanceof Declarative<?>) {
			DeclaratorBase db = ((Declarative<?>) this).getDeclarator();
			if (db != null) {
				sb.append(",declarator:").append(db.getClass().getName());
				if (db.bundle != null) {
					sb.append(",bundle:").append(db.bundle.name);
				}
			}
		}
		return sb.append(']').toString();
	}

	/**
	 * 名称
	 */
	public final String name;

	/**
	 * 标题
	 */
	protected String title;

	public NamedDefineImpl(String name) {
		if (name == null || name.length() == 0) {
			throw new NullPointerException();
		}
		this.name = name;
		this.title = "";
	}

	public NamedDefineImpl(NamedDefineImpl sample) {
		super(sample);
		this.name = sample.name;
		this.title = sample.title;
	}

	final void digestAuthAndName(Digester digester) {
		digester.update(this.name);
	}

	@Override
	void assignFrom(Object sample) {
		super.assignFrom(sample);
		this.title = ((NamedDefineImpl) sample).title;
	}

	public static final String xml_attr_name = "name";
	public static final String xml_attr_title = "title";

	@Override
	public void render(SXElement element) {
		render(this, element);
	}

	@Override
	void merge(SXElement element, SXMergeHelper helper) {
		merge(this, element);
	}

	static final void render(NamedDefineImpl define, SXElement element) {
		element.setAttribute(xml_attr_name, define.name);
		if (define.title != null && define.title.length() > 0) {
			element.setAttribute(xml_attr_title, define.title);
		}
		DefineBaseImpl.render(define, element);
	}

	static final void merge(NamedDefineImpl define, SXElement element) {
		define.title = element.getAttribute(xml_attr_title, define.title);
		DefineBaseImpl.merge(define, element);
	}

	public final String desc() {
		return "[" + this.getName() + (this.getTitle().equals("") ? "" : ", " + this.getTitle()) + "]";
	}
}
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.MissingDefineException;
import com.jiuqi.dna.core.def.query.DerivedQueryDeclare;

/**
 * 导出查询定义(内联视图),仅提供from子句及with子句使用.
 * 
 * @author houchunlei
 * 
 */
public final class DerivedQueryImpl extends
		SelectImpl<DerivedQueryImpl, DerivedQueryColumnImpl> implements
		DerivedQueryDeclare {

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "derived-query";

	/**
	 * 构造该dq的域.不能直接使用其关系引用.
	 * 
	 * <p>
	 * 除非定义当前dq为with,否则限制dq只能使用在该域的from,union子句中.
	 * <p>
	 * 严格上讲,dq的owner的类型不应该是RelationRefDomain,但目前两者使用范围完全一致.
	 * 
	 */
	final RelationRefDomain owner;

	DerivedQueryImpl(RelationRefDomain owner) {
		this(owner, "derived-query", false);
	}

	DerivedQueryImpl(RelationRefDomain owner, String name, boolean with) {
		super(name);
		this.owner = owner;
		this.isWith = with;
	}

	final boolean isWith;

	@Override
	protected final DerivedQueryColumnImpl newColumnOnly(String name,
			String alias, ValueExpr value) {
		return new DerivedQueryColumnImpl(this, name, alias, value);
	}

	public final RelationRefDomain getDomain() {
		return ContextVariableIntl.derived_query_standalone ? null : this.owner.getDomain();
	}

	public final DerivedQueryImpl getWith(String name) {
		if (this.isWith) {
			return this.fromWithCanOnlyAccessBefore(name);
		} else {
			return this.owner.getWith(name);
		}
	}

	private final DerivedQueryImpl fromWithCanOnlyAccessBefore(String name) {
		NamedDefineContainerImpl<DerivedQueryImpl> withs = ((Withable) this.owner).getWiths();
		for (int i = 0, c = withs.size(); i < c; i++) {
			DerivedQueryImpl with = withs.get(i);
			if (with == this) {
				break;
			}
			if (with.name == name || with.name.equals(name)) {
				return with;
			}
		}
		throw new MissingDefineException("无法找到名称为[" + name + "]的with定义.");
	}

	final SelectImpl<?, ?> getUnionTopSelect() {
		if (this.owner instanceof SelectImpl<?, ?>) {
			SelectImpl<?, ?> owner = (SelectImpl<?, ?>) this.owner;
			if (owner.sets != null) {
				for (int i = 0, c = owner.sets.size(); i < c; i++) {
					if (owner.sets.get(i).target == this) {
						if (owner instanceof DerivedQueryImpl) {
							DerivedQueryImpl dq = (DerivedQueryImpl) owner;
							return dq.getUnionTopSelect();
						} else {
							return owner;
						}
					}
				}
			}
		}
		return this;
	}

	final void checkDomain(RelationRefDomain from) {
		if (this.isWith) {
			DerivedQueryImpl dq = from.getWith(this.name);
			if (dq != this) {
				throw new UnsupportedOperationException("导出查询定义的使用域错误。");
			}
		} else if (this.owner != from) {
			throw new UnsupportedOperationException("导出查询定义的使用域错误。");
		}
	}

}

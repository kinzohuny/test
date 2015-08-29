package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.MissingDefineException;
import com.jiuqi.dna.core.def.query.DerivedQueryDeclare;

/**
 * ������ѯ����(������ͼ),���ṩfrom�Ӿ估with�Ӿ�ʹ��.
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
	 * �����dq����.����ֱ��ʹ�����ϵ����.
	 * 
	 * <p>
	 * ���Ƕ��嵱ǰdqΪwith,��������dqֻ��ʹ���ڸ����from,union�Ӿ���.
	 * <p>
	 * �ϸ��Ͻ�,dq��owner�����Ͳ�Ӧ����RelationRefDomain,��Ŀǰ����ʹ�÷�Χ��ȫһ��.
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
		throw new MissingDefineException("�޷��ҵ�����Ϊ[" + name + "]��with����.");
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
				throw new UnsupportedOperationException("������ѯ�����ʹ�������");
			}
		} else if (this.owner != from) {
			throw new UnsupportedOperationException("������ѯ�����ʹ�������");
		}
	}

}

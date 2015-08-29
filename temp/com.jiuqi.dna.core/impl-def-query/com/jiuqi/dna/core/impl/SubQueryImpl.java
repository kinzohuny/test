package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.SubQueryDeclare;

/**
 * �Ӳ�ѯʵ����
 * 
 * <p>
 * ��ָʹ���ڷ�from�Ӿ��е��Ӳ�ѯ�ṹ.
 * 
 * @author houchunlei
 * 
 */
class SubQueryImpl extends SelectImpl<SubQueryImpl, SubQueryColumnImpl>
		implements SubQueryDeclare {

	public final SubQueryExpr newExpression() {
		return new SubQueryExpr(this);
	}

	public final PredicateExpr exists() {
		return new PredicateExpr(false, PredicateImpl.EXISTS, this.newExpression());
	}

	public final PredicateExpr notExists() {
		return new PredicateExpr(false, PredicateImpl.NOT_EXISTS, this.newExpression());
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "sub-query";

	final RelationRefDomain domain;

	SubQueryImpl(RelationRefDomain domain) {
		this(domain, "subquery");
	}

	SubQueryImpl(RelationRefDomain domain, String name) {
		super(name);
		this.domain = domain;
	}

	@Override
	protected final SubQueryColumnImpl newColumnOnly(String name, String alias,
			ValueExpr expr) {
		return new SubQueryColumnImpl(this, name, alias, expr);
	}

	public final RelationRefDomain getDomain() {
		return this.domain;
	}

	public final DerivedQueryImpl getWith(String name) {
		if (this.domain == null) {
			throw new IllegalArgumentException();
		}
		return this.domain.getWith(name);
	}
}
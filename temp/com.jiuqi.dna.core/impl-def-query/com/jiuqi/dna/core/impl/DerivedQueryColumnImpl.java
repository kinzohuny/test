package com.jiuqi.dna.core.impl;

public final class DerivedQueryColumnImpl extends
		SelectColumnImpl<DerivedQueryImpl, DerivedQueryColumnImpl> {

	DerivedQueryColumnImpl(DerivedQueryImpl owner, String name, String alias,
			ValueExpr value) {
		super(owner, name, alias, value);
	}
}
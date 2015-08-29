package com.jiuqi.dna.core.impl;

public abstract class NumberType extends DataTypeBase {

	NumberType(Class<?> javaClass) {
		super(javaClass);
	}

	@Override
	public final boolean isNumber() {
		return true;
	}

	@Override
	public boolean isDBType() {
		return true;
	}

	public abstract NumberTypePrecedence getPrecedence();
}
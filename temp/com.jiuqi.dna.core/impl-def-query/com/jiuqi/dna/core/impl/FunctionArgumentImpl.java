package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.query.FunctionArgumentDeclare;

final class FunctionArgumentImpl extends NamedDefineImpl implements
		FunctionArgumentDeclare {

	final DataTypeInternal type;

	FunctionArgumentImpl(String name, DataTypeInternal type) {
		super(name);
		this.type = type;
	}

	public final DataTypeInternal getType() {
		return this.type;
	}

	@Override
	public final String getXMLTagName() {
		return null;
	}

}

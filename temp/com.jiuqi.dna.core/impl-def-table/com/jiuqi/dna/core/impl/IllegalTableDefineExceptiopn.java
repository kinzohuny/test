package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDefine;

public final class IllegalTableDefineExceptiopn extends RuntimeException {

	private static final long serialVersionUID = 2301701985842090978L;

	public final TableDefine table;

	public IllegalTableDefineExceptiopn(TableDefine table, String message) {
		super(message);
		this.table = table;
	}

}

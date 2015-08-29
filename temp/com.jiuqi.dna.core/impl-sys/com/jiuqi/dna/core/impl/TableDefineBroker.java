package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.def.obja.StructField;

@StructClass
public final class TableDefineBroker {

	enum Operation {

		INITIALIZE_CREATE,

		CREATE,

		MODIFY,

		REMOVE

	}

	TableDefineBroker(final TableDefineImpl tableDefine) {
		this.name = tableDefine.name;
		this.tableDefine = tableDefine;
	}

	final String name;

	Operation operation;

	@StructField(stateField = false)
	TableDefineImpl tableDefine;

}

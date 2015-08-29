package com.jiuqi.dna.core.internal.db.support.db2.sync;

import com.jiuqi.dna.core.internal.db.sync.DbColumn;
import com.jiuqi.dna.core.internal.db.sync.TypeAlterability;
import com.jiuqi.dna.core.type.DataType;

final class Db2Column extends
		DbColumn<Db2Table, Db2Column, Db2DataType, Db2Index> {

	Db2Column(Db2Table table, String name) {
		super(table, name);
	}

	int codepage;

	final boolean forbitdata() {
		return (this.type == Db2DataType.CHARACTER || this.type == Db2DataType.VARCHAR) && this.codepage == 0;
	}

	// ø…“‘÷¥––alter column x set data type
	@Override
	protected final TypeAlterability typeAlterable(DataType type) {
		return this.type.typeAlterable(this, type);
	}
}
package com.jiuqi.dna.core.internal.db.support.hana.sync;

import com.jiuqi.dna.core.internal.db.sync.DbColumn;
import com.jiuqi.dna.core.internal.db.sync.TypeAlterability;
import com.jiuqi.dna.core.type.DataType;

public class HanaColumn extends
		DbColumn<HanaTable, HanaColumn, HanaDataType, HanaIndex> {

	protected HanaColumn(HanaTable table, String name) {
		super(table, name);
	}

	@Override
	protected final TypeAlterability typeAlterable(DataType type) {
		return this.type.typeAlterable(this, type);
	}
}
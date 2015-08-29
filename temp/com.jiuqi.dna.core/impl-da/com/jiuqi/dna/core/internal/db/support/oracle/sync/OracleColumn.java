package com.jiuqi.dna.core.internal.db.support.oracle.sync;

import com.jiuqi.dna.core.internal.db.sync.DbColumn;
import com.jiuqi.dna.core.internal.db.sync.TypeAlterability;
import com.jiuqi.dna.core.type.DataType;

final class OracleColumn extends
		DbColumn<OracleTable, OracleColumn, OracleDataType, OracleIndex> {

	OracleColumn(OracleTable table, String name) {
		super(table, name);
	}

	@Override
	protected final TypeAlterability typeAlterable(DataType type) {
		return this.type.typeAlterable(this, type);
	}
}
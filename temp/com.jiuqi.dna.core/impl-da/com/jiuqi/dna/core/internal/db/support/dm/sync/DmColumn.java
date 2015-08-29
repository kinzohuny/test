package com.jiuqi.dna.core.internal.db.support.dm.sync;

import com.jiuqi.dna.core.internal.db.sync.DbColumn;
import com.jiuqi.dna.core.internal.db.sync.TypeAlterability;
import com.jiuqi.dna.core.type.DataType;

final class DmColumn extends DbColumn<DmTable, DmColumn, DmDataType, DmIndex> {

	protected DmColumn(DmTable table, String name) {
		super(table, name);
	}

	@Override
	protected TypeAlterability typeAlterable(DataType type) {
		return this.type.typeAlterable(this, type);
	}
}
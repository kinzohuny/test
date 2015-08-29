package com.jiuqi.dna.core.internal.db.support.sqlserver.sync;

import com.jiuqi.dna.core.internal.db.sync.DbColumn;
import com.jiuqi.dna.core.internal.db.sync.TypeAlterability;
import com.jiuqi.dna.core.type.DataType;

final class SqlserverColumn
		extends
		DbColumn<SqlserverTable, SqlserverColumn, SqlserverDataType, SqlserverIndex> {

	SqlserverColumn(SqlserverTable table, String name) {
		super(table, name);
	}

	String defaultConstraint;
	String collation;

	@Override
	protected final TypeAlterability typeAlterable(DataType type) {
		return this.type.typeAlterable(this, type);
	}

	final void typeDefinition(Appendable s) {
		this.type.define(this, s);
	}
}
package com.jiuqi.dna.core.internal.db.support.kingbase.sync;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.jiuqi.dna.core.internal.db.sync.DbColumn;
import com.jiuqi.dna.core.internal.db.sync.TypeAlterability;
import com.jiuqi.dna.core.type.DataType;

final class KingbaseColumn
		extends
		DbColumn<KingbaseTable, KingbaseColumn, KingbaseDataType, KingbaseIndex> {

	KingbaseColumn(KingbaseTable table, String name) {
		super(table, name);
	}

	final void load(ResultSet rs) throws SQLException {
		this.type = KingbaseDataType.typeOf(rs.getString(2));
		if (this.type == KingbaseDataType.NCHAR || this.type == KingbaseDataType.NVARCHAR2 || this.type == KingbaseDataType.NVARCHAR) {
			this.length = rs.getInt(3) / 2;
		} else {
			this.length = rs.getInt(3);
		}
		this.precision = rs.getInt(4);
		this.scale = rs.getInt(5);
		this.notNull = rs.getString(6).equals("N");
		String defaultVal = rs.getString(7);
		if (defaultVal != null) {
			this.defaultDefinition = defaultVal.trim();
			if (this.defaultDefinition.length() == 0) {
				this.defaultDefinition = null;
			}
		} else {
			this.defaultDefinition = null;
		}
	}

	@Override
	protected final TypeAlterability typeAlterable(DataType type) {
		return this.type.typeAlterable(this, type);
	}
}
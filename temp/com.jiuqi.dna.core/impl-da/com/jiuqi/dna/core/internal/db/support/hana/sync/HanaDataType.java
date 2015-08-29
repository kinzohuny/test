package com.jiuqi.dna.core.internal.db.support.hana.sync;

import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.Never;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.jiuqi.dna.core.impl.IntType;
import com.jiuqi.dna.core.impl.LongType;
import com.jiuqi.dna.core.impl.ShortType;
import com.jiuqi.dna.core.impl.TextDBType;
import com.jiuqi.dna.core.internal.db.sync.DbDataType;
import com.jiuqi.dna.core.internal.db.sync.TypeAlterability;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.TypeFactory;

public enum HanaDataType implements
		DbDataType<HanaTable, HanaColumn, HanaDataType, HanaIndex> {

	TINYINT() {

		@Override
		public void tired(HanaColumn column, ResultSet rs) {
		}

		@Override
		public TypeAlterability typeAlterable(HanaColumn column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(HanaColumn column) {
			return ShortType.TYPE;
		}

	},

	SMALLINT() {
		@Override
		public void tired(HanaColumn column, ResultSet rs) {
		}

		@Override
		public TypeAlterability typeAlterable(HanaColumn column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(HanaColumn column) {
			return IntType.TYPE;
		}
	},

	INTEGER() {
		@Override
		public void tired(HanaColumn column, ResultSet rs) {
		}

		@Override
		public TypeAlterability typeAlterable(HanaColumn column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(HanaColumn column) {
			return IntType.TYPE;
		}
	},

	BIGINT() {
		@Override
		public void tired(HanaColumn column, ResultSet rs) {
		}

		@Override
		public TypeAlterability typeAlterable(HanaColumn column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(HanaColumn column) {
			return LongType.TYPE;
		}
	},

	DOUBLE() {
		@Override
		public void tired(HanaColumn column, ResultSet rs) {
		}

		@Override
		public TypeAlterability typeAlterable(HanaColumn column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(HanaColumn column) {
			return TypeFactory.DOUBLE;
		}
	},

	DECIMAL() {
		@Override
		public void tired(HanaColumn column, ResultSet rs) throws SQLException {
			column.precision = rs.getInt(3);
			column.scale = rs.getInt(4);
		}

		@Override
		public TypeAlterability typeAlterable(HanaColumn column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(HanaColumn column) {
			return TypeFactory.DOUBLE;
		}
	},

	NVARCHAR() {

		@Override
		public void tired(HanaColumn column, ResultSet rs) throws SQLException {
			column.length = rs.getInt(3);
		}

		@Override
		public TypeAlterability typeAlterable(HanaColumn column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(HanaColumn column) {
			return TypeFactory.NVARCHAR(column.length);
		}
	},

	VARBINARY() {
		@Override
		public void tired(HanaColumn column, ResultSet rs) throws SQLException {
			column.length = rs.getInt(3);
		}

		@Override
		public TypeAlterability typeAlterable(HanaColumn column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(HanaColumn column) {
			return TypeFactory.VARBINARY(column.length);
		}
	},

	CLOB() {
		@Override
		public void tired(HanaColumn column, ResultSet rs) {
		}

		@Override
		public TypeAlterability typeAlterable(HanaColumn column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(HanaColumn column) {
			return TextDBType.TYPE;
		}
	},

	NCLOB() {
		@Override
		public void tired(HanaColumn column, ResultSet rs) {
		}

		@Override
		public TypeAlterability typeAlterable(HanaColumn column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(HanaColumn column) {
			return TypeFactory.NTEXT;
		}
	},

	BLOB() {
		@Override
		public void tired(HanaColumn column, ResultSet rs) {
		}

		@Override
		public TypeAlterability typeAlterable(HanaColumn column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(HanaColumn column) {
			return TypeFactory.BLOB;
		}
	},

	DATE() {
		@Override
		public void tired(HanaColumn column, ResultSet rs) {
		}

		@Override
		public TypeAlterability typeAlterable(HanaColumn column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(HanaColumn column) {
			return TypeFactory.DATE;
		}
	},

	TIMESTAMP() {
		@Override
		public void tired(HanaColumn column, ResultSet rs) {
		}

		@Override
		public TypeAlterability typeAlterable(HanaColumn column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(HanaColumn column) {
			return TypeFactory.DATE;
		}
	},

	;

	public abstract void tired(HanaColumn column, ResultSet rs)
			throws SQLException;

	public abstract TypeAlterability typeAlterable(HanaColumn column,
			DataType target);

	public String toString(HanaColumn column) {
		// HANA Auto-generated method stub
		return null;
	}

	public void define(HanaColumn column, Appendable s) {
		// HANA Auto-generated method stub

	}
}
package com.jiuqi.dna.core.internal.db.support.oracle.sync;

import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.Always;
import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.ColumnNull;
import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.ExceedExist;
import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.Never;

import java.io.IOException;

import com.jiuqi.dna.core.impl.BinDBType;
import com.jiuqi.dna.core.impl.BooleanType;
import com.jiuqi.dna.core.impl.CharDBType;
import com.jiuqi.dna.core.impl.CharsType;
import com.jiuqi.dna.core.impl.DateType;
import com.jiuqi.dna.core.impl.DoubleType;
import com.jiuqi.dna.core.impl.FloatType;
import com.jiuqi.dna.core.impl.GUIDType;
import com.jiuqi.dna.core.impl.IntType;
import com.jiuqi.dna.core.impl.LongType;
import com.jiuqi.dna.core.impl.NCharDBType;
import com.jiuqi.dna.core.impl.NVarCharDBType;
import com.jiuqi.dna.core.impl.NumericDBType;
import com.jiuqi.dna.core.impl.ShortType;
import com.jiuqi.dna.core.impl.TextDBType;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.impl.VarCharDBType;
import com.jiuqi.dna.core.internal.db.sync.DbDataType;
import com.jiuqi.dna.core.internal.db.sync.TypeAlterability;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.TypeFactory;

enum OracleDataType implements
		DbDataType<OracleTable, OracleColumn, OracleDataType, OracleIndex> {

	NUMBER {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			if (target == BooleanType.TYPE) {
				if (column.precision == 1 && column.scale == 0) {
					return Always;
				}
			} else if (target == ShortType.TYPE) {
				if (column.precision != 0 && column.precision <= 5 && column.scale == 0) {
					return Always;
				}
			} else if (target == IntType.TYPE) {
				if (column.precision != 0 && column.precision <= 10 && column.scale == 0) {
					return Always;
				}
			} else if (target == LongType.TYPE) {
				if (column.precision != 0 && column.precision <= 19 && column.scale == 0) {
					return Always;
				}
			} else if (target instanceof NumericDBType) {
				// HCL ¾¡¿ÉÄÜµÄ¼æÈÝ
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= column.precision - column.scale && nt.scale >= column.scale) {
					return Always;
				}
			} else if (target.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}

		public DataType convertToDataType(OracleColumn column) {
			DataType type = null;
			if(column.precision == 1 && column.scale == 0) {
				type = TypeFactory.BOOLEAN;
			}else if(column.scale == 0) {
				if(column.precision < 32) {
					type = TypeFactory.INT;
				}else {
					type = TypeFactory.LONG;
				}
			}else {
				type = TypeFactory.NUMERIC(column.precision, column.scale);
			}
			return type;
		}
	},

	FLOAT {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			if (target.isLOB()) {
				return Never;
			} else if (column.precision == 63) {
				if (target == FloatType.TYPE || target == DoubleType.TYPE) {
					return Always;
				}
			} else if (column.precision == 126) {
				if (target == DoubleType.TYPE) {
					return Always;
				}
			}
			return ColumnNull;
		}

		public DataType convertToDataType(OracleColumn column) {
			return TypeFactory.FLOAT;
		}
	},

	BINARY_FLOAT {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			if (target.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}

		public DataType convertToDataType(OracleColumn column) {
			return TypeFactory.BLOB;
		}
	},

	BINARY_DOUBLE {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			if (target.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}

		public DataType convertToDataType(OracleColumn column) {
			return TypeFactory.BLOB;
		}
	},

	CHAR {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			if (target instanceof CharDBType || target instanceof VarCharDBType) {
				CharsType ct = (CharsType) target;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ColumnNull;
				}
			} else if (target instanceof NCharDBType || target instanceof NVarCharDBType) {
				CharsType ct = (CharsType) target;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}

		public DataType convertToDataType(OracleColumn column) {
			return TypeFactory.CHAR(column.length);
		}
	},

	VARCHAR2 {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			if (target instanceof CharDBType || target instanceof VarCharDBType) {
				CharsType ct = (CharsType) target;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}

		public DataType convertToDataType(OracleColumn column) {
			return TypeFactory.VARCHAR(column.length);
		}
	},

	CLOB {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			return Never;
		}

		public DataType convertToDataType(OracleColumn column) {
			return TextDBType.TYPE;
		}
	},

	NCHAR {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			if (target instanceof NCharDBType || target instanceof NVarCharDBType) {
				CharsType ct = (CharsType) target;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}

		public DataType convertToDataType(OracleColumn column) {
			return TypeFactory.NCHAR(column.length);
		}
	},

	NVARCHAR2 {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			if (target instanceof NCharDBType || target instanceof NVarCharDBType) {
				CharsType ct = (CharsType) target;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}

		public DataType convertToDataType(OracleColumn column) {
			return TypeFactory.NVARCHAR(column.length);
		}
	},

	NCLOB {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			return Never;
		}

		public DataType convertToDataType(OracleColumn column) {
			return TypeFactory.NTEXT;
		}
	},

	RAW {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			if (target instanceof BinDBType) {
				BinDBType bt = (BinDBType) target;
				if (bt.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target == GUIDType.TYPE && column.length <= 16) {
				return Always;
			} else if (target.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}

		public DataType convertToDataType(OracleColumn column) {
			DataType type = null;
			if(column.length == 16) {
				type = TypeFactory.GUID;
			}else {
				type = TypeFactory.VARBINARY(column.length);
			}
			return type;
		}
	},

	BLOB {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			return Never;
		}

		public DataType convertToDataType(OracleColumn column) {
			return TypeFactory.BLOB;
		}
	},

	DATE {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			return forDateCategory(target);
		}

		public DataType convertToDataType(OracleColumn column) {
			return TypeFactory.DATE;
		}
	},

	TIMESTAMP {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			return forDateCategory(target);
		}

		public DataType convertToDataType(OracleColumn column) {
			return TypeFactory.DATE;
		}
	},

	TIMESTAMP_WITH_TIME_ZONE {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			return forDateCategory(target);
		}

		public DataType convertToDataType(OracleColumn column) {
			return TypeFactory.DATE;
		}
	},

	TIMESTAMP_WITH_LOCAL_TIME_ZONE {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			return forDateCategory(target);
		}

		public DataType convertToDataType(OracleColumn column) {
			return TypeFactory.DATE;
		}
	},

	INTERVAL_YEAR_TO_MONTH {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		public DataType convertToDataType(OracleColumn column) {
			throw new UnsupportedOperationException();
		}
	},

	INTERVAL_DAY_TO_SECOND {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		public DataType convertToDataType(OracleColumn column) {
			throw new UnsupportedOperationException();
		}
	},

	ROWID {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		public DataType convertToDataType(OracleColumn column) {
			throw new UnsupportedOperationException();
		}
	},

	UROWID {

		@Override
		public TypeAlterability typeAlterable(OracleColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		public DataType convertToDataType(OracleColumn column) {
			throw new UnsupportedOperationException();
		}
	};

	private static final TypeAlterability forDateCategory(DataType type) {
		if (type == DateType.TYPE) {
			return Always;
		} else if (type.isLOB()) {
			return Never;
		}
		return ColumnNull;
	}

	public abstract TypeAlterability typeAlterable(OracleColumn column,
			DataType target);

	static final OracleDataType typeOf(String type) {
		if (type.startsWith("TIMESTAMP")) {
			if (type.endsWith("WITH TIME ZONE")) {
				return OracleDataType.TIMESTAMP_WITH_TIME_ZONE;
			} else if (type.endsWith("WITH LOCAL TIME ZONE")) {
				return OracleDataType.TIMESTAMP_WITH_LOCAL_TIME_ZONE;
			}
			return OracleDataType.TIMESTAMP;
		} else if (type.startsWith("INTERVAL YEAR")) {
			return OracleDataType.INTERVAL_YEAR_TO_MONTH;
		} else if (type.startsWith("INTERVAL DAY")) {
			return OracleDataType.INTERVAL_DAY_TO_SECOND;
		} else {
			return OracleDataType.valueOf(type);
		}
	}

	public void define(OracleColumn column, Appendable s) {
		try {
			s.append(this.name());
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final String toString(OracleColumn column) {
		StringBuilder s = new StringBuilder();
		this.define(column, s);
		return s.toString();
	}

}
package com.jiuqi.dna.core.internal.db.support.kingbase.sync;

import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.Always;
import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.ColumnNull;
import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.ExceedExist;
import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.Never;

import java.io.IOException;

import com.jiuqi.dna.core.impl.BinDBType;
import com.jiuqi.dna.core.impl.BooleanType;
import com.jiuqi.dna.core.impl.CharDBType;
import com.jiuqi.dna.core.impl.CharsType;
import com.jiuqi.dna.core.impl.DataTypeInternal;
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
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.impl.VarCharDBType;
import com.jiuqi.dna.core.internal.db.sync.DbDataType;
import com.jiuqi.dna.core.internal.db.sync.TypeAlterability;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.TypeFactory;

enum KingbaseDataType
		implements
		DbDataType<KingbaseTable, KingbaseColumn, KingbaseDataType, KingbaseIndex> {

	NUMBER {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			if (target == IntType.TYPE || target == ShortType.TYPE || target == BooleanType.TYPE) {
				if (column.precision != 0 && column.precision <= 32 && column.scale == 0) {
					return Always;
				}
			} else if (target == LongType.TYPE) {
				if (column.precision != 0 && column.precision <= 64 && column.scale == 0) {
					return Always;
				}
			} else if (target instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= column.precision - column.scale && nt.scale >= column.scale) {
					return Always;
				}
			} else if (target.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.NUMERIC(column.precision, column.scale);
		}

	},
	BIGINT {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			if (target.isLOB()) {
				return Never;
			} else if (column.precision == 19 || column.precision == 64) {
				if (target == LongType.TYPE) {
					return Always;
				}
			}
			return ColumnNull;
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.INT;
		}

	},
	FLOAT {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			if (target.isLOB()) {
				return Never;
			} else if (column.precision <= 24) {
				if (target == FloatType.TYPE || target == DoubleType.TYPE) {
					return Always;
				}
			} else if (column.precision <= 53) {
				if (target == DoubleType.TYPE) {
					return Always;
				}
			}
			return ColumnNull;
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.FLOAT;
		}
	},
	BINARY_FLOAT {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			if (target.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.BLOB;
		}
	},
	BINARY_DOUBLE {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			if (target.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.BLOB;
		}
	},
	CHAR {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
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

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.CHAR(column.length);
		}
	},
	VARCHAR2 {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
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

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.VARCHAR(column.length);
		}

	},
	VARCHAR {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
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

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.VARCHAR(column.length);
		}
	},
	CLOB {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			return Always;
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.TEXT;
		}
	},
	NCHAR {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
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

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.NCHAR(column.length);
		}
	},
	NVARCHAR2 {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
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

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.NVARCHAR(column.length);
		}
	},
	// OTHER
	NVARCHAR {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
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

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.NVARCHAR(column.length);
		}
	},
	NCLOB {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			return Never;
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.NTEXT;
		}
	},
	BYTEA {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			if (target instanceof BinDBType) {
				BinDBType bt = (BinDBType) target;
				if (bt.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target == GUIDType.TYPE && column.length <= 32) {
				return Always;
			} else if (target.isLOB()) {
				return Never;
			}
			return ColumnNull;
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return BinDBType.TYPE;
		}
	},
	RAW {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
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

		public DataType convertToDataType(KingbaseColumn column) {
			DataType type = null;
			if(column.length == 16) {
				type = TypeFactory.GUID;
			}else {
				type = TypeFactory.VARBINARY(column.length);
			}
			return type;
		}
	},
	CHARACTER {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
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

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.NVARCHAR(column.length);
		}
	},
	TEXT {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			return Always;
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.TEXT;
		}
	},
	NTEXT {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			return Always;
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.TEXT;
		}

	},
	BLOB {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			return Never;
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.BLOB;
		}
	},
	DATE {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			return forDateCategory(target);
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.DATE;
		}
	},
	TIMESTAMP {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			return forDateCategory(target);
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.DATE;
		}
	},
	TIMESTAMP_WITH_TIME_ZONE {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			return forDateCategory(target);
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.DATE;
		}
	},
	TIMESTAMP_WITH_LOCAL_TIME_ZONE {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			return forDateCategory(target);
		}

		public DataType convertToDataType(KingbaseColumn column) {
			return TypeFactory.DATE;
		}
	},

	INTERVAL_YEAR_TO_MONTH {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		public DataType convertToDataType(KingbaseColumn column) {
			throw new UnsupportedOperationException();
		}
	},
	INTERVAL_DAY_TO_SECOND {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		public DataType convertToDataType(KingbaseColumn column) {
			throw new UnsupportedOperationException();
		}
	},
	ROWID {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		public DataType convertToDataType(KingbaseColumn column) {
			throw new UnsupportedOperationException();
		}
	},
	UROWID {
		@Override
		public TypeAlterability typeAlterable(KingbaseColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		public DataType convertToDataType(KingbaseColumn column) {
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

	public abstract TypeAlterability typeAlterable(KingbaseColumn column,
			DataType target);

	static final KingbaseDataType typeOf(String type) {
		if (type.startsWith("TIMESTAMP")) {
			if (type.endsWith("WITH TIME ZONE")) {
				return KingbaseDataType.TIMESTAMP_WITH_TIME_ZONE;
			} else if (type.endsWith("WITH LOCAL TIME ZONE")) {
				return KingbaseDataType.TIMESTAMP_WITH_LOCAL_TIME_ZONE;
			}
			return KingbaseDataType.TIMESTAMP;
		} else if (type.startsWith("INTERVAL YEAR")) {
			return KingbaseDataType.INTERVAL_YEAR_TO_MONTH;
		} else if (type.startsWith("INTERVAL DAY")) {
			return KingbaseDataType.INTERVAL_DAY_TO_SECOND;
		} else if (type.startsWith("DOUBLE PRECISION")) {
			return KingbaseDataType.valueOf("FLOAT");
		} else if (type.startsWith("INTEGER") || type.startsWith("SMALLINT") || type.startsWith("TINYINT")) {
			return KingbaseDataType.valueOf("NUMBER");
		} else if (type.startsWith("BIGINT")) {
			return KingbaseDataType.valueOf("NUMBER");
		} else if (type.startsWith("NUMERIC")) {
			return KingbaseDataType.valueOf("NUMBER");
		} else if (type.startsWith("REAL")) {
			return KingbaseDataType.valueOf("FLOAT");
		} else if (type.startsWith("CHARACTER VARYING")) {
			return KingbaseDataType.valueOf("VARCHAR");
		} else if (type.startsWith("NVARCHAR")) {
			return KingbaseDataType.valueOf("VARCHAR");
		} else if (type.startsWith("NCHAR")) {
			return KingbaseDataType.valueOf("CHAR");
		} else if (type.startsWith("CHARACTER")) {
			return KingbaseDataType.valueOf("CHAR");
		} else if (type.startsWith("BYTEA")) {
			return KingbaseDataType.valueOf("BYTEA");
		} else if (type.startsWith("BIT")) {
			return KingbaseDataType.valueOf("BYTEA");
		} else if (type.startsWith("BIT VARYING")) {
			return KingbaseDataType.valueOf("BYTEA");
		} else {
			return KingbaseDataType.valueOf(type);
		}
	}

	public void define(KingbaseColumn column, Appendable s) {
		try {
			s.append(this.name());
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final String toString(KingbaseColumn column) {
		StringBuilder s = new StringBuilder();
		this.define(column, s);
		return s.toString();
	}

	public DataTypeInternal dnaTypeOf(int length, int precision, int scale) {
		throw Utils.notImplemented();
	}

}
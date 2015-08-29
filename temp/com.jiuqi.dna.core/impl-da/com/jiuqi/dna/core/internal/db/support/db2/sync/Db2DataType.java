package com.jiuqi.dna.core.internal.db.support.db2.sync;

import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.Always;
import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.Never;

import java.io.IOException;
import java.sql.Types;

import com.jiuqi.dna.core.impl.BinDBType;
import com.jiuqi.dna.core.impl.BlobDBType;
import com.jiuqi.dna.core.impl.CharDBType;
import com.jiuqi.dna.core.impl.CharsType;
import com.jiuqi.dna.core.impl.DataTypeBase;
import com.jiuqi.dna.core.impl.DateType;
import com.jiuqi.dna.core.impl.DoubleType;
import com.jiuqi.dna.core.impl.FloatType;
import com.jiuqi.dna.core.impl.IntType;
import com.jiuqi.dna.core.impl.LongType;
import com.jiuqi.dna.core.impl.NCharDBType;
import com.jiuqi.dna.core.impl.NTextDBType;
import com.jiuqi.dna.core.impl.NVarCharDBType;
import com.jiuqi.dna.core.impl.NumericDBType;
import com.jiuqi.dna.core.impl.ShortType;
import com.jiuqi.dna.core.impl.TextDBType;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.impl.VarCharDBType;
import com.jiuqi.dna.core.internal.db.sync.DbDataType;
import com.jiuqi.dna.core.internal.db.sync.TypeAlterability;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeFactory;

enum Db2DataType implements
		DbDataType<Db2Table, Db2Column, Db2DataType, Db2Index> {

	SMALLINT {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return ShortType.TYPE;
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			if (target == IntType.TYPE || target == LongType.TYPE) {
				return Always;
			} else if (target instanceof NumericDBType) {
				NumericDBType n = (NumericDBType) target;
				if (n.precision - n.scale >= 5) {
					return Always;
				}
			}
			return Never;
		}


		public DataType convertToDataType(Db2Column column) {
			return TypeFactory.BOOLEAN;
		}

	},

	INTEGER {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return IntType.TYPE;
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			if (target == LongType.TYPE) {
				return Always;
			} else if (target instanceof NumericDBType) {
				NumericDBType n = (NumericDBType) target;
				if (n.precision - n.scale >= 10) {
					return Always;
				}
			}
			return Never;
		}

		public DataType convertToDataType(Db2Column column) {
			return IntType.TYPE;
		}

	},

	BIGINT {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return IntType.TYPE;
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			if (target instanceof NumericDBType) {
				NumericDBType n = (NumericDBType) target;
				if (n.precision - n.scale >= 19) {
					return Always;
				}
			}
			return Never;
		}

		public DataType convertToDataType(Db2Column column) {
			return LongType.TYPE;
		}

	},

	REAL {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return FloatType.TYPE;
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			if (target == DoubleType.TYPE) {
				return Always;
			}
			return Never;
		}

		public DataType convertToDataType(Db2Column column) {
			return FloatType.TYPE;
		}

	},

	DOUBLE {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return DoubleType.TYPE;
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(Db2Column column) {
			return DoubleType.TYPE;
		}

	},

	DECIMAL {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return NumericDBType.map.get(length, precision, scale);
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			if (target instanceof NumericDBType) {
				NumericDBType n = (NumericDBType) target;
				if (n.precision - n.scale >= column.precision - column.scale && n.scale >= column.scale) {
					return Always;
				}
			}
			return Never;
		}

		@Override
		public void define(Db2Column column, Appendable s) {
			try {
				s.append("DECIMAL(");
				s.append(String.valueOf(column.precision));
				s.append(',');
				s.append(String.valueOf(column.scale));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(Db2Column column) {
			return NumericDBType.map.get(column.length, column.precision, column.scale);
		}

	},

	DATE {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return DateType.TYPE;
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(Db2Column column) {
			return DateType.TYPE;
		}

	},

	TIMESTAMP {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return DateType.TYPE;
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(Db2Column column) {
			return DateType.TYPE;
		}

	},

	CHARACTER {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return CharDBType.map.get(length, precision, scale);
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			if (column.forbitdata()) {
				if (target instanceof BinDBType) {
					SequenceDataType c = (SequenceDataType) target;
					if (c.getMaxLength() >= column.length) {
						return Always;
					}
				}
			} else if (target instanceof CharDBType || target instanceof VarCharDBType) {
				SequenceDataType c = (SequenceDataType) target;
				if (c.getMaxLength() >= column.length) {
					return Always;
				}
			} else if (target instanceof NCharDBType || target instanceof NVarCharDBType) {
				SequenceDataType c = (SequenceDataType) target;
				if (c.getMaxLength() * 2 >= column.length) {
					return Always;
				}
			}
			return Never;
		}

		@Override
		public void define(Db2Column column, Appendable s) {
			try {
				s.append("CHAR(");
				s.append(String.valueOf(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(Db2Column column) {
			DataType type = TypeFactory.CHAR(column.length);
			if(column.forbitdata() && column.length == 16) {
				type = TypeFactory.GUID;
			}
			return type;
		}

	},

	VARCHAR {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return VarCharDBType.map.get(length, precision, scale);
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			return CHARACTER.typeAlterable(column, target);
		}

		@Override
		public void define(Db2Column column, Appendable s) {
			try {
				s.append("VARCHAR(");
				s.append(String.valueOf(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(Db2Column column) {
			DataType type = TypeFactory.VARCHAR(column.length);
			if(column.forbitdata()) {
				type = TypeFactory.VARBINARY(column.length);
			}
			return type;
		}

	},

	CLOB {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return TextDBType.TYPE;
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(Db2Column column) {
			return TextDBType.TYPE;
		}

	},

	GRAPHIC {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return NCharDBType.map.get(length, precision, scale);
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			if (target instanceof NCharDBType || target instanceof NVarCharDBType) {
				CharsType c = (CharsType) target;
				if (c.getMaxLength() >= column.length) {
					return Always;
				}
			}
			return Never;
		}

		@Override
		public void define(Db2Column column, Appendable s) {
			try {
				s.append("GRAPHIC(");
				s.append(String.valueOf(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(Db2Column column) {
			return NCharDBType.map.get(column.length, column.precision, column.scale);
		}

	},

	VARGRAPHIC {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return NVarCharDBType.map.get(length, precision, scale);
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			return GRAPHIC.typeAlterable(column, target);
		}

		@Override
		public void define(Db2Column column, Appendable s) {
			try {
				s.append("VARGRAPHIC(");
				s.append(String.valueOf(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(Db2Column column) {
			return NVarCharDBType.map.get(column.length, column.precision, column.scale);
		}

	},

	DBCLOB {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return NTextDBType.TYPE;
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(Db2Column column) {
			return NTextDBType.TYPE;
		}

	},

	BLOB {

		@Override
		public DataTypeBase dnaTypeOf(int length, int precision, int scale) {
			return BlobDBType.TYPE;
		}

		@Override
		public TypeAlterability typeAlterable(Db2Column column, DataType target) {
			return Never;
		}

		public DataType convertToDataType(Db2Column column) {
			return BlobDBType.TYPE;
		}

	};

	public abstract DataTypeBase dnaTypeOf(int length, int precision, int scale);

	public abstract TypeAlterability typeAlterable(Db2Column column,
			DataType target);

	static final Db2DataType jdbcTypeOf(Db2Column column, int dataType,
			String typeName) {
		switch (dataType) {
		case Types.BINARY:
			column.codepage = 0;
			column.type = Db2DataType.CHARACTER;
			break;
		case Types.BIGINT:
			column.type = Db2DataType.BIGINT;
			break;
		case Types.SMALLINT:
			column.type = Db2DataType.SMALLINT;
			break;
		case Types.INTEGER:
			column.type = Db2DataType.INTEGER;
			break;
		case Types.REAL:
			column.type = Db2DataType.REAL;
			break;
		case Types.DOUBLE:
			column.type = Db2DataType.DOUBLE;
			break;
		case Types.DECIMAL:
		case Types.NUMERIC:
			column.type = Db2DataType.DECIMAL;
			break;
		case Types.CHAR:
			if (typeName.equals("CHAR")) {
				column.type = Db2DataType.CHARACTER;
			} else {
				column.type = Db2DataType.GRAPHIC;
			}
			break;
		case Types.VARCHAR:
			if (typeName.equals("VARCHAR")) {
				column.type = Db2DataType.VARCHAR;
			} else {
				column.type = Db2DataType.VARGRAPHIC;
			}
			break;
		}
		return null;
	}

	public void define(Db2Column column, Appendable s) {
		try {
			s.append(this.toString());
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final String toString(Db2Column column) {
		StringBuilder s = new StringBuilder();
		this.define(column, s);
		return s.toString();
	}
}
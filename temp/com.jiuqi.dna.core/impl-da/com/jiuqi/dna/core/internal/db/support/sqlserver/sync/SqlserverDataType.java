package com.jiuqi.dna.core.internal.db.support.sqlserver.sync;

import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.Always;
import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.ExceedExist;
import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.Never;

import java.io.IOException;

import com.jiuqi.dna.core.impl.BinDBType;
import com.jiuqi.dna.core.impl.BlobDBType;
import com.jiuqi.dna.core.impl.BooleanType;
import com.jiuqi.dna.core.impl.CharsType;
import com.jiuqi.dna.core.impl.DateType;
import com.jiuqi.dna.core.impl.DoubleType;
import com.jiuqi.dna.core.impl.FloatType;
import com.jiuqi.dna.core.impl.IntType;
import com.jiuqi.dna.core.impl.LongType;
import com.jiuqi.dna.core.impl.NTextDBType;
import com.jiuqi.dna.core.impl.NumericDBType;
import com.jiuqi.dna.core.impl.ShortType;
import com.jiuqi.dna.core.impl.TextDBType;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.db.sync.DbDataType;
import com.jiuqi.dna.core.internal.db.sync.TypeAlterability;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.TypeFactory;

enum SqlserverDataType
		implements
		DbDataType<SqlserverTable, SqlserverColumn, SqlserverDataType, SqlserverIndex> {

	BIT {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target == BooleanType.TYPE || target == ShortType.TYPE || target == IntType.TYPE || target == LongType.TYPE || target == FloatType.TYPE || target == DoubleType.TYPE || target instanceof NumericDBType) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("bit");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.BOOLEAN;
		}

	},

	TINYINT {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target == ShortType.TYPE || target == IntType.TYPE || target == LongType.TYPE || target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			} else if (target instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= 3) {
					return Always;
				}
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("tinyint");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return ShortType.TYPE;
		}
	},

	SMALLINT {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target == ShortType.TYPE || target == IntType.TYPE || target == LongType.TYPE || target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			} else if (target instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= 5) {
					return Always;
				}
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("smallint");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return ShortType.TYPE;
		}
	},

	INT {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target == ShortType.TYPE) {
				return ExceedExist;
			} else if (target == IntType.TYPE || target == LongType.TYPE || target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			} else if (target instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= 10) {
					return Always;
				}
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("int");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return IntType.TYPE;
		}
	},

	BIGINT {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target == ShortType.TYPE || target == IntType.TYPE) {
				return ExceedExist;
			} else if (target == LongType.TYPE || target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			} else if (target instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= 19) {
					return Always;
				}
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("bigint");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return LongType.TYPE;
		}
	},

	REAL {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target == ShortType.TYPE || target == IntType.TYPE || target == LongType.TYPE) {
				return ExceedExist;
			} else if (target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("real");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.FLOAT;
		}
	},

	FLOAT {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target == ShortType.TYPE || target == IntType.TYPE || target == LongType.TYPE) {
				return ExceedExist;
			} else if (target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("float");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.FLOAT;
		}
	},

	NUMERIC {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target == ShortType.TYPE || target == IntType.TYPE || target == LongType.TYPE) {
				return ExceedExist;
			} else if (target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			} else if (target instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= column.precision - column.scale && nt.scale >= column.scale) {
					return Always;
				}
			}
			return Never;
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.NUMERIC(column.precision, column.scale);
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("numeric(").append(Integer.toString(column.precision));
				s.append('.').append(Integer.toString(column.scale)).append(")");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}
	},

	DECIMAL {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			return NUMERIC.typeAlterable(column, target);
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("decimal(").append(Integer.toString(column.precision));
				s.append('.').append(Integer.toString(column.scale)).append(")");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.NUMERIC(column.precision, column.scale);
		}
	},

	SMALLMONEY {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("smallmoney");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			throw new UnsupportedOperationException();
		}
	},

	MONEY {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("money");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			throw new UnsupportedOperationException();
		}
	},

	CHAR {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target instanceof CharsType) {
				CharsType ct = (CharsType) target;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target == TextDBType.TYPE || target == NTextDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("char(").append(Integer.toString(column.length)).append(")");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		@Override
		final boolean isString() {
			return true;
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.CHAR(column.length);
		}
	},

	VARCHAR {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target instanceof CharsType) {
				CharsType ct = (CharsType) target;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target == TextDBType.TYPE || target == NTextDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("varchar(").append(Integer.toString(column.length)).append(")");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		@Override
		final boolean isString() {
			return true;
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.VARCHAR(column.length);
		}
	},

	TEXT {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target instanceof CharsType) {
				return ExceedExist;
			} else if (target == TextDBType.TYPE || target == NTextDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("varchar(max)");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		@Override
		final boolean isString() {
			return true;
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.TEXT;
		}
	},

	NCHAR {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target instanceof CharsType) {
				CharsType ct = (CharsType) target;
				if (ct.length >= 2 * column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target == TextDBType.TYPE || target == NTextDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("nchar(").append(Integer.toString(column.length)).append(")");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		@Override
		final boolean isString() {
			return true;
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.NCHAR(column.length);
		}
	},

	NVARCHAR {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target instanceof CharsType) {
				CharsType ct = (CharsType) target;
				if (ct.length >= 2 * column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target == TextDBType.TYPE || target == NTextDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("nvarchar(").append(Integer.toString(column.length)).append(")");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		@Override
		final boolean isString() {
			return true;
		}

		public DataType convertToDataType(SqlserverColumn column) {
			DataType type = null;
			if(column.length > 1000) {
				type = TypeFactory.NTEXT;
			}else {
				type = TypeFactory.NVARCHAR(column.length);
			}
			return type;
		}
	},

	NTEXT {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target instanceof CharsType) {
				return ExceedExist;
			} else if (target == TextDBType.TYPE || target == NTextDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("next(max)");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		@Override
		final boolean isString() {
			return true;
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.NTEXT;
		}
	},

	BINARY {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target instanceof BinDBType) {
				BinDBType bt = (BinDBType) target;
				if (bt.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target == BlobDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("binary(").append(Integer.toString(column.length)).append(")");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.BINARY(column.length);
		}
	},

	VARBINARY {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target instanceof BinDBType) {
				BinDBType bt = (BinDBType) target;
				if (bt.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target == BlobDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("varbinary(").append(Integer.toString(column.length)).append(")");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			DataType type = null;
			if(column.length > 2000) {
				type = TypeFactory.BLOB;
			}else {
				type = TypeFactory.VARBINARY(column.length);
			}
			return type;
		}
	},

	IMAGE {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target instanceof BinDBType) {
				return ExceedExist;
			} else if (target == BlobDBType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("varbinary(max)");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.BLOB;
		}
	},

	SMALLDATETIME {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target == DateType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("smalldatetime");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.DATE;
		}
	},

	DATETIME {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			if (target == DateType.TYPE) {
				return Always;
			}
			return Never;
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("datetime");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			return TypeFactory.DATE;
		}
	},

	TIMESTAMP {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("timestamp");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			throw new UnsupportedOperationException();
		}
	},

	UNIQUEIDENTIFIER {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			try {
				s.append("uniqueidentifier");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(SqlserverColumn column) {
			throw new UnsupportedOperationException();
		}
	},

	SQL_VARIANT {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			throw new UnsupportedOperationException();
		}

		public DataType convertToDataType(SqlserverColumn column) {
			throw new UnsupportedOperationException();
		}
	},

	XML {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			throw new UnsupportedOperationException();
		}

		public DataType convertToDataType(SqlserverColumn column) {
			throw new UnsupportedOperationException();
		}
	},

	SYSNAME {

		@Override
		public TypeAlterability typeAlterable(SqlserverColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final void define(SqlserverColumn column, Appendable s) {
			throw new UnsupportedOperationException();
		}

		public DataType convertToDataType(SqlserverColumn column) {
			throw new UnsupportedOperationException();
		}
	};

	static final SqlserverDataType typeOf(String type) {
		return valueOf(type.toUpperCase());
	}

	boolean isString() {
		return false;
	}

	public abstract TypeAlterability typeAlterable(SqlserverColumn column,
			DataType target);

	public abstract void define(SqlserverColumn column, Appendable s);

	public final String toString(SqlserverColumn column) {
		StringBuilder s = new StringBuilder();
		this.define(column, s);
		return s.toString();
	}

}
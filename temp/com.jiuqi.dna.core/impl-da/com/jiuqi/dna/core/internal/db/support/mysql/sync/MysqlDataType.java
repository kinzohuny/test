package com.jiuqi.dna.core.internal.db.support.mysql.sync;

import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.Always;
import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.ColumnNull;
import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.ExceedExist;

import java.io.IOException;

import com.jiuqi.dna.core.impl.BinDBType;
import com.jiuqi.dna.core.impl.BlobDBType;
import com.jiuqi.dna.core.impl.BooleanType;
import com.jiuqi.dna.core.impl.CharsType;
import com.jiuqi.dna.core.impl.DateType;
import com.jiuqi.dna.core.impl.DoubleType;
import com.jiuqi.dna.core.impl.FloatType;
import com.jiuqi.dna.core.impl.GUIDType;
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

enum MysqlDataType implements
		DbDataType<MysqlTable, MysqlColumn, MysqlDataType, MysqlIndex> {

	BIT {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			return Always;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("bit");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.BOOLEAN;
		}

	},

	TINYINT {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == ShortType.TYPE || target == IntType.TYPE || target == LongType.TYPE || target == FloatType.TYPE || target == DoubleType.TYPE || target == GUIDType.TYPE) {
				return Always;
			} else if (target instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= 3) {
					return Always;
				}
			} else if (target == BooleanType.TYPE) {
				return ExceedExist;
			} else if (target.isString() || target.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("tinyint");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.INT;
		}
	},

	SMALLINT {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == ShortType.TYPE || target == IntType.TYPE || target == LongType.TYPE || target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			} else if (target instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= 5) {
					return Always;
				}
			} else if (target == GUIDType.TYPE) {
				return Always;
			} else if (target == BooleanType.TYPE) {
				return ExceedExist;
			} else if (target.isString() || target.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("smallint");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.INT;
		}
	},

	MEDIUMINT {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == IntType.TYPE || target == LongType.TYPE || target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			} else if (target instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= 7) {
					return Always;
				}
			} else if (target == BooleanType.TYPE || target == ShortType.TYPE) {
				return ExceedExist;
			} else if (target.isString() || target.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("mediumint");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.INT;
		}
	},

	INT {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == IntType.TYPE || target == LongType.TYPE || target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			} else if (target instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= 10) {
					return Always;
				}
			} else if (target == BooleanType.TYPE || target == ShortType.TYPE) {
				return ExceedExist;
			} else if (target.isString() || target.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("int");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.INT;
		}
	},

	BIGINT {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == LongType.TYPE || target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			} else if (target instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= 19) {
					return Always;
				}
			} else if (target == BooleanType.TYPE || target == ShortType.TYPE || target == IntType.TYPE) {
				return ExceedExist;
			} else if (target.isString() || target.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("bigint");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.LONG;
		}
	},

	FLOAT {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target.isNumber()) {
				return ExceedExist;
			} else if (target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			} else if (target.isString() || target.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("float");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.FLOAT;
		}
	},

	DOUBLE {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target.isNumber()) {
				return ExceedExist;
			} else if (target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			} else if (target.isString() || target.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("double");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.DOUBLE;
		}
	},

	DECIMAL {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= column.precision - column.scale && nt.scale >= column.scale) {
					return Always;
				}
			} else if (target.isNumber()) {
				return ExceedExist;
			} else if (target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("decimal(");
				s.append(Integer.toString(column.precision));
				s.append(',');
				s.append(Integer.toString(column.scale));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.NUMERIC(column.precision, column.scale);
		}
	},

	YEAR {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == DateType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("year");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.DATE;
		}
	},

	DATE {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == DateType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("date");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.DATE;
		}
	},

	TIME {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == DateType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("time");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.DATE;
		}
	},

	DATETIME {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == DateType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("datetime");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.DATE;
		}
	},

	TIMESTAMP {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == DateType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("timestamp");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.DATE;
		}
	},

	CHAR {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == TextDBType.TYPE || target == NTextDBType.TYPE) {
				return Always;
			} else if (target instanceof CharsType) {
				CharsType ct = (CharsType) target;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				if (column.national()) {
					s.append('n');
				}
				s.append("char(");
				s.append(Integer.toString(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.NTEXT;
		}
	},

	VARCHAR {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == TextDBType.TYPE || target == NTextDBType.TYPE) {
				return Always;
			} else if (target instanceof CharsType) {
				CharsType ct = (CharsType) target;
				if (ct.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				if (column.national()) {
					s.append('n');
				}
				s.append("varchar(");
				s.append(Integer.toString(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.VARCHAR(column.length);
		}
	},

	TINYTEXT {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == TextDBType.TYPE || target == NTextDBType.TYPE) {
				return Always;
			} else if (target instanceof CharsType) {
				CharsType ct = (CharsType) target;
				if (ct.length >= 255) {
					return Always;
				} else {
					return ExceedExist;
				}
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("tinytext");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.NTEXT;
		}
	},

	TEXT {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == TextDBType.TYPE || target == NTextDBType.TYPE) {
				return Always;
			} else if (target instanceof CharsType) {
				return ExceedExist;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("text");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.NTEXT;
		}
	},

	MEDIUMTEXT {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == TextDBType.TYPE || target == NTextDBType.TYPE) {
				return Always;
			} else if (target instanceof CharsType) {
				return ExceedExist;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("mediumtext");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.NTEXT;
		}
	},

	LONGTEXT {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == TextDBType.TYPE || target == NTextDBType.TYPE) {
				return Always;
			} else if (target instanceof CharsType) {
				return ExceedExist;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("longtext");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.NTEXT;
		}
	},

	BINARY {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
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
			} else if (target == BlobDBType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("binary(");
				s.append(Integer.toString(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			DataType type = TypeFactory.BINARY(column.length);
			if(column.length == 16) {
				type = TypeFactory.GUID;
			}
			return type;
		}
	},

	VARBINARY {
		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
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
			} else if (target == BlobDBType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("varbinary(");
				s.append(Integer.toString(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.VARBINARY(column.length);
		}
	},

	TINYBLOB {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target == GUIDType.TYPE) {
				return ExceedExist;
			} else if (target instanceof BinDBType) {
				BinDBType bt = (BinDBType) target;
				if (bt.length >= 255) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target == BlobDBType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("tinyblob");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.BLOB;
		}
	},

	BLOB {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target instanceof BinDBType || target == GUIDType.TYPE) {
				return ExceedExist;
			} else if (target == BlobDBType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("blob");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.BLOB;
		}
	},

	MEDIUMBLOB {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target instanceof BinDBType || target == GUIDType.TYPE) {
				return ExceedExist;
			} else if (target == BlobDBType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("mediumblob");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.BLOB;
		}
	},

	LONGBLOB {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			if (target instanceof BinDBType || target == GUIDType.TYPE) {
				return ExceedExist;
			} else if (target == BlobDBType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(MysqlColumn column, Appendable s) {
			try {
				s.append("longblob");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(MysqlColumn column) {
			return TypeFactory.BLOB;
		}
	},

	SET {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		public void define(MysqlColumn column, Appendable s) {
			throw new UnsupportedOperationException();
		}

		public DataType convertToDataType(MysqlColumn column) {
			throw new UnsupportedOperationException();
		}
	},

	ENUM {

		@Override
		public TypeAlterability typeAlterable(MysqlColumn column,
				DataType target) {
			throw new UnsupportedOperationException();
		}

		public void define(MysqlColumn column, Appendable s) {
			throw new UnsupportedOperationException();
		}

		public DataType convertToDataType(MysqlColumn column) {
			throw new UnsupportedOperationException();
		}
	};

	public static final MysqlDataType typeOf(String typeName) {
		return valueOf(typeName.toUpperCase());
	}

	public abstract TypeAlterability typeAlterable(MysqlColumn column,
			DataType target);

	public final String toString(MysqlColumn column) {
		StringBuilder s = new StringBuilder();
		this.define(column, s);
		return s.toString();
	}
}
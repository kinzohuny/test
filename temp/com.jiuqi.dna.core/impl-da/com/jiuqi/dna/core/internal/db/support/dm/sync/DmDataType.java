package com.jiuqi.dna.core.internal.db.support.dm.sync;

import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.Always;
import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.ColumnNull;
import static com.jiuqi.dna.core.internal.db.sync.TypeAlterability.ExceedExist;

import java.io.IOException;

import com.jiuqi.dna.core.impl.BinDBType;
import com.jiuqi.dna.core.impl.BooleanType;
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
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.internal.db.sync.DbDataType;
import com.jiuqi.dna.core.internal.db.sync.TypeAlterability;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.TypeFactory;

enum DmDataType implements DbDataType<DmTable, DmColumn, DmDataType, DmIndex> {

	BIT() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			return Always;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("bit");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}


		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.BOOLEAN;
		}
	},

	SMALLINT() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
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

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("smallint");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.SHORT;
		}
	},

	INT() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
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

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("int");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.INT;
		}
	},

	BIGINT() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
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

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("bigint");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.LONG;
		}
	},

	NUMERIC() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			if (target instanceof NumericDBType) {
				NumericDBType nt = (NumericDBType) target;
				if (nt.precision - nt.scale >= column.precision - column.scale && nt.scale >= column.scale) {
					return Always;
				}
			} else if (target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			}
			// return ColumnNull;
			return TypeAlterability.Never;// TODO 临时
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("numeric(");
				s.append(Integer.toString(column.precision));
				s.append(',');
				s.append(Integer.toString(column.scale));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.NUMERIC(column.precision, column.scale);
		}
	},

	REAL() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			if (target.isNumber()) {
				return ExceedExist;
			} else if (target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			} else if (target.isString() || target.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("real");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.FLOAT;
		}
	},

	DOUBLE() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			if (target.isNumber()) {
				return ExceedExist;
			} else if (target == FloatType.TYPE || target == DoubleType.TYPE) {
				return Always;
			} else if (target.isString() || target.isBytes()) {
				return ExceedExist;
			}
			return ColumnNull;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("double");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.DOUBLE;
		}
	},

	CHAR() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			if (target instanceof CharsType) {
				CharsType ct = (CharsType) target;
				if (getCharLength(ct) >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			}
			return ColumnNull;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("char(");
				s.append(Integer.toString(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.CHAR(column.length);
		}
	},

	VARCHAR() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			if (target instanceof CharsType) {
				CharsType ct = (CharsType) target;
				if (getCharLength(ct) >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			}
			return ColumnNull;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("varchar(");
				s.append(Integer.toString(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.VARCHAR(column.length);
		}
	},

	NCHAR() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			if (target instanceof CharsType) {
				CharsType ct = (CharsType) target;
				if (getCharLength(ct) >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			}
			return ColumnNull;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("nchar(");
				s.append(Integer.toString(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.NCHAR(column.length);
		}
	},

	NVARCHAR() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			if (target instanceof CharsType) {
				CharsType ct = (CharsType) target;
				if (getCharLength(ct) >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			}
			return ColumnNull;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("nvarchar(");
				s.append(Integer.toString(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.NVARCHAR(column.length);
		}
	},

	CLOB() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			return TypeAlterability.Never;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("clob");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.TEXT;
		}
	},

	TEXT() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			return TypeAlterability.Never;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("text");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.TEXT;
		}
	},

	BINARY() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			if (target instanceof BinDBType) {
				BinDBType bt = (BinDBType) target;
				if (bt.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target == GUIDType.TYPE && column.length <= 16) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("binary(");
				s.append(Integer.toString(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			DataType type = null;
			if(column.length == 16) {
				type = TypeFactory.GUID;
			}else {
				type = TypeFactory.VARBINARY(column.length);
			}
			return type;
		}
	},

	VARBINARY() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			if (target instanceof BinDBType) {
				BinDBType bt = (BinDBType) target;
				if (bt.length >= column.length) {
					return Always;
				} else {
					return ExceedExist;
				}
			} else if (target == GUIDType.TYPE && column.length <= 16) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("varbinary(");
				s.append(Integer.toString(column.length));
				s.append(')');
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			DataType type = null;
			if(column.length == 16) {
				type = TypeFactory.GUID;
			}else {
				type = TypeFactory.VARBINARY(column.length);
			}
			return type;
		}
	},

	BLOB() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			return TypeAlterability.Never;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("blob");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.BLOB;
		}
	},

	IMAGE() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			return TypeAlterability.Never;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("image");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return TypeFactory.BLOB;
		}
	},

	TIMESTAMP() {

		public TypeAlterability typeAlterable(DmColumn column, DataType target) {
			if (target == DateType.TYPE) {
				return Always;
			}
			return ColumnNull;
		}

		public void define(DmColumn column, Appendable s) {
			try {
				s.append("timestamp(0)");
			} catch (IOException e) {
				throw Utils.tryThrowException(e);
			}
		}

		public DataType convertToDataType(DmColumn column) {
			return DateType.TYPE;
		}
	}

	;

	public String toString(DmColumn column) {
		StringBuilder s = new StringBuilder();
		this.define(column, s);
		return s.toString();
	}

	public static final DmDataType get(String name) {
		try {
			return valueOf(name);
		} catch (Throwable e) {
			if (name.equals("NUMBER")) {
				return NUMERIC;
			} else if (name.endsWith("FLOAT")) {
				return DOUBLE;
			} else if (name.equals("DEC")) {
				return NUMERIC;
			// DM里varchar2和varchar类型同义，兼容处理
			} else if (name.equals("VARCHAR2")) {
				return VARCHAR;
			} else if (name.equals("NVARCHAR2")) {
				return NVARCHAR;
			}
		}
		throw new UnsupportedOperationException(name);
	}

	final int getCharLength(CharsType type) {
		if(type instanceof NVarCharDBType || type instanceof NCharDBType) {
			return type.length * 2;
		}
		return type.length;
	}
}
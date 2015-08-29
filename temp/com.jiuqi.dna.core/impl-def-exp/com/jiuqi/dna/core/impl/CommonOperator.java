package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.statement.expr.NotBytesTypeException;
import com.jiuqi.dna.core.internal.da.statement.expr.NotDateTypeException;
import com.jiuqi.dna.core.internal.da.statement.expr.NotStringTypeException;
import com.jiuqi.dna.core.internal.db.support.db2.sqlbuffer.DB2ExprBuffer;

public enum CommonOperator implements OperatorIntrl {

	/**
	 * 加
	 */
	ADD {

		@Override
		public DataTypeInternal checkValues(ValueExpr[] values) {
			return ExprUtl.genericNumberType(this, values);
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			for (ValueExpr value : expr.values) {
				value.render(buffer, usages);
			}
			buffer.add(expr.values.length);
		}
	},

	/**
	 * 减
	 */
	SUB {

		@Override
		public DataTypeInternal checkValues(ValueExpr[] values) {
			return ExprUtl.genericNumberType(this, values);
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			for (ValueExpr value : expr.values) {
				value.render(buffer, usages);
			}
			buffer.sub(expr.values.length);
		}
	},

	/**
	 * 乘
	 */
	MUL {

		@Override
		public DataTypeInternal checkValues(ValueExpr[] values) {
			return ExprUtl.genericNumberType(this, values);
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			for (ValueExpr value : expr.values) {
				value.render(buffer, usages);
			}
			buffer.mul(expr.values.length);
		}
	},

	/**
	 * 除
	 */
	DIV {

		@Override
		public DataTypeInternal checkValues(ValueExpr[] values) {
			return ExprUtl.genericNumberType(this, values);
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			for (ValueExpr value : expr.values) {
				value.render(buffer, usages);
			}
			buffer.div(expr.values.length);
		}
	},

	/**
	 * 取负
	 */
	MINUS {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return ExprUtl.genericNumberType(this, values[0].getType());
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.minus();
		}
	},

	/**
	 * 取余
	 */
	MOD {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return ExprUtl.genericNumberType(this, values);
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			for (ValueExpr value : expr.values) {
				value.render(buffer, usages);
			}
			buffer.mod();
		}

	},

	/**
	 * 字符串连接
	 */
	STR_CONCAT {

		@Override
		public DataTypeInternal checkValues(ValueExpr[] values) {
			this.checkStringOrNull(values);
			return StringType.TYPE;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			if (buffer instanceof DB2ExprBuffer) {
				for (ValueExpr value : expr.values) {
					if (value == NullExpr.NULL) {
						StringConstExpr.EMPTY.render(buffer, usages);
					} else if (value instanceof RelationColumnRefImpl || value instanceof ArgumentRefExpr) {
						new OperateExpr(COALESCE, new ValueExpr[] { value, StringConstExpr.EMPTY }).render(buffer, usages);
					} else {
						value.render(buffer, usages);
					}
				}
			} else {
				for (ValueExpr value : expr.values) {
					value.render(buffer, usages);
				}
			}
			buffer.concat(expr.values.length);
		}
	},

	BIN_CONCAT {

		@Override
		public DataTypeInternal checkValues(ValueExpr[] values) {
			this.checkBytesOrNull(values);
			return BytesType.TYPE;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			for (ValueExpr value : expr.values) {
				value.render(buffer, usages);
			}
			buffer.bin_concat(expr.values.length);
		}

	},

	/**
	 * COALESCE(返回其参数中第一个非空表达式)
	 */
	COALESCE {

		@Override
		public DataTypeInternal checkValues(ValueExpr[] values) {
			DataTypeInternal ret = ExprUtl.checkSameCategory(this, values[0].getType(), values[1].getType());
			for (int i = 2; i < values.length; i++) {
				ret = ExprUtl.checkSameCategory(this, ret, values[i].getType());
			}
			return ret;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			if (buffer instanceof DB2ExprBuffer) {
				for (int i = 0; i < expr.values.length; i++) {
					ValueExpr value = expr.values[i];
					if (i == 0 && value instanceof ArgumentRefExpr) {
						ArgumentRefExpr are = (ArgumentRefExpr) value;
						are.renderWithRefer(buffer);
					} else {
						value.render(buffer, usages);
					}
				}
			} else {
				for (ValueExpr value : expr.values) {
					value.render(buffer, usages);
				}
			}
			buffer.coalesce(expr.values.length);
		}
	},

	/**
	 * SIMPLE_CASE
	 */
	SIMPLE_CASE {

		@Override
		public DataTypeInternal checkValues(ValueExpr[] values) {
			DataTypeInternal compare = ExprUtl.checkSameCategory(this, values[0].getType(), values[1].getType());
			DataTypeInternal returns = values[2].getType();
			int i = 4;
			for (; i < values.length; i += 2) {
				compare = ExprUtl.checkSameCategory(this, compare, values[i - 1].getType());
				returns = ExprUtl.checkSameCategory(this, returns, values[i].getType());
			}
			if (i == values.length) {
				returns = ExprUtl.checkSameCategory(this, returns, values[i - 1].getType());
			}
			return returns;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			for (ValueExpr value : expr.values) {
				value.render(buffer, usages);
			}
			buffer.simpleCase(expr.values.length);
		}

	},

	PARENT_RECID {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return GUIDType.TYPE;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			throw Utils.notImplemented();
		}
	},

	RELATIVE_ANCESTOR_RECID {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return GUIDType.TYPE;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			throw Utils.notImplemented();
		}
	},

	ABUSOLUTE_ANCESTOR_RECID {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return GUIDType.TYPE;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			throw Utils.notImplemented();
		}
	},

	LEVEVL_OF {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return IntType.TYPE;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			throw Utils.notImplemented();
		}
	};

	public boolean isNonDeterministic() {
		return false;
	}

	// HCL 不允许null,不允许lob
	final void checkStringOrNull(ValueExpr[] values) {
		for (ValueExpr value : values) {
			final DataTypeInternal type = value.getType();
			if (type.isString() || type == NullType.TYPE) {
				continue;
			}
			throw new NotStringTypeException(this, type);
		}
	}

	// HCL 不允许null,不允许lob
	final void checkBytesOrNull(ValueExpr[] values) {
		for (ValueExpr value : values) {
			final DataTypeInternal type = value.getType();
			if (type.isBytes() || type == NullType.TYPE) {
				continue;
			}
			throw new NotBytesTypeException(this, type);
		}
	}

	final void checkDateOrNull(ValueExpr expr) {
		if (expr.getType() != DateType.TYPE) {
			throw new NotDateTypeException(this, expr.getType());
		}
	}

	public DataTypeInternal checkValues(ValueExpr[] values) {
		DataTypeInternal type = null;
		for (ValueExpr value : values) {
			type = value.getType();
			if (!(type instanceof EnumTypeImpl<?>)) {
				return type;
			}
		}
		return type;
	}
}

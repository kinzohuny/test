package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.internal.da.statement.expr.NotNumberTypeException;
import com.jiuqi.dna.core.internal.da.statement.expr.NotSameCategoryTypeException;

public final class ExprUtl {

	public static final boolean containNullExpr(ValueExpr[] values) {
		if (values != null && values.length > 0) {
			for (ValueExpr v : values) {
				if (v == NullExpr.NULL) {
					return true;
				}
			}
		}
		return false;
	}

	public static final boolean constainEnumArgExpr(ValueExpr[] values) {
		if (values != null && values.length > 0) {
			for (ValueExpr value : values) {
				if (value instanceof ArgumentRefExpr) {
					ArgumentRefExpr arg = (ArgumentRefExpr) value;
					if (arg.arg.type instanceof EnumTypeImpl<?>) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static final void checkHierarchyPathValue(ValueExpr value) {
		try {
			TableFieldRefImpl fr = (TableFieldRefImpl) value;
			if (!(fr.getType() instanceof VarBinDBType)) {
				throw new IllegalArgumentException("级次谓词的运算体字段类型错误.");
			}
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("级次谓词的运算体不是字段引用表达式.");
		}
	}

	// public static final DataType checkNonDecimalNumber(ValueExpr expr, String
	// at) {
	// DataType type = expr.getType();
	// if (type == ByteType.TYPE || type == ShortType.TYPE
	// || type == IntType.TYPE || type == LongType.TYPE) {
	// return type;
	// } else if (type instanceof NumericDBType) {
	// NumericDBType nt = (NumericDBType) type;
	// if (nt.scale == 0) {
	// return nt;
	// }
	// }
	// throw new IllegalOperandDataTypeException(at.toString(), IntType.TYPE,
	// type);
	// }

	public static final DataTypeInternal checkSameCategory(Object op,
			DataTypeInternal left, DataTypeInternal right)
			throws NotSameCategoryTypeException {
		if (left == NullType.TYPE && right != NullType.TYPE) {
			return right;
		} else if (left != NullType.TYPE && right == NullType.TYPE) {
			return left;
		} else if (left == NullType.TYPE && right == NullType.TYPE) {
			return NullType.TYPE;
		} else if (left == GUIDType.TYPE && right == GUIDType.TYPE) {
			return GUIDType.TYPE;
		} else if (left == BooleanType.TYPE && right == BooleanType.TYPE) {
			return BooleanType.TYPE;
		} else if (left.isBytes() && right.isBytes()) {
			return BytesType.TYPE;
		} else if (left.isString() && right.isString()) {
			return StringType.TYPE;
		} else if (left == DateType.TYPE && right == DateType.TYPE) {
			return DateType.TYPE;
		} else {
			try {
				return genericNoneNullNumberType(op, left, right);
			} catch (NotNumberTypeException t) {
				throw new NotSameCategoryTypeException(op, left, right);
			}
		}
	}

	/**
	 * 检查为数值类型,并泛化.至少两个表达式
	 * 
	 * @param op
	 * @param values
	 * @return
	 */
	public static final DataTypeInternal genericNumberType(Object op,
			ValueExpr[] values) {
		DataTypeInternal ret = genericNumberType(op, values[0].getType(), values[1].getType());
		for (int i = 2; i < values.length; i++) {
			ret = genericNumberType(op, ret, values[i].getType());
		}
		return ret;
	}

	/**
	 * 检查都属于数值类型
	 * 
	 * @param left
	 * @param right
	 * @return 优先级更高的类型
	 */
	public static final DataTypeInternal genericNumberType(Object op,
			DataTypeInternal left, DataTypeInternal right) {
		if (left == NullType.TYPE && right != NullType.TYPE) {
			return right;
		} else if (left != NullType.TYPE && right == NullType.TYPE) {
			return left;
		} else if (left == NullType.TYPE && right == NullType.TYPE) {
			return NullType.TYPE;
		} else {
			return genericNoneNullNumberType(op, left, right);
		}
	}

	private static final DataTypeInternal genericNoneNullNumberType(Object op,
			DataTypeInternal left, DataTypeInternal right) {
		if (!isGenericNumber(left)) {
			throw new NotNumberTypeException(op, left);
		}
		if (!isGenericNumber(right)) {
			throw new NotNumberTypeException(op, right);
		}
		if (left == BooleanType.TYPE) {
			return genericNumberType(op, right);
		} else if (right == BooleanType.TYPE) {
			return genericNumberType(op, left);
		}
		final NumberType lg = left.isNumber() ? (NumberType) left : DoubleType.TYPE;
		final NumberType rg = left.isNumber() ? (NumberType) right : DoubleType.TYPE;
		return rg.getPrecedence().ordinal() > lg.getPrecedence().ordinal() ? rg : lg;
	}

	public static final DataTypeInternal genericType(Object op,
			DataTypeInternal type) {
		if (type.isString()) {
			return StringType.TYPE;
		} else if (type.isBytes()) {
			return BytesType.TYPE;
		} else if (type == DateType.TYPE) {
			return type;
		} else {
			return genericNumberType(op, type);
		}
	}

	public static final DataTypeInternal genericNumberType(Object op,
			DataTypeInternal type) {
		if (type == NullType.TYPE) {
			return type;
		} else if (type == BooleanType.TYPE || type == ShortType.TYPE || type == ByteType.TYPE || type == IntType.TYPE) {
			return IntType.TYPE;
		} else if (type.isNumber()) {
			return type;
		} else if (type == RefDataType.bigDecimalType || type == RefDataType.bigIntegerType) {
			return DoubleType.TYPE;
		}
		throw new NotNumberTypeException(op, type);
	}

	private static final boolean isGenericNumber(DataTypeInternal type) {
		return type.isNumber() || type == NullType.TYPE || type == BooleanType.TYPE || type == RefDataType.bigDecimalType || type == RefDataType.bigIntegerType;
	}

	public static final boolean isSimpleEquivalenceCondition(
			ConditionalExpr condition) {
		if (condition.isEqualsPredicate()) {
			PredicateExpr pe = (PredicateExpr) condition;
			if (pe.values[0] instanceof TableFieldRefImpl && pe.values[1] instanceof TableFieldRefImpl) {
				return true;
			}
		} else if (condition instanceof CombinedExpr) {
			CombinedExpr ce = (CombinedExpr) condition;
			if (!ce.isNot() && ce.isAnd()) {
				for (ConditionalExpr c : ce.conditions) {
					if (!isSimpleEquivalenceCondition(c)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
}
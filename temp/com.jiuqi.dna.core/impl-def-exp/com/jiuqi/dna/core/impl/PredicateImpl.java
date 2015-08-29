package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.exp.Predicate;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlPredicate;

/**
 * 谓词实现类
 * 
 * <p>
 * 谓词运算符广义上指表达式运算结果为条件表达式的运算符
 * 
 * @author houchunlei
 * 
 */
public enum PredicateImpl implements Predicate {

	LESS_THAN {

		@Override
		protected final void checkValues(ValueExpr[] values) {
			checkCompareDataType(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			if (isArgExpr(expr.values)) {
				renderArgValues(buffer, expr);
			} else {
				renderValues(buffer, expr, usages);
			}
			buffer.lt();
			renderNot(buffer, expr);
		}

	},

	LESS_THAN_OR_EQUAL_TO {

		@Override
		protected final void checkValues(ValueExpr[] values) {
			checkCompareDataType(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			if (isArgExpr(expr.values)) {
				renderArgValues(buffer, expr);
			} else {
				renderValues(buffer, expr, usages);
			}
			buffer.le();
			renderNot(buffer, expr);
		}

	},

	GREATER_THAN {

		@Override
		protected final void checkValues(ValueExpr[] values) {
			checkCompareDataType(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			if (isArgExpr(expr.values)) {
				renderArgValues(buffer, expr);
			} else {
				renderValues(buffer, expr, usages);
			}
			buffer.gt();
			renderNot(buffer, expr);
		}

	},

	GREATER_THAN_OR_EQUAL_TO {

		@Override
		protected final void checkValues(ValueExpr[] values) {
			checkCompareDataType(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			if (isArgExpr(expr.values)) {
				renderArgValues(buffer, expr);
			} else {
				renderValues(buffer, expr, usages);
			}
			buffer.ge();
			renderNot(buffer, expr);
		}

	},

	EQUAL_TO {

		@Override
		protected final void checkValues(ValueExpr[] values) {
			checkCompareDataType(this, values);
		}

		@Override
		final protected boolean canBeTableRelation() {
			return true;
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			if (isArgExpr(expr.values)) {
				renderArgValues(buffer, expr);
			} else {
				renderValues(buffer, expr, usages);
			}
			if (expr.not) {
				buffer.ne();
			} else {
				buffer.eq();
			}
		}

	},

	NOT_EQUAL_TO {

		@Override
		protected final void checkValues(ValueExpr[] values) {
			checkCompareDataType(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			if (isArgExpr(expr.values)) {
				renderArgValues(buffer, expr);
			} else {
				renderValues(buffer, expr, usages);
			}
			if (expr.not) {
				buffer.eq();
			} else {
				buffer.ne();
			}
		}

	},

	BETWEEN {

		@Override
		protected final void checkValues(ValueExpr[] values) {
			checkCompareDataType(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			if (isArgExpr(expr.values)) {
				renderArgValues(buffer, expr);
			} else {
				renderValues(buffer, expr, usages);
			}
			if (expr.not) {
				buffer.predicate(SqlPredicate.NOT_BETWEEN, expr.values.length);
			} else {
				buffer.predicate(SqlPredicate.BETWEEN, expr.values.length);
			}
		}

	},

	BETWEEN_EXCLUDE_LEFT_SIDE {

		@Override
		protected final void checkValues(ValueExpr[] values) {
			checkCompareDataType(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}

	},

	BETWEEN_EXCLUDE_RIGHT_SIDE {

		@Override
		protected final void checkValues(ValueExpr[] values) {
			checkCompareDataType(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}

	},

	BETWEEN_EXCLUDE_BOTH_SIDES {

		@Override
		protected final void checkValues(ValueExpr[] values) {
			checkCompareDataType(this, values);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}

	},

	IN {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			if (isArgExpr(expr.values)) {
				renderArgValues(buffer, expr);
			} else {
				renderValues(buffer, expr, usages);
			}
			if (expr.not) {
				buffer.predicate(SqlPredicate.NOT_IN, expr.values.length);
			} else {
				buffer.predicate(SqlPredicate.IN, expr.values.length);
			}
		}

	},

	STR_LIKE {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			if (expr.values[0] instanceof ArgumentRefExpr) {
				System.err.println("在运算[" + this + "]中，第一个运算体不能使用参数定义。");
			}
			renderValues(buffer, expr, usages);
			if (expr.not) {
				buffer.predicate(SqlPredicate.NOT_LIKE, expr.values.length);
			} else {
				buffer.predicate(SqlPredicate.LIKE, expr.values.length);
			}
		}

	},

	STR_STARTS_WITH {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			if (expr.values[0] instanceof ArgumentRefExpr) {
				System.err.println("在运算[" + this + "]中，第一个运算体不能使用参数定义。");
			}
			expr.values[0].render(buffer, usages);
			expr.values[1].render(buffer, usages);
			buffer.loadStr("%");
			buffer.loadStr("#%");
			buffer.replace();
			buffer.loadStr("_");
			buffer.loadStr("#_");
			buffer.replace();
			buffer.loadStr("%");
			buffer.concat(2);
			buffer.loadStr("#");
			buffer.predicate(SqlPredicate.LIKE, 3);
		}

	},

	STR_ENDS_WITH {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			if (expr.values[0] instanceof ArgumentRefExpr) {
				System.err.println("在运算[" + this + "]中，第一个运算体不能使用参数定义。");
			}
			expr.values[0].render(buffer, usages);
			buffer.loadStr("%");
			expr.values[1].render(buffer, usages);
			buffer.loadStr("%");
			buffer.loadStr("#%");
			buffer.replace();
			buffer.loadStr("_");
			buffer.loadStr("#_");
			buffer.replace();
			buffer.concat(2);
			buffer.loadStr("#");
			buffer.predicate(SqlPredicate.LIKE, 3);
		}

	},

	STR_CONTAINS {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			if (expr.values[0] instanceof ArgumentRefExpr) {
				System.err.println("在运算[" + this + "]中，第一个运算体不能使用参数定义。");
			}
			expr.values[0].render(buffer, usages);
			buffer.loadStr("%");
			expr.values[1].render(buffer, usages);
			buffer.loadStr("%");
			buffer.loadStr("#%");
			buffer.replace();
			buffer.loadStr("_");
			buffer.loadStr("#_");
			buffer.replace();
			buffer.loadStr("%");
			buffer.concat(3);
			buffer.loadStr("#");
			buffer.predicate(SqlPredicate.LIKE, 3);
		}

	},

	IS_NULL {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			ValueExpr value = expr.values[0];
			if (value instanceof ArgumentRefExpr) {
				ArgumentRefExpr arg = (ArgumentRefExpr) value;
				if (arg.isNoneEnumArg()) {
					arg.renderWithRefer(buffer);
				} else {
					arg.renderUsingRefer(buffer, IntType.TYPE);
				}
			} else {
				value.render(buffer, usages);
			}
			if (expr.not) {
				buffer.predicate(SqlPredicate.IS_NOT_NULL, 1);
			} else {
				buffer.predicate(SqlPredicate.IS_NULL, 1);
			}
		}

	},

	IS_NOT_NULL {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			ValueExpr value = expr.values[0];
			if (value instanceof ArgumentRefExpr) {
				ArgumentRefExpr arg = (ArgumentRefExpr) value;
				if (arg.isNoneEnumArg()) {
					arg.renderWithRefer(buffer);
				} else {
					arg.renderUsingRefer(buffer, IntType.TYPE);
				}
			} else {
				value.render(buffer, usages);
			}
			if (expr.not) {
				buffer.predicate(SqlPredicate.IS_NULL, expr.values.length);
			} else {
				buffer.predicate(SqlPredicate.IS_NOT_NULL, expr.values.length);
			}
		}
	},

	EXISTS {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			buffer.predicate(SqlPredicate.EXISTS, expr.values.length);
			if (expr.not) {
				buffer.not();
			}
		}
	},

	NOT_EXISTS {

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			renderValues(buffer, expr, usages);
			buffer.predicate(SqlPredicate.EXISTS, expr.values.length);
			if (!expr.not) {
				buffer.not();
			}

		}

	},

	IS_CHILD_OF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			ExprUtl.checkHierarchyPathValue(exprs[0]);
			ExprUtl.checkHierarchyPathValue(exprs[1]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}

	},

	IS_CHILD_OF_OR_SELF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			ExprUtl.checkHierarchyPathValue(exprs[0]);
			ExprUtl.checkHierarchyPathValue(exprs[1]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_DESCENDANT_OF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			ExprUtl.checkHierarchyPathValue(exprs[0]);
			ExprUtl.checkHierarchyPathValue(exprs[1]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_DESCENDANT_OF_OR_SELF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			ExprUtl.checkHierarchyPathValue(exprs[0]);
			ExprUtl.checkHierarchyPathValue(exprs[1]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_RELATIVE_DESCENDANT_OF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			ExprUtl.checkHierarchyPathValue(exprs[0]);
			ExprUtl.checkHierarchyPathValue(exprs[1]);
			// ExprUtl.checkNonDecimalNumber(exprs[2], this.toString());
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_RELATIVE_DESCENDANT_OF_OR_SELF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			ExprUtl.checkHierarchyPathValue(exprs[0]);
			ExprUtl.checkHierarchyPathValue(exprs[1]);
			// ExprUtl.checkNonDecimalNumber(exprs[2], this.toString());
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_RANGE_DESCENDANT_OF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			ExprUtl.checkHierarchyPathValue(exprs[0]);
			ExprUtl.checkHierarchyPathValue(exprs[1]);
			// ExprUtl.checkNonDecimalNumber(exprs[2], this.toString());
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_RANGE_DESCENDANT_OF_OR_SELF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			ExprUtl.checkHierarchyPathValue(exprs[0]);
			ExprUtl.checkHierarchyPathValue(exprs[1]);
			// ExprUtl.checkNonDecimalNumber(exprs[2], this.toString());
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_PARENT_OF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			ExprUtl.checkHierarchyPathValue(exprs[0]);
			ExprUtl.checkHierarchyPathValue(exprs[1]);
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	},

	IS_RELATIVE_ANCESTOR_OF {

		@Override
		protected void checkValues(ValueExpr[] exprs) {
			ExprUtl.checkHierarchyPathValue(exprs[0]);
			ExprUtl.checkHierarchyPathValue(exprs[1]);
			// ExprUtl.checkNonDecimalNumber(exprs[2], this.toString());
		}

		@Override
		void render(ISqlExprBuffer buffer, PredicateExpr expr,
				TableUsages usages) {
			throw Utils.notImplemented();
		}
	};

	/**
	 * 是否可作为表关系的条件
	 */
	protected boolean canBeTableRelation() {
		return false;
	}

	/**
	 * 检查表达式合法性
	 */
	protected void checkValues(ValueExpr[] values) {
	}

	private static final void checkCompareDataType(PredicateImpl predicate,
			ValueExpr[] values) {
		DataTypeInternal type = values[0].getType().getRootType();
		for (int i = 1, c = values.length; i < c; i++) {
			DataTypeInternal another = values[1].getType().getRootType();
			if (type.equals(another) || type.isNumber() && another.isNumber()) {
				continue;
			}
			if (type == BooleanType.TYPE && another.isNumber() || type.isNumber() && another == BooleanType.TYPE) {
				continue;
			}
			if (type == GUIDType.TYPE && another.isBytes() && !another.isLOB() || another == GUIDType.TYPE && type.isBytes() && !type.isLOB()) {
				continue;
			}
			switch (ContextVariableIntl.isStrictCompareDatatype()) {
			case 1:
				System.err.println(illegalCompareType(predicate, values));
				break;
			case 2:
				new UnsupportedOperationException(illegalCompareType(predicate, values)).printStackTrace();
				break;
			case 3:
				throw new UnsupportedOperationException(illegalCompareType(predicate, values));
			}
		}
	}

	private static final String illegalCompareType(PredicateImpl predicate,
			ValueExpr[] values) {
		return "比较运算[" + predicate + "]类型错误，运算体类型分别为[" + values[0].getType() + "]和[" + values[1].getType() + "]。";
	}

	abstract void render(ISqlExprBuffer buffer, PredicateExpr expr,
			TableUsages usages);

	private static final void renderValues(ISqlExprBuffer buffer,
			PredicateExpr expr, TableUsages usages) {
		DataTypeInternal firstNoneEnum = null;
		for (int i = 0; i < expr.values.length; i++) {
			ValueExpr value = expr.values[i];
			if (value.isNoneEnumArg()) {
				value.render(buffer, usages);
				if (firstNoneEnum == null) {
					firstNoneEnum = value.getType().getRootType();
				}
			} else {
				ArgumentRefExpr arg = (ArgumentRefExpr) value;
				if (firstNoneEnum == null) {
					for (int j = i + 1; j < expr.values.length; j++) {
						ValueExpr test = expr.values[j];
						if (test.isNoneEnumArg()) {
							firstNoneEnum = test.getType();
							break;
						}
					}
					if (firstNoneEnum == null) {
						firstNoneEnum = StringType.TYPE;
					}
				}
				arg.renderUsingRefer(buffer, firstNoneEnum);
			}
		}
	}

	private static final void renderNot(ISqlExprBuffer buffer,
			PredicateExpr expr) {
		if (expr.not) {
			buffer.not();
		}
	}

	private static final boolean isArgExpr(ValueExpr[] values) {
		for (ValueExpr value : values) {
			if (!(value instanceof ArgumentRefExpr)) {
				return false;
			}
		}
		return true;
	}

	private static final void renderArgValues(ISqlExprBuffer buffer,
			PredicateExpr expr) {
		DataTypeInternal firstNoneEnum = null;
		for (int i = 0; i < expr.values.length; i++) {
			ArgumentRefExpr arg = (ArgumentRefExpr) expr.values[i];
			if (arg.isNoneEnumArg()) {
				arg.renderWithRefer(buffer);
				if (firstNoneEnum == null) {
					firstNoneEnum = arg.getType();
				}
			} else if (firstNoneEnum == null) {
				for (int j = i + 1; j < expr.values.length; j++) {
					ArgumentRefExpr tf = (ArgumentRefExpr) expr.values[j];
					if (tf.isNoneEnumArg()) {
						firstNoneEnum = tf.getType();
						break;
					}
				}
				if (firstNoneEnum == null) {
					firstNoneEnum = StringType.TYPE;
					arg.renderUsingRefer(buffer, firstNoneEnum);
				} else {
					arg.renderUsingRefer(buffer, firstNoneEnum);
				}
			} else {
				arg.renderUsingRefer(buffer, firstNoneEnum);
			}
		}
	}
	// private UnsupportedOperationException firstNotArg(PredicateImpl
	// predicate) {
	// return new UnsupportedOperationException("在运算[" + predicate
	// + "]中,第一个运算体不能使用参数定义.");
	// }
}
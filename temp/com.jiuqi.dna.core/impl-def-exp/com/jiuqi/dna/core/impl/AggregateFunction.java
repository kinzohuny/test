package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;

public enum AggregateFunction implements OperatorIntrl {

	COUNT_ALL {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return IntType.TYPE;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.count(1, false);
		}
	},

	COUNT_DISTINCT {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return IntType.TYPE;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.count(1, true);
		}

	},

	COUNT_ASTERISK {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return IntType.TYPE;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			buffer.count(0, false);
		}

	},

	AVG_ALL {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return ExprUtl.genericNumberType(this, values[0].getType());
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.avg(false);
		}
	},

	AVG_DISTINCT {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return ExprUtl.genericNumberType(this, values[0].getType());
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.avg(true);
		}
	},

	SUM_ALL {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return ExprUtl.genericNumberType(this, values[0].getType());
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.sum(false);
		}
	},

	SUM_DISTINCT {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return ExprUtl.genericNumberType(this, values[0].getType());
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.sum(true);
		}
	},

	MIN {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return super.checkValues(values);
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.min();
		}

	},

	MAX {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			return super.checkValues(values);
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.max();
		}
	},

	GROUPING {

		@Override
		public final DataTypeInternal checkValues(ValueExpr[] values) {
			if (ContextVariableIntl.FORBID_GROUPING) {
				throw new UnsupportedOperationException("函数[grouping]已停止支持。");
			} else {
				System.err.println("函数[grouping]存在兼容问题，不建议继续使用。");
			}
			return IntType.TYPE;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.grouping();
		}
	};

	public DataTypeInternal checkValues(ValueExpr[] values) {
		DataTypeInternal type = null;
		for (ValueExpr value : values) {
			type = value.getType();
			if (!(type instanceof EnumTypeImpl<?>)) {
				return ExprUtl.genericType(this, type);
			}
		}
		return type;
	}

	public final boolean isNonDeterministic() {
		return true;
	}
}
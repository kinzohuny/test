package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.exp.Operator;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;

public enum AnalyticFunction implements Operator {

	SUM() {

		@Override
		public DataTypeInternal checkValue(ValueExpr r) {
			return r.getType().getRootType();
		}

		@Override
		public void render(ISqlExprBuffer buffer, TableUsages usages,
				AnalyticFunctionExpr expr) {
			expr.value.render(buffer, usages);
			if (expr.partitions != null) {
				for (ValueExpr ve : expr.partitions) {
					ve.render(buffer, usages);
				}
			}
			int desc = 0;
			for (int i = 0; i < expr.orderbys.length; i++) {
				OrderByItemImpl orderby = expr.orderbys[i];
				orderby.value.render(buffer, usages);
				if (orderby.isDesc()) {
					desc |= (1 << i);
				}
			}
			if (expr.preceding != null && (expr.preceding instanceof AnalyticFunctionExpr.LimitBound)) {
				AnalyticFunctionExpr.LimitBound pb = (AnalyticFunctionExpr.LimitBound) expr.preceding;
				pb.value.render(buffer, usages);
			}
			if (expr.following != null && (expr.following instanceof AnalyticFunctionExpr.LimitBound)) {
				AnalyticFunctionExpr.LimitBound fb = (AnalyticFunctionExpr.LimitBound) expr.following;
				fb.value.render(buffer, usages);
			}
			buffer.analytic("sum", expr.partitions == null ? 0 : expr.partitions.length, expr.orderbys.length, desc, expr.windowType, expr.preceding, expr.following);
		}

	},
	ROW_NUMBER() {

		@Override
		public DataTypeInternal checkValue(ValueExpr r) {
			return IntType.TYPE;
		}

		@Override
		public void render(ISqlExprBuffer buffer, TableUsages usages,
				AnalyticFunctionExpr expr) {
			if (expr.partitions != null) {
				for (ValueExpr ve : expr.partitions) {
					ve.render(buffer, usages);
				}
			}
			int desc = 0;
			for (int i = 0; i < expr.orderbys.length; i++) {
				OrderByItemImpl orderby = expr.orderbys[i];
				orderby.value.render(buffer, usages);
				if (orderby.isDesc()) {
					desc |= (1 << i);
				}
			}
			buffer.analytic("row_number", expr.partitions == null ? 0 : expr.partitions.length, expr.orderbys.length, desc);
		}
		
	},
	RANK() {

		@Override
		public DataTypeInternal checkValue(ValueExpr r) {
			return IntType.TYPE;
		}

		@Override
		public void render(ISqlExprBuffer buffer, TableUsages usages,
				AnalyticFunctionExpr expr) {
			if (expr.partitions != null) {
				for (ValueExpr ve : expr.partitions) {
					ve.render(buffer, usages);
				}
			}
			int desc = 0;
			for (int i = 0; i < expr.orderbys.length; i++) {
				OrderByItemImpl orderby = expr.orderbys[i];
				orderby.value.render(buffer, usages);
				if (orderby.isDesc()) {
					desc |= (1 << i);
				}
			}
			buffer.analytic("rank", expr.partitions == null ? 0 : expr.partitions.length, expr.orderbys.length, desc);
		}

		@Override
		public boolean isNonDeterministic() {
			return true;
		}
		
	},
	DENSE_RANK() {

		@Override
		public DataTypeInternal checkValue(ValueExpr r) {
			return IntType.TYPE;
		}

		@Override
		public void render(ISqlExprBuffer buffer, TableUsages usages,
				AnalyticFunctionExpr expr) {
			if (expr.partitions != null) {
				for (ValueExpr ve : expr.partitions) {
					ve.render(buffer, usages);
				}
			}
			int desc = 0;
			for (int i = 0; i < expr.orderbys.length; i++) {
				OrderByItemImpl orderby = expr.orderbys[i];
				orderby.value.render(buffer, usages);
				if (orderby.isDesc()) {
					desc |= (1 << i);
				}
			}
			buffer.analytic("dense_rank", expr.partitions == null ? 0 : expr.partitions.length, expr.orderbys.length, desc);
		}
		
	};

	public abstract DataTypeInternal checkValue(ValueExpr r);

	public boolean isNonDeterministic() {
		return false;
	}

	public abstract void render(ISqlExprBuffer buffer, TableUsages usages,
			AnalyticFunctionExpr expr);

}
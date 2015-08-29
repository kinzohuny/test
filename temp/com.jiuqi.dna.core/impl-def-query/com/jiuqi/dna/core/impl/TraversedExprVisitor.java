package com.jiuqi.dna.core.impl;

/**
 * ���ս���ʽ�����Ա�׼������Ϊʵ�֣��ս���ʽ���������ΪΪ�ա�
 * 
 * @author houchunlei
 * 
 * @param <TContext>
 */
public abstract class TraversedExprVisitor<TContext> extends
		ExprVisitor<TContext> {

	public void visitCombinedExpr(CombinedExpr expr, TContext context) {
		for (int i = 0; i < expr.conditions.length; i++) {
			expr.conditions[i].visit(this, context);
		}
	}

	public void visitHierarchyOperateExpr(HierarchyOperateExpr expr,
			TContext context) {
		expr.level.visit(this, context);
	}

	public void visitHierarchyPredicateExpr(HierarchyPredicateExpr expr,
			TContext context) {
		if (expr.level != null) {
			expr.level.visit(this, context);
		}
	}

	public void visitOperateExpr(OperateExpr expr, TContext context) {
		for (int i = 0; i < expr.values.length; i++) {
			expr.values[i].visit(this, context);
		}
	}

	public void visitAnalyticFunctionExpr(AnalyticFunctionExpr expr,
			TContext context) {
		if (expr.value != null) {
			expr.value.visit(this, context);
		}
		if (expr.partitions != null) {
			for (ValueExpr pe : expr.partitions) {
				pe.visit(this, context);
			}
		}
		for (OrderByItemImpl orderby : expr.orderbys) {
			orderby.value.visit(this, context);
		}
		if (expr.preceding != null) {
			expr.preceding.visit(this, context);
		}
		if (expr.following != null) {
			expr.following.visit(this, context);
		}
	}

	public void visitPredicateExpr(PredicateExpr expr, TContext context) {
		for (int i = 0; i < expr.values.length; i++) {
			expr.values[i].visit(this, context);
		}
	}

	public void visitSearchedCase(SearchedCaseExpr expr, TContext context) {
		for (int i = 0; i < expr.whens.length; i++) {
			expr.whens[i].visit(this, context);
			expr.thens[i].visit(this, context);
		}
		if (expr.other != null) {
			expr.other.visit(this, context);
		}
	}

	public void visitSubQueryExpr(SubQueryExpr expr, TContext context) {
		this.visitSubQuery(expr.subquery, context);
	}

	public void visitArgumentRefExpr(ArgumentRefExpr expr, TContext context) {
	}

	public void visitBooleanExpr(BooleanConstExpr value, TContext context) {
	}

	public void visitByteExpr(ByteConstExpr value, TContext context) {
	}

	public void visitBytesExpr(BytesConstExpr value, TContext context) {
	}

	public void visitDateExpr(DateConstExpr value, TContext context) {
	}

	public void visitDoubleExpr(DoubleConstExpr value, TContext context) {
	}

	public void visitFloatExpr(FloatConstExpr value, TContext context) {
	}

	public void visitGUIDExor(GUIDConstExpr value, TContext context) {
	}

	public void visitIntExpr(IntConstExpr value, TContext context) {
	}

	public void visitLongExpr(LongConstExpr value, TContext context) {
	}

	public void visitNullExpr(NullExpr expr, TContext context) {
	}

	public void visitSelectColumnRef(SelectColumnRefImpl expr, TContext context) {
	}

	public void visitShortExpr(ShortConstExpr value, TContext context) {
	}

	public void visitStringExpr(StringConstExpr value, TContext context) {
	}

	public void visitTableFieldRef(TableFieldRefImpl expr, TContext context) {
	}

	public void visitQueryColumnRef(QueryColumnRefExpr expr, TContext context) {
	}
}
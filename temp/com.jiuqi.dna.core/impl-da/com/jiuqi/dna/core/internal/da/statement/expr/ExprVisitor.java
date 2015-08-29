package com.jiuqi.dna.core.internal.da.statement.expr;

import com.jiuqi.dna.core.impl.ArgumentRefExpr;
import com.jiuqi.dna.core.impl.BooleanConstExpr;
import com.jiuqi.dna.core.impl.ByteConstExpr;
import com.jiuqi.dna.core.impl.BytesConstExpr;
import com.jiuqi.dna.core.impl.CombinedExpr;
import com.jiuqi.dna.core.impl.DateConstExpr;
import com.jiuqi.dna.core.impl.DoubleConstExpr;
import com.jiuqi.dna.core.impl.FloatConstExpr;
import com.jiuqi.dna.core.impl.GUIDConstExpr;
import com.jiuqi.dna.core.impl.HierarchyOperateExpr;
import com.jiuqi.dna.core.impl.HierarchyPredicateExpr;
import com.jiuqi.dna.core.impl.IntConstExpr;
import com.jiuqi.dna.core.impl.LongConstExpr;
import com.jiuqi.dna.core.impl.NullExpr;
import com.jiuqi.dna.core.impl.OperateExpr;
import com.jiuqi.dna.core.impl.QueryColumnRefExpr;
import com.jiuqi.dna.core.impl.SearchedCaseExpr;
import com.jiuqi.dna.core.impl.SelectColumnRefImpl;
import com.jiuqi.dna.core.impl.ShortConstExpr;
import com.jiuqi.dna.core.impl.StringConstExpr;
import com.jiuqi.dna.core.impl.SubQueryExpr;
import com.jiuqi.dna.core.impl.TableFieldRefImpl;

public interface ExprVisitor<TContext> {

	void visitBooleanExpr(BooleanConstExpr value, TContext context);

	void visitDateExpr(DateConstExpr value, TContext context);

	void visitGUIDExor(GUIDConstExpr value, TContext context);

	void visitByteExpr(ByteConstExpr value, TContext context);

	void visitShortExpr(ShortConstExpr value, TContext context);

	void visitIntExpr(IntConstExpr value, TContext context);

	void visitLongExpr(LongConstExpr value, TContext context);

	void visitFloatExpr(FloatConstExpr value, TContext context);

	void visitDoubleExpr(DoubleConstExpr value, TContext context);

	void visitStringExpr(StringConstExpr value, TContext context);

	void visitBytesExpr(BytesConstExpr value, TContext context);

	void visitNullExpr(NullExpr expr, TContext context);

	void visitArgumentRefExpr(ArgumentRefExpr expr, TContext context);

	void visitHierarchyOperateExpr(HierarchyOperateExpr expr, TContext context);

	void visitOperateExpr(OperateExpr expr, TContext context);

	void visitSelectColumnRef(SelectColumnRefImpl expr, TContext context);

	void visitQueryColumnRef(QueryColumnRefExpr expr, TContext context);

	void visitTableFieldRef(TableFieldRefImpl expr, TContext context);

	void visitSearchedCase(SearchedCaseExpr expr, TContext context);

	void visitSubQueryExpr(SubQueryExpr expr, TContext context);

	void visitCombinedExpr(CombinedExpr expr, TContext context);

	void visitHierarchyPredicateExpr(HierarchyPredicateExpr expr,
			TContext context);
}
package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.OperateExpr;
import com.jiuqi.dna.core.impl.TableUsages;

public final class InsertStatementStatusVisitor extends TableUsages {

	private boolean isValuesNonDeterministic;

	public InsertStatementStatusVisitor() {
	}

	public final boolean isValuesNonDeterministic() {
		return this.isValuesNonDeterministic;
	}

	@Override
	public void visitOperateExpr(OperateExpr expr, Object context) {
		super.visitOperateExpr(expr, context);
		if (expr.isNonDeterministic()) {
			this.isValuesNonDeterministic = true;
		}
	}
}
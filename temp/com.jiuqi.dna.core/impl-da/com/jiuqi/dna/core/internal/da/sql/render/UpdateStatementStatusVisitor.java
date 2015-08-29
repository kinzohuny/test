package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.MoJoinedQueryRef;
import com.jiuqi.dna.core.impl.MoJoinedTableRef;
import com.jiuqi.dna.core.impl.SelectColumnRefImpl;
import com.jiuqi.dna.core.impl.TableFieldRefImpl;
import com.jiuqi.dna.core.impl.TableUsages;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl.FieldAssign;

public final class UpdateStatementStatusVisitor extends TableUsages {

	public UpdateStatementStatusVisitor(UpdateStatementImpl update) {
		update.visit(this, null);
	}

	private boolean visitingAssignValue;

	final boolean visitingAssignValue() {
		return this.visitingAssignValue;
	}

	private boolean assignValueFromJoinedRef;

	final boolean assignValueFromJoinedRef() {
		return this.assignValueFromJoinedRef;
	}

	@Override
	public void visitUpdateAssign(FieldAssign assign, Object context) {
		this.visitingAssignValue = true;
		super.visitUpdateAssign(assign, context);
		this.visitingAssignValue = false;
	}

	@Override
	public void visitSelectColumnRef(SelectColumnRefImpl expr, Object context) {
		super.visitSelectColumnRef(expr, context);
		if (this.visitingAssignValue && expr.queryRef instanceof MoJoinedQueryRef) {
			this.assignValueFromJoinedRef = true;
		}
	}

	@Override
	public void visitTableFieldRef(TableFieldRefImpl expr, Object context) {
		super.visitTableFieldRef(expr, context);
		if (this.visitingAssignValue && expr.tableRef instanceof MoJoinedTableRef) {
			this.assignValueFromJoinedRef = true;
		}
	}

}
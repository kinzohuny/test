package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldRefImpl;
import com.jiuqi.dna.core.impl.TableUsage;
import com.jiuqi.dna.core.impl.TableUsages;

public final class DeleteStatementStatusVisitor extends TableUsages {

	public final TableDefineImpl target;

	public final TableUsage conditionSource;

	public DeleteStatementStatusVisitor(DeleteStatementImpl delete) {
		this.target = delete.moTableRef.target;
		this.conditionSource = new TableUsage(delete.moTableRef);
	}

	@Override
	public void visitTableFieldRef(TableFieldRefImpl expr, Object context) {
		super.visitTableFieldRef(expr, context);
		if (expr.field.owner == this.target) {
			this.conditionSource.use(expr.field.getDBTable());
		}
	}
}
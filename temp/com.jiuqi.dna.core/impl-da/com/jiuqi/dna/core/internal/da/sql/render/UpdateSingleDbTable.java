package com.jiuqi.dna.core.internal.da.sql.render;

import java.util.ArrayList;
import java.util.BitSet;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.MoJoinedQueryRef;
import com.jiuqi.dna.core.impl.MoJoinedTableRef;
import com.jiuqi.dna.core.impl.SelectColumnRefImpl;
import com.jiuqi.dna.core.impl.TableFieldRefImpl;
import com.jiuqi.dna.core.impl.TraversedExprVisitor;
import com.jiuqi.dna.core.impl.UpdateStatementImpl.FieldAssign;

/**
 * 物理表的更新信息
 * 
 * <p>
 * 同时是赋值依赖，赋值来源的检查器
 * 
 * @author houchunlei
 */
public final class UpdateSingleDbTable extends TraversedExprVisitor<Object> {

	final ArrayList<FieldAssign> assigns = new ArrayList<FieldAssign>();
	final UpdateMultipleResolver resolver;
	final DBTableDefineImpl dbTable;
	final int dbTableIndex;
	final BitSet assignValueUsingTables = new BitSet();

	UpdateSingleDbTable(UpdateMultipleResolver resolver,
			DBTableDefineImpl dbTable) {
		this.resolver = resolver;
		this.dbTable = dbTable;
		this.dbTableIndex = dbTable.index();
	}

	final void visitAssignValue() {
		for (int i = 0, c = this.assigns.size(); i < c; i++) {
			FieldAssign fa = this.assigns.get(i);
			fa.value().visit(this, null);
		}
	}

	private boolean assignValueFromJoinedRef;

	private boolean assignValueFromTargetRefOnSlave;

	boolean assignValueFromJoin() {
		return this.assignValueFromJoinedRef || this.assignValueFromTargetRefOnSlave;
	}

	@Override
	public void visitSelectColumnRef(SelectColumnRefImpl expr, Object context) {
		if (expr.queryRef instanceof MoJoinedQueryRef) {
			this.assignValueFromJoinedRef = true;
		}
	}

	@Override
	public void visitTableFieldRef(TableFieldRefImpl fieldRef, Object context) {
		if (fieldRef.tableRef instanceof MoJoinedTableRef) {
			this.assignValueFromJoinedRef = true;
		}
		DBTableDefineImpl dbTable = fieldRef.field.getDBTable();
		if (fieldRef.field.owner == this.resolver.table && dbTable != this.dbTable) {
			if (fieldRef.tableRef == this.resolver.update.moTableRef) {
				this.assignValueFromTargetRefOnSlave = true;
			}
			if (this.resolver.update.assigns.contains(fieldRef.field)) {
				this.assignValueUsingTables.set(dbTable.index());
				this.resolver.assignValueDependOn = true;
			}
		}
	}

	@Override
	public final String toString() {
		return "dbUpdate[" + this.dbTableIndex + "]";
	}
}
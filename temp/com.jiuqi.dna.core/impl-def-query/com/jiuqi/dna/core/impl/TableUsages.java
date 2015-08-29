package com.jiuqi.dna.core.impl;

import java.util.HashMap;

public class TableUsages extends TraversedExprVisitor<Object> {

	private final HashMap<TableRef, TableUsage> usages = new HashMap<TableRef, TableUsage>();

	public final TableUsage usageOf(TableRef tableRef) {
		return this.usages.get(tableRef);
	}

	@Override
	public void visitTableFieldRef(TableFieldRefImpl expr, Object context) {
		TableUsage usage = this.usages.get(expr.tableRef);
		if (usage == null) {
			usage = new TableUsage(expr.tableRef);
			this.usages.put(expr.tableRef, usage);
		}
		usage.use(expr.field.getDBTable());
	}

	public final TableUsage ensureUsageOf(TableRef tableRef) {
		TableUsage usage = this.usages.get(tableRef);
		if (usage == null) {
			usage = new TableUsage(tableRef);
			this.usages.put(tableRef, usage);
		}
		return usage;
	}
}
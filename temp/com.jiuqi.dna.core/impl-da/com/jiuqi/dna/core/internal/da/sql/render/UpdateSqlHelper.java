package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.MoRootTableRef;
import com.jiuqi.dna.core.impl.TableUsage;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl.FieldAssign;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;

final class UpdateSqlHelper {

	static final void multiple(UpdateStatementImpl update,
			UpdateStatementStatusVisitor status, UpdateSingleDbTable single,
			ISqlUpdateBuffer buffer, String alias) {
		join(buffer.target(), alias, update.moTableRef, status, single.dbTable);
		for (FieldAssign fa : single.assigns) {
			fa.value().render(buffer.newValue(fa.field.namedb()), status);
		}
		if (update.getCondition() != null) {
			update.getCondition().render(buffer.where(), status);
		}
	}

	static final void join(ISqlTableRefBuffer from, String alias,
			MoRootTableRef tableRef, UpdateStatementStatusVisitor status,
			DBTableDefineImpl except) {
		TableUsage usage = status.usageOf(tableRef);
		if (usage != null) {
			for (DBTableDefineImpl dbTable : usage.tables()) {
				if (dbTable == except) {
					continue;
				}
				String ja = Render.aliasOf(tableRef, dbTable);
				Render.renderLeftJoinOnRecidEq(from, alias, dbTable.name, ja);
			}
		}
		tableRef.render(from, status);
	}
}
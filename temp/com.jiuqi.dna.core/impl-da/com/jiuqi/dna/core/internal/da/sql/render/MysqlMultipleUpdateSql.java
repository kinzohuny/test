package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.MoRootTableRef;
import com.jiuqi.dna.core.impl.TableUsage;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl.FieldAssign;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateMultiBuffer;
import com.jiuqi.dna.core.internal.db.support.mysql.MysqlMetadata;

public final class MysqlMultipleUpdateSql extends SimpleModifySql {

	public MysqlMultipleUpdateSql(MysqlMetadata dbMetadata,
			UpdateStatementImpl update, UpdateStatementStatusVisitor visitor) {
		final MoRootTableRef tableRef = update.moTableRef;
		TableUsage usage = visitor.ensureUsageOf(tableRef);
		for (int i = 0; i < update.assigns.size(); i++) {
			usage.use(update.assigns.get(i).field.dbTable);
		}
		ISqlUpdateMultiBuffer buffer = null;
		String alias = null;
		for (DBTableDefineImpl dbTable : usage.tables()) {
			if (buffer == null) {
				alias = Render.aliasOf(tableRef, dbTable);
				buffer = dbMetadata.sqlbuffers().updateMultiple(dbTable.namedb(), alias);
			} else {
				if (alias == null) {
					throw new IllegalStateException();
				}
				String ja = Render.aliasOf(tableRef, dbTable);
				Render.renderLeftJoinOnRecidEq(buffer.target(), alias, dbTable.namedb(), ja);
			}
		}
		if (buffer == null) {
			throw new IllegalStateException();
		}
		tableRef.render(buffer.target(), visitor);
		for (int i = 0; i < update.assigns.size(); i++) {
			FieldAssign fa = update.assigns.get(i);
			ISqlExprBuffer value = buffer.newValue(Render.aliasOf(tableRef, fa.field.dbTable), fa.field.namedb());
			fa.value().render(value, visitor);
		}
		if (update.getCondition() != null) {
			update.getCondition().render(buffer.where(), visitor);
		}
		this.build(buffer);
	}
}
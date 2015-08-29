package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl.FieldAssign;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class UpdateSingleSql extends SimpleModifySql {

	public UpdateSingleSql(DbMetadata dbMetadata, UpdateStatementImpl update,
			UpdateStatementStatusVisitor status) {
		ISqlUpdateBuffer buffer = dbMetadata.sqlbuffers().update(update.moTableRef.target.primary.namedb(), update.moTableRef.name, status.assignValueFromJoinedRef());
		update.moTableRef.render(buffer.target(), status);
		for (int i = 0; i < update.assigns.size(); i++) {
			FieldAssign fa = update.assigns.get(i);
			ISqlExprBuffer value = buffer.newValue(fa.field.namedb());
			fa.value().render(value, status);
		}
		if (update.getCondition() != null) {
			update.getCondition().render(buffer.where(), status);
		}
		this.build(buffer);
	}

	public UpdateSingleSql(DbMetadata dbMetadata, UpdateStatementImpl update,
			UpdateStatementStatusVisitor status, UpdateSingleDbTable single) {
		final String alias = Render.aliasOf(update.moTableRef, single.dbTable);
		ISqlUpdateBuffer buffer = dbMetadata.sqlbuffers().update(single.dbTable.namedb(), alias, single.assignValueFromJoin());
		UpdateSqlHelper.multiple(update, status, single, buffer, alias);
		this.build(buffer);
	}
}
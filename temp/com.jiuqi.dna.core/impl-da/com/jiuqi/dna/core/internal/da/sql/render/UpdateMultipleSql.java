package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl.FieldAssign;
import com.jiuqi.dna.core.internal.da.sql.execute.MultipleSqlModifier;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class UpdateMultipleSql extends MultipleSql implements ModifySql {

	public UpdateMultipleSql(DbMetadata dbMetadata, UpdateStatementImpl update,
			UpdateStatementStatusVisitor visitor,
			UpdateMultipleResolver resolver) {
		if (resolver.updateByRecidAndRecver()) {
			for (int i = 0; i < resolver.dbTables.size(); i++) {
				final UpdateSingleDbTable single = resolver.dbTables.get(i);
				final String alias = Render.aliasOf(update.moTableRef, single.dbTable);
				ISqlUpdateBuffer buffer = dbMetadata.sqlbuffers().update(single.dbTable.namedb(), alias, single.assignValueFromJoin());
				if (i == 0) {
					UpdateSqlHelper.multiple(update, visitor, single, buffer, alias);
					this.build(buffer);
				} else {	//根据recid更新从表
					for (FieldAssign fa : single.assigns) {
						fa.value().render(buffer.newValue(fa.field.namedb()), visitor);
					}
					ISqlExprBuffer where = buffer.where();
					where.loadColumnRef(alias, TableDefineImpl.FIELD_DBNAME_RECID);
					resolver.recidValue.render(where, visitor);
					where.eq();
					this.addSql().build(buffer);
				}
			}
		} else if (resolver.tryResolveSequence()) {
			for (int i = 0; i < resolver.dbTables.size(); i++) {
				final UpdateSingleDbTable single = resolver.dbTables.get(i);
				final String alias = Render.aliasOf(update.moTableRef, single.dbTable);
				ISqlUpdateBuffer buffer = dbMetadata.sqlbuffers().update(single.dbTable.namedb(), alias, single.assignValueFromJoin());
				UpdateSqlHelper.multiple(update, visitor, single, buffer, alias);
				if (i == 0) {
					this.build(buffer);
				} else {
					this.addSql().build(buffer);
				}
			}
		}
	}

	public MultipleSqlModifier newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new MultipleSqlModifier(adapter, this, notify);
	}
}
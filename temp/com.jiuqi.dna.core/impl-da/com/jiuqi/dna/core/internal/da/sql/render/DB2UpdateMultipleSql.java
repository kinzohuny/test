package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl.FieldAssign;
import com.jiuqi.dna.core.internal.da.sql.execute.MultipleSqlModifier;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlConditionBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class DB2UpdateMultipleSql extends MultipleSql implements ModifySql {

	public DB2UpdateMultipleSql(DbMetadata dbMetadata, UpdateStatementImpl update,
			UpdateStatementStatusVisitor visitor,
			UpdateMultipleResolver resolver) {
		final ISqlCommandFactory factory = dbMetadata.sqlbuffers();
		final ISqlSegmentBuffer segment = factory.segment();
		if (resolver.updateByRecidAndRecver()) {
			ISqlSegmentBuffer then = null;
			for (int i = 0; i < resolver.dbTables.size(); i++) {
				final UpdateSingleDbTable single = resolver.dbTables.get(i);
				final String alias = Render.aliasOf(update.moTableRef, single.dbTable);
				if (i == 0) {
					ISqlUpdateBuffer buffer = segment.update(single.dbTable.namedb(), alias, single.assignValueFromJoin());
					UpdateSqlHelper.multiple(update, visitor, single, buffer, alias);
					// 获取更新条数
					segment.assign("$ROWCOUNT").loadVar("ROW_COUNT");
					// if else条件
					ISqlConditionBuffer ifElse = segment.ifThenElse();
					ifElse.newWhen().loadVar("$ROWCOUNT").load(0).gt();
					then = ifElse.newThen();
				} else {
					ISqlUpdateBuffer buffer = then.update(single.dbTable.namedb(), alias, single.assignValueFromJoin());
					// 根据recid更新从表	
					for (FieldAssign fa : single.assigns) {
						fa.value().render(buffer.newValue(fa.field.namedb()), visitor);
					}
					ISqlExprBuffer where = buffer.where();
					resolver.recidValue.render(where, visitor);
					where.loadColumnRef(alias, TableDefineImpl.FIELD_DBNAME_RECID);
					where.eq();
				}
			}
		} else if (resolver.tryResolveSequence()) {
			for (UpdateSingleDbTable single : resolver.dbTables) {
				final String alias = Render.aliasOf(update.moTableRef, single.dbTable);
				ISqlUpdateBuffer buffer = segment.update(single.dbTable.namedb(), alias, single.assignValueFromJoin());
				UpdateSqlHelper.multiple(update, visitor, single, buffer, alias);
			}
		}
		this.build(segment);
	}
	
	public MultipleSqlModifier newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new MultipleSqlModifier(adapter, this, notify);
	}
}
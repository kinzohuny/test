package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.MoJoinedRelationRef;
import com.jiuqi.dna.core.impl.PlainSql;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableUsage;
import com.jiuqi.dna.core.internal.da.sql.execute.KingbaseMultipleSqlModifier;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class KingbaseMultiDeleteSql extends MultiSql implements ModifySql {

	public final KingbaseMultipleSqlModifier newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new KingbaseMultipleSqlModifier(adapter, this, notify);
	}

	public KingbaseMultiDeleteSql(DbMetadata dbMetadata,
			DeleteStatementImpl delete) {
		final DeleteStatementStatusVisitor status = new DeleteStatementStatusVisitor(delete);
		delete.visit(status, null);
		final TableDefineImpl target = delete.moTableRef.target;
		DBTableDefineImpl last = status.conditionSource.firstTable();
		for (int i = 0, c = target.dbTables.size(); i < c; i++) {
			DBTableDefineImpl dbTable = target.dbTables.get(i);
			if (dbTable == last) {
				continue;
			}
			final String alias = Render.aliasOf(delete.moTableRef, dbTable);
			ISqlDeleteBuffer buffer = dbMetadata.sqlbuffers().delete(dbTable.namedb(), alias);
			ISqlTableRefBuffer trb = buffer.target();
			TableUsage usage = status.usageOf(delete.moTableRef);
			if (usage != null) {
				for (DBTableDefineImpl tojoin : usage.tables()) {
					if (tojoin != dbTable) {
						Render.renderLeftJoinOnRecidEq(trb, alias, tojoin.namedb(), Render.aliasOf(delete.moTableRef, tojoin));
					}
				}
			}
			MoJoinedRelationRef join = delete.moTableRef.getJoins();
			if (join != null) {
				join.render(trb, status);
			}
			if (delete.getCondition() != null) {
				delete.getCondition().render(buffer.where(), status);
			}
			PlainSql sqlinfo = new PlainSql();
			sqlinfo.build(buffer);
			this.sqls.add(sqlinfo);
		}
		if (last != null) {
			ISqlDeleteBuffer buffer = dbMetadata.sqlbuffers().delete(last.namedb(), Render.aliasOf(delete.moTableRef, last));
			final String alias = Render.aliasOf(delete.moTableRef, last);
			ISqlTableRefBuffer trb = buffer.target();
			TableUsage usage = status.usageOf(delete.moTableRef);
			if (usage != null) {
				for (DBTableDefineImpl tojoin : usage.tables()) {
					if (tojoin != last) {
						Render.renderLeftJoinOnRecidEq(trb, alias, tojoin.namedb(), Render.aliasOf(delete.moTableRef, tojoin));
					}
				}
			}
			MoJoinedRelationRef join = delete.moTableRef.getJoins();
			if (join != null) {
				join.render(trb, status);
			}
			if (delete.getCondition() != null) {
				delete.getCondition().render(buffer.where(), status);
			}
			PlainSql sqlinfo = new PlainSql();
			sqlinfo.build(buffer);
			this.sqls.add(sqlinfo);
		}
	}
}
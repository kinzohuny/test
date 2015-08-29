package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.MoJoinedRelationRef;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableUsage;
import com.jiuqi.dna.core.internal.da.sql.execute.MultipleSqlModifier;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlModifier;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class DeleteMultipleSql extends MultipleSql implements ModifySql {

	public DeleteMultipleSql(DbMetadata dbMetadata, DeleteStatementImpl delete,
			DeleteStatementStatusVisitor visitor) {
		final TableDefineImpl target = delete.moTableRef.target;
		DBTableDefineImpl last = visitor.conditionSource.firstTable();
		boolean isFisrt = true;
		for (int i = 0, c = target.dbTables.size(); i < c; i++) {
			DBTableDefineImpl dbTable = target.dbTables.get(i);
			if (dbTable == last) {
				continue;
			}
			ISqlDeleteBuffer db = this.deleteSingleFor(dbMetadata.sqlbuffers(), delete, dbTable, visitor);
			if (isFisrt) {
				this.build(db);
				isFisrt = false;
			} else {
				this.addSql().build(db);
			}
		}
		if (last != null) {
			this.addSql().build(this.deleteSingleFor(dbMetadata.sqlbuffers(), delete, last, visitor));
		}
	}

	private final ISqlDeleteBuffer deleteSingleFor(ISqlCommandFactory factory,
			DeleteStatementImpl delete, DBTableDefineImpl dbTable,
			DeleteStatementStatusVisitor status) {
		final String alias = Render.aliasOf(delete.moTableRef, dbTable);
		ISqlDeleteBuffer ds = factory.delete(dbTable.namedb(), alias);
		ISqlTableRefBuffer trb = ds.target();
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
			delete.getCondition().render(ds.where(), status);
		}
		return ds;
	}

	public SqlModifier newExecutor(DBAdapterImpl adapter, ActiveChangable notify) {
		return new MultipleSqlModifier(adapter, this, notify);
	}
}
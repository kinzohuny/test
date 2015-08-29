package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.IntType;
import com.jiuqi.dna.core.impl.MoJoinedRelationRef;
import com.jiuqi.dna.core.impl.MoRootTableRef;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableUsage;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCursorLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteMultiBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteMultiCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class DeprecatedSimpleDeleteSql extends SimpleModifySql {

	private final void deleteSingle(DbMetadata dbMetadata,
			DeleteStatementImpl delete, DeleteStatementStatusVisitor status) {
		final TableDefineImpl target = delete.moTableRef.target;
		ISqlDeleteBuffer buffer = dbMetadata.sqlbuffers().delete(target.primary.namedb(), Render.aliasOf(delete.moTableRef, target.primary));
		MoJoinedRelationRef join = delete.moTableRef.getJoins();
		if (join != null) {
			join.render(buffer.target(), status);
		}
		if (delete.getCondition() != null) {
			delete.getCondition().render(buffer.where(), status);
		}
		this.build(buffer);
	}

	private final void deleteUsingCursor(DbMetadata dbMetadata,
			DeleteStatementImpl delete, DeleteStatementStatusVisitor status) {
		final TableDefineImpl target = delete.moTableRef.target;
		ISqlSegmentBuffer buffer = dbMetadata.sqlbuffers().segment();
		ISqlCursorLoopBuffer cursor = buffer.cursorLoop("SC", true);
		delete.renderUpdateRelationIntoSelect(cursor.query().select(), status);
		cursor.query().select().newColumn("DUMMY").load(1);
		cursor.declare("DUMMY", IntType.TYPE);
		for (int i = 0, c = target.dbTables.size(); i < c; i++) {
			DBTableDefineImpl dbTable = target.dbTables.get(i);
			ISqlDeleteBuffer ds = cursor.delete(dbTable.namedb(), Render.aliasOf(delete.moTableRef, dbTable));
			ds.whereCurrentOf("SC");
		}
		this.build(buffer);
	}

	// 没有依赖,多次delete
	private final void deleteUsingCompound(DbMetadata dbMetadata,
			DeleteStatementImpl delete, DeleteStatementStatusVisitor visitor) {
		final TableDefineImpl target = delete.moTableRef.target;
		DBTableDefineImpl last = visitor.conditionSource.firstTable();
		ISqlSegmentBuffer buffer = dbMetadata.sqlbuffers().segment();
		for (int i = 0, c = target.dbTables.size(); i < c; i++) {
			DBTableDefineImpl dbTable = target.dbTables.get(i);
			if (dbTable == last) {
				continue;
			}
			this.deleteSingleFor(buffer, delete, dbTable, visitor);
		}
		if (last != null) {
			this.deleteSingleFor(buffer, delete, last, visitor);
		}
		this.build(buffer);
	}

	// mysql支持的,delete删除多表
	private final void deleteUsingMultipleDelete(
			ISqlDeleteMultiCommandFactory dmcf, DeleteStatementImpl delete,
			DeleteStatementStatusVisitor status) {
		final MoRootTableRef tableRef = delete.moTableRef;
		final TableDefineImpl table = tableRef.target;
		final String alias = Render.aliasOf(tableRef, table.primary);
		ISqlDeleteMultiBuffer buffer = dmcf.deleteMulti(table.primary.namedb(), alias);
		ISqlTableRefBuffer from = buffer.target();
		for (int i = 1, c = table.dbTables.size(); i < c; i++) {
			DBTableDefineImpl j = table.dbTables.get(i);
			String ja = Render.aliasOf(tableRef, j);
			Render.renderLeftJoinOnRecidEq(from, alias, j.namedb(), ja);
			buffer.from(ja);
		}
		tableRef.render(from, status);
		if (delete.getCondition() != null) {
			delete.getCondition().render(buffer.where(), status);
		}
		this.build(buffer);
	}

	private final void deleteSingleFor(ISqlSegmentBuffer buffer,
			DeleteStatementImpl delete, DBTableDefineImpl dbTable,
			DeleteStatementStatusVisitor status) {
		final String alias = Render.aliasOf(delete.moTableRef, dbTable);
		ISqlDeleteBuffer ds = buffer.delete(dbTable.namedb(), alias);
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
	}

	public DeprecatedSimpleDeleteSql(DbMetadata dbMetadata,
			DeleteStatementImpl delete) {
		final DeleteStatementStatusVisitor visitor = new DeleteStatementStatusVisitor(delete);
		delete.visit(visitor, null);
		final int tbCount = delete.moTableRef.target.dbTables.size();
		if (tbCount == 1) {
			this.deleteSingle(dbMetadata, delete, visitor);
		} else {
			ISqlDeleteMultiCommandFactory dmcf = dbMetadata.sqlbuffers().getFeature(ISqlDeleteMultiCommandFactory.class);
			if (dmcf != null) {
				this.deleteUsingMultipleDelete(dmcf, delete, visitor);
			} else if (visitor.conditionSource.tableCount() > 1) {
				this.deleteUsingCursor(dbMetadata, delete, visitor);
			} else {
				this.deleteUsingCompound(dbMetadata, delete, visitor);
			}
		}
	}
}
package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.GUIDType;
import com.jiuqi.dna.core.impl.MoRootTableRef;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl.FieldAssign;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlConditionBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCursorLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlPredicate;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class UpdateUsingCursorSql extends SimpleModifySql {

	public UpdateUsingCursorSql(DbMetadata dbMetadata,
			UpdateStatementImpl update, UpdateStatementStatusVisitor visitor,
			UpdateMultipleResolver resolver) {
		ISqlSegmentBuffer segment = dbMetadata.sqlbuffers().segment();
		cursor(update, visitor, resolver, segment);
		this.build(segment);
	}

	private static final void cursor(UpdateStatementImpl update,
			UpdateStatementStatusVisitor visitor,
			UpdateMultipleResolver resolver, ISqlSegmentBuffer segment) {
		segment.declare(VAR_LAST_RECID, GUIDType.TYPE);
		ISqlCursorLoopBuffer cursor = defineCursor(segment, update, visitor);
		ISqlConditionBuffer ifs = cursor.ifThenElse();
		whenNotLastRecid(ifs.newWhen());
		updateCurrent(ifs.newThen(), resolver);
	}

	private static final String VAR_LAST_RECID = "LAST_RECID";
	private static final String CUR_NAME = "CUR";
	private static final String RECID_OUTPUT_ALIAS = "RECID_OUTPUT_ALIAS";
	private static final String VAR_RECID_OUTPUT = "RECID_OUTPUT";

	private static final ISqlCursorLoopBuffer defineCursor(
			ISqlSegmentBuffer segment, UpdateStatementImpl update,
			UpdateStatementStatusVisitor visitor) {
		MoRootTableRef tableRef = update.moTableRef;
		TableDefineImpl table = tableRef.target;
		ISqlCursorLoopBuffer cursor = segment.cursorLoop(CUR_NAME, true);
		ISqlSelectBuffer select = cursor.query().select();
		String alias = Render.aliasOf(tableRef, table.primary);
		ISqlTableRefBuffer from = select.newTableRef(table.primary.name, alias);
		UpdateSqlHelper.join(from, alias, tableRef, visitor, table.primary);
		select.newColumn(RECID_OUTPUT_ALIAS).loadColumnRef(alias, TableDefineImpl.FIELD_DBNAME_RECID);
		cursor.declare(VAR_RECID_OUTPUT, GUIDType.TYPE);
		for (int i = 0, c = update.assigns.size(); i < c; i++) {
			FieldAssign fa = update.assigns.get(i);
			fa.value().render(select.newColumn(fa.field.name), visitor);
			cursor.declare(fa.field.name, fa.field.getType());
		}
		cursor.query().newOrder(false).loadColumnRef(alias, TableDefineImpl.FIELD_DBNAME_RECID);
		return cursor;
	}

	private static final void whenNotLastRecid(ISqlExprBuffer when) {
		when.loadVar(VAR_LAST_RECID).predicate(SqlPredicate.IS_NULL, 1);
		when.loadVar(VAR_LAST_RECID).loadVar(VAR_RECID_OUTPUT).ne();
		when.or(2);
	}

	private static final void updateCurrent(ISqlSegmentBuffer segment,
			UpdateMultipleResolver resolver) {
		segment.assign(VAR_LAST_RECID).loadVar(VAR_RECID_OUTPUT);
		for (UpdateSingleDbTable single : resolver.dbTables) {
			ISqlUpdateBuffer update = segment.update(single.dbTable.namedb(), "T", false);
			update.whereCurrentOf(CUR_NAME);
			for (int i = 0, c = single.assigns.size(); i < c; i++) {
				FieldAssign fa = single.assigns.get(i);
				update.newValue(fa.field.namedb()).loadVar(fa.field.name);
			}
		}
	}
}
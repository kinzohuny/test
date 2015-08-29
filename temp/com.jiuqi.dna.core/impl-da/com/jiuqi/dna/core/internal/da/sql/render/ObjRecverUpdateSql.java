package com.jiuqi.dna.core.internal.da.sql.render;

import static com.jiuqi.dna.core.impl.TableDefineImpl.FIELD_DBNAME_RECID;
import static com.jiuqi.dna.core.impl.TableDefineImpl.FIELD_DBNAME_RECVER;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.MappingQueryStatementImpl;
import com.jiuqi.dna.core.impl.ORMAccessorImpl.RecverUpdater;
import com.jiuqi.dna.core.impl.QuRootTableRef;
import com.jiuqi.dna.core.impl.QueryColumnImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.render.RowSimpleUpdateSql.UpdatePart;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlConditionBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class ObjRecverUpdateSql extends
		SimpleSql<ObjRecverUpdateSql, RecverUpdater> {

	public final ArgumentPlaceholder a_recid;

	public final ParameterPlaceholder recver = new ParameterPlaceholder();

	public ObjRecverUpdateSql(DbMetadata dbMetadata,
			MappingQueryStatementImpl statement) {
		statement.validate();
		statement.checkModifyRootOnly();
		QuRootTableRef tableRef = (QuRootTableRef) statement.rootRelationRef();
		TableDefineImpl table = tableRef.getTarget();
		QueryColumnImpl recid = statement.findEqualColumn(tableRef, table.f_recid);
		if (recid == null) {
			throw Render.noRecidColumnForTable(statement, table);
		}
		this.a_recid = new ArgumentPlaceholder(recid.getMapingField(), table.f_recid.getType());
		ArrayList<RowSimpleUpdateSql.UpdatePart> parts = new ArrayList<RowSimpleUpdateSql.UpdatePart>();
		RowSimpleUpdateSql.UpdatePart part = null;
		for (DBTableDefineImpl dbTable : table.dbTables) {
			for (QueryColumnImpl qc : statement.columns) {
				TableFieldDefineImpl field = Render.tryGetUpdateFieldFor(qc, tableRef, dbTable);
				if (field != null) {
					if (part == null || part.dbTable != dbTable) {
						part = new RowSimpleUpdateSql.UpdatePart(dbTable, recid);
						parts.add(part);
					}
					part.assignValue(field, qc);
				}
			}
		}
		final boolean firstPrimary = parts.get(0).dbTable.isPrimary();
		if (parts.size() == 0) {
			throw Render.modifyTableNotSupport(statement);
		} else if (parts.size() == 1 && firstPrimary) {
			RowSimpleUpdateSql.UpdatePart first = parts.get(0);
			ISqlUpdateBuffer update = dbMetadata.sqlbuffers().update(first.dbTable.namedb(), alias, false);
			this.update(first, update, null);
			this.build(update);
		} else {
			ISqlSegmentBuffer buffer = dbMetadata.sqlbuffers().segment();
			if (!firstPrimary) {
				ISqlUpdateBuffer lock = buffer.update(table.primary.namedb(), alias, false);
				lock.newValue(FIELD_DBNAME_RECVER).loadColumnRef(alias, FIELD_DBNAME_RECVER);
				ISqlExprBuffer where = lock.where();
				where.loadColumnRef(alias, FIELD_DBNAME_RECID).loadParam(this.a_recid).eq();
				where.loadColumnRef(alias, FIELD_DBNAME_RECVER).loadParam(this.recver).eq();
				where.and(2);
				ISqlConditionBuffer ifseg = buffer.ifThenElse();
				ifseg.newWhen().rowcount().load(1).eq();
				ISqlSegmentBuffer then = ifseg.newThen();
				for (int i = 0; i < parts.size(); i++) {
					this.update(parts.get(i), null, then);
				}
			} else {
				this.update(parts.get(0), null, buffer);
				ISqlConditionBuffer ifs = buffer.ifThenElse();
				ifs.newWhen().rowcount().load(1).eq();
				ISqlSegmentBuffer then = ifs.newThen();
				for (int i = 1; i < parts.size(); i++) {
					this.update(parts.get(i), null, then);
				}
			}
			this.build(buffer);
		}
	}

	private static final String alias = "T";

	private final void update(UpdatePart part, ISqlUpdateBuffer update,
			ISqlSegmentBuffer segment) {
		if (update == null) {
			update = segment.update(part.dbTable.namedb(), alias, false);
		}
		for (Entry<TableFieldDefineImpl, ArgumentPlaceholder> e : part.entrySet()) {
			update.newValue(e.getKey().namedb()).loadParam(e.getValue());
		}
		ISqlExprBuffer where = update.where();
		where.loadColumnRef(alias, TableDefineImpl.FIELD_DBNAME_RECID);
		where.loadParam(this.a_recid);
		where.eq();
		if (part.dbTable.isPrimary()) {
			where.loadColumnRef(alias, TableDefineImpl.FIELD_DBNAME_RECVER);
			where.loadParam(this.recver).eq().and(2);
		}
	}

	@Override
	public final RecverUpdater newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new RecverUpdater(adapter, this, notify);
	}
}
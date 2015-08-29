package com.jiuqi.dna.core.internal.da.sql.render;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.QuRelationRef;
import com.jiuqi.dna.core.impl.QuTableRef;
import com.jiuqi.dna.core.impl.QueryColumnImpl;
import com.jiuqi.dna.core.impl.QueryStatementImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sql.render.RowSimpleUpdateSql.UpdatePart;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlConditionBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlMergeBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlMergeCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlReplaceBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlReplaceCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class RowSaveSql extends SimpleModifySql {

	public RowSaveSql(DbMetadata dbMetadata, QueryStatementImpl statement) {
		statement.validate();
		final LinkedHashMap<DBTableDefineImpl, UpdatePart> parts = new LinkedHashMap<DBTableDefineImpl, UpdatePart>();
		fillSaveParts(statement, parts);
		if (parts.size() == 0) {
			throw Render.modifyTableNotSupport(statement);
		} else {
			ISqlCommandFactory f = dbMetadata.sqlbuffers();
			ISqlMergeCommandFactory mf = dbMetadata.sqlbuffers().getFeature(ISqlMergeCommandFactory.class);
			ISqlReplaceCommandFactory rf = dbMetadata.sqlbuffers().getFeature(ISqlReplaceCommandFactory.class);
			if (mf != null) {
				usingMerge(f, mf, parts, this);
			} else if (rf != null) {
				usingReplace(f, rf, parts, this);
			} else {
				usingUpsert(f, parts, this);
			}
		}
	}

	private static final void fillSaveParts(QueryStatementImpl statement,
			LinkedHashMap<DBTableDefineImpl, UpdatePart> parts) {
		UpdatePart part = null;
		for (QuRelationRef relationRef : statement.rootRelationRef()) {
			if (statement.supportModify(relationRef)) {
				QuTableRef tableRef = (QuTableRef) relationRef;
				TableDefineImpl table = tableRef.getTarget();
				QueryColumnImpl recid = statement.findEqualColumn(tableRef, table.f_recid);
				if (recid == null) {
					throw Render.noRecidColumnForTable(statement, table);
				}
				for (DBTableDefineImpl dbTable : table.dbTables) {
					for (int i = 0, c = statement.columns.size(); i < c; i++) {
						QueryColumnImpl qc = statement.columns.get(i);
						TableFieldDefineImpl field = Render.tryGetUpdateFieldFor(qc, tableRef, dbTable);
						if (field != null) {
							if (part == null || part.dbTable != dbTable) {
								part = new UpdatePart(dbTable, recid);
								if (parts.put(dbTable, part) != null) {
									throw Render.duplicateModifyTable(statement, table);
								}
							}
							part.assignValue(field, qc);
						}
					}
				}
			}
		}
	}

	private static final void usingMerge(ISqlCommandFactory cf,
			ISqlMergeCommandFactory mf,
			LinkedHashMap<DBTableDefineImpl, UpdatePart> parts, RowSaveSql sql) {
		if (parts.size() == 1) {
			UpdatePart p = parts.entrySet().iterator().next().getValue();
			ISqlMergeBuffer merge = mf.merge(p.dbTable.namedb(), Render.rowModifyAlias(p.dbTable));
			mergePart(p, merge, null);
			sql.build(merge);
		} else {
			ISqlSegmentBuffer buffer = cf.segment();
			for (UpdatePart p : parts.values()) {
				mergePart(p, null, mf);
			}
			sql.build(buffer);
		}
	}

	private static final void mergePart(UpdatePart p, ISqlMergeBuffer merge,
			ISqlMergeCommandFactory f) {
		if (merge == null) {
			merge = f.merge(p.dbTable.namedb(), Render.rowModifyAlias(p.dbTable));
		}
		merge.usingDummy();
		merge.onCondition().loadColumnRef(Render.rowModifyAlias(p.dbTable), p.c_recid.name).loadParam(p.a_recid).eq();
		merge.insert(p.dbTable.owner.f_recid.namedb()).loadParam(p.a_recid);
		for (Entry<TableFieldDefineImpl, ArgumentPlaceholder> e : p.entrySet()) {
			merge.insert(e.getKey().namedb()).loadParam(e.getValue());
			merge.update(e.getKey().namedb()).loadParam(e.getValue());
		}
	}

	private static final void usingReplace(ISqlCommandFactory cf,
			ISqlReplaceCommandFactory rf,
			LinkedHashMap<DBTableDefineImpl, UpdatePart> parts, RowSaveSql sql) {
		if (parts.size() == 1) {
			UpdatePart p = parts.entrySet().iterator().next().getValue();
			ISqlReplaceBuffer replace = rf.replace(p.dbTable.namedb());
			replacePart(p, replace, rf);
			sql.build(replace);
		} else {
			ISqlSegmentBuffer buffer = cf.segment();
			for (UpdatePart p : parts.values()) {
				replacePart(p, null, rf);
			}
			sql.build(buffer);
		}
	}

	private static final void replacePart(UpdatePart p,
			ISqlReplaceBuffer replace, ISqlReplaceCommandFactory f) {
		if (replace == null) {
			replace = f.replace(p.dbTable.namedb());
		}
		replace.newField(p.c_recid.namedb());
		replace.newValue().loadParam(p.a_recid);
		for (Entry<TableFieldDefineImpl, ArgumentPlaceholder> e : p.entrySet()) {
			replace.newField(e.getKey().namedb());
			replace.newValue().loadParam(e.getValue());
		}
	}

	private static final void usingUpsert(ISqlCommandFactory cf,
			LinkedHashMap<DBTableDefineImpl, UpdatePart> parts, RowSaveSql sql) {
		ISqlSegmentBuffer buffer = cf.segment();
		for (UpdatePart p : parts.values()) {
			final String alias = Render.rowModifyAlias(p.dbTable);
			ISqlUpdateBuffer update = buffer.update(p.dbTable.namedb(), alias, false);
			update.where().loadColumnRef(alias, p.c_recid.name).loadParam(p.a_recid).eq();
			for (Entry<TableFieldDefineImpl, ArgumentPlaceholder> e : p.entrySet()) {
				update.newValue(e.getKey().namedb()).loadParam(e.getValue());
			}
			ISqlConditionBuffer ifs = buffer.ifThenElse();
			ifs.newWhen().rowcount().load(0).eq();
			ISqlInsertBuffer insert = ifs.newThen().insert(p.dbTable.namedb());
			insert.newField(p.c_recid.namedb());
			insert.newValue().loadParam(p.a_recid);
			for (Entry<TableFieldDefineImpl, ArgumentPlaceholder> e : p.entrySet()) {
				insert.newField(e.getKey().namedb());
				insert.newValue().loadParam(e.getValue());
			}
		}
		sql.build(buffer);
	}
}
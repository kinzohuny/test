package com.jiuqi.dna.core.internal.da.sql.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IllegalStatementDefineException;
import com.jiuqi.dna.core.impl.QuRelationRef;
import com.jiuqi.dna.core.impl.QuTableRef;
import com.jiuqi.dna.core.impl.QueryColumnImpl;
import com.jiuqi.dna.core.impl.QueryStatementBase;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class RowSimpleInsertSql extends SimpleModifySql {

	@SuppressWarnings("serial")
	static final class InsertPart extends
			LinkedHashMap<TableFieldDefineImpl, ArgumentPlaceholder> {

		final DBTableDefineImpl dbTable;

		InsertPart(DBTableDefineImpl dbTable) {
			this.dbTable = dbTable;
		}

		final ArgumentPlaceholder put(TableFieldDefineImpl field,
				QueryColumnImpl qc) {
			return this.put(field, new ArgumentPlaceholder(qc.getMapingField(), field.getType()));
		}
	}

	public RowSimpleInsertSql(DbMetadata dbMetadata,
			QueryStatementBase statement) {
		statement.validate();
		final ArrayList<RowSimpleInsertSql.InsertPart> parts = new ArrayList<RowSimpleInsertSql.InsertPart>();
		final HashSet<TableDefineImpl> tables = new HashSet<TableDefineImpl>();
		RowSimpleInsertSql.InsertPart part = null;
		for (QuRelationRef relationRef : statement.rootRelationRef()) {
			if (statement.supportModify(relationRef)) {
				final QuTableRef tableRef = (QuTableRef) relationRef;
				final TableDefineImpl table = tableRef.getTarget();
				if (tables.contains(table)) {
					throw Render.duplicateModifyTable(statement, table);
				}
				tables.add(table);
				if (statement.findEqualColumn(tableRef, table.f_recid) == null) {
					throw Render.noRecidColumnForTable(statement, table);
				}
				for (DBTableDefineImpl dbTable : table.dbTables) {
					for (QueryColumnImpl qc : statement.columns) {
						TableFieldDefineImpl field = Render.tryGetInsertFieldFor(qc, tableRef, dbTable);
						if (field != null) {
							if (part == null || part.dbTable != dbTable) {
								part = new RowSimpleInsertSql.InsertPart(dbTable);
								parts.add(part);
							}
							if (part.put(field, qc) != null) {
								throw new IllegalStatementDefineException(statement, "重复的修改列值。");
							}
						}
					}
				}
			}
		}
		if (parts.size() == 0) {
			throw Render.modifyTableNotSupport(statement);
		} else if (parts.size() == 1) {
			part = parts.get(0);
			ISqlInsertBuffer insert = dbMetadata.sqlbuffers().insert(part.dbTable.namedb());
			insert(part, insert, null);
			this.build(insert);
		} else {
			ISqlSegmentBuffer buffer = dbMetadata.sqlbuffers().segment();
			for (RowSimpleInsertSql.InsertPart p : parts) {
				insert(p, null, buffer);
			}
			this.build(buffer);
		}
	}

	private static final void insert(RowSimpleInsertSql.InsertPart part,
			ISqlInsertBuffer insert, ISqlSegmentBuffer segment) {
		if (insert == null) {
			insert = segment.insert(part.dbTable.namedb());
		}
		for (Entry<TableFieldDefineImpl, ArgumentPlaceholder> e : part.entrySet()) {
			insert.newField(e.getKey().namedb());
			insert.newValue().loadParam(e.getValue());
		}
	}
}
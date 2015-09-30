package com.jiuqi.dna.core.internal.da.sql.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.QuRelationRef;
import com.jiuqi.dna.core.impl.QuTableRef;
import com.jiuqi.dna.core.impl.QueryColumnImpl;
import com.jiuqi.dna.core.impl.QueryStatementBase;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class RowSimpleUpdateSql extends SimpleModifySql {

	@SuppressWarnings("serial")
	public static final class UpdatePart extends
			LinkedHashMap<TableFieldDefineImpl, ArgumentPlaceholder> {

		public final DBTableDefineImpl dbTable;
		public final TableFieldDefineImpl c_recid;
		public final ArgumentPlaceholder a_recid;

		public UpdatePart(DBTableDefineImpl dbTable, QueryColumnImpl recid) {
			this.dbTable = dbTable;
			this.c_recid = dbTable.owner.f_recid;
			this.a_recid = new ArgumentPlaceholder(recid.getMapingField(), this.c_recid.getType());
		}

		/**
		 * ���ø����ֶεĸ�ֵ,���Ѿ�Ϊ�ֶ����ø�ֵ,�µĸ�ֵ����Ч.
		 * 
		 * @param field
		 * @param qc
		 *            ��ֵ��Դ
		 * @return �Ƿ�ɹ��������˸�ֵ,�ɹ�����true,���ɹ�(�Ѿ�����)����false.
		 */
		public final boolean assignValue(TableFieldDefineImpl field,
				QueryColumnImpl qc) {
			if (this.containsKey(field)) {
				return false;
			}
			this.put(field, new ArgumentPlaceholder(qc.getMapingField(), field.getType()));
			return true;
		}
	}

	private static final String alias = "T";

	public RowSimpleUpdateSql(DbMetadata dbMetadata,
			QueryStatementBase statement) {
		statement.validate();
		final ArrayList<RowSimpleUpdateSql.UpdatePart> parts = new ArrayList<RowSimpleUpdateSql.UpdatePart>();
		final HashSet<TableDefineImpl> tables = new HashSet<TableDefineImpl>();
		RowSimpleUpdateSql.UpdatePart part = null;
		for (QuRelationRef relationRef : statement.rootRelationRef()) {
			if (statement.supportModify(relationRef)) {
				final QuTableRef tableRef = (QuTableRef) relationRef;
				final TableDefineImpl table = tableRef.getTarget();
				if (tables.contains(table)) {
					throw Render.duplicateModifyTable(statement, table);
				}
				tables.add(table);
				final QueryColumnImpl recid = statement.findEqualColumn(tableRef, table.f_recid);
				if (recid == null) {
					throw Render.noRecidColumnForTable(statement, table);
				}
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
			}
		}
		if (parts.size() == 0) {
			throw Render.modifyTableNotSupport(statement);
		} else if (parts.size() == 1) {
			part = parts.get(0);
			ISqlUpdateBuffer update = dbMetadata.sqlbuffers().update(part.dbTable.namedb(), alias, false);
			update(part, update, null);
			this.build(update);
		} else {
			ISqlSegmentBuffer buffer = dbMetadata.sqlbuffers().segment();
			for (RowSimpleUpdateSql.UpdatePart p : parts) {
				update(p, null, buffer);
			}
			this.build(buffer);
		}
	}

	private static final void update(RowSimpleUpdateSql.UpdatePart part,
			ISqlUpdateBuffer update, ISqlSegmentBuffer segment) {
		if (update == null) {
			update = segment.update(part.dbTable.namedb(), alias, false);
		}
		for (Entry<TableFieldDefineImpl, ArgumentPlaceholder> e : part.entrySet()) {
			TableFieldDefineImpl field = e.getKey();
			update.newValue(field.namedb()).loadParam(e.getValue());
		}
		ISqlExprBuffer where = update.where();
		where.loadColumnRef(alias, part.c_recid.namedb());
		where.loadParam(part.a_recid);
		where.eq();
	}
}
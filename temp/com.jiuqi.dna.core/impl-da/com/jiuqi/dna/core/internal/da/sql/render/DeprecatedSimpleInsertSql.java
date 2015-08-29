package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.DerivedQueryColumnImpl;
import com.jiuqi.dna.core.impl.IllegalStatementDefineException;
import com.jiuqi.dna.core.impl.InsertStatementImpl;
import com.jiuqi.dna.core.impl.NamedDefineContainerImpl;
import com.jiuqi.dna.core.impl.SelectColumnImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.TableUsages;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCursorLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class DeprecatedSimpleInsertSql extends SimpleModifySql {

	private static final EFilter<SelectColumnImpl<?, ?>, DBTableDefineImpl> filter = new EFilter<SelectColumnImpl<?, ?>, DBTableDefineImpl>() {

		public boolean accept(SelectColumnImpl<?, ?> column,
				DBTableDefineImpl dbTable) {
			return !isInsertColumnFor(column.name, dbTable);
		}
	};

	private final void insertSingle(DbMetadata dbMetadata,
			InsertStatementImpl insert, TableUsages usages) {
		final TableDefineImpl target = insert.moTableRef.target;
		final NamedDefineContainerImpl<DerivedQueryColumnImpl> columns = insert.values.columns;
		ISqlInsertBuffer buffer = dbMetadata.sqlbuffers().insert(target.primary.namedb());
		if (insert.isSubqueried()) {
			for (int i = 0, c = columns.size(); i < c; i++) {
				buffer.newField(target.fields.get(columns.get(i).name).namedb());
			}
			insert.values.renderFullSelect(buffer.select(), usages);
		} else {
			for (int i = 0, c = columns.size(); i < c; i++) {
				DerivedQueryColumnImpl column = columns.get(i);
				TableFieldDefineImpl field = target.fields.get(column.name);
				buffer.newField(field.namedb());
				column.value().render(buffer.newValue(), usages);
			}
		}
		this.build(buffer);
	}

	private final void insertMultiUsingCursor(DbMetadata dbMetadata,
			InsertStatementImpl insert, TableUsages usages) {
		final TableDefineImpl target = insert.moTableRef.target;
		final NamedDefineContainerImpl<DerivedQueryColumnImpl> columns = insert.values.columns;
		final ISqlSegmentBuffer buffer = dbMetadata.sqlbuffers().segment();
		// HCL 各数据库对游标的支持情况
		final ISqlCursorLoopBuffer cursor = buffer.cursorLoop("SC", true);
		insert.values.renderFullSelect(cursor.query().select(), usages);
		for (int i = 0, c = insert.values.columns.size(); i < c; i++) {
			DerivedQueryColumnImpl column = insert.values.columns.get(i);
			cursor.declare(column.name, target.fields.get(column.name).getType());
		}
		if (insert.values.rootRelationRef() == null) {
			cursor.query().select().fromDummy();
		}
		for (int i = 0; i < target.dbTables.size(); i++) {
			DBTableDefineImpl dbTable = target.dbTables.get(i);
			ISqlInsertBuffer si = cursor.insert(dbTable.namedb());
			for (int j = 0; j < columns.size(); j++) {
				DerivedQueryColumnImpl column = columns.get(j);
				if (isInsertColumnFor(column.name, dbTable)) {
					si.newField(target.fields.get(column.name).namedb());
					si.newValue().loadVar(column.name);
				}
			}
		}
		this.build(buffer);
	}

	private final void insertMultiUsingCompound(DbMetadata dbMetadata,
			InsertStatementImpl insert, TableUsages usages) {
		final TableDefineImpl target = insert.moTableRef.target;
		final NamedDefineContainerImpl<DerivedQueryColumnImpl> columns = insert.values.columns;
		final ISqlSegmentBuffer buffer = dbMetadata.sqlbuffers().segment();
		for (int i = 0; i < target.dbTables.size(); i++) {
			DBTableDefineImpl dbTable = target.dbTables.get(i);
			ISqlInsertBuffer si = buffer.insert(dbTable.namedb());
			if (insert.isSubqueried()) {
				for (int j = 0; j < columns.size(); j++) {
					DerivedQueryColumnImpl column = columns.get(j);
					if (isInsertColumnFor(column.name, dbTable)) {
						si.newField(target.fields.get(column.name).namedb());
					}
				}
				insert.values.renderFullSelect(si.select(), usages, filter, dbTable);
			} else {
				for (int j = 0; j < columns.size(); j++) {
					DerivedQueryColumnImpl column = columns.get(j);
					TableFieldDefineImpl field = target.fields.get(column.name);
					if (isInsertColumnFor(column.name, dbTable)) {
						si.newField(field.namedb());
						column.value().render(si.newValue(), usages);
					}
				}
			}
		}
		this.build(buffer);
	}

	static final boolean isInsertColumnFor(String field,
			DBTableDefineImpl dbTable) {
		TableFieldDefineImpl f = dbTable.owner.fields.get(field);
		return f.isRECID() || f.getDBTable() == dbTable;
	}

	public DeprecatedSimpleInsertSql(DbMetadata dbMetadata,
			InsertStatementImpl insert) {
		if (insert.values.columns.size() == 0) {
			throw new IllegalStatementDefineException(insert, "插入语句定义[" + insert.name + "]未定义插入值。");
		}
		final InsertStatementStatusVisitor visitor = new InsertStatementStatusVisitor();
		insert.visit(visitor, null);
		if (insert.values.columns.find(TableDefineImpl.FIELD_NAME_RECID) == null) {
			throw new IllegalStatementDefineException(insert, "插入语句定义[" + insert.name + "]未定义RECID字段的插入值。");
		}
		final int tbCount = insert.moTableRef.target.dbTables.size();
		if (tbCount == 1) {
			this.insertSingle(dbMetadata, insert, visitor);
		} else if (visitor.isValuesNonDeterministic()) {
			this.insertMultiUsingCursor(dbMetadata, insert, visitor);
		} else {
			this.insertMultiUsingCompound(dbMetadata, insert, visitor);
		}
	}
}
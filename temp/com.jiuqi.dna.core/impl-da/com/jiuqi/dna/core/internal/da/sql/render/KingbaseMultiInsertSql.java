package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.DerivedQueryColumnImpl;
import com.jiuqi.dna.core.impl.IllegalStatementDefineException;
import com.jiuqi.dna.core.impl.InsertStatementImpl;
import com.jiuqi.dna.core.impl.NamedDefineContainerImpl;
import com.jiuqi.dna.core.impl.PlainSql;
import com.jiuqi.dna.core.impl.SelectColumnImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.KingbaseMultipleSqlModifier;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public class KingbaseMultiInsertSql extends MultiSql implements ModifySql {

	private static final EFilter<SelectColumnImpl<?, ?>, DBTableDefineImpl> filter = new EFilter<SelectColumnImpl<?, ?>, DBTableDefineImpl>() {

		public boolean accept(SelectColumnImpl<?, ?> column,
				DBTableDefineImpl dbTable) {
			return !isInsertColumnFor(column.name, dbTable);
		}
	};

	public final KingbaseMultipleSqlModifier newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new KingbaseMultipleSqlModifier(adapter, this, notify);
	}

	public KingbaseMultiInsertSql(DbMetadata dbMetadata,
			InsertStatementImpl insert) {
		if (insert.values.columns.size() == 0) {
			throw new IllegalStatementDefineException(insert, "插入语句定义[" + insert.name + "]未定义任何赋值。");
		}
		final InsertStatementStatusVisitor visitor = new InsertStatementStatusVisitor();
		insert.visit(visitor, null);
		if (insert.values.columns.find(TableDefineImpl.FIELD_NAME_RECID) == null) {
			throw new IllegalStatementDefineException(insert, "插入语句定义[" + insert.name + "]未定义RECID字段的插入值。");
		}
		final TableDefineImpl target = insert.moTableRef.target;
		final NamedDefineContainerImpl<DerivedQueryColumnImpl> columns = insert.values.columns;
		for (int i = 0; i < target.dbTables.size(); i++) {
			DBTableDefineImpl dbTable = target.dbTables.get(i);
			ISqlInsertBuffer si = dbMetadata.sqlbuffers().insert(dbTable.namedb());
			if (insert.isSubqueried()) {
				for (int j = 0; j < columns.size(); j++) {
					DerivedQueryColumnImpl column = columns.get(j);
					if (isInsertColumnFor(column.name, dbTable)) {
						si.newField(target.fields.get(column.name).namedb());
					}
				}
				insert.values.renderFullSelect(si.select(), visitor, filter, dbTable);
			} else {
				for (int j = 0; j < columns.size(); j++) {
					DerivedQueryColumnImpl column = columns.get(j);
					TableFieldDefineImpl field = target.fields.get(column.name);
					if (isInsertColumnFor(column.name, dbTable)) {
						si.newField(field.namedb());
						column.value().render(si.newValue(), visitor);
					}
				}
			}
			PlainSql sqlinfo = new PlainSql();
			sqlinfo.build(si);
			this.sqls.add(sqlinfo);
		}
	}

	static final boolean isInsertColumnFor(String field,
			DBTableDefineImpl dbTable) {
		TableFieldDefineImpl f = dbTable.owner.fields.get(field);
		return f.isRECID() || f.getDBTable() == dbTable;
	}
}
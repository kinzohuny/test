package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.DerivedQueryColumnImpl;
import com.jiuqi.dna.core.impl.InsertStatementImpl;
import com.jiuqi.dna.core.impl.NamedDefineContainerImpl;
import com.jiuqi.dna.core.impl.SelectColumnImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.MultipleSqlModifier;
import com.jiuqi.dna.core.internal.da.sql.execute.SqlModifier;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class InsertMultipleSql extends MultipleSql implements ModifySql {

	public InsertMultipleSql(DbMetadata dbMetadata, InsertStatementImpl insert,
			InsertStatementStatusVisitor visitor) {
		final TableDefineImpl target = insert.moTableRef.target;
		final NamedDefineContainerImpl<DerivedQueryColumnImpl> columns = insert.values.columns;
		for (int i = 0; i < target.dbTables.size(); i++) {
			DBTableDefineImpl dbTable = target.dbTables.get(i);
			ISqlInsertBuffer si = dbMetadata.sqlbuffers().insert(dbTable.namedb());
			if (insert.isSubqueried()) {
				for (int j = 0; j < columns.size(); j++) {
					DerivedQueryColumnImpl column = columns.get(j);
					if (InsertSqlHelper.isInsertColumnFor(column.name, dbTable)) {
						si.newField(target.fields.get(column.name).namedb());
					}
				}
				insert.values.renderFullSelect(si.select(), visitor, filter, dbTable);
			} else {
				for (int j = 0; j < columns.size(); j++) {
					DerivedQueryColumnImpl column = columns.get(j);
					TableFieldDefineImpl field = target.fields.get(column.name);
					if (InsertSqlHelper.isInsertColumnFor(column.name, dbTable)) {
						si.newField(field.namedb());
						column.value().render(si.newValue(), visitor);
					}
				}
			}
			if (i == 0) {
				this.build(si);
			} else {
				this.addSql().build(si);
			}
		}
	}

	private static final EFilter<SelectColumnImpl<?, ?>, DBTableDefineImpl> filter = new EFilter<SelectColumnImpl<?, ?>, DBTableDefineImpl>() {

		public boolean accept(SelectColumnImpl<?, ?> column,
				DBTableDefineImpl dbTable) {
			return !InsertSqlHelper.isInsertColumnFor(column.name, dbTable);
		}
	};

	public final SqlModifier newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new MultipleSqlModifier(adapter, this, notify);
	}
}
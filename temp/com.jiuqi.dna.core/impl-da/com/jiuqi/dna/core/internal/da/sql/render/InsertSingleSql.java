package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.DerivedQueryColumnImpl;
import com.jiuqi.dna.core.impl.InsertStatementImpl;
import com.jiuqi.dna.core.impl.NamedDefineContainerImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class InsertSingleSql extends SimpleModifySql {

	public InsertSingleSql(DbMetadata dbMetadata, InsertStatementImpl insert,
			InsertStatementStatusVisitor visitor) {
		final TableDefineImpl target = insert.moTableRef.target;
		final NamedDefineContainerImpl<DerivedQueryColumnImpl> columns = insert.values.columns;
		ISqlInsertBuffer buffer = dbMetadata.sqlbuffers().insert(target.primary.namedb());
		if (insert.isSubqueried()) {
			for (int i = 0, c = columns.size(); i < c; i++) {
				buffer.newField(target.fields.get(columns.get(i).name).namedb());
			}
			insert.values.renderFullSelect(buffer.select(), visitor);
		} else {
			for (int i = 0, c = columns.size(); i < c; i++) {
				DerivedQueryColumnImpl column = columns.get(i);
				TableFieldDefineImpl field = target.fields.get(column.name);
				buffer.newField(field.namedb());
				column.value().render(buffer.newValue(), visitor);
			}
		}
		this.build(buffer);
	}
}
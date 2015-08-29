package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.DerivedQueryColumnImpl;
import com.jiuqi.dna.core.impl.InsertStatementImpl;
import com.jiuqi.dna.core.impl.NamedDefineContainerImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCursorLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlInsertBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public class InsertUsingCursorSql extends SimpleModifySql {

	public InsertUsingCursorSql(DbMetadata dbMetadata,
			InsertStatementImpl insert, InsertStatementStatusVisitor visitor) {
		final TableDefineImpl target = insert.moTableRef.target;
		final NamedDefineContainerImpl<DerivedQueryColumnImpl> columns = insert.values.columns;
		final ISqlSegmentBuffer buffer = dbMetadata.sqlbuffers().segment();
		// HCL 各数据库对游标的支持情况
		final ISqlCursorLoopBuffer cursor = buffer.cursorLoop("SC", true);
		insert.values.renderFullSelect(cursor.query().select(), visitor);
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
				if (InsertSqlHelper.isInsertColumnFor(column.name, dbTable)) {
					si.newField(target.fields.get(column.name).namedb());
					si.newValue().loadVar(column.name);
				}
			}
		}
		this.build(buffer);
	}
}
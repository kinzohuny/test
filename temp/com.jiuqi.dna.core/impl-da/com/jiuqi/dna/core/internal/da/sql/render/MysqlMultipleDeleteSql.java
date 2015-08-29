package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.MoRootTableRef;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteMultiBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.db.support.mysql.MysqlMetadata;

public final class MysqlMultipleDeleteSql extends SimpleModifySql {

	public MysqlMultipleDeleteSql(MysqlMetadata dbMetadata,
			DeleteStatementImpl delete, DeleteStatementStatusVisitor status) {
		final MoRootTableRef tableRef = delete.moTableRef;
		final TableDefineImpl table = tableRef.target;
		final String alias = Render.aliasOf(tableRef, table.primary);
		ISqlDeleteMultiBuffer buffer = dbMetadata.sqlbuffers().deleteMulti(table.primary.namedb(), alias);
		ISqlTableRefBuffer from = buffer.target();
		for (int i = 1, c = table.dbTables.size(); i < c; i++) {
			DBTableDefineImpl j = table.dbTables.get(i);
			String ja = Render.aliasOf(tableRef, j);
			Render.renderLeftJoinOnRecidEq(from, alias, j.namedb(), ja);
			buffer.from(ja);
		}
		tableRef.render(from, status);
		if (delete.getCondition() != null) {
			delete.getCondition().render(buffer.where(), status);
		}
		this.build(buffer);
	}
}
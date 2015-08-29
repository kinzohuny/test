package com.jiuqi.dna.core.internal.da.report;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

final class RPTRecordDeleteSql extends SimpleModifySql {

	final ArgumentPlaceholder recid;

	private static final String alias = "T";

	RPTRecordDeleteSql(DbMetadata dbMetadata, RPTRecordSetTableInfo tableInfo) {
		this.recid = new ArgumentPlaceholder(tableInfo.recidSf, tableInfo.recidSf.getType());
		if (tableInfo.size() == 1) {
			DBTableDefineImpl dbTable = tableInfo.get(0).dbTable;
			ISqlDeleteBuffer buffer = dbMetadata.sqlbuffers().delete(dbTable.namedb(), alias);
			delete(buffer, null, dbTable, this.recid);
			this.build(buffer);
		} else if (tableInfo.size() > 1) {
			ISqlSegmentBuffer buffer = dbMetadata.sqlbuffers().segment();
			for (int i = 0, c = tableInfo.size(); i < c; i++) {
				RPTRecordSetDBTableInfo dbTableInfo = tableInfo.get(i);
				delete(null, buffer, dbTableInfo.dbTable, this.recid);
			}
			this.build(buffer);
		}
	}

	private static final void delete(ISqlDeleteBuffer delete,
			ISqlSegmentBuffer segment, DBTableDefineImpl dbTable,
			ParameterPlaceholder recid) {
		if (delete == null) {
			delete = segment.delete(dbTable.namedb(), alias);
		}
		delete.where().loadColumnRef(alias, TableDefineImpl.FIELD_DBNAME_RECID).loadParam(recid).eq();
	}
}
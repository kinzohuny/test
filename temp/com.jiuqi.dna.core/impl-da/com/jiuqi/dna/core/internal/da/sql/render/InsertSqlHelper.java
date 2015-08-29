package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;

public final class InsertSqlHelper {

	static final boolean isInsertColumnFor(String field,
			DBTableDefineImpl dbTable) {
		TableFieldDefineImpl f = dbTable.owner.fields.get(field);
		return f.isRECID() || f.getDBTable() == dbTable;
	}
}
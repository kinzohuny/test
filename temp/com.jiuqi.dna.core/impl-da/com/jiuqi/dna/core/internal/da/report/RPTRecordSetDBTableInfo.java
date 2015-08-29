package com.jiuqi.dna.core.internal.da.report;

import java.util.ArrayList;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;

final class RPTRecordSetDBTableInfo extends ArrayList<RPTRecordSetFieldImpl> {

	private static final long serialVersionUID = 1L;
	final DBTableDefineImpl dbTable;

	RPTRecordSetDBTableInfo(DBTableDefineImpl dbTable) {
		this.dbTable = dbTable;
	}
}
package com.jiuqi.dna.core.db.sync;

import com.jiuqi.dna.core.def.table.DBTableDefine;

/**
 * 表名称为保留字.
 * 
 * @author houchunlei
 * 
 */
public class DbTableNameReservedException extends TableSyncException {

	private static final long serialVersionUID = -3389464660732175169L;

	public DbTableNameReservedException(DBTableDefine dbTable) {
		super(dbTable.getOwner(), message(dbTable));
	}

	public static final String message(DBTableDefine dbTable) {
		return "物理表[" + dbTable.getName() + "]名称为保留字或关键字.";
	}
}
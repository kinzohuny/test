package com.jiuqi.dna.core.db.sync;

import com.jiuqi.dna.core.def.table.DBTableDefine;

/**
 * ������Ϊ������.
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
		return "�����[" + dbTable.getName() + "]����Ϊ�����ֻ�ؼ���.";
	}
}
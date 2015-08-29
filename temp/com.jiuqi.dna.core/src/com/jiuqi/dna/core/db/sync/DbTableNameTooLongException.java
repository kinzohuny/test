package com.jiuqi.dna.core.db.sync;

import com.jiuqi.dna.core.def.table.DBTableDefine;

/**
 * 表名称长度过大
 * 
 * @author houchunlei
 * 
 */
public class DbTableNameTooLongException extends TableSyncException {

	private static final long serialVersionUID = 7964727907459234334L;

	public DbTableNameTooLongException(DBTableDefine dbTable, int maximum) {
		super(dbTable.getOwner(), "物理表[" + dbTable.getName()
				+ "]名称的字节长度超过最大允许的字节长度[" + maximum + "].");
	}
}
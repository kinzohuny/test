package com.jiuqi.dna.core.db.sync;

import com.jiuqi.dna.core.def.table.DBTableDefine;

/**
 * �����Ƴ��ȹ���
 * 
 * @author houchunlei
 * 
 */
public class DbTableNameTooLongException extends TableSyncException {

	private static final long serialVersionUID = 7964727907459234334L;

	public DbTableNameTooLongException(DBTableDefine dbTable, int maximum) {
		super(dbTable.getOwner(), "�����[" + dbTable.getName()
				+ "]���Ƶ��ֽڳ��ȳ������������ֽڳ���[" + maximum + "].");
	}
}
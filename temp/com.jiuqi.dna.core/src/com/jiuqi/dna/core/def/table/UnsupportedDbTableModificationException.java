package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;

/**
 * ��֧�ֵĶ����������޸Ĳ���
 * 
 * @author houchunlei
 * 
 */
public final class UnsupportedDbTableModificationException extends
		UnsupportedTableModificationException {

	private static final long serialVersionUID = -6629961493091261989L;

	public final transient DBTableDefine dbTable;

	/**
	 * Ϊ�������
	 */
	public static final int PRIMARY_TABLE = 0;
	/**
	 * ������԰����ֶ�
	 */
	public static final int NOT_EMPTY_TABLE = 1;

	public final int action;
	public final int condition;

	public UnsupportedDbTableModificationException(TableDefineImpl table,
			DBTableDefineImpl dbTable, int action, int condition) {
		super(table, message(dbTable, action, condition));
		this.dbTable = dbTable;
		this.action = action;
		this.condition = condition;
	}

	private static final String message(DBTableDefineImpl dbTable, int action,
			int condition) {
		return "������" + action(action) + condition(condition) + "�������"
				+ dbTable.desc() + ".";
	}

	private static final String condition(int condition) {
		switch (condition) {
		case PRIMARY_TABLE:
			return "��";
		case NOT_EMPTY_TABLE:
			return "�ǿյ�";
		default:
			throw new IllegalStateException();
		}
	}
}
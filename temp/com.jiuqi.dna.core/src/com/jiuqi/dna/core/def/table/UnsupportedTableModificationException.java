package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.impl.TableDefineImpl;

public abstract class UnsupportedTableModificationException extends
		RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * �޸Ĳ���
	 */
	public static final int ACTION_MODIFY = 0;
	/**
	 * �Ƴ�����
	 */
	public static final int ACTION_REMOVE = 1;
	/**
	 * �ƶ�����
	 */
	public static final int ACTION_MOVE = 2;

	public final TableDefine table;

	public UnsupportedTableModificationException(TableDefineImpl table,
			String cause) {
		super(message0(table) + cause);
		this.table = table;
	}

	public static final String message0(TableDefineImpl table) {
		return "��֧�ֵ�����߼���" + table.desc() + "���޸Ĳ���: ";
	}

	protected static final String action(int action) {
		switch (action) {
		case ACTION_MODIFY:
			return "�޸�";
		case ACTION_REMOVE:
			return "�Ƴ�";
		case ACTION_MOVE:
			return "�ƶ�";
		default:
			throw new IllegalArgumentException();
		}
	}
}